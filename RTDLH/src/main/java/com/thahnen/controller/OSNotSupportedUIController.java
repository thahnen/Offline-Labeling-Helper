package com.thahnen.controller;

import com.thahnen.util.SysUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
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
    @FXML Button btnSaveToFile;
    @FXML Button btnCreateGitHubIssue;
    @FXML TextArea txtSystemInformation;


    public OSNotSupportedUIController() { }


    /**
     *  Runs when the FXML-UI is loaded (FXML-Elements are available)
     *  TODO: maybe get more informations (and more structured)
     *  TODO: see https://www.roseindia.net/java/beginners/OSInformation.shtml
     *  TODO: see https://stackoverflow.com/questions/25552/get-os-level-system-information
     *  TODO: see https://crunchify.com/java-how-to-get-system-properties-information-programmatically/
     */
    @FXML public void initialize() {
        // Add Operating System Information
        txtSystemInformation.appendText("Information about the Operating System:\n");
        txtSystemInformation.appendText("OS-Name: "             + System.getProperty("os.name")             + "\n");
        txtSystemInformation.appendText("OS-Version: "          + System.getProperty("os.version")          + "\n");
        txtSystemInformation.appendText("OS-Architecture: "     + System.getProperty("os.arch")             + "\n");

        // Add JRE Information
        txtSystemInformation.appendText("\nInformation about the JRE:\n");
        txtSystemInformation.appendText("Java-Vendor: "         + System.getProperty("java.vendor")         + "\n");
        txtSystemInformation.appendText("Java-Version: "        + System.getProperty("java.version")        + "\n");

        // Add JVM Information
        txtSystemInformation.appendText("\nInformation about the JVM:\n");
        txtSystemInformation.appendText("Java-VM-Name: "        + System.getProperty("java.vm.name")        + "\n");
        txtSystemInformation.appendText("Java-VM-Vendor: "      + System.getProperty("java.vm.vendor")      + "\n");
        txtSystemInformation.appendText("Java-VM-Version: "     + System.getProperty("java.vm.version")     + "\n");

        // Add Java Settings Information
        txtSystemInformation.appendText("\nInformation about Java Settings:\n");
        //txtSystemInformation.appendText("Java-Compiler: "       + System.getProperty("java.compiler")       + "\n");
        txtSystemInformation.appendText("Java-Class-Version: "  + System.getProperty("java.class.version")  + "\n");
        //txtSystemInformation.appendText("Java-Class-Path: "     + System.getProperty("java.class.path")     + "\n");
        txtSystemInformation.appendText("Java-Library-Path: "   + System.getProperty("java.library.path")   + "\n");
        //txtSystemInformation.appendText("Java-Extension-Path: " + System.getProperty("java.ext.dirs")       + "\n");
        txtSystemInformation.appendText("Java-Tempfile-Path: "  + System.getProperty("java.io.tmpdir")      + "\n");
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
     *  Saves the information (meant for an issue) to a user chosen file
     */
    @FXML protected void saveToFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(SysUtil.getHomeDir()));
        fc.setTitle("Datei zum speichern auswählen!");
        File file = fc.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
        if (file != null) {
            try {
                FileWriter fw = new FileWriter(file);
                String info = txtSystemInformation.getText();
                fw.write(info);
                fw.close();
            } catch (Exception e) {
                // konnte nicht abgespeichert werden
                System.err.println(e);
            }
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
