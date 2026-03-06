module io.gitub.hyscript7.drony {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens io.gitub.hyscript7.drony to javafx.fxml;
    exports io.gitub.hyscript7.drony;
    opens io.gitub.hyscript7.drony.domain to javafx.fxml;
    exports io.gitub.hyscript7.drony.domain;
    opens io.gitub.hyscript7.drony.factory to javafx.fxml;
    exports io.gitub.hyscript7.drony.factory;
    opens io.gitub.hyscript7.drony.workers to javafx.fxml;
    exports io.gitub.hyscript7.drony.workers;
    opens io.gitub.hyscript7.drony.crosscutting to javafx.fxml;
    exports io.gitub.hyscript7.drony.crosscutting;
}