package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.Category;
import com.shoppinglist.shoppinglistclient.datamodel.Product;
import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import com.shoppinglist.shoppinglistclient.datamodel.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ControllerAvailableProducts {

    private Stage stage;
    private Scene scene;
    private boolean editMode = false;

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

    // TODO clean this
    public void setStage(Stage stage) {
        this.stage = stage;

        // Dodaj obsługę zamknięcia okna
        stage.setOnCloseRequest(event -> {
            System.out.println("Zamykanie okna – zwalniam listę");
            ConnectionHandler.setListState(0, "FREE");
        });
    }

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        catCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("category"));
        typeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("type"));
        unitCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("unit"));

        setEditModeVisibility(false);

        ObservableList<Product> products = fetchProducts();
        availableProductsTable.setItems(products);

        availableProductsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            deleteProductBtn.setDisable(newSelection == null);
        });

        // odblokowuje liste po nieoczekiwanym zamknięciu
        // TODO or that
        Platform.runLater(() -> {
            Stage stage = (Stage) availableProductsTable.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                if(editMode) {
                    ConnectionHandler.setListState(0, "FREE");
                }
            });
        });


        goBackBtn.toFront();
        deleteProductBtn.setDisable(true);

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
        if(ConnectionHandler.isListBeingEdited(0)){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Lista jest aktualnie edytowana");
            alert.setContentText("Inny użytkownik właśnie edytuje tę listę. Spróbuj ponownie później.");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image("/icon.png"));
            // TODO: fix
//            alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());
            alert.showAndWait();
        }else{
            ConnectionHandler.setListState(0, "BUSY");
            setEditModeVisibility(true);
            editMode = true;
        }

    }

    @FXML
    private void quitEditModeBtnClick() {
        setEditModeVisibility(false);
        editMode = false;
        ConnectionHandler.setListState(0, "FREE");
    }

    @FXML
    private void saveListBtnClick() {
        ConnectionHandler.sendUserData();
        editMode = false;
        setEditModeVisibility(false);
        ConnectionHandler.setListState(0, "FREE");
    }

    @FXML
    private void deleteProductBtnClick() {
        Product selectedProduct = availableProductsTable.getSelectionModel().getSelectedItem();
        ProductsList list = ProgramData.currentUser.getProductLists().getFirst();
        list.dropProduct(selectedProduct);
        availableProductsTable.getItems().remove(selectedProduct);

    }

    @FXML
    private void addProductBtnClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/newproduct_window.fxml"));
            Parent root = loader.load();
            ControllerNewProductWindow controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Stwórz produkt");
            dialogStage.getIcons().add(new Image("/icon.png"));
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

            Product newProduct = controller.getResultProduct();
            if (newProduct != null) {
                ProductsList list = ProgramData.currentUser.getProductLists().getFirst();
                list.addProduct(newProduct);
                availableProductsTable.getItems().add(newProduct);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
