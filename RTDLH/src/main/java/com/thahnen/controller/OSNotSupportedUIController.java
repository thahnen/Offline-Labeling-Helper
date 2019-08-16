package com.thahnen.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.awt.Desktop;
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
 *      TODO: maybe add option to save information to file?
 *
 ***********************************************************************************************************************/


public class OSNotSupportedUIController {
    @FXML Button btnCloseWindow;
    @FXML Button btnCreateGitHubIssue;
    @FXML TextArea txtSystemInformation;


    public OSNotSupportedUIController() { }


    /**
     *  Runs when the FXML-UI is loaded (FXML-Elements are available)
     *  TODO: maybe get more informations (and more structured)
     *  TODO: see https://www.roseindia.net/java/beginners/OSInformation.shtml
     *  TODO: see https://stackoverflow.com/questions/25552/get-os-level-system-information
     */
    @FXML public void initialize() {
        // Add Operating System Information
        txtSystemInformation.appendText("Information about the Operating System:\n");
        txtSystemInformation.appendText("OS-Name: "         + System.getProperty("os.name")         + "\n");
        txtSystemInformation.appendText("OS-Version: "      + System.getProperty("os.version")      + "\n");
        txtSystemInformation.appendText("OS-Architecture: " + System.getProperty("os.arch")         + "\n");

        // Add JRE Information
        txtSystemInformation.appendText("\nInformation about the JRE:\n");
        txtSystemInformation.appendText("Java-Vendor: "     + System.getProperty("java.vendor")     + "\n");
        txtSystemInformation.appendText("Java-Version: "    + System.getProperty("java.version")    + "\n");

        // Add JVM Information
        txtSystemInformation.appendText("\nInformation about the JVM:\n");
        // ...
    }


    /**
     *  Browser öffnen, um GitHub-Issue zu verfassen
     *  => wenn das nicht unterstützt wird, erscheint ein Popup!
     */
    @FXML protected void openGitHubIssuePage(ActionEvent event) {
        final String url = "https://github.com/thahnen/Offline-Labeling-Helper/issues";

        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().browse(new URL(url).toURI());
                } catch (Exception e) {
                    // TODO: muss hier eigentlich überhaupt irgendwas gemacht werden?
                    e.printStackTrace();
                }
            }).start();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Warning: No browser support");
            alert.setHeaderText("Can not open GitHub Issues Page :(");

            Hyperlink link = new Hyperlink(url);
            FlowPane fp = new FlowPane();
            fp.getChildren().addAll(
                    new Label("Please open page manually (click copies to clipboard):"),
                    link
            );

            link.setOnAction((e) -> {
                final ClipboardContent content = new ClipboardContent();
                content.putString(url);

                Clipboard.getSystemClipboard().setContent(content);
                System.out.println("Copied url to clipboard!");

                alert.close();
            });

            alert.getDialogPane().contentProperty().set(fp);
            alert.showAndWait();
        }
    }


    /**
     *  Einfach Fenster schliessen
     */
    @FXML protected void closeWindow(ActionEvent event) {
        Stage stage = (Stage) btnCloseWindow.getScene().getWindow();
        stage.close();
    }


    /**
     *  Copies the information for an issue to the clipboard!
     */
    @FXML protected void copyInformationToClipboard(MouseEvent event){
        String information = txtSystemInformation.getText();

        final ClipboardContent content = new ClipboardContent();
        content.putString(information);

        Clipboard.getSystemClipboard().setContent(content);
        System.out.println("Copied information to clipboard!");
    }
}
