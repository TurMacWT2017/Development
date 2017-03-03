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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import model.StateTransition;

/**
 *
 * @author Nick Ahring
 */
public class MachineViewController implements Initializable {
    
    //used for text formatting in tape, changing text here will change tape text for the program
    private String family = "Helvetica";
    private int size = 16;
    
    //UI Buttons
    @FXML private Button runButton;
    @FXML private Button stepButton;
    @FXML private Button stopButton;
    @FXML private Button resetButton;
    @FXML private Button tapeOneClearButton;
    //Displays
    @FXML private TextField currentState;
    @FXML private TextField currentSteps;
    @FXML private TextFlow tapeOne;
    //Canvas
    @FXML private Canvas canvas;
    private static int XCOORD = 10;
    private static int YCOORD = 10;
    private static final double RADIUS = 30.0;
    //Slider
    @FXML private Slider speedSlider;
    @FXML private Label changeLabel;
    //Menu items
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem menuQuitButton;
    @FXML private MenuItem recentFilesMenu;
    @FXML private MenuItem showCodeWindow;
    @FXML private AnchorPane diagramDisplay;
    @FXML private TextFlow codeViewTab;
    //Code Window 
    //@FXML private TextArea codeDisplay;
    
    //Machine Controller
    private final MachineController controller = new MachineController();
    //Interpreter instance (new interpreter is created on load of a program)
    private Interpreter interp;
    private ArrayList<File> recentFiles;
    //list of states, used in drawing
    ArrayList<StateTransition> currentStates;
    //keeps track of file status
    boolean fileLoaded = false;
    
    
    //private Tape tm = new charTape();
    
