/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/** 
 *  This class represents a state transition within the machine
 *  when a new state transition is created it must be provided with the following
 *  properties: The tape on which to act, the initial state, the read token,
 *              the write token, the direction to move the tape, and the state
 *              to transition to
 */
public class StateTransition {
    private final String tape;
    private final String initialState;
    private final String readToken;
    private final String writeToken;
    private final String direction;
    private final String endState;
    
    public StateTransition(String t, String iS, String read, String write, String dir, String eS) {
        this.tape = t;
        this.initialState = iS;
        this.readToken = read;
        this.writeToken = write;
        this.direction = dir;
        this.endState = eS;
    }
    
    public String getTape() {
        return tape;
    }
    
    public String getInitialState() {
        return initialState;
    }
    
    public String getReadToken() {
        return readToken;
    }
    
    public String getWriteToken() {
        return writeToken;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public String getEndState() {
        return endState;
    }    
}
