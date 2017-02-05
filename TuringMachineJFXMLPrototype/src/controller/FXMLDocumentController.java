/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

/**
 *
 * @author Nick Ahring
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    //UI Buttons
    private Button runButton;
    private Button stepButton;
    private Button stopButton;
    private Button resetButton;
    private Button clearButton;
    //Menu Buttons
    private Button quitMenuButton;
    
    @FXML
    private void runButtonClicked(ActionEvent event) {
        if (runButton.getText().equals("Run")) {
            runButton.setText("Pause");
        }
        else {
            runButton.setText("Run");
        }
    }
    
    @FXML
    private void stepButtonClicked(ActionEvent event) {
        System.out.println("Step");
    }
    
    @FXML
    private void stopButtonClicked(ActionEvent event) {
        System.out.println("Machine stopped");
    }
    
    @FXML
    private void resetButtonClicked(ActionEvent event) {
        System.out.println("Machine Reset");
    }
    
    @FXML
    private void menuQuitButtonClicked(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    private void clearButtonClicked(ActionEvent event) {
        System.out.println("Clear Tape 1");
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
