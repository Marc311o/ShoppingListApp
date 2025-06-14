package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.Product;
import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerNewListWindow {

    @FXML private TextField nameField;
    @FXML private Button createBtn;

    private ProductsList newList = null;
    private String newListName;

    @FXML
    public void initialize() {
        createBtn.setDisable(true);

        nameField.textProperty().addListener((obs, oldText, newText) -> {
            createBtn.setDisable(newText.trim().isEmpty());
        });
    }

    @FXML
    private void handleCreate() {

        newListName = nameField.getText().trim();
        int id = ConnectionHandler.getUnusedId();

        if(id != -1){
            newList = new ProductsList();
            newList.setName(newListName);
            newList.setId(id);
            newList.getUsersID().add(ProgramData.currentUser.getId());
            newList.getUsernames().add(ProgramData.currentUser.getName());
        }

        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }

    public ProductsList getList() {
        return newList;
    }
}

