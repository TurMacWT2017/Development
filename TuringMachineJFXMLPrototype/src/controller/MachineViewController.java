/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Interpreter;

/**
 *
 * @author Nick Ahring
 */
public class MachineViewController implements Initializable {
    
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
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem menuQuitButton;
    @FXML private MenuItem recentFilesMenu;
    //Code Window 
    //@FXML private TextArea codeDisplay;
    
    //Machine Controller
    private MachineController controller = new MachineController();
    //Interpreter instance (new interpreter is created on load of a program)
    private Interpreter interp;
    private ArrayList<File> recentFiles;
    
    
    //private Tape tm = new charTape();
    
    @FXML
    private void runButtonClicked(ActionEvent event) {
        if (runButton.getText().equals("Run")) {
            runButton.setText("Pause");
            interp.run();
        }
        else {
            runButton.setText("Run");
            interp.pause();
        }
    }
    
    @FXML
    private void stepButtonClicked(ActionEvent event) {
        interp.step();
    }
    
    @FXML
    private void stopButtonClicked(ActionEvent event) {
        interp.stop();
    }
    
    @FXML
    private void resetButtonClicked(ActionEvent event) {
        interp.reset();
 //       tm.resetRWHead();
    }
    
    @FXML
    private void menuQuitButtonClicked(ActionEvent event) {
        Platform.exit();
    }
    
    private void clearButtonClicked(ActionEvent event) {
        System.out.println("Clear Tape 1");
 //       tm.clearTape();
    }
    
    @FXML
    private void openFileMenuItemClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Machine File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Machine Files", "*.tm"));
        File selectedFile = fileChooser.showOpenDialog(tapeOne.getScene().getWindow());
        if (selectedFile != null) {
            String input = controller.openFile(selectedFile);
            interp = new Interpreter(input);
            tapeOne.setText(interp.getInitialInput());
//            recentFiles.add(selectedFile);
            //launch window to show code or error
            if (interp.errorFound()) {
                try {
                    launchCodeWindow(interp.getErrorReport());
                } catch (IOException ex) {
                    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            else
                try {
                    launchCodeWindow(input);
                } catch (IOException ex) {
                    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
        }       
    }
    
    @FXML
    private void tapeOneClearButtonClicked(ActionEvent event) {
        tapeOne.setText("");
//        tm.clearTape();
    }
    
    private void launchCodeWindow(String content) throws IOException {
        //Parent root;
        Stage stage;
        System.out.println("Making code window");
        stage = new Stage();
        //root = FXMLLoader.load(getClass().getResource("/view/codeView.fxml"));
        ScrollPane layout = new ScrollPane();
        TextArea codeDisplay = new TextArea();
        codeDisplay.setPrefHeight(450);
        codeDisplay.setPrefWidth(450);
        layout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        layout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        layout.setContent(codeDisplay);        
        codeDisplay.setText(content);
        stage.setScene(new Scene(layout, 450, 450));
        stage.setTitle("Code Window");
        stage.initOwner(tapeOne.getScene().getWindow());
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 4); 
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 8); 
        stage.show();
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
