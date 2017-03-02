package model;

import controller.MachineViewController;
import controller.Tape;
import controller.TuringMachineJFXMLPrototype;
import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextInputDialog;

/**
 *
 * @author Landon Bressler 
 * @author Nick Ahring
 */
public class Interpreter
{
    private String[] tokens;
    private ArrayList<String> tokenList;
    private String initialInput = "";
    private String inputCode;
    private final String delim = ",|;";
    private final StringBuilder errorReport = new StringBuilder();
    private MachineViewController view; 
    private boolean errorsPresent = false;    
    /* keeps track of current state of interpreter, possible options are 
    HALT, RUN, PAUSE, STEP */
    private String interpRunState = "HALT";
    private String interpState;
    private ArrayList<StateTransition> transitions;
    private Parser par;
    private int controlPointer = 0;
    private int speed = 1000;
    private int stepCount = 0;
    //used to stop the thread if needed.
    private boolean notInterrupted = true;
    private boolean turnedOff = false;
    private boolean reset = false;
    private InterpreterThread interpThread;
    private final Object monitor = new Object();
    private Tape currentTape;
    
    /** Default interpreter constructor
    * 
    * <pre> Pre condition: Interpreter does not yet exist </pre>
    * <pre> Post condition: New Interpreter created </pre> 
     * @param input the String representation of the *.tm program file
     * @param view an instance of the view controller this interpreter will use
    */
    public Interpreter(String input, MachineViewController view) 
    {
        this.view = view;
        tokenize(input);
        if (errorsPresent == false) 
        {
            par = new Parser(this);
            transitions = par.compile();
            currentTape = new Tape(initialInput);
            view.drawStates(transitions);
            view.setInitialTapeContent(initialInput);
        }
        
    }
    
    /**
     * Used for setting the view controller of this interpreter to a specific view controller
     * @param view the view controller to be used
     */
    public void setViewController(MachineViewController view) {
        this.view = view;
    }
    
    
    /* To highlight text in a JavaFX textField (for R/W Head location) */
    /*public void handle(ActionEvent t) {
            textField.requestFocus(); // get focus first
            textField.positionCaret(0);
            textField.selectNextWord();

            System.out.println(textField.getSelectedText());
    }*/
    
    /** Starts up the interpreter 
     * <pre> Pre-condition: Interpreter not started</pre>
     * <pre> Post condition: Interpreter and interpreter thread started</pre>
     * @throws model.InterpreterException
     */
    public void start() throws InterpreterException { 
        
        turnedOff = false;
        reset = false;
        
        //set the view to the start state
        view.setStartState();
        interpThread = new InterpreterThread();

        
    }
    
