package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class Controller {

    @FXML
    private Label welcomeText;

    public void initialize() {
        User user = ConnectionHandler.getUser();
//        welcomeText.setText("Welcome " + user.getName());

    }
}