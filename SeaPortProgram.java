
/*
 *  Name: SeaPortProgram
 *  Date: 8/28/2019
 *  Author: Sean Filer
 *  Purpose: Create the GUI.  This will allow the user to select a file to build
 *  the world from.  The world will be displayed in the GUI.  Once the world is
 *  built, the GUI will provide options for searching the data structure or going back to the select a file button.
 */

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.*;

public class SeaPortProgram extends JFrame implements ActionListener, TreeSelectionListener{

    private Path filePath; // holds the path for the file selected by the user.
    private JTextArea worldText = new JTextArea(); //holds the world's toString return value
    private JTextArea searchResultsText = new JTextArea(); // holds the search results for a name, index, or skill
    private JTextField searchTargetField; // initialized in displaySearchOptions(), user enters the search target in this field
    private ButtonGroup searchRadioButtons; // initialized in displaySearchOptions(), user selects the type of search to run from these options
    private String categoryArray[];  // initialized in displaySortOptions(), sets the categories that require buttons
    private ButtonGroup categoryRadioButtons; // initialized in displaySortOptions(), user selects the category to sort from these options
    private JTextArea sortResultsText = new JTextArea();  // holds the sort results for a category and sort-by option
    private JPanel sortOptionButtonsPanel; // Will hold sort options depending on the category selected.  Updated with the method retrieveSortOptionButtonsPanel(String category)
    private ButtonGroup sortOptionRadioButtons; // initialized in retrieveSortOptionButtonsPanel();
    private World world;
    private JTree worldTree;
    private DefaultTreeModel worldTreeModel;
    private JPanel optionsPanel;

    public static void main(String[] args){
        SeaPortProgram programMain = new SeaPortProgram();
    }

    public SeaPortProgram(){ //This constructor will set up the initial window seen by the user when the program starts.
        super(" Sea Port Program");

        this.setVisible(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getDataFile();
    }

    public void valueChanged(TreeSelectionEvent treeSelected){
        this.pack();
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
                    try{
                        buildWorld();
                    } finally {
                        displayWorld();
                    }

                } else {
                    System.out.println("did not select file");
                }
                break;
            case "create new world":
                System.out.println("create new world button pressed");
                getDataFile();
                break;
            case "sort options":
                System.out.println("sort options button pressed");
                displaySortOptions();
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
                sortResultsText.setText(""); // Clear the sort results text area
                worldTreeModel = new DefaultTreeModel(world.getWorldRoot());
                worldTree = new JTree(worldTreeModel); // Refresh the world tree to reflect any sorting changes
                displayWorld();
                break;
            case "sort":
                System.out.println("sort button pressed");
                sortWorld();
                break;
            case "reset world":
                System.out.println("reset world button pressed");
                buildWorld();
                displayWorld();
                break;
            case "start jobs":
                System.out.println("start jobs button pressed");
                break;
            /*
             * The following cases are used when the user selects a sort category button to display the correct sort options for that category  
             */
            case "All":
                System.out.println("All button selected");
                sortOptionButtonsPanel.removeAll();
                sortOptionButtonsPanel.add(retrieveSortOptionPanel(actionCommand));
                this.pack();
                break;
            case "SeaPorts":
                System.out.println("SeaPorts button selected");
                sortOptionButtonsPanel.removeAll();
                sortOptionButtonsPanel.add(retrieveSortOptionPanel(actionCommand));
                this.pack();
                break;
            case "Docks":
                System.out.println("Docks button selected");
                sortOptionButtonsPanel.removeAll();
                sortOptionButtonsPanel.add(retrieveSortOptionPanel(actionCommand));
                this.pack();
                break;
            case "Ships":
                System.out.println("Ships button selected");
                sortOptionButtonsPanel.removeAll();
                sortOptionButtonsPanel.add(retrieveSortOptionPanel(actionCommand));
                this.pack();
                break;
            case "Persons":
                System.out.println("Persons button selected");
                sortOptionButtonsPanel.removeAll();
                sortOptionButtonsPanel.add(retrieveSortOptionPanel(actionCommand));
                this.pack();
                break;
            case "Jobs":
                System.out.println("Jobs button selected");
                sortOptionButtonsPanel.removeAll();
                sortOptionButtonsPanel.add(retrieveSortOptionPanel(actionCommand));
                this.pack();
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
        JSplitPane treeAndJobPane; // This will hold the world tree panel and job progress bar panel.
        JSplitPane displayPane; // This will hold the tree and job split pane and a panel with option buttons
        optionsPanel = new JPanel(); // Holds the option buttons for search and create new world
        optionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        /*
         * Create panel to hold the world tree
         */

        JPanel worldTreePanel = new JPanel();
        worldTreePanel.add(worldTree);

        /*
         * Create panel to hold the job progress bars.
         */
        JPanel jobProgressBarPanel = buildJobProgressBarPanel();

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
         * Add world tree and job progress bars panels to treeAndJobPane
         */
        JScrollPane worldTreeScrollPane = new JScrollPane(worldTreePanel);
        worldTreeScrollPane.getViewport().setPreferredSize(new Dimension(300,400));
        JScrollPane jobProgressScrollPane = new JScrollPane(jobProgressBarPanel);
        treeAndJobPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, worldTreeScrollPane, jobProgressScrollPane);

