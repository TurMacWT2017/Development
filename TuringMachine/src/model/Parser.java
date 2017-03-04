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
 * @author Karel Team One
 */
public class Parser {
    private Interpreter interp;
    private String[] tokens;
    private MachineViewController view;
    
    String tape, iS, read, write, dir, eS;
    
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
        //This loop works because input is "tuples" of 6
        //NOTE: This would have to be edited if the tuple was expanded
        for (int i = 0; i < tokensLength - 1; i+= 6) {
            //grab each set of 6 and build the transition
            tape = tokens[i];
            iS = tokens[i+1];
            read = tokens[i+2];
            write = tokens[i+3];
            dir = tokens[i+4];
            eS = tokens[i+5];
            //System.out.printf("\n%s %s %s %s %s %s\n", tape, iS, read, write, dir, eS);
            transitions.add(new StateTransition(tape, iS, read, write, dir, eS));
            
        }
        return transitions;
    }
    
}
