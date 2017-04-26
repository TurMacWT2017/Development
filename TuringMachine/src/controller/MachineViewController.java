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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;

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


import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javafx.scene.text.TextFlow;
import model.StateTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;

import javafx.scene.shape.StrokeType;
import javafx.scene.text.FontPosture;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/**
 * This controller controls the Main Program User Interface. The FXML that it
 * corresponds to is MachineView.
 * @author Nick Ahring
 */
public class MachineViewController implements Initializable {
    
    //used for text formatting in tape, changing text here will change tape text for the program
    //the custom control FontControl will modify these while running, if the user chooses
    //to modify them in the font control and accepts the changes. The values set here are only defaults
    //for the code tab and window. Elsewhere, the defaults will be the user defaults.
    private String family = "Courier";
    private int size = 14;
    private boolean isBold = false;
    private boolean isItalic = false;
    private Color RWHeadFillColor = Color.RED;
    private String codeFamily = "Courier";
    private int codeSize = 14;
    private boolean isCodeBold = false;
    private boolean isCodeItalic = false;
    
    //state diagram variables
    private Circle acceptNode;
    private Circle rejectNode;
    private Circle startNode;
    private Circle[] stateNodes;
    private Circle[] uniqueNodes;
    private Circle[] endNodes;
    private Label[] stateLabels;
    private Label[] endLabels;
    private Label startLabel;
    
    private String[] initialUniqueStates;
    private Set<String> uniqueStateSet;
    private String[] allInitStates;
    private String[] allTapes;
    private String[] allTransitions;
    private String[] allWriteTapes;
    private String[] allEndStates;
    private int acceptCheck = 0;
    private int rejectCheck = 0;
    private int numUniqueStates;
    private int numAllStates;
    private Pane statePane;
    @FXML private Pane statePaneTab;
    private Pane statePaneWin;
    //used to keep track of how many tapes the user is working with and is given
    //to the interpreter upon its creation so it knows how many tapes it has
    //default is one tape
    public static int tapes = 1;
    
    //Keeps track of the currently selected tape (in this case, its TextFlow, since
    //that's what is edited in the view)
    @FXML private TextFlow selectedTape;
    private String tapeSelection;
    private Color[] tapeColor;
    private int connected = 0; 
    private Line[] transLines;
    private Label[] transLabels;
    private Circle[] transNodes;
    private Arc[] transArcs;
    private ArrayList pullNodes;
    private Boolean staticNodes = false;
    
    //UI Buttons
    @FXML private Button runButton;
    @FXML private Button stepButton;
    @FXML private Button stopButton;
    @FXML private Button resetButton;
    @FXML private Button tapeOneClearButton;
    @FXML private Button tapeTwoClearButton;
    @FXML private Button tapeThreeClearButton;
    //Displays
    @FXML public TextField currentState;
    @FXML private TextField currentSteps;
    //Text flows for each tape
    @FXML private TextFlow tapeOne;
    @FXML private TextFlow tapeTwo;
    @FXML private TextFlow tapeThree;

    private static double XCOORD = 50.0;
    private static double YCOORD = 25.0;
    private static final double RADIUS = 30.0;
    //Slider
    @FXML private Slider speedSlider;
    @FXML private Label changeLabel;
    @FXML private Label speedLabel;
    //Menu items
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem menuQuitButton;
    @FXML private MenuItem recentFilesMenu;
    @FXML private MenuItem showCodeWindow;
    @FXML private MenuItem fontOptions;
    @FXML private AnchorPane diagramDisplay;
    @FXML private MenuItem about;
    @FXML private MenuItem langref;
    @FXML private MenuItem clearAllTapesButton;
    @FXML private MenuItem loadPalindrome;
    @FXML private MenuItem loadGoBuffs;
    @FXML private MenuItem loadBinaryAddition;
    //Titled panes for the tape views
    @FXML private TitledPane tapeOnePane;
    @FXML private TitledPane tapeTwoPane;
    @FXML private TitledPane tapeThreePane;
    @FXML private ScrollPane tapeOneScroll;
    //Code Window
    @FXML private TextFlow codeViewTab;
    //Main window
    @FXML private VBox mainWindow;
    //Code diagram window
    @FXML private Stage codeStage;
    @FXML private TextFlow codeDisplay;
    
    //Machine Controller
    private final MachineController controller = new MachineController();
    //Interpreter instance (new interpreter is created on load of a program)
    private Interpreter interp;
    private ArrayList<File> recentFiles;
    //list of states, used in drawing
    private static ArrayList<StateTransition> currentStates;
    //keeps track of file status
    boolean fileLoaded = false;
    //DEBUG
    private final boolean DEBUG = false;
    
    /**
     * Handles a click on the run button. Utilizes helper method to determine
     * if program is ready to run. For helper method see checkProgramStatus();
     * documentation
     * @param event ActionEvent
     */
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
    
