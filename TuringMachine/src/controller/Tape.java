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
    
    /**
     * Sets the content of this tape.
     * @param newContent new content to set
     */
    public void setContent(String newContent) {
        content = newContent.toCharArray();
    }
    
    /**
     * Assembles tape content into a string and returns it.
     * @return string 
     */
    public String getContent() {
        String tapeContents = new String(content);
        return tapeContents;
    }
    
    /**
     * Moves the RW Head of this tape to the left. If it has ran off the tape
     * this will utilize string builder and setContent to extend the tape.
     */
    public void moveHeadLeft() {
        rwHead--;
        if (rwHead < 0) {
            StringBuilder sb = new StringBuilder();
            String oldContent = new String(content);
            sb.append("_");
            sb.append(oldContent);
            setContent(sb.toString());
            //reset to the new zero, negative indexes are not valid
            rwHead = 0;
        }
    }
    
    /**
     * Moves the RW Head of this tape to the right. If it has ran off the tape,
     * this will utilize string builder and setContent to extend the tape.
     */
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
    
    /**
     * Resets the RWHead of this tape to 0.
     */
    public void resetHead() {
        rwHead = 0;
    }
    
    /**
     * Writes the provided character to the location under this tapes
     * RW Head.
     * @param character character to write
     */
    public void write(char character) {
        content[rwHead] = character;
    }
    
    /**
     * Reads the character currently underneath the RW Head of this tape.
     * @return char
     */
    public char read() {
        return content[rwHead];
    }
    
    /**
     * Returns the location of this tapes RW Head
     * @return int
     */
    public int getHead() {
        return rwHead;
    }
    
    /**
     * Returns the length of this tapes content
     * @return int
     */
    public int getLength() {
        return content.length;
    }
}
