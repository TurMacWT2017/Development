package model;

import controller.MachineViewController;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * This class represents a turing machine interpreter
 * The interpreter is responsible for tokenizing the file, and run the compiled
 * transition list "Program" provided by the parser
 * @author Landon Bressler 
 * @author Nick Ahring
 */
public class Interpreter
{
    private String[] tokens;
    private ArrayList<String> tokenList;
    //Initial input for tapes
    private String initialInput = "______";
    private String initialInput2 = "______";
    private String initialInput3 = "______";
    private String inputCode;
    private final String delim = ",|;";
    private final StringBuilder errorReport = new StringBuilder();
    private MachineViewController view; 
    private boolean errorsPresent = false;    
    /* keeps track of current state of interpreter, possible options are 
    HALT, RUN, PAUSE, STEP */
    private String interpRunState = "HALT";
    private String interpState;
    public ArrayList<StateTransition> transitions;
    private Parser par;
    private int controlPointer = 0;
    private int speed = 50;
    private int stepCount = 0;
    //used to stop the thread if needed.
    private static boolean notInterrupted = true;
    private boolean turnedOff;
    private boolean reset;
    //interpreter thread andd thread monitor
    private InterpreterThread interpThread;
    private final Object monitor = new Object();
    //tape related variables
    private Tape tapeOne;
    private Tape tapeTwo;
    private Tape tapeThree;
    private int numTapes;
    private boolean animationEnabled;
    //DEBUG
    private final boolean DEBUG = false;
    
    /** Default interpreter constructor
    * 
    * <pre> Pre condition: Interpreter does not yet exist </pre>
    * <pre> Post condition: New Interpreter created </pre> 
     * @param input the String representation of the *.tm program file
     * @param view an instance of the view controller this interpreter will use
     * @param tapes number of tapes
    */
    public Interpreter(String input, MachineViewController view, int tapes) 
    {
        this.turnedOff = false;
        this.reset = false;
        this.animationEnabled = true;
        this.numTapes = 1;
        this.view = view;
        numTapes = tapes;
        tokenize(input);
        
        if (errorsPresent == false) 
        {
            //if the program referenced selected a tape the user forgot to activate
            //this will take care of that, tokenize will have changed the numTapes to be correct already
            if (numTapes != tapes) {
                if (numTapes == 2) {
                    view.activateTapeTwo();
                }
                else if (numTapes == 3) {
                    view.activateTapeTwo();
                    view.activateTapeThree();
                }
                view.showModeChangeWarning();
            }
            par = new Parser(this);
            transitions = par.compile();
            //build the required tapes
            switch (numTapes) {
                case 1:
                    tapeOne = new Tape(initialInput);
                    view.setInitialTapeContent(initialInput, 1);
                    break;
                case 2:
                    tapeOne = new Tape(initialInput);
                    view.setInitialTapeContent(initialInput, 1);
                    tapeTwo = new Tape(initialInput2);
                    view.setInitialTapeContent(initialInput2, 2);
                    break;
                case 3:
                    tapeOne = new Tape(initialInput);
                    view.setInitialTapeContent(initialInput, 1);
                    tapeTwo = new Tape(initialInput2);
                    view.setInitialTapeContent(initialInput2, 2);
                    tapeThree = new Tape(initialInput3);
                    view.setInitialTapeContent(initialInput3, 3);
                    break;
                default:
                    tapeOne = new Tape(initialInput);
                    view.setInitialTapeContent(initialInput, 1);
                    break;
            }
            //statePane.getChildren().add(drawStates(transitions));
            view.drawStates(transitions);         
            // Set interpreter state to be the start state of the program
            interpState = transitions.get(0).getInitialState().trim();
            view.updateState(interpState);
            view.updateStepCount(0);
        }
        
    }
    
