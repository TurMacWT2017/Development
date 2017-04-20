/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Main application class. Launches the Primary FXML application
 * @author Nick Ahring
 */
public class TuringMachine extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/machine_view.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Turing Machine");
        
        stage.show();

        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void stop() {
        setUserTapePreferences(controller.MachineViewController.tapes);
        model.Interpreter.haltSimulation();
        
    }
    
    /**
     * Returns the user font preferences
     * The preference is read from the OS specific registry. If no such
     * preference can be found, an appropriate value is returned.
     * 
     * @return settings, an object array containing the retrieved settings
     */
    public static Object[] getUserFontPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(TuringMachine.class);
        //settings is kept in an object array for the purpose of returning the settings
        //see the portion of the controller in initialize of the view controller to see
        //the values being retrieved and set
        Object[] settings = new Object[7];        
        String family = prefs.get("family", null);
        int size = prefs.getInt("size", 28);
        boolean isItalic = prefs.getBoolean("isItalic", false);
        boolean isBold = prefs.getBoolean("isBold", false);
        
        settings[0] = family;
        settings[1] = size;
        settings[2] = isItalic;
        settings[3] = isBold;
        return settings;
        
    }
    
    /**
    * Sets the users font settings. The settings are persisted in the OS specific registry
    * 
    * @param family font family
     * @param size size of font
     * @param isItalic if font is italic
     * @param isBold if font is bold
    */
    public static void setUserFontPreferences(String family, int size, boolean isItalic, boolean isBold) {
        Preferences prefs = Preferences.userNodeForPackage(TuringMachine.class);
        prefs.put("family", family);
        prefs.putInt("size", size);
        prefs.putBoolean("isItalic", isItalic);
        prefs.putBoolean("isBold", isBold);
    }
    
    /**
     * Returns the user code font preferences
     * The preference is read from the OS specific registry. If no such
     * preference can be found, an appropriate default is returned.
     * 
     * @return settings, an object array containing the retrieved settings
     */
    public static Object[] getUserCodeFontPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(TuringMachine.class);
        //settings is kept in an object array for the purpose of returning the settings
        //see the portion of the controller in initialize of the view controller to see
        //the values being retrieved and set
        Object[] settings = new Object[4];
        String codeFamily = prefs.get("codeFamily", "Courier");
        int codeSize = prefs.getInt("codeSize", 14);
        boolean isCodeItalic = prefs.getBoolean("isCodeItalic", false);
        boolean isCodeBold = prefs.getBoolean("isCodeBold", false);
        
        settings[0] = codeFamily;
        settings[1] = codeSize;
        settings[2] = isCodeItalic;
        settings[3] = isCodeBold;
        return settings;
        
    }
    
    /**
    * Sets the users code font settings. The settings are persisted in the OS specific registry
     * @param cFamily code family
     * @param cSize code font size
     * @param isCodeItalic whether code is italic
     * @param isCodeBold whether code is bold
    */
    public static void setUserCodeFontPreferences(String cFamily, int cSize, boolean isCodeItalic, boolean isCodeBold) {
        Preferences prefs = Preferences.userNodeForPackage(TuringMachine.class);
        prefs.putInt("codeSize", cSize);
        prefs.put("codeFamily", cFamily);
        prefs.putBoolean("isCodeItalic", isCodeItalic);
        prefs.putBoolean("isCodeBold", isCodeBold);
    }
    
    /**
     * Returns the user rwhead head color preference
     * The preference is read from the OS specific registry. If no such
     * preference can be found, a default of maroon is returned.
     * 
     * @return settings, an object array containing the retrieved settings
     */
    public static Color getUserRWHeadPreferences() {
        Preferences prefs = Preferences.userNodeForPackage(TuringMachine.class);
        //gets the color string stored, the default is red if none
        String colorString = prefs.get("RWHeadColor", Color.MAROON.toString());
        return Color.valueOf(colorString);       
    }
    
    /**
    * Sets the users rwhead color preference. The settings are persisted in the OS specific registry
    * 
    * @param color new color to store as default
    */
    public static void setUserRWHeadPreferences(Color color) {
        Preferences prefs = Preferences.userNodeForPackage(TuringMachine.class);
        prefs.put("RWHeadColor", color.toString());
    }
    
    
    /**
     * Sets the user's tape preference (these will always be saved as a convenience to the user)
     * @param tapes number of tapes
     */
    public static void setUserTapePreferences(int tapes) {
        Preferences prefs = Preferences.userNodeForPackage(TuringMachine.class);
        prefs.putInt("activeTapesSetting", tapes);
    }
    
    /**
     * Gets the user's tape preference (these will always be saved as a convenience to the user)
     * @return number of tapes
     */
    public static int getUserTapePreferences() {
        Preferences prefs = Preferences.userNodeForPackage(TuringMachine.class);
        int tapes = prefs.getInt("activeTapesSetting", 1);
        return tapes;
    }
    
}
