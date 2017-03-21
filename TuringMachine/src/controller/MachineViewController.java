/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.File;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;


import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Interpreter;
import model.InterpreterException;

import javafx.scene.canvas.Canvas;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javafx.scene.text.TextFlow;
import model.StateTransition;
import javafx.scene.control.Accordion;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.FontPosture;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 *
 * @author Nick Ahring
 */
public class MachineViewController implements Initializable {
    
    //used for text formatting in tape, changing text here will change tape text for the program
    //the custom control FontControl will modify these while running, if the user chooses
    //to modify them in the font control and accepts the changes. The values set here are only defaults
    private String family = "Helvetica";
    private int size = 16;
    private boolean isBold = false;
    private boolean isItalic = false;
    private Color RWHeadFillColor = Color.RED;
    
    //used to keep track of how many tapes the user is working with and is given
    //to the interpreter upon its creation so it knows how many tapes it has
    //default is one tape
    private int tapes = 1;
    
    //Keeps track of the currently selected tape (in this case, its TextFlow, since
    //that's what is edited in the view)
    @FXML private TextFlow selectedTape;
    
    //UI Buttons
    @FXML private Button runButton;
    @FXML private Button stepButton;
    @FXML private Button stopButton;
    @FXML private Button resetButton;
    @FXML private Button tapeOneClearButton;
    @FXML private Button tapeTwoClearButton;
    @FXML private Button tapeThreeClearButton;
    //Displays
    @FXML private TextField currentState;
    @FXML private TextField currentSteps;
    //Text flows for each tape
    @FXML private TextFlow tapeOne;
    @FXML private TextFlow tapeTwo;
    @FXML private TextFlow tapeThree;
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
    @FXML private MenuItem fontOptions;
    @FXML private AnchorPane diagramDisplay;
    @FXML private TextFlow codeViewTab;
    //Titled panes for the tape views
    @FXML private TitledPane tapeOnePane;
    @FXML private TitledPane tapeTwoPane;
    @FXML private TitledPane tapeThreePane;
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
        boolean isReady = checkProgramStatus();
        if (isReady) {
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
    
    @FXML
    private void stepButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.step();
        }
    }
    
    @FXML
    private void stopButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.stop();
        }
    }
    
    @FXML
    private void resetButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.reset();
            tapeOne.getChildren().clear();
        }
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
        File initialDirectory;
        // This if-else determines if the program was ran through the .jar file or
        // through NetBeans, and then directs the file chooser to the correct
        // path of the TestFiles directory
        if (System.getProperty("user.dir").contains("dist"))
            initialDirectory = new File(".." + File.separator + ".." + File.separator + "TestFiles");
        else
            initialDirectory = new File(".." + File.separator + "TestFiles");
        //System.out.println("PWD: " + System.getProperty("user.dir"));
        fileChooser.setTitle("Open Machine File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Machine Files", "*.tm"));
        fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(tapeOne.getScene().getWindow());
        if (selectedFile != null) {
            //note that a file was loaded
            fileLoaded = true;
            //clear the tapes of any old content
            tapeOne.getChildren().clear();
            tapeTwo.getChildren().clear();
            tapeThree.getChildren().clear();
            codeViewTab.getChildren().clear();
            String input = controller.openFile(selectedFile);
            //when initializing interpreter, give it both an input and a view controller (this) to work with
            interp = new Interpreter(input, this, tapes);
//            recentFiles.add(selectedFile);
            //Show code or error report
            if (interp.errorFound()) {
                updateCodeTabContent(interp.getErrorReport());
                //show an error dialog explaining the errors
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Errors Found");
                alert.setHeaderText("The program you have loaded contains syntax errors");
                alert.setContentText("Please correct them or load a different program"
                        + "\nA complete error report can be found in the code tab");
                Label label = new Label("Errors found:");

                TextArea textArea = new TextArea(interp.getErrorReport());
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                GridPane expContent = new GridPane();
                expContent.add(label, 0, 0);
                expContent.add(textArea, 0, 1);
                GridPane.setVgrow(textArea, Priority.ALWAYS);
                GridPane.setHgrow(textArea, Priority.ALWAYS);
                expContent.setMaxWidth(Double.MAX_VALUE);
                // Set expandable Exception into the dialog pane.
                alert.getDialogPane().setExpandableContent(expContent);
                alert.showAndWait();
            }
            else {
                //try {
                //    launchCodeWindow(input);
                    Text text1 = new Text(input);
                    text1.setFont(Font.font(family, size));
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
        boolean isReady = checkProgramStatus();
        if (isReady) {
            tapeOne.getChildren().clear();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Tape Input Dialog");
            dialog.setHeaderText(null);
            dialog.setContentText("Tape One: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(input -> interp.setInitialContent(result.get(), 1));
            interp.reset();
        }
    }
    
    @FXML
    private void tapeTwoClearButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            tapeTwo.getChildren().clear();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Tape Input Dialog");
            dialog.setHeaderText(null);
            dialog.setContentText("Tape Two: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(input -> interp.setInitialContent(result.get(), 2));
            interp.reset();
        }
    }
    
    @FXML
    private void tapeThreeClearButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            tapeThree.getChildren().clear();
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Tape Input Dialog");
            dialog.setHeaderText(null);
            dialog.setContentText("Tape Three: ");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(input -> interp.setInitialContent(result.get(), 3));            
            interp.reset();
        }
    }
    
    /** The below methods handle toggling the view to either 1, 2, or 3 tape mode **/
    
    @FXML
    private void setOneTapeMode(ActionEvent event) {
        tapeTwoPane.setExpanded(false);
        tapeTwoPane.setVisible(false);
        tapeThreePane.setExpanded(false);
        tapeThreePane.setVisible(false);
        tapes = 1;
        if (fileLoaded) {
            interp.setNumberOfTapes(1);
            //reboot the interpreter
            interp.stop();
            tapeOne.getChildren().clear();
            String input = interp.getMachineCode();
            interp = null;
            //reboot the interpreter in the new mode
            interp = new Interpreter(input, this, tapes);
        }
    }
    
    @FXML
    private void setTwoTapeMode(ActionEvent event) {
        tapeTwoPane.setExpanded(true);
        tapeTwoPane.setVisible(true);
        tapeThreePane.setExpanded(false);
        tapeThreePane.setVisible(false);
        tapes = 2;
        if (fileLoaded) {
            interp.setNumberOfTapes(2);
            //reboot the interpreter
            interp.stop();
            tapeOne.getChildren().clear();
            tapeTwo.getChildren().clear();
            String input = interp.getMachineCode();
            interp = null;
            //reboot the interpreter in the new mode
            interp = new Interpreter(input, this, tapes);
        }
    }
    
    @FXML
    private void setThreeTapeMode(ActionEvent event) {
        tapeTwoPane.setExpanded(true);
        tapeTwoPane.setVisible(true);
        tapeThreePane.setExpanded(true);
        tapeThreePane.setVisible(true);
        tapes = 3;
        if (fileLoaded) {
            interp.setNumberOfTapes(3);
            //reboot the interpreter
            interp.stop();
            tapeOne.getChildren().clear();
            tapeTwo.getChildren().clear();
            tapeThree.getChildren().clear();
            String input = interp.getMachineCode();
            interp = null;
            //reboot the interpreter in the new mode
            interp = new Interpreter(input, this, tapes);
        }

    }
    
    /**********************************************************/
    
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
            String code;
            if (interp.errorFound()) {
                code = interp.getErrorReport();
            }
            else {
                code = interp.getMachineCode();
            }
            Text content = new Text(code);
            //style the content and add it
            content.setFont(getCurrentFontSettings());
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
    public void showFontChooser(ActionEvent event) {
            FontControl fontControl = new FontControl();
            //initialize the font chooser
            fontControl.initialize(family, size, isBold, isItalic, RWHeadFillColor);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(new Scene(fontControl));
            stage.setWidth(300);
            stage.setHeight(300);
            stage.setTitle("Font Chooser");
            stage.initOwner(tapeOne.getScene().getWindow());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
            //get and apply changes that were made if there were any
            size = fontControl.getFontSize();
            family = fontControl.getFontFamily();
            isBold = fontControl.getIsBold();
            isItalic = fontControl.getIsItalic();
            RWHeadFillColor = fontControl.getRWHeadFillColor();
            if (fileLoaded) {
                if (interp.errorFound()) {
                    updateCodeTabContent(interp.getErrorReport());
                }
                else {
                    updateCodeTabContent(interp.getMachineCode());
                }
                //update content across all three tapes
                updateTapeContent(interp.getTapeContent(1), 1);
                updateTapeContent(interp.getTapeContent(2), 2);
                updateTapeContent(interp.getTapeContent(3), 3);
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
    public void resetView() {
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
    public String getTapeInput(int tape) {
        switch (tape) {
            case 1:
                return tapeOne.getChildren().toString();
            case 2:
                return tapeTwo.getChildren().toString();
            case 3:
                return tapeThree.getChildren().toString();
            default:
                return tapeOne.getChildren().toString();
        }
    }
    
    @FXML
    public void setInitialTapeContent(String content, int tapeNumber) {
        Text input = new Text(content.substring(1));
        Text underHead = new Text(Character.toString(content.charAt(0)));
        underHead.setFill(RWHeadFillColor);
        underHead.setFont(Font.font(family, FontWeight.BOLD, size));
        input.setFont(getCurrentFontSettings());
        switch (tapeNumber) {
            case 1:
                tapeOne.getChildren().addAll(underHead, input);
                break;
            case 2:
                tapeTwo.getChildren().addAll(underHead, input);
                break;
            case 3:
                tapeThree.getChildren().addAll(underHead, input);
                break;
            default:
                break;
        }
    }
    
    @FXML
    public void updateTapeContent(String content, int tape) {
        int headLocation = interp.getRWHead(tape);
        System.out.println("The requested tape was " + tape);


        //compensates for possibility of head being at left or right
        boolean headAtRight = false;
        boolean headAtLeft = false;
        
        //check if head is at left or right
        if (headLocation == 0) {
            headAtLeft = true;            
        }
        else if (headLocation == interp.getTapeLength(tape) - 1) {
            headAtRight = true;            
        }
        
        //get the content under head
        Text tapeContentHead = new Text(Character.toString(content.charAt(headLocation)));
        //style it
        tapeContentHead.setFill(RWHeadFillColor);
        tapeContentHead.setFont((Font.font(family, FontWeight.BOLD, size)));
        
        //build depending on where head was at
        if (headAtLeft) {
            Text tapeContentRight = new Text(content.substring(headLocation + 1));
            tapeContentRight.setFont(getCurrentFontSettings());
            Platform.runLater(() -> {
                switch (tape) {
                    case 1:
                        tapeOne.getChildren().clear();
                        tapeOne.getChildren().addAll(tapeContentHead, tapeContentRight);
                        break;
                    case 2:
                        tapeTwo.getChildren().clear();
                        tapeTwo.getChildren().addAll(tapeContentHead, tapeContentRight);
                        break;
                    case 3:
                        tapeThree.getChildren().clear();
                        tapeThree.getChildren().addAll(tapeContentHead, tapeContentRight);
                        break;
                    default:
                        break;                        
                }                  
            });            
        }
        else if (headAtRight) {
            Text tapeContentLeft = new Text(content.substring(0, headLocation));
            tapeContentLeft.setFont(getCurrentFontSettings());
            Platform.runLater(() -> {
                switch (tape) {
                    case 1:
                        tapeOne.getChildren().clear();
                        tapeOne.getChildren().addAll(tapeContentLeft, tapeContentHead);
                        break;
                    case 2:
                        tapeTwo.getChildren().clear();
                        tapeTwo.getChildren().addAll(tapeContentLeft, tapeContentHead);
                        break;
                    case 3:
                        tapeThree.getChildren().clear();
                        tapeThree.getChildren().addAll(tapeContentLeft, tapeContentHead);
                        break;
                    default:
                        break;                        
                }
            });
        }
        else {
            Text tapeContentLeft = new Text(content.substring(0, headLocation));
            tapeContentLeft.setFont(getCurrentFontSettings());
            Text tapeContentRight = new Text(content.substring(headLocation + 1));
            tapeContentRight.setFont(getCurrentFontSettings());
            Platform.runLater(() -> {
                switch (tape) {
                    case 1:
                        tapeOne.getChildren().clear();
                        tapeOne.getChildren().addAll(tapeContentLeft, tapeContentHead, tapeContentRight);
                        break;
                    case 2:
                        tapeTwo.getChildren().clear();
                        tapeTwo.getChildren().addAll(tapeContentLeft, tapeContentHead, tapeContentRight);
                        break;
                    case 3:
                        tapeThree.getChildren().clear();
                        tapeThree.getChildren().addAll(tapeContentLeft, tapeContentHead, tapeContentRight);
                        break;
                    default:
                        break;                        
                }
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
        codeViewTab.getChildren().clear();
        newContent.setFont(getCurrentFontSettings());        
        codeViewTab.getChildren().add(newContent);
    }
    
    /**
     * Gets the content from the code tab
     * @return String code tab content
     */
    private String getCodeTabContent() {
        return codeViewTab.getChildren().toString();
    }
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        speedSlider.valueProperty().addListener(new ChangeListenerImpl());

        //these lines allow the canvas to dynamically resize when the program does
        canvas.widthProperty().addListener(observable -> redraw());
        canvas.heightProperty().addListener(observable -> redraw());
        
        canvas.widthProperty().bind(
                       diagramDisplay.widthProperty());
        canvas.heightProperty().bind(
                       diagramDisplay.heightProperty());
        
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
    
    /**
     * Helper method used to prevent the user from attempting to run, stop, reset, step, etc.
     * without a valid program loaded. Called before any of these actions and will return 
     * a boolean status okay to proceed
     * @return boolean machineReady
     */
    private boolean checkProgramStatus() {
        boolean machineReady = true;
        
        //Check and make sure the user has actually loaded a program
        //Also inform them of errors in the program if they need to correct them and reload
        //else, process their request
        if (fileLoaded) {
            if (interp.errorFound()) {
                machineReady = false;
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Your program contained errors");
                alert.setContentText("Please correct them or load a different program");
                alert.showAndWait();
            }
        }
        else {
            machineReady = false;
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("You have not loaded a program");
            alert.setContentText("Please load the program you want to run first");
            alert.showAndWait();
        }
        
        return machineReady;
    }

    public void showAutoStopDialog(String lookingForState) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Your program might be getting stuck in an infinite loop looking for state " + lookingForState);
        alert.setContentText("The machine has gone through your file four times, but still hasn't found the next state to go to."
                + "\nWe've stopped your program for now, but you can run again if you really want to"
                + "\nThis most likely means that there is an error in your program logic causing an infinite loop."
                + "\nIf you keep seeing this error, please check your program and its logic for errors");
        alert.showAndWait();
    }

    /**
    * Returns the current Font settings to allow easy text formatting
    * @return Font current font
    */
    private Font getCurrentFontSettings() {
        if (isBold && isItalic) {
            return Font.font(family, FontWeight.BOLD, FontPosture.ITALIC, size);
        }
        else if (isBold) {
            return Font.font(family, FontWeight.BOLD, size);
        }
        else if (isItalic) {
            return Font.font(family, FontPosture.ITALIC, size);
        }
        else {
            return Font.font(family, size);
        }   
    }

    public String[] showInputDialog(int numTapes) {
        String[] input = {"____", "_____", "_____"};
        //build and show the appropriate dialog
        switch (numTapes) {
            case 1:
            {
                Dialog<String []> dialog = new Dialog<>();
                dialog.setTitle("Tape Input");
                dialog.setHeaderText("Initial Tape Input");
                dialog.setContentText("Enter initial Tape input for tapes 1 and 2");
                
                Label label1 = new Label("Tape One: ");
                TextField text1 = new TextField();
                
                GridPane grid = new GridPane();
                grid.add(label1, 1, 1);
                grid.add(text1, 2, 1);
                dialog.getDialogPane().setContent(grid);
                
                ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
                
                dialog.setResultConverter((ButtonType b) -> {
                    if (b == buttonTypeOk) {
                        String [] result = new String[1];
                        result[0] = text1.getText();
                        return result;
                    }
                    return null;
                });
                
                Optional<String []> result = dialog.showAndWait();
                if (result.isPresent()) {
                    input = result.get();
                }
                break;
            }
            case 2:
            {
                Dialog<String []> dialog = new Dialog<>();
                dialog.setTitle("Tape Input");
                dialog.setHeaderText("Initial Tape Input");
                dialog.setContentText("Enter initial Tape input for tapes 1 and 2");
                
                Label label1 = new Label("Tape One: ");
                Label label2 = new Label("Tape Two: ");
                TextField text1 = new TextField();
                TextField text2 = new TextField();
                
                GridPane grid = new GridPane();
                grid.add(label1, 1, 1);
                grid.add(text1, 2, 1);
                grid.add(label2, 1, 2);
                grid.add(text2, 2, 2);
                dialog.getDialogPane().setContent(grid);
                
                ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
                
                dialog.setResultConverter((ButtonType b) -> {
                    if (b == buttonTypeOk) {
                        String [] result = new String[2];
                        result[0] = text1.getText();
                        result[1] = text2.getText();
                        return result;
                    }
                    return null;
                });
                
                Optional<String []> result = dialog.showAndWait();
                if (result.isPresent()) {
                    input = result.get();
                }
                break;
            }
            case 3:
            {
                Dialog<String []> dialog = new Dialog<>();
                dialog.setTitle("Tape Input");
                dialog.setHeaderText("Initial Tape Input");
                dialog.setContentText("Enter initial Tape input for tapes 1 and 2");
                
                Label label1 = new Label("Tape One: ");
                Label label2 = new Label("Tape Two: ");
                Label label3 = new Label("Tape Three: ");
                TextField text1 = new TextField();
                TextField text2 = new TextField();
                TextField text3 = new TextField();
                
                GridPane grid = new GridPane();
                grid.add(label1, 1, 1);
                grid.add(text1, 2, 1);
                grid.add(label2, 1, 2);
                grid.add(text2, 2, 2);
                grid.add(label3, 1, 3);
                grid.add(text3, 2, 3);
                dialog.getDialogPane().setContent(grid);
                
                ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
                
                dialog.setResultConverter((ButtonType b) -> {
                    if (b == buttonTypeOk) {
                        String [] result = new String[3];
                        result[0] = text1.getText();
                        result[1] = text2.getText();
                        result[2] = text3.getText();
                        return result;
                    }
                    return null;
                });
                
                Optional<String []> result = dialog.showAndWait();
                if (result.isPresent()) {
                    input = result.get();
                }
                break;
                
            }
            default:
            {
                System.out.println("Unexpected tape Number");
                break;
            }
            
        }
        System.out.println(Arrays.toString(input));
        return input;
    }
    
    private class ChangeListenerImpl implements ChangeListener {

        public ChangeListenerImpl() {
        }

        @Override
        public void changed(ObservableValue arg0, Object arg1, Object arg2) {
            changeLabel.textProperty().setValue(String.valueOf((int)speedSlider.getValue()));
            
            //System.out.println("Speed slider = " + getSpeed());  //output speed changes
        }
    }
}
