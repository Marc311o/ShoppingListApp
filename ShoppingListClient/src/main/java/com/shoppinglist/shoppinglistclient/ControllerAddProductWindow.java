package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.Category;
import com.shoppinglist.shoppinglistclient.datamodel.Product;
import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ControllerAddProductWindow {

    private Product resultProduct;
    private ProductsList availableProductsList;

    @FXML private Button AddProductBtn, CancelBtn;
    @FXML private ComboBox<String> categoryBox, productBox;
    @FXML private TextField amountField;




    @FXML
    public void initialize() {

        for (ProductsList list : ProgramData.currentUser.getProductLists()) {
            if (list.getId() == 0) {
                availableProductsList = list;
            }
        }

        ArrayList<String> cats = new ArrayList<>();

        for(Category cat : availableProductsList.getCategories()) {
            cats.add(cat.name);
        }

        AddProductBtn.setDisable(true);
        categoryBox.getItems().addAll(cats);

    }

    @FXML
    private void handleAdd() {

//        Product newProduct = new Product()
    }

    @FXML
    private void handleCancel() {
        resultProduct = null;
        ((Stage) categoryBox.getScene().getWindow()).close();
    }

    @FXML
//    private void handleKeyReleased() {
//        AddProductBtn.setDisable(!isCategorySelected() || amountField.getText().isEmpty() || unitField.getText().isEmpty());
//    }

//    private boolean isCategorySelected() {
////        if ("Inna".equals(categoryBox.getValue())) {
////            return !customCategoryField.getText().trim().isEmpty();
////        } else {
////            return categoryBox.getValue() != null && !categoryBox.getValue().trim().isEmpty();
////        }
//    }


    public Product getResultProduct() {
        return resultProduct;
    }
}