        /*
         *  Add treeAndJobPane and options panel to the display pane.
         */

//        worldTreeScrollPane.setMinimumSize(new Dimension(75,150));
        displayPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeAndJobPane, optionsPanel);
        displayPane.setSize(75, 150);
        displayPane.setResizeWeight(1);
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
                world = new World(fileLines);
                worldTreeModel = new DefaultTreeModel(world.getWorldRoot());
                worldTree = new JTree(worldTreeModel);
                worldText.setText(world.displayWorldString().trim());
            } catch (IOException io){
                /*Do nothing*/
            }

        }
    }

    public void displaySortOptions(){ // Sets the content pane to show the category selection (radio buttons) and corresponding sort options for each category. Also has button to go back to world display
        this.getContentPane().removeAll(); // Clear the content pane
        JPanel sortPanel = new JPanel();
        sortPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel categoryButtonsPanel = new JPanel(); // select category...
        categoryButtonsPanel.setBorder(new TitledBorder("Select Category..."));
        /*
         * Create the category select panel with radio buttons for each category: All, SeaPorts, Docks, Ships, Persons, Jobs.
         */
        categoryArray = new String[]{"All","Ships","Persons"}; // 3 options for category
        /*
         * Create for loop to go through each entry in categoryArray, make a radio button for the button group, and add an action command to that button, add that button to the panel
         */
        categoryRadioButtons = new ButtonGroup();
        String currentCategory = categoryArray[0];
        JRadioButton tempButton = new JRadioButton(currentCategory);
        tempButton.setActionCommand(currentCategory);
        tempButton.addActionListener(this);
        categoryButtonsPanel.setLayout(new GridLayout(1, 3));
        categoryRadioButtons.add(tempButton); // Add the first option to the categories panel, "All'
        categoryButtonsPanel.add(tempButton);
        categoryRadioButtons.setSelected(tempButton.getModel(), true); // Make "All" the default category.
        for(int i = 1; i < categoryArray.length; i++){
            currentCategory = categoryArray[i];
            tempButton = new JRadioButton(currentCategory);
            tempButton.setActionCommand(currentCategory);
            tempButton.addActionListener(this);
            categoryButtonsPanel.add(tempButton);
            categoryRadioButtons.add(tempButton);
        }
        sortOptionButtonsPanel = new JPanel();
        sortOptionButtonsPanel.add(retrieveSortOptionPanel(categoryArray[0]));
        gbc.gridy = 0;
        gbc.gridx = 0;
        sortPanel.add(categoryButtonsPanel,gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        sortPanel.add(sortOptionButtonsPanel, gbc);
        sortResultsText.setText("");
        gbc.gridy = 2;
        gbc.gridx = 0;
        sortPanel.add(sortResultsText, gbc);
        /*
         * Create panel for sort button and world display button
        */
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setLayout(new GridBagLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(this);
        backButton.setActionCommand("back");
        backButtonPanel.add(backButton); 
        /*
         * Create split pane to hold the sort options panel and the button panel.  Sort option will have the resize weight.
        */
        JSplitPane sortDisplayPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(sortPanel), backButtonPanel);
        sortDisplayPane.setResizeWeight(1);
        this.getContentPane().add(sortDisplayPane);
        this.pack();
    }

    /*
     * Create a method to retrieve an appropriate panel with options for a particular category.  retrieveSortOptionPanel(String option)?
     */
    public JPanel retrieveSortOptionPanel(String category){
        JPanel categoricalSortOptionPanel = new JPanel();
        categoricalSortOptionPanel.setBorder(new TitledBorder("Sort " + category +" By..."));
        JRadioButton nameButton = new JRadioButton("Name");
        nameButton.setActionCommand("name sort");
        sortOptionRadioButtons = new ButtonGroup();
        sortOptionRadioButtons.add(nameButton);
        sortOptionRadioButtons.setSelected(nameButton.getModel(), true);  // All things can be sorted by name, so this will be the default sort option for every category.
        categoricalSortOptionPanel.add(nameButton);
        /*
         * If the category is Ships, add sort options for draft, length, weight, and width
        */
        if(category.equals("Ships")){
            JRadioButton draftButton = new JRadioButton("Draft");
            draftButton.setActionCommand("draft sort");
            JRadioButton lengthButton = new JRadioButton("Length");
            lengthButton.setActionCommand("length sort");
            JRadioButton weightButton = new JRadioButton("Weight");
            weightButton.setActionCommand("weight sort");
            JRadioButton widthButton = new JRadioButton("Width");
            widthButton.setActionCommand("width sort");
            sortOptionRadioButtons.add(draftButton);
            sortOptionRadioButtons.add(lengthButton);
            sortOptionRadioButtons.add(weightButton);
            sortOptionRadioButtons.add(widthButton);
            categoricalSortOptionPanel.add(draftButton);
            categoricalSortOptionPanel.add(lengthButton);
            categoricalSortOptionPanel.add(weightButton);
            categoricalSortOptionPanel.add(widthButton);
        } else if (category.equals("Persons")){  // If the category is persons, add sort options for skill
            JRadioButton skillButton = new JRadioButton("Skill");
            skillButton.setActionCommand("skill sort");
            sortOptionRadioButtons.add(skillButton);
            categoricalSortOptionPanel.add(skillButton);
        }
        JPanel returnPanel = new JPanel();
        returnPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JButton sortButton = new JButton("Sort");
        sortButton.setActionCommand("sort");
        sortButton.addActionListener(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        returnPanel.add(categoricalSortOptionPanel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        returnPanel.add(sortButton, gbc);
        return returnPanel;
    }
    
    public void sortWorld(){
        sortResultsText.setText("");  // make sure the sort results start off blank.
        
        String sortCategory = categoryRadioButtons.getSelection().getActionCommand(); // identify the selected category button by the action command string
        String sortOption = sortOptionRadioButtons.getSelection().getActionCommand(); // identify the sorting option button by the action command string
        world.categorizedSort(sortCategory, sortOption);
        sortResultsText.setText(world.displayCategory(sortCategory));
        this.pack();
    }
    
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

    public JPanel buildJobProgressBarPanel(){
        System.out.println("building job progress bar panel");
        JPanel jobProgressPanel = new JPanel();
        jobProgressPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        ArrayList<SeaPort> worldPorts = world.getPorts();

        int portCounter = 0;
        /*
         * For each port in the world, check to see if it has docks and ships. If it does, a jobProgressPanel will be created.
         */
        for(SeaPort currentPort: worldPorts){
            gbc.gridy = portCounter;
            gbc.gridx = 0;
            if(!currentPort.getDocks().isEmpty() && !currentPort.getShips().isEmpty()){
                jobProgressPanel.add(new PortPanel(currentPort));
                Thread portThread = new Thread(currentPort, currentPort.getName());
                portThread.start();
                portCounter++;
            }
        }
        return jobProgressPanel;
    }
//    public void updateOptionsPanel(){  // Called when the user selects the "Start Jobs" button.
//        /*
//         * Remove options for sort and search, add option for resetting the current world
//         */
//
//        optionsPanel.removeAll();
//        GridBagConstraints gbc = new GridBagConstraints();
//
//        JButton resetWorldButton = new JButton("Reset World");
//        resetWorldButton.addActionListener(this);
//        resetWorldButton.setActionCommand("reset world");
//        gbc.gridy = 0;
//        gbc.gridx = 0;
//        optionsPanel.add(resetWorldButton, gbc);
//
//        JButton createNewWorldButton = new JButton("Create New World");
//        createNewWorldButton.addActionListener(this);
//        createNewWorldButton.setActionCommand("create new world");
//        gbc.gridy = 0;
//        gbc.gridx = 1;
//        optionsPanel.add(createNewWorldButton, gbc);
//
//        this.pack();
//    }
}