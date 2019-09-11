import java.util.*;

public class World extends Thing{

    private ArrayList<SeaPort> ports;
    private PortTime time;
    private TreeMap<Integer,Thing> worldTreeMap;
    
    public World(ArrayList<String> fileLines){
        Scanner lineScanner;
        ports = new ArrayList<>();
        worldTreeMap = new TreeMap<>();
        /*
        * Scan each line and give it to readLine.
        */
        for(String currentLine: fileLines){
            lineScanner = new Scanner(currentLine);
            readLine(lineScanner);
        }

        System.out.println("world tree size: " + worldTreeMap.size());
    }

    public void readLine(Scanner scannerLine){
        String objectIdentifier;
        if(scannerLine.hasNext()){
            objectIdentifier = scannerLine.next();
            /*
             * Based of the objectIdentifier, switch to a case for the object that needs to be built.  If it is not a recognized object or a comment line, print the line to console and continue.
            */
            switch (objectIdentifier){
                case "port":
                    SeaPort port = new SeaPort(scannerLine, this);
                    ports.add(port);
                    worldTreeMap.put(port.getIndex(), port); // adding a port to the worldTreeMap, index should be 10000-19999
                    break;
                case "dock":
                    Dock dock = new Dock(scannerLine, this);
                    SeaPort dockPort = (SeaPort) searchForIndex(dock.getParent());  // Find the parent SeaPort for the dock
                    dockPort.addToDocks(dock); // Add the dock to its parent SeaPort
                    worldTreeMap.put(dock.getIndex(), dock); // Add the port to the worldTreeMap.  Index is between 20000-29999
                    break;
                case "pship":
                    PassengerShip pship = new PassengerShip(scannerLine, this);
                    Thing pshipDestination = searchForIndex(pship.getParent());
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
                    worldTreeMap.put(pship.getIndex(), pship); // Add the pship to the worldTreeMap
                    break;
                case "cship":
                    CargoShip cship = new CargoShip(scannerLine, this);
                    Thing cshipDestination = searchForIndex(cship.getParent());
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
                    break;
                case "person":
                    Person person = new Person(scannerLine, this);
                    SeaPort personPort = (SeaPort) searchForIndex(person.getParent());  // Find the parent SeaPort for the dock
                    personPort.addToPersons(person); // Add the dock to its parent SeaPort
                    break;
                case "job":
                    Job job = new Job(scannerLine, this);
                    Ship jobShip = (Ship) searchForIndex(job.getParent());  // Find the parent Ship for the job
                    jobShip.addJob(job); // add the job to its ship
                    System.out.println(job.toString());
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
         * Check the ports of the world for matching index
        */
        for(SeaPort currentPort: ports){
            if(currentPort.getIndex() == targetIndex){
                return currentPort;
            }
        }
        
        /*
         * If none of the port indices matched, runs a search within each port
        */
        Thing searchResult;
        for(SeaPort currentPort: ports){
            searchResult = currentPort.searchForIndex(targetIndex);
            if(searchResult != null){
                return searchResult;
            }
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
    
    public String displayWorldString(){
        String worldString = "Welcome to the World!\n\nPorts:\n";
        
        for(SeaPort currentPort: ports){
            worldString = worldString + currentPort.displayPortString();
        }
        
        return worldString;
    }
}
