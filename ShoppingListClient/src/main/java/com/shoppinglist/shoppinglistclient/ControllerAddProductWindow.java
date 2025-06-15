package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.Category;
import com.shoppinglist.shoppinglistclient.datamodel.Product;
import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ControllerAddProductWindow {

    private Product resultProduct;
    private ProductsList availableProductsList;
    private List<Category> categories;

    @FXML private Button AddProductBtn, CancelBtn;
    @FXML private ComboBox<String> categoryBox, productBox;
    @FXML private TextField amountField;
    @FXML private Label unitLabel;

    @FXML
    public void initialize() {
        availableProductsList = ProgramData.admin.getProductLists().getFirst();
        categories = availableProductsList.getCategories();
        unitLabel.setText("");

        for (Category cat : categories) {
            categoryBox.getItems().add(cat.name);
        }

        AddProductBtn.setDisable(true);
        productBox.setDisable(true);

        categoryBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            productBox.getItems().clear();
            productBox.setDisable(true);
            AddProductBtn.setDisable(true);

            if (newVal != null) {
                categories.stream()
                        .filter(c -> c.name.equals(newVal))
                        .findFirst()
                        .ifPresent(cat -> {
                            for (Product p : cat.getProducts()) {
                                productBox.getItems().add(p.name);
                            }
                            productBox.setDisable(false);
                        });
            }


        });
        productBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateUnitLabel(newVal);
            updateAddButtonState();
        });

        amountField.textProperty().addListener((obs, oldVal, newVal) -> updateAddButtonState());
    }

    private void updateAddButtonState() {
        boolean ok = categoryBox.getValue() != null
                && productBox.getValue() != null
                && !amountField.getText().trim().isEmpty();
        AddProductBtn.setDisable(!ok);
    }

    @FXML
    private void updateUnitLabel(String prodName) {
        if (prodName == null || categoryBox.getValue() == null) {
            unitLabel.setText("");
            return;
        }
        Product p = availableProductsList.znajdzPozycje(prodName, categoryBox.getValue());
        unitLabel.setText(p != null ? p.getUnit() : "");
    }

    @FXML
    private void handleAdd() {
        String catName = categoryBox.getValue();
        String prodName = productBox.getValue();
        String input = amountField.getText().trim();

//        System.out.println( catName + " " + prodName + " " + input);

        Product target = availableProductsList.znajdzPozycje(prodName, catName);
        resultProduct = new Product(prodName, catName, target.getUnit(), target.getType(), 0);


        try {
            if ("int".equalsIgnoreCase(target.getType())) {
                int qty = Integer.parseInt(input);
                resultProduct.setQuantity(qty);
            } else {
                double amt = Double.parseDouble(input);
                resultProduct.setAmount(amt);
            }

            ((Stage) categoryBox.getScene().getWindow()).close();

        } catch (NumberFormatException ex) {
            String expected = "int".equalsIgnoreCase(target.getType())
                    ? "liczbę całkowitą (np. 3)"
                    : "liczbę zmiennoprzecinkową (np. 1.5)";
            showError("Niepoprawny format", "Wartość musi być " + expected + ".");
        }


    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        resultProduct = null;
        ((Stage) categoryBox.getScene().getWindow()).close();
    }



    public Product getResultProduct() {
        return resultProduct;
    }
}
