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
        }
        
    }
    
    public void setViewController(MachineViewController view) {
        this.view = view;
    }
    
    
    /* To highlight text in a JavaFX textField */
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
        {   // if no tape input initial string supplied...
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
                String token = tokens[i].trim();
                if (token.equals("*")) {
                    tokens[i] = "WILDCARD";
                }
                tokens[i] = token;
                
            }

            // Write desired token
            if (i%6 == 3)
            {
                String token = tokens[i].trim();
                if (token.matches("[*]")) {
                    tokens[i] = "NO_CHANGE";
                }
                tokens[i] = token;
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
        view.setTapeContent(initialInput);
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
     * @return 
     */
    public String getMachineCode() 
    {
        return inputCode;
    }
    
  
    
    /**
     * Retrieves the initial input if provided in the program file
     * @return 
     */
    public String getInitialInput()
    {
        return initialInput;
    }
    
    /**
     * Retrieves the individual tokens
     * @return 
     */
    public String[] getTokens() 
    {
        return tokens;
    }
    
    /**
     * Returns the current state of the machine
     * @return 
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
        String initialState = transition.getInitialState().trim();
        String readToken = transition.getReadToken();
        String writeToken = transition.getWriteToken();
        String direction = transition.getDirection();
        String endState = transition.getEndState().trim();
        
        //if current token matches or is wildcard
        if ((currentTape.read() == readToken.charAt(0)) || readToken.equals("WILDCARD")) {
            //if no change requested, write no new token, otherwise write
            if (!writeToken.equals("NO_CHANGE")) {
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
            if (interpState.equalsIgnoreCase("accepthalt") || interpState.equalsIgnoreCase("rejectHalt")) {
                notInterrupted = false;
                view.setStoppedState();
            }
            stepCount++;
        }
        System.out.printf("On tape %s and initial state %s read for token %s and write token %s then move %s and end in state %s at speed %d\n", tape, initialState, readToken, writeToken, direction, endState, view.getSpeed());
        
    }
    
    private class InterpreterThread extends Thread {
                /** Handles running of the robot by steeping through commands
                 * 
                 * @precondition robot is on and has been run
                 * @postcondition robot will have stepped through commands
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
                
                public void step() {
                    System.out.println("Program stepped");
                    int size = transitions.size();
                    if ("HALT".equals(interpRunState) || "STEP".equals(interpRunState)) 
                    {
                        if (controlPointer == size) 
                        {
                            //we have stepped to the end, so halt
                            interpRunState = "HALT";
                            notInterrupted = false;
                            view.setStoppedState();
                        }
                        else 
                        {
                            interpRunState = "STEP";
                            StateTransition tr = transitions.get(controlPointer);
                            performTransition(tr);
                            controlPointer++;
                            //UI update
                            view.updateStepCount(stepCount);
                            view.updateState(interpState);
                            System.out.println(currentTape.getContent());
                            view.setTapeContent(currentTape.getContent());

                        }
                        
                    }                   
                }
                    
    }
    
}