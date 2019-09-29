/*
 *  Name: SeaPort
 *  Date: 8/29/2019
 *  Author: Sean Filer
 *  Purpose: Contained by a World object.  Contains a list of docks, ships, persons, and has a queue for ships that need to be worked.
 */

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class SeaPort extends Thing{
    ArrayList<Dock> docks;
    ArrayList<Ship> ships;
    ArrayList<Ship> shipQueue;
    ArrayList<Person> persons;
    World world;

    public SeaPort(String name, Scanner scannerLine, World world){
        super(name, scannerLine);
        this.world = world;
        docks = new ArrayList<>();
        ships = new ArrayList<>();
        shipQueue = new ArrayList<>();
        persons = new ArrayList<>();
    }

    
    public void addToDocks(Dock dock){
        docks.add(dock);
    }
    
    public void addShip(Dock dock){ // Called by a dock if a ship starts off docked
        ships.add(dock.getShip());
        System.out.println("adding docked ship");
    }
    
    public void addShip(Ship ship){ // Called by a ship if the ship is not docked
        ships.add(ship);
        shipQueue.add(ship);
        System.out.println("adding undocked ship");
    }
    
    public void addToPersons(Person person){
        persons.add(person);
    }
    
    public boolean isSeaPort(){
        return true;
    }
    
    public void sortShips(String sortOption){
        switch(sortOption){ //determines what kind of sort to do on the ship related ArrayLists based on the sort option string.
            case "name sort":
                System.out.println("running name sort on ships");
                Collections.sort(ships, new NameComparator());
                Collections.sort(shipQueue, new NameComparator());
                break;
            case "draft sort":
                System.out.println("running draft sort on ships");
                Collections.sort(ships, new DraftComparator());
                Collections.sort(shipQueue, new DraftComparator());
                break;
            case "length sort":
                System.out.println("running length sort on ships");
                Collections.sort(ships, new LengthComparator());
                Collections.sort(shipQueue, new LengthComparator());
                break;
            case "weight sort":
                System.out.println("running weight sort on ships");
                Collections.sort(ships, new WeightComparator());
                Collections.sort(shipQueue, new WeightComparator());
                break;
            case "width sort":
                System.out.println("running width sort on ships");
                Collections.sort(ships, new WidthComparator());
                Collections.sort(shipQueue, new WidthComparator());
                break;
        }
    }
    
    public void sortPersons(String sortOption){
        switch(sortOption){
            case "name sort":
                System.out.println("running name sort on persons");
                Collections.sort(persons, new NameComparator());
                break;
            case "skill sort":
                System.out.println("running skill sort on persons");
                Collections.sort(persons, new SkillComparator());
                break;
        }
    }

    public String searchForName(String targetName){
        /*
         * Check the objects contained within this port for matching name, and any objects encapsulated by within those objects
         */
        String nameSearchResult = "";
        for(Dock currentDock: docks){
            if(currentDock.getName().equals(targetName)){
                nameSearchResult = nameSearchResult + currentDock.toString() + "\n";
            }
        }
        for(Ship currentShip: ships){
            if(currentShip.getName().equals(targetName)){
                nameSearchResult = nameSearchResult + currentShip.toString() + "\n";
            }
            if(currentShip.hasJobs()){
                nameSearchResult = nameSearchResult + currentShip.searchForName(targetName); // If the ship has any jobs, check their names for a match
            }
        }
        for(Person currentPerson: persons){
            if(currentPerson.getName().equals(targetName)){
                nameSearchResult = nameSearchResult + currentPerson.toString() + "\n";
            }
        }

        return nameSearchResult;
    }

    public String searchForSkill(String targetSkill){
        /*
         * Search ships with jobs and persons for the target skill
         */
        String searchResult = "";
        for(Ship currentShip: ships){
            if(currentShip.hasJobs()){ // Check if the current ship has any jobs to search through
                searchResult = searchResult + currentShip.searchForSkill(targetSkill);
            }
        }
        for(Person currentPerson: persons){
            if(currentPerson.getSkill().equals(targetSkill)){
                searchResult = searchResult + currentPerson.toString() + "\n";
            }
        }

        return searchResult;
    }
    
    public void sortAllByName(){
        Collections.sort(docks, new NameComparator());
        Collections.sort(ships, new NameComparator());
        Collections.sort(shipQueue, new NameComparator());
        Collections.sort(persons, new NameComparator());
    }
    
    public String displayPortString(){
        String portString ="  SeaPort: " + super.toString() + "\n";
        portString = portString + "    Docks:\n";
        for(Dock currentDock: docks){
            portString = portString + currentDock.displayDockString() + "\n";
        }
        portString = portString + "\n    Ship Queue:\n";
        for(Ship currentShip: shipQueue){
            portString = portString + "      " + currentShip.toString() + "\n";
        }
        portString = portString + "\n    Persons:\n";
        for(Person currentPerson: persons){
            portString = portString + "      " + currentPerson.toString() + "\n";
        }
        portString = portString + "\n    All Ships at this sea port:\n";
        for(Ship currentShip: ships){
            portString = portString + "      " + currentShip.toString() + "\n";
        }
        portString = portString + "\n\n";
        return portString;
    }

    public String toString(){
        String portString = "SeaPort: " + super.toString();
        return portString;
    }

    public String displayShips(){ // Method used to display ships after they have been selected as a sort category and sorted.
        String shipsString = "";
        if(!ships.isEmpty()){
            shipsString = "Ships at Port " + this.getName() + ":";
            for(Ship currentShip: ships){
                shipsString = shipsString + "\n   " + currentShip.toString();
            }
        }

        return shipsString;
    }

    public String displayPersons(){
        String personsString = "";
        if(!persons.isEmpty()){
            personsString = "Persons at Port " + this.getName() + ":";
            for(Person currentPerson: persons){
                personsString = personsString + "\n   " + currentPerson.toString();
            }
        }
        return personsString;
    }

    public DefaultMutableTreeNode getPortNode(){
        DefaultMutableTreeNode portNode = new DefaultMutableTreeNode(getName());
        DefaultMutableTreeNode docksNode = new DefaultMutableTreeNode("Docks");
        DefaultMutableTreeNode shipsNode = new DefaultMutableTreeNode("Ships");
        DefaultMutableTreeNode shipQueueNode = new DefaultMutableTreeNode("Queue");
        DefaultMutableTreeNode personsNode = new DefaultMutableTreeNode("Persons");

        if(!docks.isEmpty()){
            for(Dock currentDock: docks){
                docksNode.add(currentDock.getDockNode());
            }
            portNode.add(docksNode);
        }
        if(!ships.isEmpty()){
            for(Ship currentShip: ships){
                shipsNode.add(currentShip.getNode());
            }
            portNode.add(shipsNode);
        }
        if(!shipQueue.isEmpty()){
            for(Ship currentShip: shipQueue){
                shipQueueNode.add(currentShip.getNode());
            }
            portNode.add(shipQueueNode);
        }
        if(!persons.isEmpty()){
            for(Person currentPerson: persons){
                personsNode.add(currentPerson.getNode());
            }
            portNode.add(personsNode);
        }
        return portNode;
    }

    public void setWorld(World world){
        this.world = world;
    }
}
