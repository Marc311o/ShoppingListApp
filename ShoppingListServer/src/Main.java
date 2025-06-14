import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import datamodel.ProductsList;
import datamodel.User;
import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static ArrayList<User> users;   // użytkownicy
    static ArrayList<ProductsList> lists;   // listy

    static final int SOCKET_PORT = 5000;
    static final int MAX_LISTS_SAVED = 100;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        // Inicjalizacja danych
        users = User.readUsers("users.txt");
        lists = ProductsList.readProductLists(MAX_LISTS_SAVED);

        for (User user : users) {

            for(ProductsList list : lists) {
                if(user.getProductListsID().contains(list.getId())){
                    user.addProductLists(list);
                }
            }

            for(ProductsList list : user.getProductLists()){
                list.synchronizeUsernames(users);
            }
        }



        for (User user : users) System.out.println(user.toString());

        int i = 1;
        for (ProductsList list : lists){
            System.out.println(list.toString(i++));
        }


        try (ServerSocket serverSocket = new ServerSocket(SOCKET_PORT)) {
            System.out.println("Serwer nasłuchuje na porcie " + SOCKET_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (InterruptedException e) {
                        System.err.println("Błąd - przerwano wątek");
                    }
                });
            }

        } catch (IOException e) {
            System.err.println("Błąd serwera: " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }
    }

    public static User findUser(String username) {
        for (User user : users) {
            if (user.getName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static void handleClient(Socket clientSocket) throws InterruptedException {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // Odbierz nazwę użytkownika jako tekst
            String request = in.readLine();
            String[] parts = request.split("\\$");

            switch (parts[0]) {
                case "GETUSER" -> {

                    String username = parts[1];
                    String userJson = handleUserDataRequest(username);
                    out.println(userJson);
                }
                case "GETSTATE" -> {

                    int id = Integer.parseInt(parts[1]);
                    String response = handleListGetStateRequest(id);
                    out.println(response);

                }
                case "SETSTATE" -> {

                    int id = Integer.parseInt(parts[1]);
                    String newState = parts[2];
                    handleListSetStateRequest(id, newState);

                }
                case "UPDATEUSERDATA" -> {
                    String username = parts[1];
                    handleUserDataUpdateRequest(username, parts[2]);
                }
                case "GETUNUSEDLISTID" -> {
                    int id = ProductsList.getNextAvailableListId(lists);
                    out.println(id);
                }case "GETALLUSERNAMES" -> {
                    String usernames = getAllUserNames();
                    out.println(usernames);
                }case "SHARELIST" -> {
                    String username = parts[1];
                    int id = Integer.parseInt(parts[2]);
                    handleAddUserToListRequest(id, username);
                }case "QUITLIST" -> {
                    String username = parts[1];
                    int id = Integer.parseInt(parts[2]);
                    handleRemoveUserFromListRequest(id, username);
                }

            }


        } catch (IOException e) {
            System.err.println("Błąd klienta: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Błąd podczas zamykania gniazda klienta: " + e.getMessage());
            }
        }
    }

    public static String handleUserDataRequest(String username) {
        System.out.println("<- Otrzymano żądanie od: " + username);

        // Znajdź użytkownika
        User user = findUser(username);

        // Zamień na JSON i wyślij
        Gson gson = new Gson();
        String userJson = gson.toJson(user);


        System.out.println(user != null
                ? "-> Wysłano dane użytkownika: " + user.getName()
                : "Nie znaleziono użytkownika: " + username
        );

        return userJson;
    }

    public static String handleListGetStateRequest(int id) {
        String response = "LISTNOTFOUND";
        for(ProductsList list : lists){
            if(list.getId() == id){
                response = (list.isBeingEdited() ? "BUSY" : "FREE");
            }
        }

        return response;
    }

    public static void handleListSetStateRequest(int id, String state) {
        for(ProductsList list : lists){
            if(list.getId() == id){
               if(state.equals("BUSY")){
                   list.setBeingEdited(true);
                   System.out.println("<- zablokowano listę (id: " + id + ")");
               }else if(state.equals("FREE")){
                   list.setBeingEdited(false);
                   System.out.println("<- odblokowano listę (id: " + id + ")");
               }
            }
        }
    }

    public static void handleUserDataUpdateRequest(String username, String json) {
        System.out.println("<- Otrzymano dane użytkownika: " + username);
//        System.out.println(json);

        User updatedUser = parseUserFromJson(json);

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getName().equalsIgnoreCase(username)) {
                users.set(i, updatedUser);
                break;
            }
        }

        ProductsList.updateGlobalListsFromUser(updatedUser, lists);
        ProductsList.saveProductLists(lists);
        User.writeUsersToFile("users.txt", users);
        System.out.println("- Zaktualizowano dane użytkownika: " + username + " -");
    }

    public static User parseUserFromJson(String json) {

        if (json == null || json.trim().isEmpty()) {
            System.err.println("Invalid JSON input: null or empty string.");
            return null;
        }

        Gson gson = new Gson();
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        // Parsuj imię i ID
        String name = jsonObject.get("name").getAsString();
        int id = jsonObject.get("id").getAsInt();

        // Parsuj productListsID
        Type listIntType = new TypeToken<ArrayList<Integer>>() {}.getType();
        ArrayList<Integer> productListsID = gson.fromJson(jsonObject.get("productListsID"), listIntType);

        // Utwórz użytkownika na podstawie konstruktora
        User user = new User(name, id, productListsID);

        // Parsuj productLists (pełne obiekty)
        JsonElement productListsElement = jsonObject.get("productLists");
        if (productListsElement != null && productListsElement.isJsonArray()) {
            Type listProductsType = new TypeToken<ArrayList<ProductsList>>() {}.getType();
            ArrayList<ProductsList> lists = gson.fromJson(productListsElement, listProductsType);
            user.setProductLists(lists);
        }

        return user;
    }

    public static String getAllUserNames() {
        ArrayList<String> userNames = new ArrayList<>();

        for (User user : users) {
            userNames.add(user.getName());
        }

        return String.join(";", userNames);
    }

    public static void handleAddUserToListRequest(int listID, String username) {
        System.out.println("<- Otrzymano prośbę o udostępnienie listy (id: "+ listID +") użytkownikowi" + username);

        User selecedUser = findUser(username);

        for (ProductsList list : lists) {
            if (list.getId() == listID) {
                list.getUsernames().add(username);
                list.synchronizeIDs(users);
                break;
            }
        }


        if(selecedUser != null){
            selecedUser.getProductListsID().add(listID);
            ProductsList.assignListsToUsers(users, lists);
            User.refreshUsers(users, lists);
        }
        synchronizeAll(users, lists);

        User.writeUsersToFile("users.txt", users);
        ProductsList.saveProductLists(lists);

        System.out.println("- Zaktualizowano dane użytkownika: " + username + " -");
        System.out.println(selecedUser);
    }

    public static void handleRemoveUserFromListRequest(int listID, String username) {
        System.out.println("<- Otrzymano prośbę o opuszczenie listy (id: " + listID + ") przez użytkownika " + username);

        User selectedUser = findUser(username);

        for (ProductsList list : lists) {
            if (list.getId() == listID) {
                list.getUsernames().remove(username);
                list.synchronizeIDs(users);
                break;
            }
        }

        if (selectedUser != null) {
            selectedUser.getProductListsID().remove(Integer.valueOf(listID)); // ← poprawka tutaj!
            ProductsList.assignListsToUsers(users, lists);
            User.refreshUsers(users, lists);
        }

        synchronizeAll(users, lists); // jeśli masz spójną metodę nadrzędną

        User.writeUsersToFile("users.txt", users);
        ProductsList.saveProductLists(lists);

        System.out.println("- Zaktualizowano dane użytkownika: " + username + " -");
        System.out.println(selectedUser);
    }

    public static void synchronizeAll(ArrayList<User> users, ArrayList<ProductsList> lists) {


        // Uaktualnij usernames i usersID w każdej liście
        for (ProductsList list : lists) {
            list.synchronizeUsernames(users);
            list.synchronizeIDs(users);
        }

        // Na podstawie list przypisz do użytkowników
        ProductsList.assignListsToUsers(users, lists);

        // Odśwież referencje list w użytkownikach (po ID)
        User.refreshUsers(users, lists);
    }

}