    /**
     * Used for setting the view controller of this interpreter to a specific view controller
     * <pre> Pre-condition: interpreter requires view controller </pre>
     * <pre> Post-condition: view controller has been set </pre>
     * @param view the view controller to be used
     */
    public void setViewController(MachineViewController view) {
        this.view = view;
    }
    
    /**
     * Sets the run speed of the interpreter
     * <pre> Pre-condition: run speed has been changed </pre>
     * <pre> Post-condition: new run speed will be set </pre>
     * @param newSpeed new speed
     */
    public void setRunSpeed(int newSpeed) {
        speed = newSpeed;
        if (speed == 100) {
            animationEnabled = false;
        }
        else {
            animationEnabled = true;
        }
    }
    /**
     * Used for changing the number of tapes the interpreter is working with
     * <pre> Pre-condition: number of tapes change has been requested </pre>
     * <pre> Post-condition: number of tapes changed appropriately </pre>
     * @param tapes number of tapes
     */
    public void setNumberOfTapes(int tapes) {
        numTapes = tapes;
    }
    
    /** Enables animation. This will determine whether the interpreter
     * will request tape animations during run (enabled) or only at the end (disabled)
     * <pre> Pre-condition: animation has been requested enabled </pre>
     * <pre> Post-condition: animations enabled will be true </pre>
     */
    public void enableAnimation() {
        animationEnabled = true;
    }
    
    /** Disables animation. This will determine whether the interpreter
     * will request tape animations during run (enabled) or only at the end (disabled)
     * <pre> Pre-condition: animations have been requested disabled </pre>
     * <pre> Post-condition: animations enabled will be false </pre>
     */
    public void disableAnimation() {
        animationEnabled = false;
    }
    
    /** Starts up the interpreter 
     * <pre> Pre-condition: Interpreter not started</pre>
     * <pre> Post condition: Interpreter and interpreter thread started</pre>
     * @throws model.InterpreterException interpreter exception
     */
    public void start() throws InterpreterException { 
        
        turnedOff = false;
        reset = false;
        
        //set the view to the start state
        view.setStartState();
        interpThread = new InterpreterThread();

        
    }
    
