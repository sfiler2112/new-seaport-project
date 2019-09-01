
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
    private JTextArea searchResultsText = new JTextArea(); // holds the search results for a name, index, or skill
    private ButtonGroup searchRadioButtons;
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
                break;
            case "search options":
                System.out.println("search options button pressed");
                displaySearchOptions();
                break;
            case "back":
                System.out.println("back button pressed");
                searchResultsText.setText(""); // Clear the search results text area
                displayWorld();
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
        JButton searchOptionsButton = new JButton("Search Options");
        searchOptionsButton.addActionListener(this);
        searchOptionsButton.setActionCommand("search options");
        gbc.gridy = 0;
        gbc.gridx = 0;
        optionsPanel.add(searchOptionsButton, gbc);

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
    
    public void displaySearchOptions(){ // Sets the content pane to show search options and a text area for search results.  Also has a button to go back to the previous window.
        this.getContentPane().removeAll();  // Clear the content pane
        
        /*
         * create the Search options panel with radio buttons, text field for search target, and search button.
        */
        JPanel searchOptionsPanel = new JPanel(); // Holds the search options: radio button panel with options, text field, search button
        JPanel searchRadioButtonsPanel = new JPanel(); // Holds the readio buttons for the search options.  Placed in the searchOptions panel.
        searchOptionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        searchOptionsPanel.setBorder(new TitledBorder("Search Options"));
        
        JRadioButton nameButton = new JRadioButton("Name");
        JRadioButton indexButton = new JRadioButton("Index");
        JRadioButton skillButton = new JRadioButton("Skill");
        nameButton.setActionCommand("name");
        indexButton.setActionCommand("index");
        skillButton.setActionCommand("skill");

        searchRadioButtons = new ButtonGroup();
        searchRadioButtons.add(nameButton);
        searchRadioButtons.add(indexButton);
        searchRadioButtons.add(skillButton);
        searchRadioButtons.setSelected(nameButton.getModel(), true);
        
        searchRadioButtonsPanel.setLayout(new GridLayout(3,1));
        searchRadioButtonsPanel.add(nameButton);
        searchRadioButtonsPanel.add(nameButton);
        searchRadioButtonsPanel.add(indexButton);
        searchRadioButtonsPanel.add(skillButton);
        
        JTextField searchTargetField = new JTextField();
        searchTargetField.setColumns(15);
        searchTargetField.requestFocus();

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchButton.setActionCommand("search");
        
        searchOptionsPanel.add(searchRadioButtonsPanel);
        searchOptionsPanel.add(searchTargetField);
        searchOptionsPanel.add(searchButton);
        
        /*
         * Create search results panel, contains a scroll pane with a text area that updates with the results of the target search
        */
        JPanel searchResultsPanel = new JPanel(); // Holds the text area with the search results
        searchResultsText.setText("");
        searchResultsPanel.add(searchResultsText);
        
        /*
         * Create back button panel, contains a back button that changes the content pane to display the world again
        */
        JPanel backButtonPanel = new JPanel(); // Holds the button that navigates back to the main window that displays all the world information
        JButton backButton = new JButton("Back");
        backButton.addActionListener(this);
        backButton.setActionCommand("back");
        backButtonPanel.add(backButton);
        
        /*
         * Create scroll pane to hold all content
         */
        JPanel searchDisplayPanel = new JPanel(); // Holds the searchOptions, searchResults, and backButton panels.
        searchDisplayPanel.setLayout(new GridBagLayout());
        gbc.gridy = 0;
        gbc.gridx = 0;
        searchDisplayPanel.add(searchOptionsPanel, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        searchDisplayPanel.add(searchResultsPanel, gbc);
        gbc.gridy = 2;
        gbc.gridx = 0;
        searchDisplayPanel.add(backButtonPanel, gbc); 
        

        this.getContentPane().add(new JPanel().add(new JScrollPane(searchDisplayPanel)));
        this.pack();
    }
}