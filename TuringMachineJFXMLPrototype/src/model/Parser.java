/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

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
    
    public void parseTokens() {
        tokens = interp.getTokens();
        
        int tokensLength = tokens.length;
        for (int i = 0; i < tokensLength; i++) {
            
        }
        
    }
    
    
}