    /** Pop-up dialog box for when the program file does not contain input or
     *  the user clears the tape input
     * <pre> Pre-condition: input is required to continue </pre>
     * <pre> Post-condition: UI has received user input </pre>
     */
    public void popup() {
        String[] input = view.showInputDialog(numTapes);
        if (!input[0].equals("")) {
            initialInput = input[0];
        }
        else {
            initialInput = "______";
        }
        if (numTapes == 2) {
            if (!input[1].equals("")) {                
                initialInput2 = input[1];
            }
            else {
                initialInput2 = "______";
            }
        }
        if (numTapes == 3) {
            if (!input[1].equals("")) {
               initialInput2 = input[1];
            }
            else {
               initialInput2 = "______";
            }
            if (!input[2].equals("")) {
               initialInput3 = input[2];
            }
            else {
               initialInput3 = "______";
            }
        }
        
    }
    
    
    /** Parses the input program into tokens that can be interpreted into the correct symbols
     * <pre> Pre-condition: input has been provided </pre>
     * <pre> Post-condition: input will be tokenized and ready to parse </pre>
     * 
     * @param input the String representation of the *.tm program file
     */
    private void tokenize(String input)
    {
        int tupleNum = 0;
        String errorString;
       
        // if initial tape input string supplied by .tm program
        if (input.toLowerCase().startsWith("input: ")) 
        {
            if (DEBUG) {System.out.println("Initial input provided");}
            //grab the value, set it, then nix the line
            int colon = input.indexOf(":");
            int semicolon = input.indexOf(";");
            String initialInputLine = input.substring(colon+1, semicolon).trim();
            if (initialInputLine.contains(",")) {
                String [] splitLine = initialInputLine.split(delim);
                initialInput = splitLine[0];
                if (splitLine.length == 2) {
                    initialInput2 = splitLine[1].trim();
                }
                if (splitLine.length == 3) {
                    initialInput3 = splitLine[2].trim();
                }
            }
            else {
                initialInput = initialInputLine;
            }
            //nix the line
            String firstLine = input.substring(0, semicolon+1);
            input = input.replace(firstLine, "");
        }
        else
        {   
            popup();
        }
        
        //store the input code before removing the new line, in case the 
        //formatted version is needed later
        inputCode = input;
        if (DEBUG) {System.out.println(input);}
        input = input.replaceAll("\n", "");
        tokens = input.split(delim);
        int tokensLength = tokens.length;
        
        for (int i = 0; i < tokensLength; i++)
        {
            // Get the current tape - if left blank, default to tape 1
            if (i%7 == 0)
            {
                tupleNum++;
                if (tokens[i].equals(""))
                {
                    tokens[i] = "t1";
                    //System.out.println("\nTape token: " + tokens[i] + " on line " + tupleNum);
                }
                if (tokens[i].equalsIgnoreCase("t1")) 
                {
                    tokens[i] = "t1";
                    if (DEBUG) {System.out.println("\nTape token: " + tokens[i] + " on line " + tupleNum);}
                }
                else if (tokens[i].equalsIgnoreCase("t2"))
                {
                    tokens[i] = "t2";
                    if ((view.getCurrentMode() != 2) && (view.getCurrentMode() != 3)) {
                        numTapes = 2;
                    }
                    if (DEBUG) {System.out.println("\nTape 2 token: " + tokens[i] + " on line " + tupleNum);}
                }
                else if (tokens[i].equalsIgnoreCase("t3"))
                {
                    tokens[i] = "t3";
                    if (view.getCurrentMode() != 3) {
                        numTapes = 3;
                    }
                    if (DEBUG) {System.out.println("\nTape 3 token: " + tokens[i] + " on line " + tupleNum);}
                }
                else 
                {
                    errorString = "Error on tuple " + tupleNum + ": Invalid tape " + tokens[i];
                    errorReport.append(errorString);
                    errorsPresent = true;
                }
            }

            // Get the initial state
            if (i%7 == 1)
            {
                tokens[i] = tokens[i].trim();
                //System.out.println("Initial state token: " + tokens[i] + " on line " + tupleNum);
            }

            // Get the token being read
            if (i%7 == 2)
            {
                String token = tokens[i].trim();
                if (token.length() > 1) {
                    errorString = "Error on tuple " + tupleNum + ": maximum length of read character is 1, provided character was " + tokens[i];
                    errorReport.append(errorString);
                    errorsPresent = true;
                }
                tokens[i] = token;
                
            }

            // Write desired token
            if (i%7 == 3)
            {
                String token = tokens[i].trim();
                if (token.length() > 1) {
                    errorString = "Error on tuple " + tupleNum + ": maximum length of write character is 1, provided character was " + tokens[i];
                    errorReport.append(errorString);
                    errorsPresent = true;
                }
                tokens[i] = token;
            }

            // Get the direction the read/write head needs to move
            if (i%7 == 4)
            {  
                String token = tokens[i].trim();
                StringBuilder dir = new StringBuilder();
                if (DEBUG) {
                    System.out.println("Direction token: " + tokens[i] + " on tuple " + tupleNum + "\n");
                    System.out.println("Token length was" + token.length());
                }
                if (token.matches("([R|r|\\>])(.*)"))
                {
                    dir.append("R");
                }
                else if (token.matches("([L|l|\\<])(.*)"))
                {
                    dir.append("L");
                }
                else if (token.matches("\\*(.*)"))
                {
                    dir.append("S");
                }
                else 
                {
                    errorString = "\nInvalid direction on tuple " + tupleNum + ":  "+ tokens[i] + " ,check your first specified direction";
                    errorReport.append(errorString);
                    errorsPresent = true;
                }
                //check for a 2nd direction
                if (token.length() >= 2) {
                    if (token.length() == 2 && numTapes != 3) {numTapes = 2;}
                    if (token.matches("(\\S{1})([R|r|\\>])(\\S{0,1})"))
                    {
                        dir.append("R");
                    }
                    else if (token.matches("(\\S{1})([L|l|\\<])(\\S{0,1})"))
                    {
                        dir.append("L");
                    }
                    else if (token.matches("(\\S{1})(\\*)(\\S{0,1})"))
                    {
                        dir.append("S");
                    }
                    else 
                    {
                        errorString = "\nInvalid direction on tuple " + tupleNum + ":  "+ tokens[i] + " ,check your 2nd specified direction";
                        errorReport.append(errorString);
                        errorsPresent = true;
                    }
                }
                //check for a 3nd direction
                if (token.length() == 3) {
                    numTapes = 3;
                    if (token.matches("(\\S)(\\S)([R|r|\\>])"))
                    {
                        dir.append("R");
                    }
                    else if (token.matches("(\\S)(\\S)([L|l|\\<])"))
                    {
                        dir.append("L");
                    }
                    else if (token.matches("(\\S)(\\S)(\\*)"))
                    {
                        dir.append("S");
                    }
                    else 
                    {
                        errorString = "\nInvalid direction on tuple " + tupleNum + ":  "+ tokens[i] + " ,check your third specified direction";
                        errorReport.append(errorString);
                        errorsPresent = true;
                    }
                }
                tokens[i] = dir.toString();
            }

            // Get the write tape, or * if no write
            if (i%7 == 5)
            {
                String token = tokens[i].trim();
                if (!token.matches("(t1|t2|t3|\\*)")) {
                    errorString = "\nInvalid write tape specified on tuple " + tupleNum + ":  "+ tokens[i] + "\n";
                    errorReport.append(errorString);
                    errorsPresent = true;
                }
                else {
                    //check to make sure we are in the right mode, and user didn't forget to switch the mode
                    //if they did, after tokenize it will be switched for them.
                    if (token.equalsIgnoreCase("t2")) {
                        if ((view.getCurrentMode() != 2) && (view.getCurrentMode() != 3)) {
                            numTapes = 2;
                        }
                    }
                    else if (token.equalsIgnoreCase("t3")) {
                        if (view.getCurrentMode() != 3) {
                            numTapes = 3;
                        }
                    }
                    tokens[i] = token;
                }
                
                if (DEBUG) {System.out.println("End state token: " + tokens[i] + " on tuple " + tupleNum + "\n");}
            }
            //get the end state taken
            if (i%7 == 6) {
                
                tokens[i] = tokens[i].trim();
            }
        }
        if (DEBUG) {
            System.out.println("Initial input provided: " + initialInput);
            System.out.println("Number of tapes: " + numTapes);
        }
    }
    
