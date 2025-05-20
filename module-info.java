module  org.example.Lab5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens  org.example.Lab5 to javafx.fxml;
    exports org.example.Lab5;
}