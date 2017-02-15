/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import model.Interpreter;

/**
 *
 * @author student
 */
public class MachineController {
    
    private Interpreter interp;
    
    public MachineController() {

    }
    
    public String openFile(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fw = new FileReader(file.getAbsoluteFile());
            try (BufferedReader bw = new BufferedReader(fw)) {

                String line;
                while ((line = bw.readLine()) != null) {
                    sb.append(line);
//                    sb.append("\n");
                }
            }
        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        }
        return sb.toString();
    }
    
}
