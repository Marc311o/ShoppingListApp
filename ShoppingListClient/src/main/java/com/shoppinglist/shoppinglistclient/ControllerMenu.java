package com.shoppinglist.shoppinglistclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ControllerMenu {

    @FXML
    private Label userWelcomeLabel;
    @FXML
    private Button myListsBtn, availableProductsBtn;

    public void initialize() {
        userWelcomeLabel.setText("Welcome, " + Main.currentUserName);
    }


}
