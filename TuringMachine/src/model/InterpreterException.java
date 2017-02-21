/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author student
 */
public class InterpreterException extends Exception {
        private final int lineNum;
        
	public InterpreterException(String message, int lineNum) {
		super(message);
                this.lineNum = lineNum;
	}
        
        public int getLineNum() {
            return lineNum;
        }
    
}
