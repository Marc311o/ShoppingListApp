module com.shoppinglist.shoppinglistclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.shoppinglist.shoppinglistclient to javafx.fxml;
    exports com.shoppinglist.shoppinglistclient;
}