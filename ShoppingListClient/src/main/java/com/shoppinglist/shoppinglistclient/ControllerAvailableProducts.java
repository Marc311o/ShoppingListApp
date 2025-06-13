package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.Category;
import com.shoppinglist.shoppinglistclient.datamodel.Product;
import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import com.shoppinglist.shoppinglistclient.datamodel.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ControllerAvailableProducts {

    private Stage stage;
    private Scene scene;

    @FXML
    private Label titleLabel;

    @FXML
    Button goBackBtn, editBtn, addProductBtn, quitEditModeBtn, deleteProductBtn, saveListBtn;

    @FXML
    TableView<Product> availableProductsTable;
    @FXML
    TableColumn<Product, String> nameCol;
    @FXML
    TableColumn<Product, String>unitCol;
    @FXML
    TableColumn<Product, String>typeCol;
    @FXML
    TableColumn<Product, String>catCol;

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        catCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("category"));
        typeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("type"));
        unitCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("unit"));

        setEditModeVisibility(false);

        ObservableList<Product> products = fetchProducts();
        availableProductsTable.setItems(products);

        goBackBtn.toFront();
    }

    private ObservableList<Product> fetchProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        User user = ConnectionHandler.getUser(ProgramData.currentUser.getName());
        ProductsList available = user.getProductLists().getFirst();

        for(Category cat : available.getCategories()) {
            products.addAll(cat.products);
        }

        return products;
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

    @FXML
    private void editBtnClick() {
        setEditModeVisibility(true);
    }

    @FXML
    private void quitEditModeBtnClick() {
        setEditModeVisibility(false);
    }

    private void setEditModeVisibility(boolean mode){
        goBackBtn.setVisible(!mode);
        editBtn.setVisible(!mode);

        String label = mode ? "Dostępne Produkty (Tryb edycji)" : "Dostępne Produkty";
        titleLabel.setText(label);

        quitEditModeBtn.setVisible(mode);
        deleteProductBtn.setVisible(mode);
        saveListBtn.setVisible(mode);
        addProductBtn.setVisible(mode);
    }

    //TODO list editing 

}
