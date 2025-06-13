package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ControllerLists {

    private final ToggleGroup listTypeGroup = new ToggleGroup();
    private Stage stage;
    private Scene scene;

    @FXML
    private ListView<String> list;
    @FXML
    private Button addListBtn, editListBtn, deleteListBtn, goBackBtn;

    @FXML
    private Label loggedAsLabel;

    @FXML
    private ToggleButton toggleMy, toggleShared;

    @FXML
    TableView<Product> productListTable;
    @FXML
    TableColumn<Product, String> nameCol;
    @FXML
    TableColumn<Product, String>unitCol;
    @FXML
    TableColumn<Product, String>quantCol;
    @FXML
    TableColumn<Product, String>catCol;

    public void initialize() {

        //toggle btn init
        toggleMy.setToggleGroup(listTypeGroup);
        toggleShared.setToggleGroup(listTypeGroup);
        toggleMy.setSelected(true);

        listTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            // If the user tries to deselect the last button (i.e., clicks the selected one)
            if (newToggle == null && oldToggle != null) {
                // Re-select the previously selected toggle
                listTypeGroup.selectToggle(oldToggle);
            }
        });

        // columns init
        catCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));
        nameCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));
        quantCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));
        unitCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));


        loggedAsLabel.setText("Zalogowany jako " + ProgramData.currentUser.getName());
    }

    @FXML
    private void goBackBtnClick(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("view/menu.fxml")));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
