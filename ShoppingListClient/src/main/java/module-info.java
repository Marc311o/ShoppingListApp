module com.shoppinglist.shoppinglistclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.shoppinglist.shoppinglistclient to javafx.fxml;
    exports com.shoppinglist.shoppinglistclient;
}