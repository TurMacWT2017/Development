package model;

import controller.MachineViewController;
import controller.Tape;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 *
 * @author Landon Bressler 
 * @author Nick Ahring
 */
public class Interpreter
{
    private String[] tokens;
    private ArrayList<String> tokenList;
    //Initial input for tapes
    private String initialInput = "_____";
    private String initialInput2 = "_____";
    private String initialInput3 = "_____";
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
    private final int speed = 1000;
    private int stepCount = 0;
    //used to stop the thread if needed.
    private static boolean notInterrupted = true;
    private boolean turnedOff = false;
    private boolean reset = false;
    //interpreter thread andd thread monitor
    private InterpreterThread interpThread;
    private final Object monitor = new Object();
    //tape related variables
    private Tape tapeOne;
    private Tape tapeTwo;
    private Tape tapeThree;
    private int numTapes = 1;
    
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
        this.view = view;
        numTapes = tapes;
        tokenize(input);
        if (errorsPresent == false) 
        {
            par = new Parser(this);
            transitions = par.compile();
            System.out.println("Fuckeronis" + initialInput2);
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
            view.drawStates(transitions);
            
            // Set interpreter state to be the start state of the program
            interpState = transitions.get(0).getInitialState().trim();
            view.updateState(interpState);
            view.updateStepCount(0);
        }
        
    }
    
    /**
     * Used for setting the view controller of this interpreter to a specific view controller
     * @param view the view controller to be used
     */
    public void setViewController(MachineViewController view) {
        this.view = view;
    }
    
    
    /**
     * Used for changing the number of tapes the interpreter is working with
     * @param tapes number of tapes
     */
    public void setNumberOfTapes(int tapes) {
        numTapes = tapes;
    }
    
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
    
    /** Pop-up dialog box for when the program file does not contain input or
     *  the user clears the tape input
     */
    public void popup() {
        String[] input = view.showInputDialog(numTapes);
        initialInput = input[0];
        if (numTapes == 2) {
            initialInput2 = input[1];
        }
        if (numTapes == 3) {
            initialInput2 = input[1];
            initialInput3 = input[2];
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
        
        //store the input code before removing the new line, in case the 
        //formatted version is needed later
        inputCode = input;
        input = input.replaceAll("\n", "");
        tokens = input.split(delim);
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
                tokens[i] = tokens[i].trim();
                //System.out.println("Initial state token: " + tokens[i] + " on line " + lineNum);
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
                tokens[i] = tokens[i].trim();
                //System.out.println("End state token: " + tokens[i] + " on line " + lineNum + "\n");
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
        System.out.println("Interpreter reset");
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
        view.setStartState();
        //set this interpreter state back to the starting state
        interpState = transitions.get(0).getInitialState().trim();
        
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
     * @return initialInput Strong
     */
    public String getInitialInput()
    {
        return initialInput; // append _ end of line marker TK
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
     * Returns the current run state of the machine
     * @return current state of this interpreter
     */
    public String getRunState() 
    {
        return interpRunState;
    }
    
    /**
     * Returns the content of the specified tape
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
        String endState = transition.getEndState();
        //System.out.println(endState);
        
        interpState = initialState;
        //if current token matches or is wildcard
        //if (interpState.equalsIgnoreCase(initialState) ) {
            if ((tapeOne.read() == readToken.charAt(0)) || (readToken.equals("*"))) {
            //if no change requested, write no new token, otherwise write
                if (!writeToken.equals("*")) {
                    //System.out.println("New token");
                    tapeOne.write(writeToken.charAt(0));
                }
                //move left or right as needed
                if (direction.equals("LEFT")) {
                    tapeOne.moveHeadLeft();
                    view.updateTapeContent(tapeOne.getContent(), 1);
                }
                else if (direction.equals("RIGHT")) {
                    tapeOne.moveHeadRight();
                    view.updateTapeContent(tapeOne.getContent(), 1);
                }
                view.updateTapeContent(tapeOne.getContent(), 1);
                //go to new state
                System.out.println(tapeOne.getContent());
                interpState = endState;
                view.updateState(interpState);
                //check if a halt state has been reached, if so, HALT
                if (interpState.equalsIgnoreCase("accepthalt") || interpState.equalsIgnoreCase("rejecthalt")) {
                    notInterrupted = false;
                    view.setStoppedState();
                }
                stepCount++;
            }
            else {
                // else stuff
                //interpState = initialState;
                stepCount++;
            }
        //}
        view.updateStepCount(stepCount);
        
        System.out.println(tapeOne.getContent());
        
        System.out.printf("Tape\t%s\nInitial state\t%s\nRead Token\t%s\nWrite Token\t%s\nMove\t%s\nEnd State\t%s\nSpeed\t%d\n\n", tape, initialState, readToken, writeToken, direction, endState, view.getSpeed());
        
    }
    /**
     * Used if an outside class needs to halt simulation in case of close during runtime or other unforeseen issue
     */
    public static void haltSimulation() {
        notInterrupted = false;
    }

    /**
     * Updates the initial content of a given tape, used when the user wants to change the starting
     * content of a particular tape
     * @param input the content to set
     * @param tape the tape desired
     */
    public void setInitialContent(String input, int tape) {
        switch (tape) {
            case 1:
                initialInput = input;
                break;
            case 2:
                initialInput2 = input;
                break;
            case 3:
                initialInput3 = input;
                break;
            default:
                initialInput = input;
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
                               InterpreterThread.sleep(5000);
                           else
                               //interpThread.sleep(500);
                               InterpreterThread.sleep(2000-20*view.getSpeed());
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
                                        System.out.println("end of commands was reached in a halt state");
                                }
                                else {
                                        System.out.println("end of commands was reached but no halt state");
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
                                System.out.println(interpState);
                                System.out.println(tr.getInitialState());
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
                            //performTransition(tr);
                            //controlPointer++;

                        }
                        
                    //}                   
                }
                    
    }
    
}