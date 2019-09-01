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
    
    public String toString(){
        String portString = super.toString() + "\n";
        portString = portString + "\tDocks:\n";
        for(Dock currentDock: docks){
            portString = portString + currentDock.toString() + "\n";
        }
        portString = portString + "\n\tShip Queue:\n";
        for(Ship currentShip: shipQueue){
            portString = portString + "\t" + currentShip.toString() + "\n";
        }
        portString = portString + "\n\tPersons:\n";
        for(Person currentPerson: persons){
            portString = portString + "\t" + currentPerson.toString() + "\n";
        }
        portString = portString + "\n\tAll Ships at this sea port:\n";
        for(Ship currentShip: ships){
            portString = portString + "\t" + currentShip.toString() + "\n";
        }
        
        return portString;
    }
}