    /** Runs the current program 
    *   <pre> Pre condition: Program will not be in run state </pre>
    *   <pre> Post condition: Program will be running </pre>
    */
    public void run() 
    {
        // don't create an interpreter thread if one is already running
        if (interpThread!= null && !interpThread.isAlive()) {
            view.setStartState(); 
            notInterrupted = true;
            interpThread = new InterpreterThread();
            interpThread.start();
        }
    }
    
    /**  Steps through current program
    *   <pre> Pre condition: Program is in stopped or paused state </pre>
    *   <pre> Post condition: Next instruction will be carried out </pre>
    */
    public void step() 
    {
    // don't create an interpreter thread if one is already running
        if (interpThread!= null && !interpThread.isAlive()) {
            notInterrupted = false;
            interpThread = new InterpreterThread();
            interpThread.start();
        }
    }
    
    /**  Stop current program
    *   <pre> Pre condition: Program is in run state </pre>
    *   <pre> Post condition: Program is in stop state </pre>
    */
    public void stop() 
    {
        notInterrupted = false;
        controlPointer = 0;
        stepCount = 0;
        interpRunState = "HALT";
                
        synchronized(monitor) {
            view.setStoppedState();
        }
        
    }
    
    /**  Reset the interpreter
    *   <pre> Pre condition: Program is in stop or run state </pre>
    *   <pre> Post condition: Interpreter is reset </pre>
    */
    public void reset() 
    {
        if (DEBUG) {System.out.println("Interpreter reset");}
        reset = true;
        //Make sure the interpreter state is halted
        interpRunState = "HALT";
        notInterrupted = false;
        controlPointer = 0;
        stepCount = 0;
        //reset rwhead
        tapeOne.resetHead();
        //update the tape
        tapeOne.setContent(initialInput);
        if (numTapes == 2) {
            tapeTwo.setContent(initialInput2);
            //update tape 2 in the view
            view.updateTapeContent(initialInput2, 2);
            tapeTwo.resetHead();
        }
        if (numTapes == 3) {
            tapeTwo.resetHead();
            tapeThree.resetHead();
            tapeTwo.setContent(initialInput2);
            //update tape 2 in the view
            view.updateTapeContent(initialInput2, 2);
            tapeThree.setContent(initialInput3);
            //update tape 3 in the view
            view.updateTapeContent(initialInput3, 3);
        }
        //update the view everywhere else
        view.updateTapeContent(initialInput, 1);
        view.updateState(transitions.get(0).getInitialState().trim());
        view.updateStepCount(0);
        view.resetView();
        view.setStartState();
        //set this interpreter state back to the starting state
        interpState = transitions.get(0).getInitialState().trim();
        
    }
    
