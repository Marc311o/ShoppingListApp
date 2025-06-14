package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.Category;
import com.shoppinglist.shoppinglistclient.datamodel.Product;
import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.stage.Stage;

import java.util.ArrayList;

public class ControllerNewProductWindow {

    private final ToggleGroup typeGroup = new ToggleGroup();

    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryBox;
    @FXML private TextField customCategoryField;
    @FXML private TextField unitField;
    @FXML private RadioButton intRadio, doubleRadio;
    @FXML private Button CancelBtn, AddProductBtn;


    private Product resultProduct;

    @FXML
    public void initialize() {

        intRadio.setToggleGroup(typeGroup);
        doubleRadio.setToggleGroup(typeGroup);

        ProductsList list = ProgramData.currentUser.productLists.getFirst();
        ArrayList<String> cats = new ArrayList<>();
        for(Category cat : list.getCategories()) {
            cats.add(cat.name);
        }
        cats.add("Inna");

        AddProductBtn.setDisable(true);
        categoryBox.getItems().addAll(cats);
        intRadio.setSelected(true);

        categoryBox.setOnAction(event -> {
            boolean isOther = "Inna".equals(categoryBox.getValue());
            customCategoryField.setVisible(isOther);

            if (!isOther) {
                customCategoryField.clear();
            }

            handleKeyReleased(); // Sprawdź, czy można włączyć przycisk
        });

        customCategoryField.setVisible(false);

        // Opcjonalnie: reaguj na wpisywanie w customCategoryField też
        customCategoryField.setOnKeyReleased(event -> handleKeyReleased());
        nameField.setOnKeyReleased(event -> handleKeyReleased());
        unitField.setOnKeyReleased(event -> handleKeyReleased());

    }

    @FXML
    private void handleAdd() {

        try {
            String name = nameField.getText().trim();
            String category = customCategoryField.getText().isEmpty()
                    ? categoryBox.getValue()
                    : customCategoryField.getText().trim();
            String unit = unitField.getText().trim();
            String type = intRadio.isSelected() ? "int" : "double";

            resultProduct = new Product(name, category, unit, type, 0);
            ((Stage) nameField.getScene().getWindow()).close();

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Upewnij się, że wszystkie dane są poprawne.").showAndWait();
        }
    }

    @FXML
    private void handleCancel() {
        resultProduct = null;
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    private void handleKeyReleased() {
        AddProductBtn.setDisable(!isCategorySelected() || nameField.getText().isEmpty() || unitField.getText().isEmpty());
    }

    private boolean isCategorySelected() {
        if ("Inna".equals(categoryBox.getValue())) {
            return !customCategoryField.getText().trim().isEmpty();
        } else {
            return categoryBox.getValue() != null && !categoryBox.getValue().trim().isEmpty();
        }
    }


    public Product getResultProduct() {
        return resultProduct;
    }
}
