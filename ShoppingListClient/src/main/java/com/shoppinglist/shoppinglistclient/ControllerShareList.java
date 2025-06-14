package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import com.shoppinglist.shoppinglistclient.datamodel.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ControllerShareList {
    @FXML private Button shareBtn;
    @FXML private ChoiceBox<String> nameBox;

    private ProductsList selectedList;
    private String username = null;

    public void setSelectedList(ProductsList list) {
        this.selectedList = list;
        ArrayList<String> usernames = ConnectionHandler.getUsernames();
        usernames.removeIf(name -> selectedList.getUsernames().contains(name));
        nameBox.getItems().addAll(usernames);
    }

    public String getUsername(){
        return username;
    }

    @FXML
    public void handleShare() {
        username = nameBox.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Udostępniono");
        alert.setHeaderText(null);
        alert.setContentText("Lista została udostępniona użytkownikowi: " + username);
        alert.showAndWait();

        handleCancel();
        ConnectionHandler.shareListToUser(selectedList.getId(), username);

    }

    @FXML
    private void handleCancel() {
        ((Stage) nameBox.getScene().getWindow()).close();
    }


}