    /**  
    * Pause current program
    * <pre> Pre condition: Program is in run state </pre>
    * <pre> Post condition: Program is in pause state </pre>
    */
    public void pause() 
    {
        if (DEBUG) {System.out.println("Machine paused");}
        notInterrupted = false;
        //view.setPauseState();
    }
    
    /**
     * Retrieves a description of what errors occurred
     * <pre> Pre-condition: error report was generated and requested </pre>
     * <pre> Post-condition: error report has been converted to string and returned </pre>
     * @return program error report 
     */
    public String getErrorReport() 
    {
        return errorReport.toString();        
    }
    
    /**
     * Used to find if there are errors
     * <pre> Pre-condition: errors present indicator has been requested </pre>
     * <pre> Post-condition: errors present indicator has been returned </pre>
     * @return boolean errorsPresent
     */
    public boolean errorFound() 
    {
        return errorsPresent;
    }
    
    /**
     * Retrieves the *.tm program code
     * <pre> Pre-condition: interpreters input code has been requested </pre>
     * <pre> Post-condition: interpreters input code has been returned </pre>
     * @return input code
     */
    public String getMachineCode() 
    {
        return inputCode;
    }
    
    /**
     * Retrieves the current rwHead location
     * <pre> Pre-condition: RWHead of a particular tape has been requested </pre>
     * <pre> Post-condition: RWHead of requested tape has been returned </pre>
     * @param tape which tape (1, 2, or 3)
     * @return rwhead location
     */
    public int getRWHead(int tape) {
        switch (tape) {
            case 1:
                return tapeOne.getHead();
            case 2:
                return tapeTwo.getHead();
            case 3:
                return tapeThree.getHead();
            default:
                return tapeOne.getHead();
        }
    }
    
    /**
     * Retrieves the current length of this interpreter's tape
     * <pre> Pre-condition: length of a tape has been requested </pre>
     * <pre> Post-condition: length of requested tape has been returned </pre>
     * @param tape which tape (1, 2 or 3)
     * @return int length
     */
    public int getTapeLength(int tape) {
        switch (tape) {
            case 1:
                return tapeOne.getLength();
            case 2:
                return tapeTwo.getLength();
            case 3:
                return tapeThree.getLength();
            default:
                return tapeOne.getLength();
        }
    }
       
