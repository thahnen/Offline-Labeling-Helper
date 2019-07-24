module RTDLH {
    requires java.desktop;                          // Wird benötigt um den Browser zu öffnen
    requires javafx.fxml;                           // Wird für die UI benötigt
    requires javafx.controls;                       // Wird für die UI-Elemente benötigt
    requires json.simple;                           // Wird zum Laden/ Speichern von Daten benötigt
    requires opencv;                                // Wird zur Verarbeitung der Bilder benötigt

    opens com.thahnen to javafx.fxml;               // Damit Klasse Controller gefunden wird
    opens com.thahnen.controller to javafx.fxml;    // Damit die Klassen OSNotSupportedUIController, ... gefunden werden
    exports com.thahnen;
}