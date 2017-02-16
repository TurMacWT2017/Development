/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author Karel Team One
 */
public class Parser {
    private Interpreter interp;
    private String[] tokens;
    
    public Parser(Interpreter interp) {
        this.interp = interp;
    }
    
    public ArrayList<StateTransition> compile() {
        tokens = interp.getTokens();
        ArrayList<StateTransition> transitions = new ArrayList();
        
        int tokensLength = tokens.length;
        for (int i = 0; i < tokensLength; i++) {
            System.out.println(tokens[i]);
        }
        System.out.println(tokensLength);
        //This loop works because input is "tuples" of 6
        //NOTE: This would have to be edited if the tuple was expanded
        for (int i = 0; i < tokensLength - 1; i+= 6) {
            //grab each set of 6 and build the transition
            String tape = tokens[i];
            String iS = tokens[i+1];
            String read = tokens[i+2];
            String write = tokens[i+3];
            String dir = tokens[i+4];
            String eS = tokens[i+5];
            transitions.add(new StateTransition(tape, iS, read, write, dir, eS));
        }
        return transitions;
    }
    
}