    /**
     * Retrieves the initial input if provided in the program file
     * <pre> Pre-condition: initialInput has been requested </pre>
     * <pre> Post-condition: initialInput has been returned </pre>
     * @return initialInput Strong
     */
    public String getInitialInput()
    {
        return initialInput;
    }
    
    /**
     * Retrieves the individual tokens
     * <pre> Pre-condition: interpreter's list of tokens has been requested </pre>
     * <pre> Post-condition: list of token's has been returned </pre>
     * @return the tokens provided to this interpreter
     */
    public String[] getTokens() 
    {
        return tokens;
    }
    
    /**
     * Returns the current state of the machine
     * <pre> Pre-condition: current state has been requested </pre>
     * <pre> Post-condition: current interpreter state has been returned </pre>
     * @return current state of this interpreter
     */
    public String getState() 
    {
        return interpState;
    }
    
    /**
     * Returns the current run state of the machine
     * <pre> Pre-condition: run state requested </pre>
     * <pre> Post-condition: run state has been returned </pre>
     * @return current state of this interpreter
     */
    public String getRunState() 
    {
        return interpRunState;
    }
    
    /**
     * Returns the content of the specified tape
     * <pre> Pre-condition: content has been requested </pre>
     * <pre> Post-condition: content has been returned </pre>
     * @param tape which tape to get from
     * @return String content
     */
    public String getTapeContent(int tape) {
        switch (tape) {
            case 1:
                return tapeOne.getContent();
            case 2:
                return tapeTwo.getContent();
            case 3:
                return tapeThree.getContent();
            default:
                return tapeOne.getContent();
        }
    }
    
