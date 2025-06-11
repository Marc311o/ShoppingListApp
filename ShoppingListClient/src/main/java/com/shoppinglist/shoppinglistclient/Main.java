package com.shoppinglist.shoppinglistclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static String currentUserName;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/login.fxml")));

        Scene scene = new Scene(root);
        Image icon = new Image("/icon.png");
        stage.getIcons().add(icon);

        stage.setTitle("Shopping List App");
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}