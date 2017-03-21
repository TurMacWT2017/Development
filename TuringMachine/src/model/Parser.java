/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import controller.MachineViewController;
import java.util.ArrayList;

/**
 *
 * @author Nick Ahring
 */
public class Parser {
    private Interpreter interp;
    private String[] tokens;
    private MachineViewController view;
    
    //String consists of: Tape, Initial State, Read Token, Write Token, Move direction, Write Tape, and End State
    String tape, iS, read, write, dir, wT, eS;
    
    public Parser(Interpreter interp) {
        this.interp = interp;
    }
    
    public ArrayList<StateTransition> compile() {
        tokens = interp.getTokens();
        ArrayList<StateTransition> transitions = new ArrayList();
        
        int tokensLength = tokens.length;
        /*for (int i = 0; i < tokensLength; i++) {
            System.out.println(tokens[i]);
        }*/
        //System.out.println(tokensLength);
        //This loop works because input is "tuples" of 7
        //NOTE: This would have to be edited if the tuple was expanded
        for (int i = 0; i < tokensLength - 1; i+= 7) {
            //grab each set of 6 and build the transition
            tape = tokens[i];
            iS = tokens[i+1];
            read = tokens[i+2];
            write = tokens[i+3];
            dir = tokens[i+4];
            wT = tokens[i+5];
            eS = tokens[i+6];
            //System.out.printf("\n%s %s %s %s %s %s\n", tape, iS, read, write, dir, eS);
            transitions.add(new StateTransition(tape, iS, read, write, dir, wT, eS));
            
        }
        return transitions;
    }
    
}
