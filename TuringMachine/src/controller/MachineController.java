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
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Controller for performing auxiliary functions such as file opening
 * @author Nick Ahring
 */
public class MachineController {
    
    public MachineController() {

    }

    /**
     * Reads a given input file
     * @param file file to be read
     * @return string content of file
     */
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
    
     /**
     * Opens an example program with the requested name.
     * Example programs are built into the jar file.
     * @param fileToOpen requested example file
     * @return String representation of program
     * @throws IOException 
     */
    public String openExample(String fileToOpen) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream stream = MachineController.class.getClassLoader().getResourceAsStream(fileToOpen);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                String line;
                while ((line = br.readLine()) != null) {
                    
                        line = line.replaceAll("[ \t]*#[^\\n]*", "").trim();
                        if (!line.equals("")) {
                            sb.append(line);
                            sb.append("\n");
                        }
                }
        
        return sb.toString();
    }
}
