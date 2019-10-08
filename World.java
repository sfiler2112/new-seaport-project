import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class World extends Thing{

    private ArrayList<SeaPort> ports;
    private PortTime time;
    private HashMap<Integer,Thing> worldHashMap;
    private JPanel jobProgressBarPanel;
    
    public World(ArrayList<String> fileLines){
        Scanner lineScanner;
        ports = new ArrayList<>();
        worldHashMap = new HashMap<>();
        /*
        * Scan each line and give it to readLine.
        */
        for(String currentLine: fileLines){
            lineScanner = new Scanner(currentLine);
            readLine(lineScanner);
        }
    }



    private void readLine(Scanner scannerLine){
        String objectIdentifier;
        String objectName;
        int objectIndex;
        if(scannerLine.hasNext()){
            /*
             * Read first item from the scannerLine
             */
            objectIdentifier = scannerLine.next();

            switch (objectIdentifier){ // Based of the objectIdentifier, switch to a case for the object that needs to be built.  If it is not a recognized object or a comment line, print the line to console and continue.
                case "port":
                    objectName = scannerLine.next();
                    objectIndex = scannerLine.nextInt();
                    SeaPort port = new SeaPort(objectName, scannerLine, this);
                    worldHashMap.put(objectIndex, port);// adding a port to the worldHashMap, index should be 10000-19999
                    ports.add(port);
                    System.out.println("port added. index: " + objectIndex);
                    break;
                case "dock":
                    objectName = scannerLine.next();
                    objectIndex = scannerLine.nextInt();
                    Dock dock = new Dock(objectName, scannerLine, this);
                    SeaPort dockPort = (SeaPort) worldHashMap.get(dock.getParent());
                    dockPort.addToDocks(dock);
                    worldHashMap.put(objectIndex, dock);// adding a dock to the worldHashMap, index should be 20000-29999
                    System.out.println("dock added. index: " + objectIndex);
                    break;
                case "pship":
                    objectName = scannerLine.next();
                    objectIndex = scannerLine.nextInt();
                    PassengerShip pship = new PassengerShip(objectName, scannerLine);
                    Thing pshipDestination = worldHashMap.get(pship.getParent());
                    /*
                     * Check if the pship destination is a SeaPort.  Otherwise, it is a dock.  Add the pship to its destination accordingly.
                    */
                    if(pshipDestination.isSeaPort()){
                        SeaPort pshipPort = (SeaPort) pshipDestination;
                        pshipPort.addShip(pship);
                    } else {
                        Dock pshipDock = (Dock) pshipDestination;
                        pshipDock.addShip(pship);
                    }
                    worldHashMap.put(objectIndex, pship); // Add the pship to the worldHashMap.  Index is between 30000-39999.
                    System.out.println("pship added. index: " + objectIndex);
                    break;
                case "cship":
                    objectName = scannerLine.next();
                    objectIndex = scannerLine.nextInt();
                    CargoShip cship = new CargoShip(objectName, scannerLine);
                    Thing cshipDestination = worldHashMap.get(cship.getParent());
                    /*
                     * Check if the cship destination is a SeaPort.  Otherwise, it is a dock.  Add the cship to its destination accordingly.
                    */
                    if(cshipDestination.isSeaPort()){
                        SeaPort cshipPort = (SeaPort) cshipDestination;
                        cshipPort.addShip(cship);
                    } else {
                        Dock cshipDock = (Dock) cshipDestination;
                        cshipDock.addShip(cship);
                    }
                    worldHashMap.put(objectIndex, cship); // Add the cship to the worldHashMap.  Index is between 40000-49999.
                    System.out.println("cship added. index: " + objectIndex);
                    break;
                case "person":
                    objectName = scannerLine.next();
                    objectIndex = scannerLine.nextInt();
                    Person person = new Person(objectName, scannerLine);
                    SeaPort personPort = (SeaPort) worldHashMap.get(person.getParent());
                    personPort.addToPersons(person); // Add the dock to its parent SeaPort
                    worldHashMap.put(objectIndex, person); // Add person to the worldHashMap.  Index is between 50000-59999
                    System.out.println("person added. index: " + objectIndex);
                    break;
                case "job":
                    objectName = scannerLine.next();
                    objectIndex = scannerLine.nextInt();
                    Job job = new Job(objectName, objectIndex, scannerLine);
                    Thread jobThread = new Thread(job, job.getName());
                    jobThread.start();
                    Ship jobShip = (Ship) worldHashMap.get(job.getParent());  // Find the parent Ship for the job
                    jobShip.addJob(job); // add the job to its ship
                    worldHashMap.put(objectIndex, job); // Add job to the worldHashMap.  Index is between 60000-69999
                    System.out.println("job added. index: " + objectIndex);
                    break;
                case "//":
                    System.out.println("comment line");
                    break;
                default:
                    System.out.println("invalid object identifier: " + objectIdentifier);
            }
        }
    }

    public String searchForName(String targetName){
        /*
         * Check the ports of the world for matching name, and the objects encapsulated by within the port
         */
        String nameSearchResult = "";
        for(SeaPort currentPort: ports){
            if(currentPort.getName().equals(targetName)){
                nameSearchResult = nameSearchResult + currentPort.toString() + "\n";
            }
            nameSearchResult = nameSearchResult + currentPort.searchForName(targetName);
        }

        return nameSearchResult;
    }
    
    public Thing searchForIndex(int targetIndex){
        /*
         * Check the worldHashMap for matching index
        */

        if(worldHashMap.containsKey(targetIndex)){
            return worldHashMap.get(targetIndex);
        }
        
        return null; // The target index could not be found
    }

    public String searchForSkill(String targetSkill){
        /*
         * Search all the persons and jobs encapsulated within each port to find matches for the target skill.
         */
        String skillSearchResult = "";

        for(SeaPort currentPort: ports){
            skillSearchResult = skillSearchResult + currentPort.searchForSkill(targetSkill);
        }

        return skillSearchResult;
    }
    
    public void categorizedSort(String sortCategory, String sortOption){
        
        /*
         * Select desired sorting to use based on the category. 
        */
        switch(sortCategory){
            case "All":
                Collections.sort(ports, new NameComparator());
                for(SeaPort currentPort: ports){
                    currentPort.sortAllByName();
                }
                break;
            case "Ships":
                for(SeaPort currentPort: ports){
                    currentPort.sortShips(sortOption);
                }
                break;
            case "Persons":
                for(SeaPort currentPort: ports){
                    currentPort.sortPersons(sortOption);
                }
                break;
        }
    }

    public String displayCategory(String sortCategory){
        /*
         * Display the desired category's contents.
         */
        String categoryString = "";
        switch(sortCategory){
            case "All":
                categoryString = displayWorldString();
                break;
            case "Ships":
                for(SeaPort currentPort: ports){
                    categoryString = categoryString + currentPort.displayShips();
                }
                break;
            case "Persons":
                for(SeaPort currentPort: ports){
                    categoryString = categoryString + currentPort.displayPersons();
                }
                break;
            default:
                break;
        }
        return categoryString;
    }
    

    public String displayWorldString(){
        String worldString = "Welcome to the World!\n\nPorts:\n";
        
        for(SeaPort currentPort: ports){
            worldString = worldString + currentPort.displayPortString();
        }
        
        return worldString;
    }

    public DefaultMutableTreeNode getWorldRoot(){
        DefaultMutableTreeNode worldRoot = new DefaultMutableTreeNode("Earth");
        DefaultMutableTreeNode portNodes = new DefaultMutableTreeNode("Ports");
        /*
         * Add each port's return value for the tree node they must generate to the world's port node.
         */
        for(SeaPort currentPort: ports){
            portNodes.add(currentPort.getPortNode());
        }

        worldRoot.add(portNodes);
        return worldRoot;
    }

    public HashMap<Integer, Thing> getWorldHashMap(){
        return worldHashMap;
    }

    public ArrayList<SeaPort> getPorts(){
        return ports;
    }
}
