/*
 *  Name: SeaPortProgram
 *  Date: 8/28/2019
 *  Author: Sean Filer
 *  Purpose: Create the GUI.  This will allow the user to select a file to build 
 *  the world from.  The world will be displayed in the GUI.  Once the world is 
 *  built, the GUI will provide options for searching the data structure.
 */

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class SeaPortProgram extends JFrame implements ActionListener{
    
    private Path filePath; // holds the path for the file selected by the user.
    
    public static void main(String[] args){
        SeaPortProgram programMain = new SeaPortProgram();
    }
    
    public SeaPortProgram(){ //This constructor will set up the initial window seen by the user when the program starts.
        super("Sea Port Program");
        
        JPanel initialPanel = new JPanel(); // This panel will contain a button that opens the JFileChooser
        initialPanel.setBorder(new EmptyBorder(50,75,50,75)); // Empty border used to create space around the button
        JButton selectButton = new JButton("Select Data File");
        selectButton.addActionListener(this);
        selectButton.setActionCommand("select");
        initialPanel.add(selectButton);
        
        this.getContentPane().add(initialPanel);
        this.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent buttonPressed){ //Runs when the user presses a button.  
        String actionCommand = buttonPressed.getActionCommand();
        
        switch(actionCommand){
            case "select":
                System.out.println("select button pressed");
                JFileChooser fileChooser = new JFileChooser(new java.io.File("."));
                fileChooser.setDialogTitle("File Selection");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int selectionResult = fileChooser.showOpenDialog(this);
                
                if(selectionResult==JFileChooser.APPROVE_OPTION){
                    System.out.println("selected file");
                    filePath = Paths.get(fileChooser.getSelectedFile().getAbsolutePath());
                    buildWorld();
                } else {
                    System.out.println("did not select file");
                }
                break;
        }
    }
    
    public void buildWorld(){ // Initalizes a World object with the data file selected
        this.getContentPane().removeAll();
        
        
        
        // Create display panel, options panel.  Options panel should include search and select new file.  Select new file should run the constructor again.
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        
        JTextArea worldText = new JTextArea();
        
        if(Files.isReadable(filePath)){
            try{
                ArrayList<String> fileLines = (ArrayList<String>)Files.readAllLines(filePath); //Create an ArrayList containing all the lines from the data file
                String testString = "";
                for(String line: fileLines){
                    testString = testString + line + "\n";
                }
                worldText.setText(testString);
            } catch (IOException io){
                /*Do nothing*/
            }
            
        }
        displayPanel.add(worldText);
        
        JScrollPane displayScrollPane = new JScrollPane(displayPanel);
        displayScrollPane.setMinimumSize(new Dimension(250,375));
        this.getContentPane().add(displayScrollPane);
        this.pack();
    }
}
