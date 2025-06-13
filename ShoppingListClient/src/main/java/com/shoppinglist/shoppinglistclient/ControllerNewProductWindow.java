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

        categoryBox.getItems().addAll(cats);
        intRadio.setSelected(true);

    }

    @FXML
    private void handleAdd() {
        // TODO: (maybe) disable add button when empty fields

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

    public Product getResultProduct() {
        return resultProduct;
    }
}
