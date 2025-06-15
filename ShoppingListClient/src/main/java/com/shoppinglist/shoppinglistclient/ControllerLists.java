package com.shoppinglist.shoppinglistclient;

import com.shoppinglist.shoppinglistclient.datamodel.Category;
import com.shoppinglist.shoppinglistclient.datamodel.Product;
import com.shoppinglist.shoppinglistclient.datamodel.ProductsList;
import com.shoppinglist.shoppinglistclient.datamodel.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ControllerLists {

    private final ToggleGroup listTypeGroup = new ToggleGroup();
    private Stage stage;
    private Scene scene;

    @FXML
    private ListView<ProductsList> listList;
    @FXML
    private Button addListBtn, editListBtn, deleteListBtn, goBackBtn, shareListBtn, quitListBtn;

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


        toggleMy.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
            if (toggleMy.isSelected()) {
                event.consume(); // Zatrzymaj zdarzenie — nie pozwól na odkliknięcie
            }
        });

        toggleShared.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, event -> {
            if (toggleShared.isSelected()) {
                event.consume();
            }
        });


        // toggle grupa list prywatnyvh i wspoldzielonych
        listTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null && oldToggle != null) {
                listTypeGroup.selectToggle(oldToggle);
            }
        });

        // table init
        catCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));
        nameCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));
        quantCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));
        unitCol.prefWidthProperty().bind(productListTable.widthProperty().multiply(0.25));

        productListTable.setPlaceholder(new Label("Wybierz listę :)"));


        loggedAsLabel.setText("Zalogowany jako " + ProgramData.currentUser.getName());

        // domyslnie okno otwiera się na prywatnych listach
        myListsClicked(null);
        refreshTable();
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
    private void myListsClicked(ActionEvent e) {
        quitListBtn.disableProperty().unbind();
        editListBtn.disableProperty().unbind();
        shareListBtn.disableProperty().unbind();

        User user = ConnectionHandler.refreshUserData();
        reloadListofPrivateLists(user);

        quitListBtn.setDisable(true);
        deleteListBtn.setDisable(false);
        addListBtn.setDisable(false);

        deleteListBtn.disableProperty().bind(
                listList.getSelectionModel().selectedItemProperty().isNull()
        );
        shareListBtn.disableProperty().bind(
                listList.getSelectionModel().selectedItemProperty().isNull()
        );
        editListBtn.disableProperty().bind(
                listList.getSelectionModel().selectedItemProperty().isNull()
        );


    }

    @FXML
    private void sharedListClicked(ActionEvent e) {
        deleteListBtn.disableProperty().unbind();
        editListBtn.disableProperty().unbind();
        shareListBtn.disableProperty().unbind();

        User user = ConnectionHandler.refreshUserData();
        reloadListofSharedLists(user);

        deleteListBtn.setDisable(true);
        addListBtn.setDisable(true);
        quitListBtn.setDisable(false);


        quitListBtn.disableProperty().bind(
                listList.getSelectionModel().selectedItemProperty().isNull()
        );
        shareListBtn.disableProperty().bind(
                listList.getSelectionModel().selectedItemProperty().isNull()
        );
        editListBtn.disableProperty().bind(
                listList.getSelectionModel().selectedItemProperty().isNull()
        );
    }

    @FXML
    private void refreshTable() {
        ProductsList selectedList = listList.getSelectionModel().getSelectedItem();
        if (selectedList == null) {
            productListTable.getItems().clear();
            return;
        }

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
                    Product product = (Product) getTableRow().getItem();
                    if (product.getType().equalsIgnoreCase("int")) {
                        setText(String.valueOf(product.getQuantity()));
                    } else {
                        setText(String.format("%.1f", product.getAmount()));
                    }
                }
            }
        });

        // Załaduj dane do tabeli

        ArrayList<Product> products = new ArrayList<>();
        for(Category cat : selectedList.getCategories()){
            products.addAll(cat.products);
        }

        productListTable.getItems().setAll(products);
    }

    @FXML
    private void addListClicked(ActionEvent e) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/new_list_window.fxml"));
            Parent root = loader.load();
            ControllerNewListWindow controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Stwórz listę");
            dialogStage.getIcons().add(new Image("/icon.png"));
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

            ProductsList list = controller.getList();
            if(list != null){
                ProgramData.currentUser.getProductListsID().add(list.getId());
                ProgramData.currentUser.getProductLists().add(list);
            }

            listList.getItems().add(list);
            ConnectionHandler.sendUserData(ProgramData.currentUser);

        } catch (IOException event) {
            event.printStackTrace();
        }
    }

    @FXML
    private void deleteListClicked(ActionEvent e) {
        ProductsList list = listList.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText("Czy na pewno chcesz usunąć listę '" + list.getName() + "'?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            for(int i = 0; i < ProgramData.currentUser.getProductListsID().size(); i++){
                if(ProgramData.currentUser.getProductListsID().get(i).equals(list.getId())){
                    ProgramData.currentUser.getProductListsID().remove(i);
                }
            }

            for(int i = 0; i < ProgramData.currentUser.getProductLists().size(); i++){
                if(ProgramData.currentUser.getProductLists().get(i).getId() == list.getId() ){
                    ProgramData.currentUser.getProductLists().remove(i);
                }
            }
            ConnectionHandler.sendUserData(ProgramData.currentUser);
            reloadListofPrivateLists(ProgramData.currentUser);
        }


    }

    @FXML
    private void editListClicked(ActionEvent e) {
        ProductsList chosenList = listList.getSelectionModel().getSelectedItem();

        if(!ConnectionHandler.isListBeingEdited(chosenList.getId())){

            ConnectionHandler.setListState(chosenList.getId(), "BUSY");

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("view/editlist.fxml"));
                Parent root = loader.load();

                ControllerEditList controller = loader.getController();
                controller.setSelectedList(chosenList);

                Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();


            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Lista jest aktualnie edytowana");
            alert.setContentText("Inny użytkownik właśnie edytuje tę listę. Spróbuj ponownie później.");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image("/icon.png"));
            // TODO: fix
//            alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());
            alert.showAndWait();
        }
    }


    @FXML
    private void quitListClicked(ActionEvent e) {
        ProductsList list = listList.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText("Czy na pewno chcesz opuścić listę '" + list.getName() + "'?");
        alert.setContentText("Tej operacji nie można cofnąć.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ConnectionHandler.deleteListFromUser(list.getId(), ProgramData.currentUser.getName());
            reloadListofSharedLists(ProgramData.currentUser);

        }
    }

    @FXML
    private void shareListClicked(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/share_window.fxml"));
            Parent root = loader.load();

            ControllerShareList controller = loader.getController();
            ProductsList list = listList.getSelectionModel().getSelectedItem();
            controller.setSelectedList(list);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Udostępnij listę");
            dialogStage.getIcons().add(new Image("/icon.png"));
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.showAndWait();

            String name = controller.getUsername();
            System.out.println(name);

        } catch (IOException event) {
            event.printStackTrace();
        }
    }

    private void reloadListofPrivateLists(User user){
        listList.getItems().clear();
        for (ProductsList list : user.getProductLists()){

            if(list.getId() == 0) continue;
            if(list.getUsersID().size() > 1) continue;

            listList.getItems().add(list);
        }
        productListTable.getItems().clear();
    }

    private void reloadListofSharedLists(User user){
        listList.getItems().clear();
        for (ProductsList list : user.getProductLists()){

            if(list.getId() == 0) continue;
            if(list.getUsersID().size() < 2) continue;

            listList.getItems().add(list);

        }
        productListTable.getItems().clear();
    }

}
