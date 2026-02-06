module io.gitub.hyscript7.drony {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens io.gitub.hyscript7.drony to javafx.fxml;
    exports io.gitub.hyscript7.drony;
}