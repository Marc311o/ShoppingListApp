package com.shoppinglist.shoppinglistclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ControllerMenu {

    private Stage stage;
    private Scene scene;

    @FXML
    private Label userWelcomeLabel;
    @FXML
    private Button myListsBtn, availableProductsBtn;

    public void initialize() {
        userWelcomeLabel.setText("Welcome, " + ProgramData.currentUser.getName());
    }

    public void myListsBtnClicked(ActionEvent e) {

        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/menu.fxml")));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    } else {
//        loginErrorMessage.setText("Nieprawidłowa nazwa użytkownika");
    
    }


}
