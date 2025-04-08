module org.example.lr2programrs {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.transaction;


    opens org.example.lr2programrs to javafx.fxml;
    exports org.example.lr2programrs;
}