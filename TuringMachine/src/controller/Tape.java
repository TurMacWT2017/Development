/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


/** Represents a Turing machine tape
 *
 * @author Nick Ahring
 */
public class Tape {
    char[] content;
    int rwHead;
    
    public Tape(String initialContent) {
        content = initialContent.toCharArray();
        rwHead = 0;
    }
    
    public Tape() {
        rwHead = 0;
    }
    
    
    public void setContent(String newContent) {
        content = newContent.toCharArray();
    }
    
    public String getContent() {
        String tapeContents = new String(content);
        return tapeContents;
    }
        
    public void moveHeadLeft() {
        rwHead--;
        if (rwHead < 0) {
            StringBuilder sb = new StringBuilder();
            String oldContent = new String(content);
            sb.append("_");
            sb.append(oldContent);
            setContent(sb.toString());
            System.out.println(sb.toString());
            //reset to the new zero, negative indexes are not valid
            rwHead = 0;
        }
    }
    
    public void moveHeadRight() {
        rwHead++;
        if (rwHead > content.length - 1) {
            StringBuilder sb = new StringBuilder();
            String oldContent = new String(content);
            sb.append(oldContent);
            sb.append("_");
            setContent(sb.toString());
        }
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
    
    public int getLength() {
        return content.length;
    }
}
