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
    @FXML private TextFlow tapeOne;
    
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
        File initialDirectory = new File("../TestFiles");
        fileChooser.setTitle("Open Machine File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Machine Files", "*.tm"));
        fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(tapeOne.getScene().getWindow());
        if (selectedFile != null) {
            String input = controller.openFile(selectedFile);
            //when initializing interpreter, give it both an input and a view controller (this) to work with
            interp = new Interpreter(input, this);
//            recentFiles.add(selectedFile);
            //launch window to show code or error
            if (interp.errorFound()) {
                try {
                    launchCodeWindow(interp.getErrorReport());
                } catch (IOException ex) {
                    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            else {
                //try {
                //    launchCodeWindow(input);
                    Text text1 = new Text(input);
                    text1.setFont(Font.font("Courier New", 14));
                    codeViewTab.getChildren().add(text1);
                    Text inputText = new Text(interp.getInitialInput());
                    tapeOne.getChildren().add(inputText);
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
        //tapeOne.setText("");
//        tm.clearTape();
        tapeOne.getChildren().clear();
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
        
    public void setTapeContent(String content) {
        Text tapeContent = new Text(content);
        tapeContent.setFill(Color.RED);
        tapeContent.setFont(Font.font("Helvetica", FontWeight.BOLD, 15));
        tapeContent.setTextAlignment(TextAlignment.CENTER);
        Platform.runLater(() -> {
            tapeOne.getChildren().clear();
            tapeOne.getChildren().add(tapeContent);
        });
        
//        tapeOne.setText(content);
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
