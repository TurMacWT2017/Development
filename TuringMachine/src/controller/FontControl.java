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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
        private String codeFamily;
        private String newCodeFamily;
        private int codeSize;
        private int newCodeSize;
        private boolean isBold;
        private boolean isItalic;
        private boolean isCodeBold;
        private boolean isCodeItalic;
        private boolean fontDefault;
        private boolean codeDefault;
        private boolean rwDefault;
        private Color RWHeadFontColor;
        private Color newRWHeadFontColor;
        
        //fonts list
        private ObservableList<String> fonts;
        private ObservableList<String> fontSizes;
        //Main Window
        @FXML private TabPane fontControlTabs;
        //Main Tab
        @FXML private TextFlow previewBar;
        @FXML private CheckBox boldCheckBox;
        @FXML private CheckBox italicCheckBox;
        @FXML private CheckBox fontDefaultToggle;
        @FXML private ComboBox sizeChooserBox;
        @FXML private ComboBox fontChooserBox;
        @FXML private Button acceptButton;
        @FXML private Button cancelButton;
        //RWHead Tab
        @FXML private TextFlow RWPreviewBar;
        @FXML private ColorPicker RWColorPicker;
        @FXML private Button RWacceptButton;
        @FXML private Button RWcancelButton;
        @FXML private CheckBox RWDefaultToggle;
        //Code tab
        @FXML private TextFlow codePreviewBar;
        @FXML private CheckBox boldCodeCheckBox;
        @FXML private CheckBox italicCodeCheckBox;
        @FXML private CheckBox codeDefaultToggle;
        @FXML private ComboBox codeSizeChooserBox;
        @FXML private ComboBox codeFontChooserBox;
        @FXML private Button codeAcceptButton;
        @FXML private Button codeCancelButton;
        
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
    
    /**
    *   Used to initialize the fields of the component
    *   NOTE: when this component is used elsewhere, this should also be 
    *       called to initialize its components properly after they are made
     * @param defaultFamily default font family
     * @param defaultSize default font size
     * @param bold whether font is bold
     * @param italic whether font is italicized
     * @param RWHead RWHead color
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
        //initializes combo boxes
        initializeFontTabComboBoxes();
        initializeCodeTabComboBoxes();
        //restore any previous bold or italic settings
        if (isBold) {
            boldCheckBox.setSelected(true);
        }
        if (isItalic) {
            italicCheckBox.setSelected(true);
        }
        //restore any previous code tab bold or italic settings
        if (isCodeBold) {
            boldCodeCheckBox.setSelected(true);
        }
        if (isCodeItalic) {
            italicCodeCheckBox.setSelected(true);
        }
        
        //color picker listener and setting of default value
        RWColorPicker.setValue(RWHeadFontColor);
        RWColorPicker.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                newRWHeadFontColor = RWColorPicker.getValue();
                updateRWHeadPreviewBar();
            }
        });
        
        fontControlTabs.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> ov, Tab t, Tab t1) -> {
            updatePreviewBar();
            updateRWHeadPreviewBar();
            updateCodePreviewBar();
        });
        
        
    }
    
    /**
     * Updates the preview bar contained within the font settings tab
     */
    @FXML
    private void updatePreviewBar() {
        previewBar.getChildren().clear();
        Text previewText = new Text("AaBbCcZz1234");
        previewText.setFont(getCurrentFontSettings());
        previewBar.getChildren().add(previewText);
    }
    
    /**
     * Updates the preview bar contained within the RW head settings tab
     */
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
    
    /**
     * Updates the preview bar contained within the code settings tab
     */
    @FXML
    private void updateCodePreviewBar() {
        codePreviewBar.getChildren().clear();
        Text previewText = new Text("AaBbCcZz1234");
        previewText.setFont(getCurrentCodeSettings());
        codePreviewBar.getChildren().add(previewText);
    }
    
    /**
     * Handles user click on Accept Changes and closes the window
     * @param event ActionEvent
     */
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
    
    /**
     * Handles user click on Cancel and closes the window
     * @param event ACtionEvent
     */
    @FXML
    private void cancelChanges(ActionEvent event) {
        Node  source = (Node)  event.getSource(); 
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Handles user click on bold check box, and calls method to update preview
     * @param event ActionEvent
     */
    @FXML
    private void boldCheckBoxClicked(ActionEvent event) {
        isBold = boldCheckBox.isSelected();
        updatePreviewBar();
    }

    /**
     * Handles user click on italicize check box, and calls method to update 
     * preview
     * @param event ActionEvent
     */
    @FXML
    private void italicCheckBoxClicked(ActionEvent event) {
        isItalic = italicCheckBox.isSelected();
        updatePreviewBar();
          
    }
    
    /**
     * Handles user click on code bold check box, and calls method to update preview
     * @param event ActionEvent
     */
    @FXML
    private void boldCodeCheckBoxClicked(ActionEvent event) {
        isCodeBold = boldCodeCheckBox.isSelected();
        updateCodePreviewBar();
    }

    /**
     * Handles user click on code italicize check box, and calls method to update 
     * preview
     * @param event ActionEvent
     */
    @FXML
    private void italicCodeCheckBoxClicked(ActionEvent event) {
        isCodeItalic = boldCodeCheckBox.isSelected();
        updateCodePreviewBar();
          
    }
    
    /**
     * Handles user click on make default checkbox for font settings
     * @param event ActionEvent
     */
    @FXML
    private void makeFontDefault(ActionEvent event) {
        fontDefault = fontDefaultToggle.isSelected();
    }
    
    /**
     * Handles user click on make default checkbox for RW head settings
     * @param event ActionEvent
     */
    @FXML
    private void makeRWDefault(ActionEvent event) {
        rwDefault = RWDefaultToggle.isSelected();
    }
    
    /**
     * Returns a boolean value indicating whether "Make Default"
     * of font settings was selected
     * @return whether font will be new default 
     */
    public boolean getIsDefaultFont() {
        return fontDefault;
    }
    
    /**
     * Returns a boolean value indicating whether "Make Default" of RW
     * Head Settings was Selected
     * @return whether RW head settings will be new default
     */
    public boolean getIsDefaultRW() {
        return rwDefault;
    }
    
    
    /**
     * Returns a boolean value indicating whether "Make Default" of code
     * Settings was Selected
     * @return whether RW head settings will be new default
     */
    public boolean getIsDefaultCode() {
        return codeDefault;
    }
    
    /**
     * Returns the current font size setting
     * @return int font size
     */
    public int getFontSize() {
        return size;
    }
    
    /**
     * Returns the current setting for font family
     * @return font family
     */
    public String getFontFamily() {
        return family;
    }
    
    /**
     * Returns the current setting for whether font is bold
     * @return boolean isBold 
     */
    public boolean getIsBold() {
        return isBold;
    }
    
    /**
     * Returns the current setting for whether font is italicized
     * @return boolean isItalic
     */
    public boolean getIsItalic() {
        return isItalic;
    }
    
    /**
     * Returns the current setting for color of the RW Head
     * @return color RWHeadFontColor
     */
    public Color getRWHeadFillColor() {
        return RWHeadFontColor;
    }
    
    /**
     * Gets the font currently set to style preview bars
     * @return Font current font
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
     * Gets the font currently set in code tab to style code preview bar
     * @return Font current font
     */
    private Font getCurrentCodeSettings() {
        if (isCodeBold && isCodeItalic) {
            return Font.font(newCodeFamily, FontWeight.BOLD, FontPosture.ITALIC, newCodeSize);
        }
        else if (isCodeBold) {
            return Font.font(newCodeFamily, FontWeight.BOLD, newCodeSize);
        }
        else if (isCodeItalic) {
            return Font.font(newCodeFamily, FontPosture.ITALIC, newCodeSize);
        }
        else {
            return Font.font(newCodeFamily, newCodeSize);
        }   
    }
    
    /**
     * Gets the current fill color for the RW Head Character
     * @return Color current highlight color 
     */
    private Color getCurrentRWHeadFontColor() {
        return RWHeadFontColor;
    }
    
    /**
     * Does all of the initializing for the font tab combo boxes
     */
    private void initializeFontTabComboBoxes() {
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
        
    }
    
    /**
     * Does all of the initializing for the code tab combo boxes
     */
    private void initializeCodeTabComboBoxes() {
        //fill font sizes combobox
        codeSizeChooserBox.getItems().addAll(fontSizes);
        //show what the default as the initial item in both combo boxes
        codeSizeChooserBox.getSelectionModel().select(Integer.toString(size));
        //fill combobox with all available system fonts
        codeFontChooserBox.getItems().addAll(fonts);
        //set the combo box default item to show as the current font
        codeFontChooserBox.getSelectionModel().select(family);
        codeFontChooserBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                newCodeFamily = t1;
                updateCodePreviewBar();
            }    
        });
        codeSizeChooserBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                newCodeSize = Integer.parseInt(t1);
                updateCodePreviewBar();
            }    
        });
    }
}
