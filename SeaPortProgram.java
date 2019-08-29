
/*
 *  Name: SeaPortProgram
 *  Date: 8/28/2019
 *  Author: Sean Filer
 *  Purpose: Create the GUI.  This will allow the user to select a file to build
 *  the world from.  The world will be displayed in the GUI.  Once the world is
 *  built, the GUI will provide options for searching the data structure or going back to the select a file button.
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
    private JTextArea worldText = new JTextArea(); //holds the world's toString return value
    private World world;

    public static void main(String[] args){
        SeaPortProgram programMain = new SeaPortProgram();
    }

    public SeaPortProgram(){ //This constructor will set up the initial window seen by the user when the program starts.
        super("Sea Port Program");

        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getDataFile();
    }

    public void actionPerformed(ActionEvent buttonPressed){ //Runs when the user presses a button.
        String actionCommand = buttonPressed.getActionCommand();

        switch(actionCommand){
            case "select": // Select a text file from the user's file directory
                System.out.println("select button pressed");
                JFileChooser fileChooser = new JFileChooser(new java.io.File("."));
                fileChooser.setDialogTitle("File Selection");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int selectionResult = fileChooser.showOpenDialog(this);

                if(selectionResult==JFileChooser.APPROVE_OPTION){
                    System.out.println("selected file");
                    filePath = Paths.get(fileChooser.getSelectedFile().getAbsolutePath());
                    buildWorld();
                    displayWorld();
                } else {
                    System.out.println("did not select file");
                }
                break;
            case "create new world":
                System.out.println("create new world button pressed");
                getDataFile();
        }
    }

    public void getDataFile(){
        this.getContentPane().removeAll();

        JPanel initialPanel = new JPanel(); // This panel will contain a button that opens the JFileChooser
        initialPanel.setBorder(new EmptyBorder(50,75,50,75)); // Empty border used to create space around the button
        JButton selectButton = new JButton("Select Data File");
        selectButton.addActionListener(this);
        selectButton.setActionCommand("select");
        initialPanel.add(selectButton);

        this.getContentPane().add(initialPanel);
        this.pack();
    }

    public void displayWorld(){ // Sets the content pane to display the newly built world and additional options
        this.getContentPane().removeAll();

        JPanel displayPanel = new JPanel(); // This will hold the world text and a panel with additional options (search, create new world).
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridBagLayout());
        displayPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();


        gbc.gridy = 0;
        gbc.gridx = 0;
        displayPanel.add(worldText, gbc);

        /*
         * Create the search and create new world buttons, add them to the options panel.
         */
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchButton.setActionCommand("search");
        gbc.gridy = 0;
        gbc.gridx = 0;
        optionsPanel.add(searchButton, gbc);

        JButton createNewWorldButton = new JButton("Create New World");
        createNewWorldButton.addActionListener(this);
        createNewWorldButton.setActionCommand("create new world");
        gbc.gridy = 0;
        gbc.gridx = 1;
        optionsPanel.add(createNewWorldButton, gbc);

        /*
         *  Add options panel to the display panel.
         */
        gbc.gridy = 1;
        gbc.gridx = 0;
        displayPanel.add(optionsPanel, gbc);

        /*
         * Create scroll pane to hold all content
         */
        JScrollPane contentScrollPane = new JScrollPane(displayPanel);

        this.getContentPane().add(new JPanel().add(contentScrollPane));
        this.pack();
    }

    public void buildWorld(){ // Initializes a World object with the data file selected
        /*
         *  Read the file provided by the filePath and send that to World to build the world.
         */
        if(Files.isReadable(filePath)){
            try{
                ArrayList<String> fileLines = (ArrayList<String>)Files.readAllLines(filePath); //Create an ArrayList containing all the lines from the data file
//                String testString = "";
//                for(String line: fileLines){
//                    testString = testString + line + "\n";
//                }
                world = new World(fileLines);
                worldText.setText(world.toString());
            } catch (IOException io){
                /*Do nothing*/
            }

        }
    }
}