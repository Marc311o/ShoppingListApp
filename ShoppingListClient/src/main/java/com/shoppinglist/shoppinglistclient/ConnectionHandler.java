package com.shoppinglist.shoppinglistclient;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import com.shoppinglist.shoppinglistclient.datamodel.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ConnectionHandler {

    public final static int SOCKET = 5000;

    public static User getUser(String username) {
        User user = null;

        try (Socket socket = new Socket("localhost", SOCKET);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Wysyłanie nazwy użytkownika do serwera
            out.println("GETUSER$" +username);
            System.out.println("<- Wysłano username: " + username);

            // Odbieranie JSON-a i konwersja do obiektu User
            String json = in.readLine();
//            System.out.println("-> Odebrano JSON: " + json);

            if(!json.equals("null")) {
                user = parseUserFromJson(json);
            }

            if (user != null) {
                System.out.println("-> Otrzymano dane użytkownika: " + user.getName());
//                System.out.println(user);
            } else {
                System.out.println("-> Użytkownik nie istnieje.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
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

    public static User refreshUserData(){
        return getUser(ProgramData.currentUser.getName());
    }

    public static void sendUserData(){
        User user = ProgramData.currentUser;

        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        try (Socket socket = new Socket("localhost", SOCKET);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            out.println("UPDATEUSERDATA$"+ user.getName() + "$" + userJson);
            System.out.println("<- Wysłano dane użytkownika: " + user.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isListBeingEdited(int id){
        boolean state = true;

        try (Socket socket = new Socket("localhost", SOCKET);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            out.println("GETSTATE$"+ id + "$");
            System.out.println("<- Wysłano zapytanie o edycję listy (id:" + id + ")");

            String response = in.readLine();

            if(response.equals("BUSY")){
                System.out.println("-> lista (id:" + id + ") jest w tym momencie edytowana");
            }else{
                state = false;
                System.out.println("-> lista (id:" + id + ") jest wolna");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public static void setListState(int id, String state){
        try (Socket socket = new Socket("localhost", SOCKET);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            if(state.equals("BUSY")) {
                out.println("SETSTATE$" + id + "$BUSY$");
                System.out.println("<- zablokowano listę (id: " + id + ")");
            }else if(state.equals("FREE")) {
                out.println("SETSTATE$" + id + "$FREE$");
                System.out.println("<- odblokowano listę (id: " + id + ")");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getUnusedId(){
        int id = -1;
        try (Socket socket = new Socket("localhost", SOCKET);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            out.println("GETUNUSEDLISTID");
            System.out.println("<- Wysłano zapytanie o nieużywane id listy");
            String response = in.readLine();
            id = Integer.parseInt(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static ArrayList<String> getUsernames(){
        ArrayList<String> usernames = new ArrayList<>();

        try (Socket socket = new Socket("localhost", SOCKET);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            out.println("GETALLUSERNAMES");
            System.out.println("<- Wysłano zapytanie o wszystkich użytkowników");
            String response = in.readLine();
            String[] parts = response.split(";");
            usernames.addAll(Arrays.asList(parts));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usernames;
    }

    public static void shareListToUser(int listID, String username){

        try (Socket socket = new Socket("localhost", SOCKET);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {

            out.println("SHARELIST$" + username + "$" + listID);
            System.out.println("<- Wysłano prośbę o udostęwanie listy (id:" + listID + ") użytkownikowi: " + username);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
