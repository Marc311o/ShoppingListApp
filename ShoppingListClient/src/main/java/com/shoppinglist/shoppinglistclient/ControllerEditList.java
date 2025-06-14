package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
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
import java.util.ArrayList;
import java.util.Objects;

public class ControllerEditList {

    private ProductsList selectedList;
    private Stage stage;
    private Scene scene;

    @FXML
    private Label usersLabel, listNameLabel;
    @FXML
    private Button addProductBtn, editAmountBtn, deleteBtn, saveBtn, goBackBtn;

    // instead of initialize bcs of data transfer
    public void setSelectedList(ProductsList list) {
        this.selectedList = list;
        listNameLabel.setText(selectedList.toString());
        usersLabel.setText("Współdzielona między: " + selectedList.getUsernames());



    }

    @FXML
    private void goBack(ActionEvent e) {
        ((Stage) usersLabel.getScene().getWindow()).close();
    }

    @FXML
    private void addProductClicked() {

    }

    @FXML
    private void editAmountClicked() {

    }

    @FXML
    private void deleteClicked() {

    }

    @FXML
    private void saveClicked() {

    }


}
