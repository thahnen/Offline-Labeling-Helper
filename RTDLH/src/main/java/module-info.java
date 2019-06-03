module RTDLH {
    requires javafx.fxml;
    requires javafx.controls;
    requires json.simple;
    requires opencv;

    opens com.thahnen to javafx.fxml;
    exports com.thahnen;
}