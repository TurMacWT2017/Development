/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author Nick Ahring
 */
public class FXMLDocumentController implements Initializable {
    
    //UI Buttons
    @FXML private Button runButton;
    @FXML private Button stepButton;
    @FXML private Button stopButton;
    @FXML private Button resetButton;
    @FXML private Button tapeOneClearButton;
    //Menu Buttons
    //Displays
    @FXML private ScrollPane diagramDisplay;
    @FXML private TextField currentState;
    @FXML private TextField currentSteps;
    @FXML private TextField tapeOne;
    
    @FXML private Slider speedSlider;
    @FXML private Label changeLabel;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem menuQuitButton;
    
    private Tape tm = new charTape();
    
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
        tm.resetRWHead();
    }
    
    @FXML
    private void menuQuitButtonClicked(ActionEvent event) {
        Platform.exit();
    }
    
    private void clearButtonClicked(ActionEvent event) {
        System.out.println("Clear Tape 1");
        tm.clearTape();
    }
    
    @FXML
    private void openFileMenuItemClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Machine File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                new ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
           System.out.println("A File was Chosen");
        }   
    }
    
    @FXML
    private void tapeOneClearButtonClicked(ActionEvent event) {
        tapeOne.setText("");
        tm.clearTape();
    }

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        speedSlider.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                    changeLabel.textProperty().setValue(String.valueOf((int)speedSlider.getValue()));
            }
        });
    }    
    
}
