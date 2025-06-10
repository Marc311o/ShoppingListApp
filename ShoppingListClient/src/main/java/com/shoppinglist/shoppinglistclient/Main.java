package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("Hello!");
//        stage.setScene(scene);
//        stage.show();

//        try (Socket socket = new Socket("localhost", 5000);
//             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
//
//            // Wysyłanie nazwy użytkownika do serwera
//            String username = "Jerzy"; // Przykładowa nazwa użytkownika
//            oos.writeObject(username);
//            System.out.println("Wysłano username: " + username);
//
//            // Odbieranie obiektu User od serwera
//            User user = (User) ois.readObject();
//            if (user != null) {
//                System.out.println("Otrzymano dane użytkownika: " + user);
//            } else {
//                System.out.println("Użytkownik nie istnieje.");
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public static void main(String[] args) {
        launch();
    }
}