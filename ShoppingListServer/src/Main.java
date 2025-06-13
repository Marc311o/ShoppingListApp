import datamodel.ProductsList;
import datamodel.User;
import com.google.gson.Gson;

import java.io.*;
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



//        for (User user : users) System.out.println(user.toString());
//        lists.getFirst().setBeingEdited(true);






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

}
