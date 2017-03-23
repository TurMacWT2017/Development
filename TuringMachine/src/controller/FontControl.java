/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * Java class for control of custom fxml element Font Control
 * @author Nick Ahring
 */
public class FontControl extends TabPane {
        //keeps track of font settings
        private String family;
        private String newFamily;
        private int size;
        private int newSize;
        private boolean isBold;
        private boolean isItalic;
        private Color RWHeadFontColor;
        private Color newRWHeadFontColor;
        
        //fonts list
        private ObservableList<String> fonts;
        private ObservableList<String> fontSizes;
        //Main Tab
        @FXML private TextFlow previewBar;
        @FXML private CheckBox boldCheckBox;
        @FXML private CheckBox italicCheckBox;
        @FXML private ComboBox sizeChooserBox;
        @FXML private ComboBox fontChooserBox;
        @FXML private Button acceptButton;
        @FXML private Button cancelButton;
        //RWHead Tab
        @FXML private TextFlow RWPreviewBar;
        @FXML private ColorPicker RWColorPicker;
        @FXML private Button RWacceptButton;
        @FXML private Button RWcancelButton;
        
    public FontControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/font_control.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    /*
    *   Used to initalize the fields of the component
    *   NOTE: when this component is used elsewhere, this should also be 
    *       called to initalize its components properly after they are made
    */
    public void initialize(String defaultFamily, int defaultSize, boolean bold, boolean italic, Color RWHead) {
        family = defaultFamily;
        size = defaultSize;
        //keep these at default values too until they are changed for consistency
        newFamily = defaultFamily;
        newSize = defaultSize;
        isBold = bold;
        isItalic = italic;
        RWHeadFontColor = RWHead;
        newRWHeadFontColor = RWHead;
        //set main preview bar
        Text previewText = new Text("AaBbCcZz1234");
        previewText.setFont(getCurrentFontSettings());
        previewBar.getChildren().add(previewText);
        //set rwhead preview bar
        Text rwpreviewText = new Text("A");
        rwpreviewText.setFont(getCurrentFontSettings());
        rwpreviewText.setFill(RWHead);
        RWPreviewBar.getChildren().add(rwpreviewText);
        rwpreviewText = new Text("aBbCcZz1234");
        rwpreviewText.setFont(getCurrentFontSettings());
        RWPreviewBar.getChildren().add(rwpreviewText);
        //fill font sizes combobox
        fontSizes = FXCollections.observableArrayList("8", "10", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36", "38");
        sizeChooserBox.getItems().addAll(fontSizes);
        //show what the default as the initial item in both combo boxes
        sizeChooserBox.getSelectionModel().select(Integer.toString(size));
        //fill combobox with all available system fonts
        String fontsList[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        ArrayList fontArray = new ArrayList<>(Arrays.asList(fontsList));
        fonts = FXCollections.observableArrayList(fontArray);
        fontChooserBox.getItems().addAll(fonts);
        //set the combo box default item to show as the current font
        fontChooserBox.getSelectionModel().select(family);
        fontChooserBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                newFamily = t1;
                updatePreviewBar();
            }    
        });
        sizeChooserBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                newSize = Integer.parseInt(t1);
                updatePreviewBar();
            }    
        });
        
        //restore any previous bold or italic settings
        if (isBold) {
            boldCheckBox.setSelected(true);
        }
        if (isItalic) {
            italicCheckBox.setSelected(true);
        }
        
        //color picker listener and setting of default value
        RWColorPicker.setValue(RWHeadFontColor);
        RWColorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                newRWHeadFontColor = RWColorPicker.getValue();
                updateRWHeadPreviewBar();
            }
        });
        
        
        
    }
    
    @FXML
    private void updatePreviewBar() {
        previewBar.getChildren().clear();
        Text previewText = new Text("AaBbCcZz1234");
        previewText.setFont(getCurrentFontSettings());
        previewBar.getChildren().add(previewText);
    }
    
    @FXML
    private void updateRWHeadPreviewBar() {
        //set rwhead preview bar
        RWPreviewBar.getChildren().clear();
        Text rwpreviewText = new Text("A");
        rwpreviewText.setFont(getCurrentFontSettings());
        rwpreviewText.setFill(newRWHeadFontColor);
        RWPreviewBar.getChildren().add(rwpreviewText);
        rwpreviewText = new Text("aBbCcZz1234");
        rwpreviewText.setFont(getCurrentFontSettings());
        RWPreviewBar.getChildren().add(rwpreviewText);
    }
    
    @FXML
    private void acceptChanges(ActionEvent event) {
        //set all the new values
        family = newFamily;
        size = newSize;
        RWHeadFontColor = newRWHeadFontColor;
        //then exit
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void cancelChanges(ActionEvent event) {
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void boldCheckBoxClicked(ActionEvent event) {
        isBold = boldCheckBox.isSelected();
        updatePreviewBar();
    }

    @FXML
    private void italicCheckBoxClicked(ActionEvent event) {
        isItalic = italicCheckBox.isSelected();
        updatePreviewBar();
          
    }
    
    public int getFontSize() {
        return size;
    }
    
    public String getFontFamily() {
        return family;
    }
    
    public boolean getIsBold() {
        return isBold;
    }
    
    public boolean getIsItalic() {
        return isItalic;
    }
    
    public Color getRWHeadFillColor() {
        return RWHeadFontColor;
    }
    
    /**
     * Gets the font currently set to style preview bars
     * @returns Font current font
     */
    private Font getCurrentFontSettings() {
        if (isBold && isItalic) {
            return Font.font(newFamily, FontWeight.BOLD, FontPosture.ITALIC, newSize);
        }
        else if (isBold) {
            return Font.font(newFamily, FontWeight.BOLD, newSize);
        }
        else if (isItalic) {
            return Font.font(newFamily, FontPosture.ITALIC, newSize);
        }
        else {
            return Font.font(newFamily, newSize);
        }   
    }
    
    /**
     * Gets the current fill color for the RW Head Character
     * @return Color current highlight color 
     */
    private Color getCurrentRWHeadFontColor() {
        return RWHeadFontColor;
    }
    
}