    /**
     * Handles a click on the step button. Utilizes helper method to determine
     * if program is ready to run. For helper method see checkProgramStatus();
     * documentation
     * @param event ActionEvent
     */
    @FXML
    private void stepButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.step();
        }
    }
    
    /**
     * Handles a click on the stop button. Utilizes helper method to determine
     * if program is ready to run. For helper method see checkProgramStatus();
     * documentation
     * @param event ActionEvent
     */
    @FXML
    private void stopButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.stop();
        }
    }
    
    /**
     * Handles a click on the reset button. Utilizes helper method to determine
     * if program is ready to run. For helper method see checkProgramStatus();
     * documentation
     * @param event ActionEvent
     */
    @FXML
    private void resetButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.reset();
            tapeOne.getChildren().clear();
        }
    }
    
    /**
     * Handles a click on the quit button. 
     * @param event ActionEvent
     */
    @FXML
    private void menuQuitButtonClicked(ActionEvent event) {
        Platform.exit();
    }
    
    /**
     * Handles a click on the open file button. Uses machine controller to open
     * file, starts a new interpreter
     * @param event ActionEvent
     */
    @FXML
    private void openFileMenuItemClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File initialDirectory;
        File referenceFile = new File("Palindrome.tm");
        String filePath = "~";
        // This if-else determines if the program was ran through the .jar file or
        // through NetBeans, and then directs the file chooser to the correct
        // path of the TestFiles directory
        if (System.getProperty("user.dir").contains("dist")) {
            /*try {
                filePath = referenceFile.getCanonicalPath();
            } catch(Exception e) {
                System.out.println(e);
            }     
            initialDirectory = new File(filePath);*/
            initialDirectory = new File(".." + File.separator + ".." + File.separator + "TestFiles");
        }
        else {
            initialDirectory = new File(".." + File.separator + "TestFiles");
        }
        //System.out.println("PWD: " + System.getProperty("user.dir"));
        fileChooser.setTitle("Open Machine File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Machine Files", "*.tm"));
        fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(tapeOne.getScene().getWindow());
        if (selectedFile != null) {
            //note that a file was loaded
            fileLoaded = true;
            //staticNodes = false;
            //clear the tapes of any old content
            tapeOne.getChildren().clear();
            tapeTwo.getChildren().clear();
            tapeThree.getChildren().clear();
            codeViewTab.getChildren().clear();
            statePaneTab.getChildren().clear();
            clearStateTuples();

            String input = controller.openFile(selectedFile);
            //when initializing interpreter, give it both an input and a view controller (this) to work with
            interp = new Interpreter(input, this, tapes);
            //enable the speed slider
            speedSlider.setDisable(false);
            changeLabel.setDisable(false);
            speedLabel.setDisable(false);
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
                    text1.setFont(getCurrentCodeFontSettings());
                    codeViewTab.getChildren().add(text1);
                    //statePaneTab.getChildren().add(statePane);
                    //tapeOne.setText(interp.getInitialInput());
                try {
                    interp.start();
                    //Ensure the interpreter's speed is up to date with UI
                    interp.setRunSpeed((int) speedSlider.getValue());
                } catch (InterpreterException ex) {
                    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                //} catch (IOException ex) {
                //    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
        }       
    }
    
    /**** This section contains the event methods for loading the built in */
    /**** example programs */
    
    /**
     * Loads the example program "Palindrome.tm".
     * @param event ActionEvent
     * @throws java.io.IOException
     */
    public void loadPalindromeClicked(ActionEvent event) throws IOException {
        //note that a file was loaded
        fileLoaded = true;
        //clear the tapes of any old content
        tapeOne.getChildren().clear();
        tapeTwo.getChildren().clear();
        tapeThree.getChildren().clear();
        codeViewTab.getChildren().clear();
        statePaneTab.getChildren().clear();
        clearStateTuples();

        String input = controller.openExample("examples/Palindrome.tm");
        //when initializing interpreter, give it both an input and a view controller (this) to work with
        interp = new Interpreter(input, this, tapes);
        //enable the speed slider
        speedSlider.setDisable(false);
        changeLabel.setDisable(false);
        speedLabel.setDisable(false);
        Text text1 = new Text(input);
        text1.setFont(getCurrentCodeFontSettings());
        codeViewTab.getChildren().add(text1);
        try {
            interp.start();
            //Ensure the interpreter's speed is up to date with UI
            interp.setRunSpeed((int) speedSlider.getValue());
        } catch (InterpreterException ex) {
            Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Loads the example program "GoBuffs.tm".
     * @param event ActionEvent
     * @throws java.io.IOException
     */
    public void loadGoBuffsClicked(ActionEvent event) throws IOException {
        //note that a file was loaded
        fileLoaded = true;
        //clear the tapes of any old content
        tapeOne.getChildren().clear();
        tapeTwo.getChildren().clear();
        tapeThree.getChildren().clear();
        codeViewTab.getChildren().clear();
        statePaneTab.getChildren().clear();
        clearStateTuples();

        String input = controller.openExample("examples/GoBuffs.tm");
        //this is a three tape program, so make sure all tapes are active
        activateTapeTwo();
        activateTapeThree();
        //when initializing interpreter, give it both an input and a view controller (this) to work with
        interp = new Interpreter(input, this, tapes);
        //enable the speed slider
        speedSlider.setDisable(false);
        changeLabel.setDisable(false);
        speedLabel.setDisable(false);
        Text text1 = new Text(input);
        text1.setFont(getCurrentCodeFontSettings());
        codeViewTab.getChildren().add(text1);
        try {
            interp.start();
            //Ensure the interpreter's speed is up to date with UI
            interp.setRunSpeed((int) speedSlider.getValue());
        } catch (InterpreterException ex) {
            Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
        /**
     * Loads the example program "BinaryAddition.tm".
     * @param event ActionEvent
     * @throws java.io.IOException
     */
    public void loadBinaryAdditionClicked(ActionEvent event) throws IOException {
        // = true;
        //note that a file was loaded
        fileLoaded = true;
        //clear the tapes of any old content
        tapeOne.getChildren().clear();
        tapeTwo.getChildren().clear();
        tapeThree.getChildren().clear();
        codeViewTab.getChildren().clear();
        statePaneTab.getChildren().clear();
        clearStateTuples();

        String input = controller.openExample("examples/BinaryAddition.tm");
        //This is a three tape program, so make sure all tapes active
        activateTapeTwo();
        activateTapeThree();
        //when initializing interpreter, give it both an input and a view controller (this) to work with
        interp = new Interpreter(input, this, tapes);
        //enable the speed slider
        speedSlider.setDisable(false);
        changeLabel.setDisable(false);
        speedLabel.setDisable(false);
        Text text1 = new Text(input);
        text1.setFont(getCurrentCodeFontSettings());
        codeViewTab.getChildren().add(text1);
        try {
            interp.start();
            //Ensure the interpreter's speed is up to date with UI
            interp.setRunSpeed((int) speedSlider.getValue());
        } catch (InterpreterException ex) {
            Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    
    /****** Tape Clear button handling methods *********/
    
    /**
     * Handles a click on the tape one clear button. Utilizes helper method to determine
     * if program is ready. For helper method see checkProgramStatus();
     * documentation
     * @param event ActionEvent
     */
    @FXML
    private void tapeOneClearButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.stop();
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
    
    /**
     * Handles a click on the tape two clear button. Utilizes helper method to determine
     * if program is ready. For helper method see checkProgramStatus();
     * documentation
     * @param event ActionEvent
     */
    @FXML
    private void tapeTwoClearButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.stop();
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
    
    /**
     * Handles a click on the tape three clear button. Utilizes helper method to determine
     * if program is ready. For helper method see checkProgramStatus();
     * documentation
     * @param event ActionEvent
     */
    @FXML
    private void tapeThreeClearButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.stop();
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
    
    /**
     * Handles a click on the clear all tapes menu button. Utilizes helper method to determine
     * if program is ready. For helper method see checkProgramStatus();
     * documentation
     * @param event ActionEvent
     */
    @FXML
    private void clearAllTapesButtonClicked(ActionEvent event) {
        boolean isReady = checkProgramStatus();
        if (isReady) {
            interp.stop();
            interp.popup();
            interp.reset();
        }
    }
    
    /** The below methods handle toggling the view to either 1, 2, or 3 tape mode **/

    /**
     * Changes the machine to one tape mode
     * @param event ActionEvent
     */
    @FXML
    private void setOneTapeMode(ActionEvent event) {
        tapeTwoPane.setExpanded(false);
        tapeTwoPane.setVisible(false);
        tapeTwoPane.setPrefHeight(0);
        tapeThreePane.setExpanded(false);
        tapeThreePane.setVisible(false);
        tapeThreePane.setPrefHeight(0);
        tapes = 1;
        if (fileLoaded) {
            boolean isReady = checkProgramStatus();
            if (isReady) {
                interp.setNumberOfTapes(1);
                //reboot the interpreter
                interp.stop();
                tapeOne.getChildren().clear();
                String input = interp.getMachineCode();
                interp = null;
                //reboot the interpreter in the new mode
                interp = new Interpreter(input, this, tapes);
                try {
                    interp.start();
                } catch (InterpreterException ex) {
                    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                setStartState();
            }
        }
    }
    
    /**
     * Changes the machine to two tape mode
     * @param event ActionEvent
     */
    @FXML
    private void setTwoTapeMode(ActionEvent event) {
        tapeTwoPane.setExpanded(true);
        tapeTwoPane.setVisible(true);
        tapeTwoPane.setPrefHeight(99);
        tapeThreePane.setExpanded(false);
        tapeThreePane.setVisible(false);
        tapeThreePane.setPrefHeight(0);
        tapes = 2;
        if (fileLoaded) {
            boolean isReady = checkProgramStatus();
            if (isReady) {
                interp.setNumberOfTapes(2);
                //reboot the interpreter
                interp.stop();
                tapeOne.getChildren().clear();
                tapeTwo.getChildren().clear();
                String input = interp.getMachineCode();
                //reboot the interpreter in the new mode
                interp = new Interpreter(input, this, tapes);
                try {
                    interp.start();
                } catch (InterpreterException ex) {
                    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                setStartState();
            }
        }
    }
    
    /**
     * Change machine to three tape mode
     * @param event ActionEvent
     */
    @FXML
    private void setThreeTapeMode(ActionEvent event) {
        tapeTwoPane.setPrefHeight(99);
        tapeTwoPane.setExpanded(true);
        tapeTwoPane.setVisible(true);
        tapeThreePane.setPrefHeight(99);
        tapeThreePane.setExpanded(true);
        tapeThreePane.setVisible(true);
        tapes = 3;
        if (fileLoaded) {
            boolean isReady = checkProgramStatus();
            if (isReady) {
                interp.setNumberOfTapes(3);
                //reboot the interpreter
                interp.stop();
                tapeOne.getChildren().clear();
                tapeTwo.getChildren().clear();
                tapeThree.getChildren().clear();
                String input = interp.getMachineCode();
                //reboot the interpreter in the new mode
                interp = new Interpreter(input, this, tapes);
                try {
                    interp.start();
                } catch (InterpreterException ex) {
                    Logger.getLogger(MachineViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
                setStartState();
            }
        }

    }
    
    /*** These methods are used by the interpreter if a tape needs to be activated that isn't ****/
    
    /**
     * Activates tape two within the UI
     */
    @FXML
    public void activateTapeTwo() {
        tapeTwo.getChildren().clear();
        tapeTwoPane.setPrefHeight(99);
        tapeTwoPane.setExpanded(true);
        tapeTwoPane.setVisible(true);
        tapes = 2;
    }
    
    /**
     * Activates tape three within the UI
     */
    @FXML
    public void activateTapeThree() {
        tapeThree.getChildren().clear();
        tapeThreePane.setPrefHeight(99);
        tapeThreePane.setExpanded(true);
        tapeThreePane.setVisible(true);
        tapes = 3;
    }
    
    /**********************************************************/
    
    /** Returns the current number of active tapes (or tape mode)
     * 
     * @return int number of active tapes 
     */
    @FXML
    public int getCurrentMode() {
        return tapes;
    }
    
    /**
     * Handles click on launch code window menu item, displays code view tab
     * content in new window
     * @param event ActionEvent
     */
    @FXML
    private void launchCodeWindow(ActionEvent event) {
        //Parent root;
        if (fileLoaded) {
            if (DEBUG) {System.out.println("Making code window");}
            codeStage = new Stage();
            ScrollPane layout = new ScrollPane();
            codeDisplay = new TextFlow();
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
            //content.setFont(getCurrentFontSettings());
            content.setFont(getCurrentCodeFontSettings());
            codeDisplay.getChildren().add(content);
            //set the scene and its owner
            codeStage.setScene(new Scene(layout, 450, 450));
            codeStage.setTitle("Code Window");
            codeStage.initOwner(tapeOne.getScene().getWindow());
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            codeStage.setX((primScreenBounds.getWidth() - codeStage.getWidth()) / 4); 
            codeStage.setY((primScreenBounds.getHeight() - codeStage.getHeight()) / 8);
            codeStage.show();
        }
        else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please load a program first");
            alert.showAndWait();
        }
    }
    
    /**
     * Handles click on font options menu item, and shows custom font control
     * component
     * @param event ActionEvent
     */
    @FXML
    public void showFontChooser(ActionEvent event) {
            FontControl fontControl = new FontControl();
            //initialize the font chooser
            fontControl.initialize(family, size, isBold, isItalic, RWHeadFillColor, codeSize, codeFamily, isCodeBold, isCodeItalic);
            Stage stage = new Stage(StageStyle.UNDECORATED);
            stage.setScene(new Scene(fontControl));
           //stage.setWidth(300);
            //stage.setHeight(300);
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
            codeSize = fontControl.getCodeFontSize();
            codeFamily = fontControl.getCodeFamily();
            isCodeBold = fontControl.getCodeBold();
            isCodeItalic = fontControl.getCodeItalic();
            RWHeadFillColor = fontControl.getRWHeadFillColor();
            if (fontControl.getIsDefaultFont()) {
                TuringMachine.setUserFontPreferences(family, size, isItalic, isBold);
            }
            if (fontControl.getIsDefaultRW()) {
                TuringMachine.setUserRWHeadPreferences(RWHeadFillColor);
            }
            if (fontControl.getIsDefaultCode()) {
                TuringMachine.setUserCodeFontPreferences(codeFamily, codeSize, isCodeItalic, isCodeBold);
            }
            if (fileLoaded) {
                if (interp.errorFound()) {
                    updateCodeTabContent(interp.getErrorReport());
                }
                else {
                    updateCodeTabContent(interp.getMachineCode());
                }
                //update content across all three tapes
                updateTapeContent(interp.getTapeContent(1), 1);
                if (tapes == 2) {
                    updateTapeContent(interp.getTapeContent(2), 2);
                }
                if (tapes == 3) {
                    updateTapeContent(interp.getTapeContent(2), 2);
                    updateTapeContent(interp.getTapeContent(3), 3);
                }
                //force update of code tab content to reflect changed font
                ObservableList<Node> nodes =  codeViewTab.getChildren();  
                StringBuilder sb = new StringBuilder();  
                nodes.forEach((node) -> {  
                    sb.append((((Text)node).getText()));
                });
                    String txt = sb.toString();  
                updateCodeTabContent(txt);
                if (codeDisplay != null) {
                    codeDisplay.getChildren().clear();
                    Text newContent = new Text(txt);
                    newContent.setFont(getCurrentCodeFontSettings());
                    codeDisplay.getChildren().add(newContent);
                }
            }
            //This line helps prevent the viewport from "breaking" scrolling
            tapeOneScroll.setPrefViewportWidth(700);
    }
    
    /**
     * Handles click on about menu item and displays about page
     * @param event ActionEvent
     */
    @FXML
    public void aboutMenu(ActionEvent event) {
        // stuff
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        URL url = MachineViewController.class.getResource(File.separator + "view" + File.separator + "about.html");
        String content = url.toExternalForm();
        webEngine.load(content);
        
        Stage stage = new Stage();
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("About");
        stage.setWidth(625);
        stage.setHeight(500);
        stage.show();
        
        root.getChildren().add(webView);
    }
    
    /**
     * Handles click on language reference menu item and displays language reference
     * @param event ActionEvent
     */
    @FXML
    public void languageReference(ActionEvent event) {
        // stuff
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        URL url = MachineViewController.class.getResource(File.separator + "view" + File.separator + "languageReference.html");
        String content = url.toExternalForm();
        webEngine.load(content);
                
        Stage stage = new Stage();
        StackPane root = new StackPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Language Reference");
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.show();
        
        root.getChildren().add(webView);
    }
    
    /**
     * Places the UI in a start (ready) state
     */
    @FXML
    public void setStartState() {
        runButton.setDisable(false);
        stopButton.setDisable(false);
        stepButton.setDisable(false);
    }

    /**
     * Places the UI in a stopped state
     */
    @FXML
    public void setStoppedState() {
        if (DEBUG) {System.out.println("Machine stopped");}
        Platform.runLater(() -> {
            runButton.setText("Run");
        });
        runButton.setDisable(true);
        stopButton.setDisable(true);
        stepButton.setDisable(true);
        
    }
    
    /**
     * Used to reset elements in the UI back to default state
     */
    @FXML
    public void resetView() {
        runButton.setText("Run");
    }

    /**
     * Updates the step count within the UI
     * @param stepCount new count
     */
    @FXML
    public void updateStepCount(int stepCount) {
        currentSteps.setText(Integer.toString(stepCount));
    }
    
    /**
     * Updates the UI State Display
     * @param state new state to display
     */
    @FXML
    public void updateState(String state) {
        currentState.setText(state);
        //updateStateNodes(state);
    }
    
    /**
     * Gets the being displayed in a requested tape
     * @param tape requested
     * @return string tape content
     */
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
    
    /**
     * Sets the initial tape content for a given tape
     * @param content content to display
     * @param tapeNumber desired tape
     */    
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
    
    /**
     * Updates the content of the specified tape within the UI
     * @param content new content to display
     * @param tape requested tape
     */
    @FXML
    public void updateTapeContent(String content, int tape) {
        int headLocation = interp.getRWHead(tape);
        if (DEBUG) {System.out.println("The requested tape was " + tape);}


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
        newContent.setFont(getCurrentCodeFontSettings());
        codeViewTab.getChildren().add(newContent);
    }
    
    /**
     * Gets the content from the code tab
     * @return String code tab content
     */
    private String getCodeTabContent() {
        return codeViewTab.getChildren().toString();
    }
    
    
    /**
     * Ran on launch to add listeners and finish initialization of UI
     * Also pulls and sets any default user preferences
     * @param url URL
     * @param rb ResourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        speedSlider.valueProperty().addListener(new ChangeListenerImpl());
        //this portion pulls any current user defaults that have been set and applies them
        Object[] settings = TuringMachine.getUserFontPreferences();
        Color savedColor = TuringMachine.getUserRWHeadPreferences();
        Object[] codeSettings = TuringMachine.getUserCodeFontPreferences();
        //set main (tape) font settings
        family = (String) settings[0];
        size = (int) settings[1];
        isItalic = (boolean) settings[2];
        isBold = (boolean) settings[3];
        RWHeadFillColor = savedColor;
        //set code font settings
        codeFamily = (String) codeSettings[0];
        codeSize = (int) codeSettings[1];
        isCodeItalic = (boolean) codeSettings[2];
        isCodeBold = (boolean) codeSettings[3];
        //these lines allow the canvas to dynamically resize when the program does
        
        statePaneTab.widthProperty().addListener(observable -> redraw(currentStates));
        statePaneTab.heightProperty().addListener(observable -> redraw(currentStates));
        
        //these lines allow the the UI to expand and shrink dynamically with panes as they expand or collapse
        tapeTwoPane.expandedProperty().addListener(observable -> resetTapePaneTwoHeight());
        tapeThreePane.expandedProperty().addListener(observable -> resetTapeThreePaneHeight());
        
        //these lines set the bind the gap between tape name and clear button
        //thse are necessary to keep the clear buttons right aligned
        tapeOnePane.setContentDisplay(ContentDisplay.RIGHT);
        tapeTwoPane.setContentDisplay(ContentDisplay.RIGHT);
        tapeThreePane.setContentDisplay(ContentDisplay.RIGHT);


        tapeOnePane.graphicTextGapProperty().bind(mainWindow.widthProperty().subtract(215.00));
        tapeTwoPane.graphicTextGapProperty().bind(mainWindow.widthProperty().subtract(230.00));
        tapeThreePane.graphicTextGapProperty().bind(mainWindow.widthProperty().subtract(230.00));
        
        //retrieves user's tape settings
        tapes = TuringMachine.getUserTapePreferences();
        if (tapes == 2) {
            activateTapeTwo();
        }
        if (tapes == 3) {
            activateTapeTwo();
            activateTapeThree();
        }
        
        
    }    

    /**
     * Gets the current value of the speed slider
     * @return int speed
     */
    public int getSpeed(){
        return (int)speedSlider.getValue();
    }
    
    //**  METHODS FOR STATE DIAGRAM DISPLAY  **//
    
    /**
     * Launches the state window diagram
     * @param event ActionEvent 
     */
    @FXML
    public void launchStateWindow(ActionEvent event){
        
        if (fileLoaded) {
            Stage stage = new Stage();
            //set the scene and its owner
            ScrollPane layout = new ScrollPane();
            stage.setTitle("State Diagram Window");
            Pane windowPane = new Pane();
            windowPane = statePane;


            
            
            System.out.println("Making state diagram window");
            layout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            layout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);        
            layout.setFitToHeight(true);
            layout.setFitToWidth(true);      
            //pane.setStyle("-fx-background-color: #F5F5DC");
            layout.setStyle("-fx-background-color: linear-gradient(to left, #F5F5DC, #777676);"
                + " -fx-border: 16px solid; -fx-border-color: #67112b; -fx-background-radius: 1.0;"
                + " -fx-border-radius: 5.0");
           
           
            Scene scene = new Scene(layout, 550, 450);
          
            stage.setScene(scene);

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
    
    /**
     * Helper method called by NULL to update colors of nodes during execution
     * @param stateLabel String of text from stateLabel
     */
    private void updateStateNodes(String stateLabel){

        pullNodes = new ArrayList<>();
        pullNodes = getAllNodes(statePane);
        
        for(int i=0; i<numUniqueStates;i++)
            if(stateLabel == null ? currentState.toString() == null : stateLabel.equals(currentState.toString())){
                //statePane.getChildren().remove(uniqueNodes[i]);
                uniqueNodes[i].setFill(Color.WHITE);
                //statePane.getChildren().add(uniqueNodes[i]);
                redraw(currentStates);
                //statePane.getChildren().remove(uniqueNodes[i]);
                uniqueNodes[i].setFill(Color.RED);
                //statePane.getChildren().add(uniqueNodes[i]);
                redraw(currentStates);
            }   
            
        //System.out.println("pullNodes 0 = " + pullNodes.get(0));
        anchorAcceptRejectNodes();
    }        

    /**
     * Helper method called by various to retrieve all nodes currently in Pane
     * @param parent Pane from which to grab all nodes
     * @return nodes ArrayList of nodes loaded with that which is retrieved
     */
    public static ArrayList<Node> getAllNodes(Pane parent) {
        ArrayList<Node> nodes = new ArrayList<>();
        addAllDescendents(parent, nodes);
        return nodes;
    }

    /**
     * Helper method called by various to add all nodes in ArrayList to Pane
     * @param pane Pane from which to grab all nodes
     * @param nodes ArrayList of nodes loaded with that which is retrieved
     */
    private static void addAllDescendents(Pane parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Pane)
                addAllDescendents((Pane)node, nodes);
        }
    }

    /**
     * Draws the state diagram via various Helper Methods in this class
     * @param states ArrayList
     */
    public void drawStates(ArrayList<StateTransition> states) {      
        staticNodes=false;       
        statePane = new Pane();
        double stateTabWidth = statePaneTab. widthProperty().get();
        double stateTabHeight = statePaneTab.heightProperty().get();        
        
        // LOAD the initial state, end state, and transition arrays
        loadTupleArrays(states);           
        loadTapeColors();
        drawTapeLegend();  
        
        transLabels = new Label[numAllStates];
        transNodes = new Circle[numAllStates];
        transLines = new Line[numAllStates]; 
        transArcs = new Arc[numAllStates];
        
        stateNodes = new Circle[numAllStates];
        uniqueNodes = new Circle[numUniqueStates];
        endNodes = new Circle[numAllStates];
        
        stateLabels = new Label[numAllStates];
        endLabels = new Label[numAllStates];
        startNode = createDraggingCircle(stateTabWidth/2.0, 15, 5, statePane, Color.GRAY);
        
        // starting node (pre-first-state)
        startLabel = new Label();       
        startLabel.setText("start");
        startLabel.layoutXProperty().bindBidirectional(startNode.centerXProperty());
        startLabel.layoutYProperty().bindBidirectional(startNode.centerYProperty());
        startNode.toBack();
        statePane.getChildren().addAll(startNode,startLabel);
                
        drawUniqueInitNodes();     
        bindStateLabels();
        anchorAcceptRejectNodes();        
        bindInitToEndStates();         
        drawTransitionLabels();
        
        Line toback = connectStates(startNode, stateNodes[0],"", -1); 
        toback.toBack();

        BorderStrokeStyle style = new BorderStrokeStyle(StrokeType.CENTERED, 
                StrokeLineJoin.BEVEL, StrokeLineCap.SQUARE,10, 0, null);

        statePaneTab.setStyle("-fx-background-color: linear-gradient(to left, #F5F5DC, #777676);"
                + " -fx-border: 16px solid; -fx-border-color: #67112b; -fx-background-radius: 1.0;"
                + " -fx-border-radius: 5.0");
        statePaneTab.setBorder(new Border(new BorderStroke(Color.web("#67112b"), style, CornerRadii.EMPTY, new BorderWidths(5))));
        statePaneTab.getChildren().add(statePane);
       currentStates = states;
    }
    
      /**
     * Helper method called by drawStates to bind stateLabels tostateNodes in pane
     */
    public void bindStateLabels(){

        double stateTabWidth = statePaneTab.widthProperty().get();
        double stateTabHeight = statePaneTab.heightProperty().get();
            for (int j = 0; j < numAllStates; j++){
            
            stateNodes[j] = createDraggingCircle(0,0, 4, statePane, Color.BLUE);
            endNodes[j] = createDraggingCircle(0, 0, 4, statePane, Color.BLUE);                  
            String stateText = "\n" + allInitStates[j];
            String endText = "\n" + allEndStates[j];

            Label stateLabel = new Label(stateText);
            Label endLabel = new Label(endText);  
            stateLabel.layoutXProperty().bindBidirectional(stateNodes[j].centerXProperty());
            stateLabel.layoutYProperty().bindBidirectional(stateNodes[j].centerYProperty());
            endLabel.layoutXProperty().bindBidirectional(endNodes[j].centerXProperty());
            endLabel.layoutYProperty().bindBidirectional(endNodes[j].centerYProperty());               
            stateLabel.setLabelFor(stateNodes[j]);
            endLabel.setLabelFor(endNodes[j]);
            stateLabels[j] = stateLabel;
            endLabels[j] = endLabel;
          
            stateNodes[j].setSmooth(true);
            endNodes[j].setSmooth(true);         
            statePane.getChildren().addAll(stateNodes[j],stateLabel, endNodes[j],endLabel);//,endLabel);
        } 
    }
    
    /**
     * Helper method called by drawStates to place the unique stateNodes on pane
     */
    public void drawUniqueInitNodes(){

        double stateTabWidth = statePaneTab.widthProperty().get();
        double stateTabHeight = statePaneTab.heightProperty().get();
        XCOORD = stateTabWidth*.05;
        YCOORD = stateTabHeight*.15;

        for (int j=0; j< numUniqueStates; j++){
            uniqueNodes[j] = createDraggingCircle(XCOORD, YCOORD, 10, statePane, tapeColor[j]);    
            uniqueNodes[j].setStrokeType(StrokeType.OUTSIDE);
            uniqueNodes[j].setStroke(tapeColor[j]);
            
            if (XCOORD + 70.0 <= stateTabWidth*.98)
            {
                XCOORD += stateTabWidth*.10;              
                if(j%2==0)
                    YCOORD += stateTabHeight*.23;
                else
                    YCOORD -= stateTabHeight*.23;                
            }
            else
            {
                XCOORD = stateTabWidth*.13;                
                YCOORD += stateTabHeight*.32;
            }             
            uniqueNodes[j].toFront();
        }
    }
    
     /**
     * Helper method called by drawStates to bind the common stateNodes together
     */
    public void bindInitToEndStates(){

        for(int i=0; i<numUniqueStates; i++){
            statePane.getChildren().add(uniqueNodes[i]);
            for(int j=0; j<numAllStates; j++){
                Circle stateNode = stateNodes[j];
                Circle endNode = endNodes[j];  

                stateNodes[j].setVisible(false);
                if(!allInitStates[j].equalsIgnoreCase(initialUniqueStates[i])){
                } else {
                    stateNode.centerXProperty().bindBidirectional(uniqueNodes[i].centerXProperty());
                    stateNode.centerYProperty().bindBidirectional(uniqueNodes[i].centerYProperty());                    
                }                
                if(!allEndStates[j].equalsIgnoreCase(initialUniqueStates[i])){
                } else {
                    endNode.centerXProperty().bindBidirectional(uniqueNodes[i].centerXProperty());
                    endNode.centerYProperty().bindBidirectional(uniqueNodes[i].centerYProperty());                    
                }                            
                if(allEndStates[j].equalsIgnoreCase("acceptHalt")){     
                    
                    endNode.centerXProperty().bindBidirectional(acceptNode.centerXProperty());
                    endNode.centerYProperty().bindBidirectional(acceptNode.centerYProperty()); 
                    endNode.setFill(Color.GREEN);
                    endNode.setRadius(4.0f);
                }
                if(allEndStates[j].equalsIgnoreCase("rejectHalt")){   
                    
                    endNode.centerXProperty().bindBidirectional(rejectNode.centerXProperty());
                    endNode.centerYProperty().bindBidirectional(rejectNode.centerYProperty()); 
                    endNode.setFill(Color.RED);
                    endNode.setRadius(4.0f);
                }                             
            }            
        } 
    }
    
    /**
     * Helper method called by drawStates and others to anchor any accept/reject halt nodes
     */

    public void anchorAcceptRejectNodes(){
        double stateTabWidth = statePaneTab.widthProperty().get();
        double stateTabHeight = statePaneTab.heightProperty().get();
        
        Color acceptColor = Color.LIGHTGREEN;
        Color rejectColor = Color.RED;
        if(acceptCheck > 0){
            int k = 0;
            while(k<1){
                acceptNode = createDraggingCircle(30,stateTabHeight - 40, 
                        12, statePane, acceptColor);    
                acceptNode.setStrokeType(StrokeType.OUTSIDE);
                acceptNode.setStroke(acceptColor);
                statePane.getChildren().add(acceptNode);
                k++;
            }
        }
        if(rejectCheck > 0){
            int k = 0;
            while(k<1){
                rejectNode = createDraggingCircle(stateTabWidth - 70,
                        stateTabHeight - 40, 12, statePane, rejectColor);    
                rejectNode.setStrokeType(StrokeType.OUTSIDE);
                rejectNode.setStroke(rejectColor);
                statePane.getChildren().add(rejectNode);
                k++;
            }
        }  
    }
    
    /**
     * Helper method called by createDraggingCircle() to update the transitionNodes
     */
    private void updateTransLines(){
        for(int j=0; j<numAllStates;j++){
            statePane.getChildren().removeAll(transNodes[j],transLabels[j], 
                    transArcs[j], transLines[j]);
        }       
        drawTransitionLabels();
    }
    
    /**
     * Helper method called by drawStates to drop lines, arcs, and transLabels to pane
     */
    public void drawTransitionLabels(){           
            for (int j = 0; j< numAllStates; j++){
                transLabels[j] = new Label();
                transNodes[j] = new Circle();    
                transArcs[j] = new Arc();  
                
                    transLines[j] = connectStates(endLabels[j].getLabelFor(), 
                        stateLabels[j].getLabelFor(), allTransitions[j], j);
                    transLines[j].toBack();
                connected++;         
            }              
        }
    
     /**
     * Helper method called by connectStates to check for line collision
     * @param tNode Transition node to be checked against all (node at center of line)
     * @param nodeList all nodes that tNode is checked against
     * @return collisionDetected Boolean is lines are on top of each other
     */
    // COLLISION CHECKER
    private boolean checkLines(Line tNode, Line[] nodeList) {
        boolean collisionDetected = false;       
        for (Line static_bloc : nodeList) {
            if(static_bloc != null)
            if (static_bloc != tNode) {
                //static_bloc.setFill(Color.GREEN);

                if (tNode.getBoundsInParent().intersects(static_bloc.getBoundsInParent())) {
                    collisionDetected = true;
                }
            }
        }
        return collisionDetected;
    }
    

    /* Helper method called by connectStates to check for circle (node) collision
     * @param tNode Circle node to be checked against all
     * @param nodeList all nodes that tNode is checked against
     * @return collisionDetected Boolean is nodes are on top of each other
     */
    // COLLISION CHECKER
    private boolean checkBounds(Circle tNode, Circle[] nodeList) {
        boolean collisionDetected = false;       
        for (Circle static_bloc : nodeList) {
            if(static_bloc != null)
            if (static_bloc != tNode) {
                static_bloc.setFill(Color.GREEN);

                if (tNode.getBoundsInParent().intersects(static_bloc.getBoundsInParent())) {
                    collisionDetected = true;
                }
            }
        }
        return collisionDetected;
    }
    
    /**
     * Helper method called by drawTransitionLabels() to connect stateNodes to each other
     * @param n1 Node for initial stateNode
     * @param n2 Node for ending stateNode
     * @param transitionText String containing transition tuple attributes for display
     * @param index int for node index
     * @return line Line to be drawn between states
     */
    private Line connectStates(Node n1, Node n2, String transitionText, int index) {
        if (n1.getParent() != n2.getParent()) {
            throw new IllegalArgumentException("Nodes are in different containers");
        }
        
        Pane parent = (Pane) n1.getParent();
        
        
    // DO checkLines here to keep from dropping dupe lines ////////////////
        
        
        
        Line line = new Line();
        line.setFill(Color.ROYALBLUE);
        line.setOpacity(.7);
        line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = n1.getBoundsInParent();
            return b.getMinX() + b.getWidth() / 2 ;
        }, n1.boundsInParentProperty()));
        line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = n1.getBoundsInParent();
            return b.getMinY() + b.getHeight() / 2 ;
        }, n1.boundsInParentProperty()));
        line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = n2.getBoundsInParent();
            return b.getMinX() + b.getWidth() / 2 ;
        }, n2.boundsInParentProperty()));
        line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds b = n2.getBoundsInParent();
            return b.getMinY() + b.getHeight() / 2 ;
        }, n2.boundsInParentProperty()));
        
        line.getStrokeDashArray().addAll(2d, 2d, 2d, 2d, 2d);
        line.setStrokeDashOffset(5);
        line.setStrokeLineJoin(StrokeLineJoin.ROUND);
        line.setStrokeLineCap(StrokeLineCap.ROUND);       
        line.toBack();
        parent.getChildren().addAll(line);
        
        // Center-label line with transition data and dynamically keep centered      
        double transCenterX = (line.getStartX() + line.getEndX())/2;
        double transCenterY = (line.getStartY() + line.getEndY())/2;         
        Circle transNode = createDraggingCircle(transCenterX,transCenterY, 5, statePane, Color.BROWN); 
        //transNode.setVisible(true);
        if (index != -1){
            transNodes[index] = transNode;             
        }            
        
        Label transLabel = new Label(transitionText.replaceAll(", ", " ")); 
        transLabel.setTextFill(Color.web("#67112b"));
        transLabel.toBack();
        Arc relayArc;
        if (index != -1){           
            transLabels[index] = transLabel;
            if(allEndStates[index].equalsIgnoreCase(allInitStates[index])){
                transLabels[index].setText("        "+transLabels[index].getText());
                relayArc = drawArcback(transNode,transLabel, statePane);
                relayArc.layoutXProperty().bindBidirectional(transNode.centerXProperty());
                relayArc.layoutYProperty().bindBidirectional(transNode.centerYProperty());
                transNode.setVisible(false);
                if(checkBounds(transNode,transNodes)==true)
                    transLabel.setText("");
                else{ 
                    transLabel.setText(transLabels[index].getText());
                }
                transLabel.layoutXProperty().bindBidirectional(relayArc.layoutXProperty());
                transLabel.layoutYProperty().bindBidirectional(relayArc.layoutYProperty());
                relayArc.toBack();
                transArcs[index] = relayArc; 
                parent.getChildren().addAll(transArcs[index],transLabel);//,transNode);
            }
            else{                
                transLabel = transLabels[index];
                if(checkBounds(transNode,transNodes)==true){
                    transLabel.setText("\n"+transLabels[index].getText());
                }else{   
                    transLabel.setText(transLabels[index].getText()+"\n");
                }
                
    // OR here a checkLines can be done to drop dupe lines... //////////////////////////////////////////////////////////////////////////////////
                
                
                transLabel.layoutXProperty().bindBidirectional(transNode.centerXProperty());
                transLabel.layoutYProperty().bindBidirectional(transNode.centerYProperty()); 
                transNode.setVisible(false);
                parent.getChildren().addAll(transLabel,transNode);
            }
           
        }     
        return line;
    }
    
    // Time wrapup
    
    /**
     * Helper method called by connectStates to draw loopBacks for Si->Si transition
     * @param tNode Circle of state to be looped back
     * @param tLabel Label to be bound to transition loopBack
     * @param parent Pane to be applied to
     * @return arc Arc to be drawn when transition is bound by the same (initial) node
     */
    private Arc drawArcback(Circle tNode, Label tLabel, Pane parent){           
        Arc arc = new Arc();
        arc.setLayoutX(tNode.getCenterX()+tNode.getRadius());
        arc.setLayoutY(tNode.getCenterY()+tNode.getRadius());
        arc.setRadiusX(35.0f);
        arc.setRadiusY(35.0f);
        arc.setStartAngle(0.0f);
        arc.setLength(50.0f);
        arc.setType(ArcType.ROUND);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.BLACK);
        arc.setStrokeType(StrokeType.OUTSIDE);
        arc.setSmooth(true);
        arc.setOpacity(.7);
        arc.setStrokeLineJoin(StrokeLineJoin.ROUND);
        arc.setStrokeLineCap(StrokeLineCap.ROUND);
        
        return arc;
    }               
    
       /**
     * Helper method called by the new source file methods to reinitialize
     */
    public void clearStateTuples(){
        allInitStates = null;
        allTapes = null;
        allTransitions = null;
        allWriteTapes = null;
        allEndStates = null;
        currentStates = null;       
    }
    
     /**
     * Helper method called by drawStates to load tuple attributes to arrays
     * @param states StateTransition ArrayList
     */
    public void loadTupleArrays(ArrayList<StateTransition> states){
        final int numStates=states.size();
        allInitStates = new String[numStates];
        allTapes = new String[numStates];
        allTransitions = new String[numStates];
        allWriteTapes = new String[numStates];
        allEndStates = new String[numStates];       
        acceptCheck = 0;
        rejectCheck = 0;
        for(int i =0; i< states.size();i++){
            allInitStates[i] = states.get(i).getInitialState();
            allTapes[i] = states.get(i).getTape();
            allTransitions[i] = states.get(i).getReadToken() + ", " +
                            states.get(i).getWriteToken() + ", " +
                            states.get(i).getDirection();
            allWriteTapes[i] = states.get(i).getWriteTape();
            allEndStates[i] = states.get(i).getEndState();
            if (allEndStates[i].equalsIgnoreCase("acceptHalt"))
                acceptCheck++;
            if (allEndStates[i].equalsIgnoreCase("rejectHalt"))
                rejectCheck++;
            
            uniqueStateSet = new HashSet<>(Arrays.asList(allInitStates));
            initialUniqueStates = new String[uniqueStateSet.size()];
            uniqueStateSet.toArray(initialUniqueStates);
            numUniqueStates = initialUniqueStates.length;
            numAllStates = allInitStates.length;        
        }         
    }
    
      /**
     * Helper method called by drawStates to set node colors by Tape number
     */
    public void loadTapeColors(){
        tapeColor = new Color[numAllStates];
        for(int i=0;i<numAllStates; i++){
            tapeSelection = allTapes[i];            
            if(tapeSelection.equalsIgnoreCase("t1"))
                tapeColor[i] = Color.AQUA;
            if(tapeSelection.equalsIgnoreCase("t2"))
                tapeColor[i] = Color.VIOLET;
            if(tapeSelection.equalsIgnoreCase("t3"))
                tapeColor[i] = Color.TAN;
        }
    }
    
      /**
     * Helper method called by drawStates to draw the tape color legend nodes/text at bottom
     */
    public void drawTapeLegend(){
        Stop[] stops1 = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.AQUA)};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops1);
        Stop[] stops2 = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.VIOLET)};
        LinearGradient lg2 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops2);
        Stop[] stops3 = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.TAN)};
        LinearGradient lg3 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops3);
        double stateTabWidth = statePaneTab.widthProperty().get();
        double stateTabHeight = statePaneTab.heightProperty().get();
        Circle oneAqua = new Circle(stateTabWidth*.35-10,stateTabHeight-10.0,5,Color.AQUA);
        Circle twoViolet = new Circle(stateTabWidth*.5-10,stateTabHeight-10.0,5,Color.VIOLET);
        Circle threeTan = new Circle(stateTabWidth*.65-10,stateTabHeight-10.0,5,Color.TAN);
        oneAqua.setFill(lg1);
        twoViolet.setFill(lg2);
        threeTan.setFill(lg3);
        Text legend1 = new Text();
        Text legend2 = new Text();
        Text legend3 = new Text();
        legend1.setText("  Tape 1");
        legend2.setText("  Tape 2");
        legend3.setText("  Tape 3");
        legend1.setLayoutX(stateTabWidth*.35-10);
        legend1.setLayoutY(stateTabHeight-5.0);
        legend2.setLayoutX(stateTabWidth*.5-10);
        legend2.setLayoutY(stateTabHeight-5.0);
        legend3.setLayoutX(stateTabWidth*.65-10);
        legend3.setLayoutY(stateTabHeight-5.0);       
        statePane.getChildren().addAll(oneAqua,twoViolet,threeTan);
        statePane.getChildren().addAll(legend1,legend2,legend3);      
    }
    
    /* Helper method called by various drawStates Helper Methods to create the states
     * Instantiates a Circle that is draggable by mouse
     * @param radius Double for circleStateNode radius
     * @param x Double for XCOORD @ center
     * @param y Double for YCOORD @ center
     * @param parent Pane to be applied to
     * @param fill Color to be applied to circleNode
     * @return c Circle returned that is draggable
     */
    private Circle createDraggingCircle(double radius, double x, double y, Pane parent, Color fill) {
        Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, fill)};
        LinearGradient lg1 = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        double stateTabWidth = statePaneTab.widthProperty().get();
        double stateTabHeight = statePaneTab.heightProperty().get();
            
        Circle c = new Circle(radius, x, y, fill);        
        ObjectProperty<Point2D> mouseLoc = new SimpleObjectProperty<>();
        c.setOnMousePressed(e -> mouseLoc.set(new Point2D(e.getX(), e.getY())));        
        c.setOnMouseDragged(e -> {
            double deltaX = e.getX() - mouseLoc.get().getX();
            double deltaY = e.getY() - mouseLoc.get().getY();
            if((c.getCenterX() + deltaX)>0 && (c.getCenterX() + deltaX) < stateTabWidth)
                c.setCenterX(c.getCenterX() + deltaX);
            else
                return;
            if((c.getCenterY() + deltaY)>0 && (c.getCenterY() + deltaY) < stateTabHeight)
                c.setCenterY(c.getCenterY() + deltaY);
            else
                return;
            
            mouseLoc.set(new Point2D(e.getX(), e.getY()));
            updateTransLines();
        });
        c.setFill(lg1);
        c.setStrokeType(StrokeType.OUTSIDE);
        c.setStroke(lg1);//Color.BLACK);                
        c.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
        return c ;
    }

    /**
     * Helper method used to redraw the state diagram at resize events
     * @param states StateTransition ArrayList containing the state portion of tuples
     */
    private void redraw(ArrayList<StateTransition> states) {
        if (states != null) {
           statePane.getChildren().clear();
           drawStates(states);
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
    
    /**
    * Returns the current code Font settings to allow easy text formatting
    * @return Font current font
    */
    private Font getCurrentCodeFontSettings() {
        if (isCodeBold && isCodeItalic) {
            return Font.font(codeFamily, FontWeight.BOLD, FontPosture.ITALIC, codeSize);
        }
        else if (isCodeBold) {
            return Font.font(codeFamily, FontWeight.BOLD, codeSize);
        }
        else if (isCodeItalic) {
            return Font.font(codeFamily, FontPosture.ITALIC, codeSize);
        }
        else {
            return Font.font(codeFamily, codeSize);
        }   
    }
    
    
    /*** Methods for displaying various dialogs are in this area
    
    /** Shows an auto stop dialog if the program is asked to transition to a 
    * state that does not exist in the input file
    * @param lookingForState state that was not found
    */
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

    /** Show the appropriate user input dialog based on current number of tapes
     * 
     * @param numTapes number of active tapes
     * @return String[] input 
     */
    public String[] showInputDialog(int numTapes) {
        String[] input = {"____", "_____", "_____"};
        //build and show the appropriate dialog
        switch (numTapes) {
            case 1:
            {
                Dialog<String []> dialog = new Dialog<>();
                dialog.setTitle("Tape One Input");
                dialog.setHeaderText("Initial Tape Input");
                dialog.setContentText("Enter initial Tape input for tape 1");
                
                Label label1 = new Label("Tape One: ");
                TextField text1 = new TextField();
                
                GridPane grid = new GridPane();
                grid.add(label1, 1, 1);
                grid.add(text1, 2, 1);
                dialog.getDialogPane().setContent(grid);
                Platform.runLater(()->text1.requestFocus());
                
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
                dialog.setTitle("Tape One and Two Input");
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
                Platform.runLater(()->text1.requestFocus());
                
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
                dialog.setTitle("Tape One, Two, and Three Input");
                dialog.setHeaderText("Initial Tape Input");
                dialog.setContentText("Enter initial Tape input for tapes 1, 2, and 3");
                
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
                Platform.runLater(()->text1.requestFocus());
                
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
                break;
            }
            
        }
        if (DEBUG) {System.out.println(Arrays.toString(input));}
        return input;
    }

    /**
     * Shows mode change warning in the event that machine mode needed to be 
     * changed. For example, a user loaded a program that references a tape
     * they did not activate
     */
    public void showModeChangeWarning() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("The Machine Mode has been changed");
        alert.getDialogPane().setContent( new Label("The Machine detected a reference in your program to a currently non-active tape."
                + "\n The requested tape(s) has/have been activated for you automatically. \nTo change their content, simply click the clear button of the tape"
                + "\n If you feel this was a mistake, please check your input file."));
        alert.showAndWait();
    }

    /**
     * Helper method called by tape pane two listener on expand or collapse
     */
    private void resetTapePaneTwoHeight() {
        if (tapeTwoPane.isExpanded()) {
            tapeTwoPane.setPrefHeight(99);
        }
        else {
            tapeTwoPane.setPrefHeight(0);
        }
    }

    /**
     * Helper method called by tape pane three listener on expand or collapse
     */
    private void resetTapeThreePaneHeight() {
        if (tapeThreePane.isExpanded()) {
            tapeThreePane.setPrefHeight(99);
        }
        else {
            tapeThreePane.setPrefHeight(0);
        }
    }
//
//    /**
//     * Helper method that automatically repositions the tape one clear button on resizing
//     */
//    private void adjustTapeOneClearButton() {
//        tapeOnePane.setGraphicTextGap(tapeOnePane.getGraphicTextGap() + );
//    }
//
//    /**
//     * Helper method that automatically repositions the tape three clear button on resizing
//     */
//    private void adjustTapeThreeClearButton() {
//        tapeThreePane.setGraphicTextGap(tapeThreePane.widthProperty().intValue() - 220);
//    }
//
//    /**
//     * Helper method that automatically repositions the tape two clear button on resizing
//     */
//    private void adjustTapeTwoClearButton() {
//        tapeTwoPane.setGraphicTextGap(tapeTwoPane.widthProperty().intValue() - 220);
//    }
    
    /*** End dialogs section **/
    
    private class ChangeListenerImpl implements ChangeListener {

        public ChangeListenerImpl() {
        }

        @Override
        public void changed(ObservableValue arg0, Object arg1, Object arg2) {
            int speed = (int) speedSlider.getValue();
            if (speed == 100) {
                changeLabel.textProperty().setValue("Instant");
            }
            else {
                changeLabel.textProperty().setValue(String.valueOf(speed));
            }
            interp.setRunSpeed(speed);
            if (DEBUG) {System.out.println("Speed slider = " + getSpeed());}  //output speed changes
        }
    }
}
