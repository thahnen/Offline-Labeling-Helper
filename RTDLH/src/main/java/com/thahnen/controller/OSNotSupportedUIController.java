package com.thahnen.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;


/***********************************************************************************************************************
 *
 *      INHALT DER KLASSE OSNotSupportedUIController
 *      ============================================
 *
 *      => der Controller der Fehler-UI (OS nicht unterstützt)
 *
 *      - Alle FXML-Elmente, die es gibt
 *
 *      Handler (Buttons):
 *      - openGitHubIssuePage   =>      öffnet Webbrowser mt der GitHub-Issue-Seite
 *      - closeWindow           =>      schliesst das Fenster
 *
 ***********************************************************************************************************************/


public class OSNotSupportedUIController {
    @FXML Button btnCloseWindow;
    @FXML Button btnCreateGitHubIssue;
    @FXML TextArea txtSystemInformation;


    public OSNotSupportedUIController() {
        // txtSystemInformation ausfüllen
    }


    /**
     *  Browser öffnen, um GitHub-Issue zu verfassen
     */
    @FXML protected void openGitHubIssuePage(ActionEvent event) {
        try {
            Desktop.getDesktop().browse(
                    new URL("https://github.com/thahnen/Offline-Labeling-Helper/issues").toURI()
            );
        } catch (Exception e) {
            // TODO: muss hier eigentlich überhaupt irgendwas gemacht werden?
        }
    }


    /**
     *  Einfach Fenster schliessen
     */
    @FXML protected void closeWindow(ActionEvent event) {
        Stage stage = (Stage) btnCloseWindow.getScene().getWindow();
        stage.close();
    }
}
