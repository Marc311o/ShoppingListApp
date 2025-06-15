package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.Category;
import com.shoppinglist.shoppinglistclient.datamodel.Product;
import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import javafx.application.Platform;
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
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ControllerEditList {

    private ProductsList selectedList;
    private Stage stage;
    private Scene scene;

    @FXML
    private Label usersLabel, listNameLabel;
    @FXML
    private Button addProductBtn, editAmountBtn, deleteBtn, saveBtn, goBackBtn;

    @FXML
    TableView<Product> productListTable;

    @FXML
    TableColumn<Product, String> nameCol, quantCol, unitCol, catCol;


    // instead of initialize bcs of data transfer
    public void setSelectedList(ProductsList list) {
        this.selectedList = list;
        initPage();



    }

    @FXML
    private void goBack(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/lists.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();


        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }finally {
            ConnectionHandler.setListState(selectedList.getId(), "FREE");
        }
    }

    @FXML
    private void addProductClicked() {

        boolean availableListExists = false;

        for (ProductsList list : ProgramData.currentUser.getProductLists()) {
            if (list.getId() == 0) {
                availableListExists = true;
            }
        }
        if(availableListExists) {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("view/addproduct_window.fxml"));
                Parent root = loader.load();
                ControllerAddProductWindow controller = loader.getController();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Dodaj produkt");
                dialogStage.getIcons().add(new Image("/icon.png"));
                dialogStage.setScene(new Scene(root));
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.showAndWait();

                Product newProduct = controller.getResultProduct();
                if (newProduct != null) {
                    selectedList.addProduct(newProduct);
                    refreshUserData();
                    refreshTable();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }




    }



    @FXML
    private void editAmountClicked() {
        Product selectedProduct = productListTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edytuj ilość");
        dialog.setHeaderText("Produkt: " + selectedProduct.getName());
        dialog.setContentText("Nowa ilość (" + selectedProduct.getUnit() + "):");

        String initial = selectedProduct.getType().equalsIgnoreCase("int")
                ? String.valueOf(selectedProduct.getQuantity())
                : String.format("%.1f", selectedProduct.getAmount());
        dialog.getEditor().setText(initial);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                if (selectedProduct.getType().equalsIgnoreCase("int")) {
                    int newQuant = Integer.parseInt(input);
                    selectedProduct.setQuantity(newQuant);
                } else {
                    double newAmount = Double.parseDouble(input);
                    selectedProduct.setAmount(newAmount);
                }
                refreshTable();
                refreshUserData();

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText("Niepoprawna wartość");
                alert.setContentText("Wprowadź liczbę " + (selectedProduct.getType().equalsIgnoreCase("int") ?
                        "całkowitą" :
                        "zmiennoprzecinkową (format x.x, np 1.5)") + ".");
                alert.showAndWait();
            }
        });
    }

    @FXML
    private void deleteClicked() {
        Product selectedProduct = productListTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            selectedList.dropProduct(selectedProduct);
        }
        refreshUserData();
        refreshTable();
    }

    @FXML
    private void saveClicked(ActionEvent e) {
        ConnectionHandler.sendUserData(ProgramData.currentUser);
        goBack(e);
    }

    private void initPage() {

        // labels
        listNameLabel.setText(selectedList.toString());
        usersLabel.setText("Współdzielona między: " + selectedList.getUsernames());

        // table
        catCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));
        nameCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));
        quantCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));
        unitCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));

        nameCol.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getName()));
        catCol.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getCategory()));
        unitCol.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getUnit()));

        quantCol.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(""));
        quantCol.setCellFactory(column -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Product product = getTableRow().getItem();
                    if (product.getType().equalsIgnoreCase("int")) {
                        setText(String.valueOf(product.getQuantity()));
                    } else {
                        setText(String.format("%.1f", product.getAmount()));
                    }
                }
            }
        });

        refreshTable();

        // ensure that list unlocks when window closed unexpectedly
        Platform.runLater(() -> {
            Stage stage = (Stage) productListTable.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                ConnectionHandler.setListState(selectedList.getId(), "FREE");
            });
        });

        // binding buttons that require selected products to work
        deleteBtn.disableProperty().bind(
                productListTable.getSelectionModel().selectedItemProperty().isNull()
        );

        editAmountBtn.disableProperty().bind(
                productListTable.getSelectionModel().selectedItemProperty().isNull()
        );

    }

    private void refreshTable() {
        productListTable.getItems().clear();
        ArrayList<Product> products = new ArrayList<>();
        for(Category cat : selectedList.getCategories()){
            products.addAll(cat.products);
        }
        productListTable.getItems().setAll(products);
    }

    private void refreshUserData(){
        ProgramData.currentUser.getProductLists().removeIf(
                l -> l.getId() == selectedList.getId()
        );
        ProgramData.currentUser.getProductLists().add(selectedList);
    }

}