    public void popup() {
        
        // if no tape input initial string supplied...
        TextInputDialog dialog = new TextInputDialog("Tape String");
        dialog.setTitle("Initial Tape Input a");
        dialog.setHeaderText("Initial Tape Input");
        dialog.setContentText("Enter initial Tape input:");

        // User input acceptance, for tape input string
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println("Tape Input: " + result.get());
            initialInput = result.get();
        }
    }
    
    
    /** Parses the input program into tokens that can be interpreted into the correct symbols
     * 
     * @param input the String representation of the *.tm program file
     */
    private void tokenize(String input)
    {
        int lineNum = 0;
        String errorString;
       
        // if initial tape input string supplied by .tm program
        if (input.toLowerCase().startsWith("input: ")) 
        {
            System.out.println("Initial input provided");
            //grab the value, set it, then nix the line
            int colon = input.indexOf(":");
            int semicolon = input.indexOf(";");
            initialInput = input.substring(colon+1, semicolon).trim();
            String firstLine = input.substring(0, semicolon+1);
            input = input.replace(firstLine, "");
            lineNum++;
        }
        else
        {   
            popup();
        }
        
        input = input.replaceAll("\n", "");
        inputCode = input;
        tokens = inputCode.split(delim);
        int tokensLength = tokens.length;
        
        for (int i = 0; i < tokensLength; i++)
        {
            // Get the current tape - if left blank, default to tape 1
            if (i%6 == 0)
            {
                lineNum++;
                if (tokens[i].equals(""))
                {
                    tokens[i] = "t1";
                    //System.out.println("\nTape token: " + tokens[i] + " on line " + lineNum);
                }
                if (tokens[i].equalsIgnoreCase("t1")) 
                {
                    tokens[i] = "t1";
                    //System.out.println("\nTape token: " + tokens[i] + " on line " + lineNum);
                }
                else if (tokens[i].equalsIgnoreCase("t2"))
                {
                    tokens[i] = "t2";
                    //System.out.println("\nTape 2 token: " + tokens[i] + " on line " + lineNum);
                }
                else if (tokens[i].equalsIgnoreCase("t3"))
                {
                    tokens[i] = "t3";
                    //System.out.println("\nTape 3 token: " + tokens[i] + " on line " + lineNum);
                }
                else 
                {
                    errorString = "Error on line " + lineNum + ": Invalid tape " + tokens[i];
                    errorReport.append(errorString);
                    errorsPresent = true;
                }
            }

            // Get the initial state
            if (i%6 == 1)
            {
                System.out.println("Initial state token: " + tokens[i] + " on line " + lineNum);
            }

            // Get the token being read
            if (i%6 == 2)
            {
                tokens[i] = tokens[i].trim();
//                if (token.equalsIgnoreCase("*")) {
//                    tokens[i] = "WILDCARD";
//                }
                //tokens[i] = token;
                
            }

            // Write desired token
            if (i%6 == 3)
            {
                tokens[i] = tokens[i].trim();
//                if (token.equalsIgnoreCase("*")) {
//                    tokens[i] = "NO_CHANGE";
//                }
                //tokens[i] = token;
            }

            // Get the direction the read/write head needs to move
            if (i%6 == 4)
            {  
                String token = tokens[i].trim();
                System.out.println("Direction token: " + tokens[i] + " on line " + lineNum + "\n");
                if (token.matches("(R|r|right|Right|>)"))
                {
                    //moveRight();
                    tokens[i] = "RIGHT";
                }
                else if (token.matches("(L|l|<|left|Left)"))
                {
                    //moveLeft();
                    tokens[i] = "LEFT";
                }
                else if (token.equalsIgnoreCase("*") || token.equalsIgnoreCase("_"))
                {
                    //stay();
                    tokens[i] = "STAY";
                }
                else 
                {
                    errorString = "\nInvalid direction on line " + lineNum + ":  "+ tokens[i] + "\n";
                    errorReport.append(errorString);
                    errorsPresent = true;
                }
            }

            // Get the end-state of the transition
            if (i%6 == 5)
            {
                System.out.println("End state token: " + tokens[i] + " on line " + lineNum + "\n");
            }
        }
            
        //System.out.println("Number of lines: " + numberOfLines);
        System.out.println("Initial input provided: " + initialInput);
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
            view.setStepState(); 
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
        System.out.println("Interpreter reset");
        reset = true;
        notInterrupted = false;
        controlPointer = 0;
        stepCount = 0;
        //reset rwhead
        currentTape.resetHead();
        //update the tape
        currentTape.setContent(initialInput);
        //update the view
        view.updateTapeContent(initialInput);
        view.updateState("");
        view.updateStepCount(0);
        
        view.setStartState();
        
    }
    
    /**  Pause current program
    *   <pre> Pre condition: Program is in run state </pre>
    *   <pre> Post condition: Program is in pause state </pre>
    */
    public void pause() 
    {
        System.out.println("Machine paused");
        notInterrupted = false;
        //view.setPauseState();
    }
    
    /**
     * Retrieves a description of what errors occurred
     * @return 
     */
    public String getErrorReport() 
    {
        return errorReport.toString();        
    }
    
    /**
     * Used to find if there are errors
     * @return 
     */
    public boolean errorFound() 
    {
        return errorsPresent;
    }
    
    /**
     * Retrieves the *.tm program code
     * @return input code
     */
    public String getMachineCode() 
    {
        return inputCode;
    }
    
    /**
     * Retrieves the current rwHead location
     * @return rwhead location
     */
    public int getRWHead() {
        return currentTape.getHead();
    }
       
    /**
     * Retrieves the initial input if provided in the program file
     * @return initialInput Strong
     */
    public String getInitialInput()
    {
        return initialInput;
    }
    
    /**
     * Retrieves the individual tokens
     * @return the tokens provided to this interpreter
     */
    public String[] getTokens() 
    {
        return tokens;
    }
    
    /**
     * Returns the current state of the machine
     * @return current state of this interpreter
     */
    public String getState() 
    {
        return interpState;
    }
    
    /**
     * Performs the provided state transition
     * @param transition the state transition to be performed
     */
    private void performTransition(StateTransition transition) 
    {
        String tape = transition.getTape();
        //System.out.println(tape);
        String initialState = transition.getInitialState().trim();
        //System.out.println(initialState);
        String readToken = transition.getReadToken();
        //System.out.println(readToken);
        String writeToken = transition.getWriteToken();
        //System.out.println(writeToken);
        String direction = transition.getDirection();
        //System.out.println(direction);
        String endState = transition.getEndState().trim();
        //System.out.println(endState);
        
        interpState = initialState;
        //if current token matches or is wildcard
        if ((currentTape.read() == readToken.charAt(0)) || (readToken.equals("*"))) {
            //if no change requested, write no new token, otherwise write
            if (!writeToken.equals("*")) {
                System.out.println("New token");
                currentTape.write(writeToken.charAt(0));
            }
            //move left or right as needed
            if (direction.equals("LEFT")) {
                currentTape.moveHeadLeft();
                view.updateHighlight();
            }
            else if (direction.equals("RIGHT")) {
                currentTape.moveHeadRight();
                view.updateHighlight();
            }
            //go to new state
            System.out.println(currentTape.getContent());
            interpState = endState;
            //check if a halt state has been reached, if so, HALT
            if (interpState.equalsIgnoreCase("accepthalt") || interpState.equalsIgnoreCase("rejecthalt")) {
                notInterrupted = false;
                view.setStoppedState();
                view.updateState(interpState);
            }
            stepCount++;
        }
        else {
            // else stuff
            interpState = initialState;
            stepCount++;
        }
        view.updateStepCount(stepCount);
        view.updateState(interpState);
        System.out.println(currentTape.getContent());
        view.updateTapeContent(currentTape.getContent());
        System.out.printf("Tape %s\n Initial state %s\n Read Token %s\n Write Token %s\n Move %s\n End State %s\n Speed %d\n", tape, initialState, readToken, writeToken, direction, endState, view.getSpeed());
        
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
                                    sleep();
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
                           if(view.getSpeed()==0)
                               //interpThread.sleep(500);
                               interpThread.sleep(5000);
                           else
                               //interpThread.sleep(500);
                               interpThread.sleep(2000-20*view.getSpeed());
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
                    System.out.println("Program stepped");
                    int size = transitions.size();
                    //if ("HALT".equals(interpRunState) || "STEP".equals(interpRunState)) 
                    //{
                        if (controlPointer == size) 
                        {
                            //we have stepped to the end, so halt
                            interpRunState = "HALT";
                            notInterrupted = false;
                            view.setStoppedState();
                            if (interpState.equalsIgnoreCase("accepthalt") || interpState.equalsIgnoreCase("rejecthalt")) {
                                    //notInterrupted = false;
                                    //view.setStoppedState();
                                    view.updateState(interpState);
                                    System.out.println("end of commands was reached in a halt state");
                            }
                            else {
                                    System.out.println("end of commands was reached but no halt state");
                            }
                            
                        }
                        else 
                        {
                            interpRunState = "STEP";
                            StateTransition tr = transitions.get(controlPointer);
                            performTransition(tr);
                            controlPointer++;

                        }
                        
                    //}                   
                }
                    
    }
    
}