    /**
     * Performs the provided state transition
     * <pre> Pre-condition: transition has been requested </pre>
     * <pre> Post-condition: state transition has been performed </pre>
     * @param transition the state transition to be performed
     */
    private void performTransition(StateTransition transition)
    {
        String tape = transition.getTape();
        //System.out.println(tape);
        String initialState = transition.getInitialState();
        //System.out.println(initialState);
        String readToken = transition.getReadToken();
        //System.out.println(readToken);
        String writeToken = transition.getWriteToken();
        //System.out.println(writeToken);
        String direction = transition.getDirection();
        //System.out.println(direction);
        String writeTape = transition.getWriteTape();
        String endState = transition.getEndState();
        //System.out.println(endState);
        //defaults to tape 1
        char token = tapeOne.read();
        boolean tapeOneUpdated = false;
        boolean tapeTwoUpdated = false;
        boolean tapeThreeUpdated = false;
        //select the appropriate tape
        if (tape.equalsIgnoreCase("t1")) {
            token = tapeOne.read();
        }
        else if (tape.equalsIgnoreCase("t2")) {
            token = tapeTwo.read();
        }
        else if (tape.equalsIgnoreCase("t3")) {
            token = tapeThree.read();
        }
       
        
        interpState = initialState;
        //if current token matches or is wildcard
        //if (interpState.equalsIgnoreCase(initialState) ) {
            if ((token == readToken.charAt(0)) || (readToken.equals("*"))) {
            //if no change requested, write no new token, otherwise write
                if (!writeToken.equals("*")) {
                    if (writeTape.equalsIgnoreCase("t1")) {
                        tapeOne.write(writeToken.charAt(0));
                        tapeOneUpdated = true;
                    }
                    else if (writeTape.equalsIgnoreCase("t2")) {
                        tapeTwo.write(writeToken.charAt(0));
                        tapeTwoUpdated = true;
                    }
                    else if (writeTape.equalsIgnoreCase("t3")) {
                        tapeThree.write(writeToken.charAt(0));
                        tapeThreeUpdated = true;
                    }
                }
                //move left or right as needed
                //direction tuple will have up to three chars, indicating different movements
                String dir = Character.toString(direction.charAt(0));
                switch (dir) {
                    case "L":                       
                        tapeOne.moveHeadLeft();
                        tapeOneUpdated = true;
                        break;
                    case "R":
                        tapeOne.moveHeadRight();
                        tapeOneUpdated = true;
                    default:
                        break;
                }
                //possible movement for tape 2
                if (direction.length() >= 2) {
                    dir = Character.toString(direction.charAt(1));
                    switch (dir) {
                    case "L":                       
                        tapeTwo.moveHeadLeft();
                        tapeTwoUpdated = true;
                        break;
                    case "R":
                        tapeTwo.moveHeadRight();
                        tapeTwoUpdated = true;
                    default:
                        break;
                    }
                }
                //possible for tape 3
                if (direction.length() == 3) {
                    dir = Character.toString(direction.charAt(2));
                    switch (dir) {
                    case "L":                       
                        tapeThree.moveHeadLeft();
                        tapeThreeUpdated = true;
                        break;
                    case "R":
                        tapeThree.moveHeadRight();
                        tapeThreeUpdated = true;
                    default:
                        break;
                    }
                }
                //go to new state
                interpState = endState;
                //update the tapes (if animation enabled)
                if (animationEnabled) {
                    if (tapeOneUpdated) {
                        Platform.runLater(() -> {
                            view.updateTapeContent(tapeOne.getContent(), 1);
                        });
                    }
                    if (tapeTwoUpdated) {
                        Platform.runLater(() -> {
                            view.updateTapeContent(tapeTwo.getContent(), 2);
                        });
                    }
                    if (tapeThreeUpdated) {
                        Platform.runLater(() -> {
                            view.updateTapeContent(tapeThree.getContent(), 3);
                        });
                    }
                    Platform.runLater(() -> {
                        view.updateState(interpState);
                        view.updateStepCount(stepCount);
                    });
                }
                //check if a halt state has been reached, if so, HALT
                if (interpState.equalsIgnoreCase("accepthalt") || interpState.equalsIgnoreCase("rejecthalt")) {
                    notInterrupted = false;
                    //ensure all tapes end fully updated
                    Platform.runLater(() -> {
                        view.setStoppedState();
                        view.updateTapeContent(tapeOne.getContent(), 1);
                    });
                    if (numTapes == 2) {
                        Platform.runLater(() -> {
                        view.updateTapeContent(tapeTwo.getContent(), 2);
                        });
                    }
                    if (numTapes == 3) {
                        Platform.runLater(() -> {
                        view.updateTapeContent(tapeTwo.getContent(), 2);
                        view.updateTapeContent(tapeThree.getContent(), 3);
                        });
                    }
                    Platform.runLater(() -> {
                        view.updateState(interpState);
                        view.updateStepCount(stepCount);
                    });
                }
                stepCount++;
            }
            else {
                // else stuff
                //interpState = initialState;
                stepCount++;
            }
        //}
        
        if (DEBUG) {System.out.printf("Tape\t%s\nInitial state\t%s\nRead Token\t%s\nWrite Token\t%s\nMove\t%s\nWrite Tape\t%s\nEnd State\t%s\nSpeed\t%d\n\n", tape, initialState, readToken, writeToken, direction, writeTape, endState, view.getSpeed());}
        
    }
    /**
     * Used if an outside class needs to halt simulation in case of close during runtime or other unforeseen issue
     * 
     * <pre> Pre-condition: simulation running </pre>
     * <pre> Post-condition: simulation halted </pre>
     */
    public static void haltSimulation() {
        notInterrupted = false;
    }

    /**
     * Updates the initial content of a given tape, used when the user wants to change the starting
     * content of a particular tape
     * <pre> Pre-condition: initial input not set </pre>
     * <pre> Post-condition: initial input for all active tapes set </pre>
     * @param input the content to set
     * @param tape the tape desired
     */
    public void setInitialContent(String input, int tape) {
        switch (tape) {
            case 1:
                if ("".equals(input)) 
                    initialInput = "______";
                else 
                    initialInput = input;
                break;
            case 2:
                if ("".equals(input)) 
                    initialInput2 = "______";
                else 
                    initialInput2 = input;
                break;
            case 3:
                if ("".equals(input)) 
                    initialInput3 = "______";
                else 
                    initialInput3 = input;
                break;
            default:
                initialInput = "______";
                break;
        }                
    }
    
