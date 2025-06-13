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

public class ConnectionHandler {

    public final static int SOCKET = 5000;

    public static User getUser(String username) {
        User user = null;

        try (Socket socket = new Socket("localhost", SOCKET);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            // Wysyłanie nazwy użytkownika do serwera
            out.println(username);
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

}
