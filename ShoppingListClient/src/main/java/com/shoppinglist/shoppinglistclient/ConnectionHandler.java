package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler {

    public static User getUser() {
        com.shoppinglist.shoppinglistclient.datamodel.User user = null;

        try {
            Class.forName("com.shoppinglist.shoppinglistclient.datamodel.User");
            System.out.println("Klasa została załadowana pomyślnie.");
        } catch (ClassNotFoundException e) {
            System.out.println("Klasa nie została odnaleziona: " + e.getMessage());
        }

        try (Socket socket = new Socket("localhost", 5000)){


            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            // Wysyłanie nazwy użytkownika do serwera
            String username = "Jerzy"; // Przykładowa nazwa użytkownika
            oos.writeObject(username);
            System.out.println("Wysłano username: " + username);

            // Odbieranie obiektu User od serwera
            user = (com.shoppinglist.shoppinglistclient.datamodel.User) ois.readObject();
            if (user != null) {
                System.out.println("Otrzymano dane użytkownika: " + user);
            } else {
                System.out.println("Użytkownik nie istnieje.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return user;
    }
}
