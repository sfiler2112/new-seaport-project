/*
 *  Name: SeaPort
 *  Date: 8/29/2019
 *  Author: Sean Filer
 *  Purpose: Contained by a World object.  Contains a list of docks, ships, persons, and has a queue for ships that need to be worked.
 */

import java.util.*;

public class SeaPort extends Thing{
    ArrayList<Dock> docks;
    ArrayList<Ship> ships;
    ArrayList<Ship> shipQueue;
    ArrayList<Person> persons;
    World world;
    
    public SeaPort(Scanner scannerLine, World world){
        super(scannerLine);
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
    
    public Thing searchForIndex(int targetIndex){
        /*
         * Search ArrayLists (docks, ships, persons) for the targetIndex.  If a match is found, return that thing.
        */
        for(Dock currentDock: docks){
            if(currentDock.getIndex() == targetIndex){
                return currentDock;
            }
        }
        
        for(Ship currentShip: ships){
            if(currentShip.getIndex() == targetIndex){
                return currentShip;
            }
        }
        
        for(Person currentPerson: persons){
            if(currentPerson.getIndex() == targetIndex){
                return currentPerson;
            }
        }
        
        /*
         * If the index has not been matched, run a search on the jobs for each ship.
        */
        Thing searchResult;
        for(Ship currentShip: ships){
            searchResult = currentShip.searchForIndex(targetIndex);
            if(searchResult != null){
                return searchResult;
            }
        }
        
        return null; // return null if no match for the target index is found
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
        
        return portString;
    }

    public String toString(){
        String portString = "SeaPort: " + super.toString();
        return portString;
    }
}
