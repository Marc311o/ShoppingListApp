module com.shoppinglist.shoppinglistclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.shoppinglist.shoppinglistclient to javafx.fxml;
    exports com.shoppinglist.shoppinglistclient;
    exports com.shoppinglist.shoppinglistclient.datamodel;
    opens com.shoppinglist.shoppinglistclient.datamodel to javafx.fxml;
}