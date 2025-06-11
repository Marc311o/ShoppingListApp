module com.shoppinglist.shoppinglistclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.shoppinglist.shoppinglistclient to javafx.fxml;
    exports com.shoppinglist.shoppinglistclient;
//    exports com.shoppinglist.shoppinglistclient.datamodel;
    opens com.shoppinglist.shoppinglistclient.datamodel to javafx.fxml;
    exports com.shoppinglist.shoppinglistclient.datamodel;
}