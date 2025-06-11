package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ControllerLogin {

    private Stage stage;
    private Scene scene;

    @FXML
    private Button loginBtn;
    @FXML
    private TextField loginUsernameForm;
    @FXML
    private Label loginErrorMessage;

    @FXML
    public void initialize() {
        loginErrorMessage.setText("");
    }

    public void clickLoginBtn(ActionEvent e) {

        String username = loginUsernameForm.getText();
        User user = ConnectionHandler.getUser(username);
        if (user != null) {
            loginErrorMessage.setText("");
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/menu.fxml")));
                Main.currentUserName = username;
                stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            loginErrorMessage.setText("Nieprawidłowa nazwa użytkownika");
        }

    }

}