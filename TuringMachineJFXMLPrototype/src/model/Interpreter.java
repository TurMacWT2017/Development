package model;

import java.util.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Landon Bressler
 */
public class Interpreter
{
    private String[] tokens;
    private ArrayList<String> tokenList;
    private String initialInput = "";
    private String inputCode;
    private final String delim = ",|;";
    private final StringBuilder errorReport = new StringBuilder();
    private boolean errorsPresent = false;
    
    /* keeps track of current state of interpreter, possible options are 
    HALT, RUN, PAUSE, STEP */
    private String interpRunState = "HALT";
    private String interpState;
    private ArrayList<StateTransition> transitions;
    private Parser par;
    private int controlPointer = 0;
    private int speed = 1000;
    
    /** Default interpreter constructor
    * 
    * <pre> Pre condition: Interpreter does not yet exist </pre>
    * <pre> Post condition: New Interpreter created </pre> 
     * @param input the String representation of the *.tm program file
    */
    public Interpreter(String input) 
    {
        
        tokenize(input);
        if (errorsPresent == false) 
        {
            par = new Parser(this);
            transitions = par.compile();
        }
        
    }
    
    /* To highlight text in a JavaFX textField */
    /*public void handle(ActionEvent t) {
            textField.requestFocus(); // get focus first
            textField.positionCaret(0);
            textField.selectNextWord();

            System.out.println(textField.getSelectedText());
    }*/
    
    /** Parses the input program into tokens that can be interpreted into the correct symbols
     * 
     * @param input the String representation of the *.tm program file
     */
    private void tokenize(String input)
    {
        int lineNum = 0;
        String errorString;
       
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
                    System.out.println("\nTape token: " + tokens[i] + " on line " + lineNum);
                }
                if (tokens[i].equalsIgnoreCase("t1")) 
                {
                    System.out.println("\nTape token: " + tokens[i] + " on line " + lineNum);
                }
                else if (tokens[i].equalsIgnoreCase("t2"))
                {
                    System.out.println("\nTape 2 token: " + tokens[i] + " on line " + lineNum);
                }
                else if (tokens[i].equalsIgnoreCase("t3"))
                {
                    System.out.println("\nTape 3 token: " + tokens[i] + " on line " + lineNum);
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
                System.out.println("Read token: " + tokens[i] + " on line " + lineNum);
            }

            // Write desired token
            if (i%6 == 3)
            {
                System.out.println("Write token: " + tokens[i] + " on line " + lineNum + "\n");
            }

            // Get the direction the read/write head needs to move
            if (i%6 == 4)
            {
                System.out.println("Direction token: " + tokens[i] + " on line " + lineNum + "\n");
                if (tokens[i].equalsIgnoreCase("R") || tokens[i].equalsIgnoreCase("right") || tokens[i].equals(">"))
                {
                    //moveRight();
                    System.out.println("Moved Right");
                }
                else if (tokens[i].equalsIgnoreCase("L") || tokens[i].equalsIgnoreCase("left") || tokens[i].equals("<"))
                {
                    //moveLeft();
                    System.out.println("Moved Left");
                }
                else if (tokens[i].equalsIgnoreCase("*") || tokens[i].equalsIgnoreCase("_") || tokens[i].equalsIgnoreCase(""))
                {
                    //stay();
                    System.out.println("No movement");
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
        System.out.println("Program started");
        //if currently halted, begin run start
        if ("HALT".equals(interpRunState)) 
        {
            interpRunState = "RUN";
            controlPointer = 0;
            //while still in run state, run through program, when done, halt automatically
            while (interpRunState.equals("RUN")) 
            {
                for (StateTransition tr: transitions) 
                {
                    performTransition(tr);
                    controlPointer++;
//                    try {
//                        Thread.sleep(speed);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(Interpreter.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                }
                interpRunState = "HALT";
            }
        }                       
    }
    
    /**  Steps through current program
    *   <pre> Pre condition: Program is in stopped or paused state </pre>
    *   <pre> Post condition: Next instruction will be carried out </pre>
    */
    public void step() 
    {
        System.out.println("Program stepped");
        int size = transitions.size();
        if ("HALT".equals(interpRunState) || "STEP".equals(interpRunState)) 
        {
            if (controlPointer == size) 
            {
                //we have stepped to the end, so halt
                interpRunState = "HALT";
                System.out.println("Reached end, please reset");
            }
            else 
            {
                interpRunState = "STEP";
                StateTransition tr = transitions.get(controlPointer);
                performTransition(tr);
                controlPointer++;
            }
        }
    }
    
    /**  Stop current program
    *   <pre> Pre condition: Program is in run state </pre>
    *   <pre> Post condition: Program is in stop state </pre>
    */
    public void stop() 
    {
        System.out.println("Program Stopped");
        
    }
    
    /**  Reset the interpreter
    *   <pre> Pre condition: Program is in stop or run state </pre>
    *   <pre> Post condition: Interpreter is reset </pre>
    */
    public void reset() 
    {
        System.out.println("Interpreter reset");
        controlPointer = 0;
    }
    
    /**  Pause current program
    *   <pre> Pre condition: Program is in run state </pre>
    *   <pre> Post condition: Program is in pause state </pre>
    */
    public void pause() 
    {
        System.out.println("Machine paused");
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
        String initialState = transition.getInitialState();
        String readToken = transition.getReadToken();
        String writeToken = transition.getWriteToken();
        String direction = transition.getDirection();
        String endState = transition.getEndState();
        
        System.out.printf("On tape %s and initial state %s read for token %s and write token %s then move %s and end in state %s\n", tape, initialState, readToken, writeToken, direction, endState);
        interpState = endState;
    }
}