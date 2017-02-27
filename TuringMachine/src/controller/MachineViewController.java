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
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Interpreter;
import model.InterpreterException;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

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
    @FXML private TextField currentState;
    @FXML private TextField currentSteps;
    @FXML private TextField tapeOne;
    
    @FXML private Canvas canvas;
    private static int XCOORD = 10;
    private static int YCOORD = 10;
    private static final double RADIUS = 30.0;
    
    @FXML private Slider speedSlider;
    @FXML private Label changeLabel;
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem menuQuitButton;
    @FXML private MenuItem recentFilesMenu;
    @FXML private AnchorPane diagramDisplay;
    //Code Window 
    //@FXML private TextArea codeDisplay;
    
    //Machine Controller
    private final MachineController controller = new MachineController();
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
    
    @FXML
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
            interp.setViewController(this);
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
                    tapeOne.setText(interp.getInitialInput());
                try {
                    interp.start();
                } catch (InterpreterException ex) {
                    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        
        layout.setFitToHeight(true);
        layout.setFitToWidth(true);
        
        layout.setContent(codeDisplay);        
        codeDisplay.setText(content);
        codeDisplay.setEditable(false);
        
        stage.setScene(new Scene(layout, 450, 450));
        stage.setTitle("Code Window");
        stage.initOwner(tapeOne.getScene().getWindow());
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 4); 
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 8);
        stage.show();
    }
    
    @FXML
    public void setStartState() {
        System.out.println("Machine started..."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @FXML
    public void setStepState() {
        System.out.println("Machine in step state");
    }

    @FXML
    public void setStoppedState() {
        System.out.println("Machine stopped");
        runButton.setText("Run");
    }

    @FXML
    public void updateStepCount(int stepCount) {
        currentSteps.setText(Integer.toString(stepCount));
    }
    
    @FXML
    public void updateState(String state) {
        currentState.setText(state);
    }
    
    @FXML
    public String getTapeInput() {
        return tapeOne.getText();
    }
        
    public void setTapeContent(String content) {
        tapeOne.setText(content);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        speedSlider.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) {
                    changeLabel.textProperty().setValue(String.valueOf((int)speedSlider.getValue()));

                    //System.out.println("Speed slider = " + getSpeed());  //output speed changes
            }
        });
        
        diagramDisplay.prefWidthProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setWidth(newValue.doubleValue());
            System.out.println("diagram display resized");
        });

        diagramDisplay.prefHeightProperty().addListener((ov, oldValue, newValue) -> {
            canvas.setHeight(newValue.doubleValue());
            System.out.println("diagram display resized");
        });
        
        drawState();
    }    

    public void updateHighlight() {
        //update the highlight
    }
    
    public int getSpeed(){
        return (int)speedSlider.getValue();
    }
    
    public void drawState() {
        // Draw circles representing State Diagrams
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                // fillOval is a filled in circle, strokeOval is an outline
                gc.fillOval(XCOORD, YCOORD, RADIUS, RADIUS);
                gc.strokeOval(XCOORD, YCOORD, RADIUS, RADIUS);
                
                // connect the "states" with a line from center to center
                gc.strokeLine(XCOORD+15, YCOORD+15, XCOORD+115, YCOORD+15);

                if (XCOORD + 100 < canvas.getWidth())
                {
                    XCOORD += 100;
                }
                else
                {
                    XCOORD = 10;
                    YCOORD += 100;
                }
            }
        });
    }
}
