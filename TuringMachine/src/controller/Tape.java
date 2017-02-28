/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.Arrays;

/** Represents a Turing machine tape
 *
 * @author Nick Ahring
 */
public class Tape {
    char[] content;
    int rwHead;
    
    public Tape(String initialContent) {
        this.content = initialContent.toCharArray();
        this.rwHead = 0;
    }
    
    public Tape() {
        this.rwHead = 0;
    }
    
    
    public void setContent(String content) {
        this.content = content.trim().toCharArray();
    }
    
    public String getContent() {
        String tapeContents = new String(content);
        return tapeContents;
    }
    
    
    public void moveHeadLeft() {
        if (rwHead > 0) {
            rwHead--;
        }
    }
    
    public void moveHeadRight() {
        rwHead++;
    }
    
    public void resetHead() {
        rwHead = 0;
    }
    
    public void write(char character) {
        content[rwHead] = character;
    }
    
    public char read() {
        return content[rwHead];
    }
    
    public int getHead() {
        return rwHead;
    }
    
}
