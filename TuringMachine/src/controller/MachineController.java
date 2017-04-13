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

/**
 * Controller for performing auxilliary functions such as file opening
 * @author Nick Ahring
 */
public class MachineController {
    
    public MachineController() {

    }
    
    public String openFile(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            FileReader fw = new FileReader(file.getAbsoluteFile());
            try (BufferedReader bw = new BufferedReader(fw)) {

                String line;
                while ((line = bw.readLine()) != null) {
                    

                        line = line.replaceAll("[ \t]*#[^\\n]*", "").trim();
                        if (!line.equals("")) {
                            sb.append(line);
                            sb.append("\n");
                        }
                }
            }
        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        }
        return sb.toString();
    }
    
}