    @FXML
    private void runButtonClicked(ActionEvent event) {
        //Check and make sure the user has actually loaded a program
        //Also inform them of errors in the program if they need to correct them and reload
        //else, process their request
        if (fileLoaded) {
            if (interp.errorFound()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Your program contained errors");
                alert.setContentText("Please correct them or load a different program");
                alert.showAndWait();
            }
                else {
                if (runButton.getText().equals("Run")) {
                    runButton.setText("Pause");
                    interp.run();
                }
                else {
                    runButton.setText("Run");
                    interp.pause();
                }
            }
        }
        else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("You have not loaded a program");
            alert.setContentText("Please load the program you want to run first");
            alert.showAndWait();
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
        tapeOne.getChildren().clear();
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
        //File initialDirectory = new File("../TestFiles");
        fileChooser.setTitle("Open Machine File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Machine Files", "*.tm"));
        //fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(tapeOne.getScene().getWindow());
        if (selectedFile != null) {
            //note that a file was loaded
            fileLoaded = true;
            tapeOne.getChildren().clear();
            codeViewTab.getChildren().clear();
            String input = controller.openFile(selectedFile);
            //when initializing interpreter, give it both an input and a view controller (this) to work with
            interp = new Interpreter(input, this);
//            recentFiles.add(selectedFile);
            //Show code or error report
            if (interp.errorFound()) {
                updateCodeTabContent(interp.getErrorReport());
            }
            else {
                //try {
                //    launchCodeWindow(input);
                    Text text1 = new Text(input);
                    text1.setFont(Font.font("Courier New", 14));
                    codeViewTab.getChildren().add(text1);
                    //tapeOne.setText(interp.getInitialInput());
                try {
                    interp.start();
                } catch (InterpreterException ex) {
                    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                //} catch (IOException ex) {
                //    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
        }       
    }
    
    @FXML
    private void tapeOneClearButtonClicked(ActionEvent event) {
        if (fileLoaded) {
            tapeOne.getChildren().clear();
            interp.popup();
            interp.reset();
        }
    }
    
    @FXML
    private void launchCodeWindow(ActionEvent event) {
        //Parent root;
        if (fileLoaded) {
            Stage stage;
            System.out.println("Making code window");
            stage = new Stage();
            ScrollPane layout = new ScrollPane();
            TextFlow codeDisplay = new TextFlow();
            codeDisplay.setPrefHeight(450);
            codeDisplay.setPrefWidth(450);
            layout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            layout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);        
            layout.setFitToHeight(true);
            layout.setFitToWidth(true);        
            layout.setContent(codeDisplay);
            //build the content
            Text content = new Text(interp.getMachineCode());
            //style the content and add it
            content.setFont((Font.font(family, size)));
            codeDisplay.getChildren().add(content);
            //set the scene and its owner
            stage.setScene(new Scene(layout, 450, 450));
            stage.setTitle("Code Window");
            stage.initOwner(tapeOne.getScene().getWindow());
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 4); 
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 8);
            stage.show();
        }
        else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please load a program first");
            alert.showAndWait();
        }
    }
    
    @FXML
    public void setStartState() {
        runButton.setDisable(false);
        stopButton.setDisable(false);
        stepButton.setDisable(false);
    }
    
    @FXML
    public void setStepState() {
        System.out.println("Machine in step state");
    }

    @FXML
    public void setStoppedState() {
        System.out.println("Machine stopped");
        Platform.runLater(() -> {
            runButton.setText("Run");
        });
        runButton.setDisable(true);
        stopButton.setDisable(true);
        stepButton.setDisable(true);
        
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
        return tapeOne.getChildren().toString();
    }
    @FXML
    public void setInitialTapeContent(String content) {
        Text input = new Text(content);
        input.setFont((Font.font(family, size)));
        tapeOne.getChildren().add(input);
    }
    
    @FXML
    public void updateTapeContent(String content) {
       
        int headLocation = interp.getRWHead();
        System.out.println(headLocation);

        //compensates for possibility of head being at left or right
        boolean headAtRight = false;
        boolean headAtLeft = false;
        
        //check if head is at left or right
        if (content.substring(0, headLocation) == null) {
            headAtLeft = true;            
        }
        else if (content.substring(headLocation + 1) == null) {
            headAtRight = true;            
        }
        
        //get the content under head
        Text tapeContentHead = new Text(Character.toString(content.charAt(headLocation)));
        //style it
        tapeContentHead.setFill(Color.RED);
        tapeContentHead.setFont((Font.font(family, FontWeight.BOLD, size)));
        
        //build depending on where head was at
        if (headAtLeft) {
            Text tapeContentRight = new Text(content.substring(headLocation + 1));
            tapeContentRight.setFont((Font.font(family, size)));
            Platform.runLater(() -> {
                  tapeOne.getChildren().clear();
                  tapeOne.getChildren().addAll(tapeContentHead, tapeContentRight);
            });            
        }
        else if (headAtRight) {
            Text tapeContentLeft = new Text(content.substring(0, headLocation));
            tapeContentLeft.setFont((Font.font(family, size)));
            Platform.runLater(() -> {
                  tapeOne.getChildren().clear();
                  tapeOne.getChildren().addAll(tapeContentLeft, tapeContentHead);
            });
        }
        else {
            Text tapeContentLeft = new Text(content.substring(0, headLocation));
            tapeContentLeft.setFont((Font.font(family, size)));
            Text tapeContentRight = new Text(content.substring(headLocation + 1));
            tapeContentRight.setFont((Font.font(family, size)));
            Platform.runLater(() -> {
                  tapeOne.getChildren().clear();
                  tapeOne.getChildren().addAll(tapeContentLeft, tapeContentHead, tapeContentRight);
            });
        }
            
    }
    
    /**
     * Allows this controller to update the content of its code tab
     * if necessary
     * @param content the content to put in the tab
     */
    private void updateCodeTabContent(String content) {
        Text newContent = new Text(content);
        newContent.setFont((Font.font(family, size)));        
        codeViewTab.getChildren().add(newContent);
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

        //these lines allow the canvas to dynamically resize when the program does
        canvas.widthProperty().addListener(observable -> redraw());
        canvas.heightProperty().addListener(observable -> redraw());
        
        canvas.widthProperty().bind(
                       diagramDisplay.widthProperty());
        canvas.heightProperty().bind(
                       diagramDisplay.heightProperty());
        
    }    

    public void updateHighlight() {
        //update the highlight
    }
    
    public int getSpeed(){
        return (int)speedSlider.getValue();
    }
    
    public void drawStates(ArrayList<StateTransition> states) {
        // Draw circles representing State Diagrams
        // keep the states in case we have to redraw because of a resize
        currentStates = states;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        // Reset the canvas from any previous drawings
        XCOORD = 10;
        YCOORD = 10;
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        //get the number of states
        int nodeCount = states.size();
        
        //draw all the states
        for (int i = 0; i< nodeCount; i++) {
                String stateName = states.get(i).getInitialState();
                String rToken = states.get(i).getReadToken();
                String wToken = states.get(i).getWriteToken();
                String direction = states.get(i).getDirection();
                gc.setFill(Color.WHITE);
                // fillOval is a filled in circle, strokeOval is an outline
                gc.fillOval(XCOORD, YCOORD, RADIUS, RADIUS);
                gc.strokeOval(XCOORD, YCOORD, RADIUS, RADIUS);
                
                // connect the "states" with a line from center to center
                // unless it's the last state, then it won't need a line.
                if (i < nodeCount - 1) 
                    gc.strokeLine(XCOORD+30, YCOORD+15, XCOORD+130, YCOORD+15);
                
                //draw label
                //change font color to black
                gc.setFill(Color.BLACK);
                gc.fillText(stateName, XCOORD + RADIUS, YCOORD + RADIUS);
                //draw transition info
                gc.fillText(rToken + ", " + wToken + ", " + direction, XCOORD + 35, YCOORD+10);

                if (XCOORD + 150 < canvas.getWidth())
                {
                    XCOORD += 115;
                }
                else
                {
                    XCOORD = 10;
                    YCOORD += 50;
                }
        }
            
    }

    private void redraw() {
        if (currentStates != null) {
            drawStates(currentStates);
        }
        
    }
}
