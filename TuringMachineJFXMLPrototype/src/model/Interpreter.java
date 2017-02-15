package model;

import java.util.*;
import java.io.*;

public class Interpreter
{
    private String[] tokens;
    private ArrayList<String> tokenList;
    private String initialInput = "";
    private String inputCode;
    private final String delim = ",|;";
    /* Default interpreter constructor
    * @param selected file to interpret and tokenize
    * <pre> Interpreter does not yet exist </pre>
    * Post condition: New Interpreter created
    */
    public Interpreter() {
        
    }
    /* To highlight text in a JavaFX textField */
    /* public void handle(ActionEvent t) {
            textField.requestFocus(); // get focus first
            textField.positionCaret(0);
            textField.selectNextWord();

            System.out.println(textField.getSelectedText());
    }*/
        
    public void tokenize(String input)
    {
//        /* We're splitting the input lines by commas, and using semicolons as end-line characters */
//        String delim = ",|;";
//        
//        StringBuilder sb = new StringBuilder();
//        int numberOfLines = 0;
          int lineNum = 0;
//        
//        //File file = new File("test.tm");
//        try {
//            Scanner in = new Scanner(file);
//            //read the optional input line if it's there
//            if (in.next().toLowerCase().startsWith("input:")) 
//            {
//                initialInput = in.next().replaceAll("input:", "").trim();
//                lineNum++;
//            }
//            
//            while (in.hasNext())
//            {
//                sb.append(in.nextLine());
//                numberOfLines++;
//            }
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
                        System.out.println("\nError on line " + lineNum + "Invalid tape " + tokens[i]);
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
                    System.out.println("Write token: " + tokens[i] + " on line " + lineNum);
                }
                
                // Get the direction the read/write head needs to move
                if (i%6 == 4)
                {
                    System.out.println("Direction token: " + tokens[i] + " on line " + lineNum);
                    if (tokens[i].equalsIgnoreCase("R") || tokens[i].equalsIgnoreCase("right") || tokens[i].equals(">"))
                    {
                        moveRight();
                    }
                    else if (tokens[i].equalsIgnoreCase("L") || tokens[i].equalsIgnoreCase("left") || tokens[i].equals("<"))
                    {
                        moveLeft();
                    }
                    else if (tokens[i].equalsIgnoreCase("*"))
                    {
                        stay();
                    }
                    else 
                    {
                        System.out.printf("\nInvalid direction on line %d", lineNum);
                    }
                }
                
                // Get the end-state of the transition
                if (i%6 == 5)
                {
                    System.out.println("End state token: " + tokens[i] + " on line " + lineNum);
                }
            }
            
//        } catch(FileNotFoundException e) {
//            System.out.println("That is an invalid file.");
//        }
        
        // Print the number of lines of the input file
//        System.out.println("Number of lines: " + numberOfLines);
        System.out.println("Initial input provided: " + initialInput);
    }
    
    public String getMachineCode() 
    {
        return inputCode;
    }
    public String getInitialInput()
    {
        return initialInput;
    }
    public String[] getTokens() {
        return tokens;
    }
    public static void moveRight()
    {
        System.out.println("Moved Right");
    }
    
    public static void moveLeft()
    {
        System.out.println("Moved Left");
    }
    
    public static void stay()
    {
        System.out.println("No movement");
    }

}