    private class InterpreterThread extends Thread {
                /** Handles running of the robot by steeping through commands
                 * 
                 * <pre> Pre-Condition: interpreter not running </pre>
                 * <pre> Post-Condition: run will be completed or will have been paused </pre>
                 */
                @Override
                @SuppressWarnings("static-access")
                public void run() {
                    // step once
                    synchronized(monitor) {                           
                            step();
                    }
                    
                    // continue to step while not interrupted
                    while (notInterrupted) {
                            synchronized(monitor) {
                                if (speed < 100) {
                                    sleep();
                                }
                                step();
                            }
                            
                    }
                    reset = false;
                }
                
                /**
                 * Sleeps the interpreter thread between instructions
                 * <pre> Pre-Condition: Interpreter is running </pre>
                 * <pre> Post-condition: Interpreter will have sleep for speed </pre>
                 */
                public void sleep(){
                    //sleep before next instruction
                    try {                                 
                        if (speed == 0) {
                            //interpThread.sleep(500);
                            InterpreterThread.sleep(5000);
                        }
                        else {
                            //interpThread.sleep(500);
                            InterpreterThread.sleep(2000-20*speed);
                        }
                    } catch (InterruptedException ex) {
                         Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                /**
                 * Steps through one instruction in the instruction set
                 * <pre> Pre-condition: Step has been requested </pre>
                 * <pre> Post-condition: Step will be completed </pre>
                 */
                public void step() {
                    boolean checkState = true;
                    int size = transitions.size();
                    //if ("HALT".equals(interpRunState) || "STEP".equals(interpRunState)) 
                    //{
                        if (controlPointer == size) 
                        {
                            //we have stepped to the end, so halt
                            //interpRunState = "HALT";
                            if (interpRunState.equalsIgnoreCase("HALT")) {
                                notInterrupted = false;
                                view.setStoppedState();
                                if (interpState.equalsIgnoreCase("accepthalt") || interpState.equalsIgnoreCase("rejecthalt")) {
                                        //notInterrupted = false;
                                        //view.setStoppedState();
                                        view.updateState(interpState);
                                        if (DEBUG) {System.out.println("end of commands was reached in a halt state");}
                                }
                                else {
                                        if (DEBUG) {System.out.println("end of commands was reached but no halt state");}
                                }
                            }
                            else {
                                controlPointer = 0;
                            }
                        }
                        else 
                        {
                            interpRunState = "STEP";
                            int timesLooped = 0;
                            //StateTransition tr = transitions.get(controlPointer);
                                                        
                            while (checkState) {
                                if (interpRunState.equals("HALT")) {
                                    notInterrupted = false;
                                    break;
                                }
                                StateTransition tr = transitions.get(controlPointer);
                                //System.out.println(interpState);
                                //System.out.println(tr.getInitialState());
                                if (interpState.equalsIgnoreCase(tr.getInitialState())) {
                                    performTransition(tr);
                                    controlPointer++;
                                    checkState = false;
                                }
                                else if (controlPointer < size-1) {
                                    controlPointer++;
                                }
                                else {
                                    controlPointer = 0;
                                    timesLooped++;
                                    if (timesLooped > 4) {
                                        interpRunState = "HALT";
                                        notInterrupted = false;
                                        Platform.runLater(() -> {
                                            String lookingForState = tr.getInitialState();
                                            view.showAutoStopDialog(lookingForState);
                                            view.setStoppedState();
                                        });
                                        break;
                                    }
                                    
                                }
                            }
                        }                   
                }                    
    }
    
}