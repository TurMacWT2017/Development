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
import java.util.HashSet;
import java.util.Optional;
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
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

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

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

import javafx.scene.paint.Color;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javafx.scene.text.TextFlow;
import model.StateTransition;
import controller.FontControl;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Accordion;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.FontPosture;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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
    private static int XCOORD = 72;
    private static int YCOORD = 72;
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
    @FXML private MenuItem about;
    @FXML private MenuItem langref;
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
    public static ArrayList<StateTransition> currentStates;
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
            //clear the tapes of any old content
            tapeOne.getChildren().clear();
            tapeTwo.getChildren().clear();
            tapeThree.getChildren().clear();
            codeViewTab.getChildren().clear();
            String input = controller.openFile(selectedFile);
            //when initializing interpreter, give it both an input and a view controller (this) to work with
            System.out.println("Number of tapes was" + tapes);
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
    
    @FXML
    private void setTwoTapeMode(ActionEvent event) {
        tapeTwoPane.setExpanded(true);
        tapeTwoPane.setVisible(true);
        tapeThreePane.setExpanded(false);
        tapeThreePane.setVisible(false);
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
    
    @FXML
    private void setThreeTapeMode(ActionEvent event) {
        tapeTwoPane.setExpanded(true);
        tapeTwoPane.setVisible(true);
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
    @FXML
    public void activateTapeTwo() {
        tapeTwo.getChildren().clear();
        tapeTwoPane.setExpanded(true);
        tapeTwoPane.setVisible(true);
        tapes = 2;
    }
    
    @FXML
    public void activateTapeThree() {
        tapeThree.getChildren().clear();
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
        stage.setHeight(400);
        stage.show();
        
        root.getChildren().add(webView);
    }
    
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
        canvas.widthProperty().addListener(observable -> redraw(currentStates));
        canvas.heightProperty().addListener(observable -> redraw(currentStates));
        
        canvas.widthProperty().bind(
                       diagramDisplay.widthProperty());
        canvas.heightProperty().bind(
                       diagramDisplay.heightProperty());        
    }    

    public int getSpeed(){
        return (int)speedSlider.getValue();
    }
    
public void launchStateWindow(){
        StateDiagram diagram = new StateDiagram();
        
        if (fileLoaded) {
            Stage stage;
            System.out.println("Making code window");
            stage = new Stage();
            ScrollPane layout = new ScrollPane();
           
            layout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            layout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);        
            layout.setFitToHeight(true);

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
            //set the scene and its owner
            stage.setScene(new Scene(layout, 450, 450));
            stage.setTitle("State Diagram Window");
            stage.initOwner(diagramDisplay.getScene().getWindow());
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 4); 
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 8);
            stage.show();
            diagram.start(stage);                 
            System.out.println("Making state diagram window");
            }
        else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Please load a program first");
            alert.showAndWait();
        }       
    }
    
    private Line connectStates(Node n1, Node n2) {
        if (n1.getParent() != n2.getParent()) {
            throw new IllegalArgumentException("Nodes are in different containers");
        }
        Pane parent = (Pane) n1.getParent();
        Line line = new Line();
        line.setFill(Color.BLUE);
        line.setOpacity(.5);
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
        
        parent.getChildren().add(line);
        return line;
    }
    
    public void drawStates(ArrayList<StateTransition> states) {
        currentStates = states;        
        StateDiagram diagram = new StateDiagram();
        Stage stage = new Stage();
        Pane pane = new Pane();
        Line line = new Line();
        final int numStates=states.size();
        String[] allInitStates = new String[numStates];
        String[] allTransitions = new String[numStates];
        String[] allEndStates = new String[numStates];
        
        // LOAD the initial state, end state, and transition arrays
        for(int i =0; i< numStates;i++){
            allInitStates[i] = states.get(i).getInitialState();
            allTransitions[i] = states.get(i).getReadToken() + ", " +
                            states.get(i).getWriteToken() + ", " +
                            states.get(i).getDirection();
            allEndStates[i] = states.get(i).getEndState();
            //System.out.println(allInitStates[i] + " " + allTransitions[i] + " " + allEndStates[i]);
        }     
        
        // Collapse duplicate-containing initialState array into non-duplicated array
        Set<String> uniqueStateSet = new HashSet<>(Arrays.asList(allInitStates));
        String[] initialUniqueStates = new String[uniqueStateSet.size()];
        String[] initialStates = allInitStates;//new String[allInitStates.length];
        String[] endStates = allEndStates;//new String[allEndStates.length];
        uniqueStateSet.toArray(initialUniqueStates);
        int numUniqueStates = initialUniqueStates.length;
        int numAllStates = allInitStates.length;
            //System.out.println("allInit len = " + allInitStates.length);
            //System.out.println("allTran len = " + allTransitions.length);
            //System.out.println("allEnds len = " + allEndStates.length);
            //System.out.println("noDupes len = " + numUniqueStates);
        
        Circle[] stateNodes = new Circle[numAllStates]; //numUniqueStates
        Circle[] uniqueNodes = new Circle[numUniqueStates];
        Circle[] endNodes = new Circle[numAllStates];
        Label[] stateLabels = new Label[numAllStates];
        Label[] uniqueLabels = new Label[numUniqueStates];
        Label[] endLabels = new Label[numAllStates];
        Circle startNode = createDraggingCircle(XCOORD-50, YCOORD, 5, pane, Color.GRAY);
        
        // starting node (pre-first-state)
        Label startLabel = new Label();       
        startLabel.setText("start");
        startLabel.layoutXProperty().bindBidirectional(startNode.centerXProperty());
        startLabel.layoutYProperty().bindBidirectional(startNode.centerYProperty());
        pane.getChildren().addAll(startNode,startLabel);
            //System.out.println("stateNodes len = " + stateNodes.length);
            //System.out.println("stateLabel len = " + stateLabels.length);
                
        // Non-duplicate initialStates generated into the initial Nodes
        for (int j=0; j< numUniqueStates; j++){       
            uniqueNodes[j] = createDraggingCircle(XCOORD, YCOORD, 15, pane, Color.GRAY);
            uniqueNodes[j].setOpacity(.2);   
            uniqueNodes[j].setStroke(Color.BLACK);
            uniqueNodes[j].setSmooth(true);
            if (XCOORD + 150 < canvas.getWidth())
            {
                XCOORD += 115;
            }
            else
            {
                XCOORD = 120;
                YCOORD += 150;
            }        
        }
        
        // DRAW acceptHalt and rejectHalt nodes to bind to
        Circle acceptNode = createDraggingCircle(150,500, 18, pane, Color.GREEN);
        Circle rejectNode = createDraggingCircle(450,500, 18, pane, Color.RED);
        acceptNode.setOpacity(.5);
        rejectNode.setOpacity(.5);
        acceptNode.setStroke(Color.BLACK);
        rejectNode.setStroke(Color.BLACK);
        acceptNode.setSmooth(true);
        rejectNode.setSmooth(true);
        pane.getChildren().addAll(acceptNode, rejectNode);
        
        // BIND all Labels to their corresponding stateNodes
        for (int j = 0; j < numAllStates; j++){
            Label stateLabel = new Label(initialStates[j]);
            Label endLabel = new Label(endStates[j]);            
            
            stateNodes[j] = createDraggingCircle(0,0, 4, pane, Color.BLUE);
            endNodes[j] = createDraggingCircle(XCOORD, YCOORD+72, 15, pane, Color.GRAY);            
            stateNodes[j].setOpacity(.5);
            endNodes[j].setOpacity(.2);            
            stateLabel.layoutXProperty().bindBidirectional(stateNodes[j].centerXProperty());
            stateLabel.layoutYProperty().bindBidirectional(stateNodes[j].centerYProperty());
            endLabel.layoutXProperty().bindBidirectional(endNodes[j].centerXProperty());
            endLabel.layoutYProperty().bindBidirectional(endNodes[j].centerYProperty());               
            //stateLabel.setMnemonicParsing(true);
            stateLabel.setLabelFor(stateNodes[j]);
            //endLabel.setMnemonicParsing(true);
            endLabel.setLabelFor(endNodes[j]);
            //System.out.println("getLabelFor = " + stateLabel.getLabelFor());
            stateLabels[j] = stateLabel;
            endLabels[j] = endLabel;
            
            if (XCOORD + 150 < canvas.getWidth())
            {
                XCOORD += 115;
            }
            else
            {
                XCOORD = 150;
                YCOORD += 50;
            }       
            stateNodes[j].setStroke(Color.BLACK);
            endNodes[j].setStroke(Color.BLACK);
            stateNodes[j].setSmooth(true);
            endNodes[j].setSmooth(true);
            pane.getChildren().addAll(stateNodes[j],stateLabel, endNodes[j],endLabel);
        }         
        
        Arc arc = new Arc();
arc.setCenterX(450.0f);
arc.setCenterY(700.0f);
arc.setRadiusX(25.0f);
arc.setRadiusY(25.0f);
arc.setStartAngle(45.0f);
arc.setLength(270.0f);
arc.setType(ArcType.CHORD);
pane.getChildren().add(arc);
        //System.out.println("numUnique = " + numUniqueStates);
        //System.out.println("numAll = " + numAllStates);     
        
        // BIND all matching initial and end states into junctions
        for(int i=0; i<numUniqueStates; i++){
            pane.getChildren().add(uniqueNodes[i]);
            for(int j=0; j<numAllStates; j++){
                Circle stateNode = stateNodes[j];
                Circle endNode = endNodes[j];
                if(!allInitStates[j].equalsIgnoreCase(initialUniqueStates[i])){
                } else {
                    stateNode.centerXProperty().bindBidirectional(uniqueNodes[i].centerXProperty());
                    stateNode.centerYProperty().bindBidirectional(uniqueNodes[i].centerYProperty());                    
                }
                if(!endStates[j].equalsIgnoreCase(initialUniqueStates[i])){
                } else {
                    endNode.centerXProperty().bindBidirectional(uniqueNodes[i].centerXProperty());
                    endNode.centerYProperty().bindBidirectional(uniqueNodes[i].centerYProperty());                    
                }            
                
                if(endStates[j].equalsIgnoreCase("acceptHalt")){                 
                    endNode.centerXProperty().bindBidirectional(acceptNode.centerXProperty());
                    endNode.centerYProperty().bindBidirectional(acceptNode.centerYProperty()); 
                    endNode.setFill(Color.GREEN);
                    endNode.setRadius(1);
                }
                if(endStates[j].equalsIgnoreCase("rejectHalt")){                 
                    endNode.centerXProperty().bindBidirectional(rejectNode.centerXProperty());
                    endNode.centerYProperty().bindBidirectional(rejectNode.centerYProperty()); 
                    endNode.setFill(Color.RED);
                    endNode.setRadius(1);
                }                   
            // HERE  WHERE THE ACCEPT HALT HAS TO BE BOUND TO EACH OTHER             

            }
        }   
        
        // DRAW EDGES from state to state per code
        //  Labels are bound to Nodes, Nodes are connected by Labels
        connectStates(startNode, stateNodes[0]);
        ObjectProperty<Node> lastUnconnectedNode = new SimpleObjectProperty<>();
        Circle prevNode = new Circle();
        Label prevLabel = new Label();
        int connected = 0;  
        for (int j = 0; j< numAllStates; j++){
                connectStates(endLabels[j].getLabelFor(), stateLabels[j].getLabelFor());
                connected++;          
        }       
            //System.out.println("numEdges = " + connected);
            
        // CREATE and DISPLAY the state diagram window loaded with source file load
        Scene scene = new Scene(pane, 600, 600);
        ScrollPane layout = new ScrollPane();
           
        layout.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        layout.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);        
        layout.setFitToHeight(true);

        stage.setTitle("State Diagram Window");
        stage.initOwner(diagramDisplay.getScene().getWindow());
        
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 4); 
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 8);
        diagram.start(stage);
        stage.setScene(scene);
        stage.show();  
    }
    
    // Instantiates a Circle that is draggable by mouse
    private Circle createDraggingCircle(double radius, double x, double y, Pane parent, Color fill) {
        Circle c = new Circle(radius, x, y, fill);        
        ObjectProperty<Point2D> mouseLoc = new SimpleObjectProperty<>();
        c.setOnMousePressed(e -> mouseLoc.set(new Point2D(e.getX(), e.getY())));        
        c.setOnMouseDragged(e -> {
            double deltaX = e.getX() - mouseLoc.get().getX();
            double deltaY = e.getY() - mouseLoc.get().getY();
            c.setCenterX(c.getCenterX() + deltaX);
            c.setCenterY(c.getCenterY() + deltaY);
            mouseLoc.set(new Point2D(e.getX(), e.getY()));
        });
        c.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
        //parent.getChildren().add(c);
        return c ;
    }

    private void redraw(ArrayList<StateTransition> states) {
        if (states != null) {
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
    
    /*** Methods for displaying various dialogs are in this area ********/

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

    public void showModeChangeWarning() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("The Machine Mode has been changed");
        alert.getDialogPane().setContent( new Label("The Machine detected a reference in your program to a currently non-active tape."
                + "\n The requested tape(s) has/have been activated for you automatically. \nTo change their content, simply click the clear button of the tape"
                + "\n If you feel this was a mistake, please check your input file."));
        alert.showAndWait();
    }
    
    /*** End dialogs section **/
    
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
