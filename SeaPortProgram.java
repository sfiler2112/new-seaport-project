
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
    private JTextField searchTargetField; // initialized in displaySearchOptions(), user enters the search target in this field
    private ButtonGroup searchRadioButtons; // initialized in displaySearchOptions(), user selects the type of search to run from these options
    String categoryArray[];  // initialized in displaySortOptions(), sets the categories that require buttons
    private ButtonGroup categoryRadioButtons; // initialized in displaySortOptions(), user selects the category to sort from these options
    private JTextArea sortResultsText = new JTextArea();  // holds the sort results for a category and sort-by option
    private JPanel sortOptionButtonsPanel; // Will hold sort options depending on the category selected.  Updated with the method updateSortOptionButtonsPanel(String category)
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
            case "search options": // Runs when the user selects the search option button from the world info window.
                System.out.println("search options button pressed");
                displaySearchOptions();
                break;
            case "search": // Runs when the user searches for a target
                System.out.println("search button pressed");
                searchWorld();
                break;
            case "back":
                System.out.println("back button pressed");
                searchResultsText.setText(""); // Clear the search results text area
                displayWorld();
                break;
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

        JSplitPane displayPane; // This will hold the world text panel and a panel with option buttons
        JPanel optionsPanel = new JPanel(); // Holds the option buttons for search and create new world
        optionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        /*
         * Create panel to hold the world text.
         */

        JPanel worldTextPanel = new JPanel();
        worldTextPanel.add(worldText);

        /*
         * Create the search, sort, and create new world buttons, add them to the options panel.
         */
        JButton searchOptionsButton = new JButton("Search Options");
        searchOptionsButton.addActionListener(this);
        searchOptionsButton.setActionCommand("search options");
        gbc.gridy = 0;
        gbc.gridx = 0;
        optionsPanel.add(searchOptionsButton, gbc);

        JButton sortOptionsButton = new JButton("Sort Options");
        sortOptionsButton.addActionListener(this);
        sortOptionsButton.setActionCommand("sort options");
        gbc.gridy = 0;
        gbc.gridx = 1;
        optionsPanel.add(sortOptionsButton, gbc);

        JButton createNewWorldButton = new JButton("Create New World");
        createNewWorldButton.addActionListener(this);
        createNewWorldButton.setActionCommand("create new world");
        gbc.gridy = 0;
        gbc.gridx = 2;
        optionsPanel.add(createNewWorldButton, gbc);

        /*
         *  Add world text panel and options panel to the display pane.
         */

        displayPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(worldTextPanel), optionsPanel);

        this.getContentPane().add(displayPane);
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
                worldText.setText(world.displayWorldString());
            } catch (IOException io){
                /*Do nothing*/
            }

        }
    }

    public void displaySortOptions(){ // Sets the content pane to show the category selection (radio buttons) and corresponding sort options for each category. Also has button to go back to world display
        this.getContentPane().removeAll(); // Clear the content pane
        JPanel sortPanel = new JPanel();
        JPanel categoryButtonsPanel = new JPanel(); // select category...
        sortOptionButtonsPanel = new JPanel(); // sort by..
        /*
         * Create the category select panel with radio buttons for each category: All, SeaPorts, Docks, Ships, Persons, Jobs.
         */
        categoryArray = new String[]{"All","Seaports","Docks","Ships","Persons","Jobs"};
        /*
         * Create for loop to go through each entry in categoryArray, make a radio button for the button group, and add an action command to that button, add that button to the panel
         */
        categoryRadioButtons = new ButtonGroup();
        String currentCategory = categoryArray[0];
        JRadioButton tempButton = new JRadioButton(currentCategory);
        categoryRadioButtons.add(tempButton); // Add the first option to the categories panel, "All'
        categoryRadioButtons.setSelected(tempButton.getModel(), true); // Make "All" the default category.
        for(int i = 1; i < categoryArray.length; i++){
            currentCategory = categoryArray[i];
            tempButton = new JRadioButton(currentCategory);
            tempButton.setActionCommand(currentCategory);
            categoryRadioButtons.add(tempButton);
        }
        categoryRadioButtons.setSelected(categoryRadioButtons.g)

    }

    /*
     * Create a method to retrieve an appropriate panel with options for a particular category.  retrieveSortOptionPanel(String option)?
     */
    
    public void displaySearchOptions(){ // Sets the content pane to show search options and a text area for search results.  Also has a button to go back to the previous window.
        this.getContentPane().removeAll();  // Clear the content pane
        
        /*
         * create the Search options panel with radio buttons, text field for search target, and search button.
        */
        JPanel searchOptionsPanel = new JPanel(); // Holds the search options: radio button panel with options, text field, search button
        JPanel searchRadioButtonsPanel = new JPanel(); // Holds the radio buttons for the search options.  Placed in the searchOptions panel.
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
        searchRadioButtonsPanel.add(indexButton);
        searchRadioButtonsPanel.add(skillButton);
        
        searchTargetField = new JTextField();
        searchTargetField.setColumns(15);
        searchTargetField.requestFocus();

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        searchButton.setActionCommand("search");
        
        searchOptionsPanel.add(searchRadioButtonsPanel);
        searchOptionsPanel.add(searchTargetField);
        searchOptionsPanel.add(searchButton);
        
        /*
         * Create search panel, contains the text area with the search results and the search options panel
        */
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        gbc.gridy = 0;
        gbc.gridx = 0;
        searchPanel.add(searchOptionsPanel, gbc);
        searchResultsText.setText("");
        gbc.gridy = 1;
        gbc.gridx = 0;
        searchPanel.add(searchResultsText,gbc);
        
        /*
         * Create back button panel, contains a back button that changes the content pane to display the world again
        */
        JPanel backButtonPanel = new JPanel(); // Holds the button that navigates back to the main window that displays all the world information
        backButtonPanel.setLayout(new GridBagLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(this);
        backButton.setActionCommand("back");
        backButtonPanel.add(backButton);

        /*
         * Create the split pane that holds the search panel and the back button panel
         */

        JSplitPane searchDisplayPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(searchPanel), backButtonPanel);
        searchDisplayPane.setResizeWeight(1);
        this.getContentPane().add(searchDisplayPane);
        this.pack();
    }

    public void searchWorld(){
        searchResultsText.setText("");  // Clear any previous search results or error messages from the search results area.
        String searchType = searchRadioButtons.getSelection().getActionCommand(); // Get the ActionCommand string for the search radio button that is currently selected.  Default choice is name.
        System.out.println(searchType);
        String searchTarget = searchTargetField.getText().trim(); // Get the text provided in the searchTargetField with any leading or trailing space removed.

        /*
         * Switch statement uses the searchType to determine what type of search method the world will use.  If the
         * search target field is empty or an invalid target, prints an error message in searchResults Text
         */
        if(searchTarget.equals("")){ // Check to see if text was provided by the user.
            System.out.println("invalid search target: no target provided.");
            searchResultsText.setText("No search target was provided.  Please provide a valid target.");
        } else {
            System.out.println("search target text provided: " + searchTarget);
            switch(searchType){
                case "name":
                    String nameSearchResults = world.searchForName(searchTarget).trim();
                    if(nameSearchResults.equals("")){
                        System.out.println("nothing matched the provided name");
                        searchResultsText.setText("No matches found for the provided name.");
                    } else {
                        System.out.println("match found for provided name");
                        searchResultsText.setText(nameSearchResults);
                    }
                    break;
                case "index":
                    try{ // Check to make sure the text provided is a number, otherwise throw a NumberFormatException
                        Thing thingFound = world.searchForIndex(Integer.valueOf(searchTarget));
                        if(thingFound == null){
                            System.out.println("nothing matched the provided index");
                            searchResultsText.setText("No matches found for the provided index.");
                        } else {
                            System.out.println("match found for provided index");
                            searchResultsText.setText(thingFound.toString());
                        }
                    } catch (NumberFormatException nfe){
                        System.out.println("invalid search target: invalid target for index search provided");
                        searchResultsText.setText("Invalid target provided for index search.  Please provide an integer value.");
                    }
                    break;
                case "skill":
                    String skillSearchResult = world.searchForSkill(searchTarget).trim();
                    if(skillSearchResult.equals("")){
                        System.out.println("nothing matched the provided skill");
                        searchResultsText.setText("No matches found for the provided skill.");
                    } else {
                        System.out.println("match found for provided skill");
                        searchResultsText.setText(skillSearchResult);
                    }
                    break;
            }
        }
        this.pack(); // Resize the window to fit new search results or error message in the search results text area.
    }
}