/*
 *  Name: SeaPort
 *  Date: 8/29/2019
 *  Author: Sean Filer
 *  Purpose: Contained by a World object.  Contains a list of docks, ships, persons, and has a queue for ships that need to be worked.
 */

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.*;

public class SeaPort extends Thing{
    private ArrayList<Dock> docks;
    private ArrayList<Ship> ships;
    private ArrayList<Ship> shipQueue;
    private ArrayList<Person> persons;
    private PortPanel portPanel;
    private World world;
    private ReentrantLock portLock = new ReentrantLock();
    private Condition docksAvailable;
    private Condition dockRequired;
    private ArrayList<Integer> reservedPersonIndex; // Used with reservePersons and getRequiredPerson, sent to the Job that instigates those method calls for later use releasing reserved persons.
    private ExecutorService spoPool;
    private SkilledPersonObtainer skilledPersonObtainer;
    private boolean waitingForDock;
    private ArrayList<Dock> unoccupiedDocks;

    public SeaPort(String name, Scanner scannerLine, World world){
        super(name, scannerLine);
        this.world = world;
        docksAvailable = portLock.newCondition();
        dockRequired = portLock.newCondition();
        docks = new ArrayList<>();
        unoccupiedDocks = new ArrayList<>();
        ships = new ArrayList<>();
        shipQueue = new ArrayList<>();
        persons = new ArrayList<>();
        spoPool = Executors.newCachedThreadPool();
        skilledPersonObtainer = new SkilledPersonObtainer(this);
        waitingForDock = false;
    }

    public void run(){
        System.out.println("Thread " + Thread.currentThread().getName() + " started.");

        portPanel.update(docks);
        for(Dock currentDock: docks){
            if(currentDock.isOccupied()){
                currentDock.getShip().setCurrentDock(currentDock);  // set the current dock for ships that were assigned to a dock from the input file.
            } else {
                unoccupiedDocks.add(currentDock);
            }
        }
        portPanel.update(docks);
        int whileLoopCounter = 0;
        while(!shipQueue.isEmpty()){// something about this while loop is stinky.  
            whileLoopCounter++;
            System.out.println("seaport loop number: " + whileLoopCounter);
            serviceNextShipInQue();
            portPanel.update(docks);
        }
        portPanel.update(docks);
        System.out.println("Thread " + Thread.currentThread().getName() + " finished.");
    }
    
    public void startSkilledPersonObtainer(Job job){
        System.out.println(getName() + " starting spo for job: " + job.getName());
        skilledPersonObtainer = new SkilledPersonObtainer(this);
        skilledPersonObtainer.setJob(job);
        spoPool.execute(skilledPersonObtainer);
        System.out.println("spo thread executed.");
       
        
    }
    
    public SkilledPersonObtainer getSkilledPersonObtainer(){
        return skilledPersonObtainer;
    }

    public void assignToAvailableDock(Ship ship){
        portLock.lock();
        while(unoccupiedDocks.isEmpty()){
            try{
                docksAvailable.await();
            } catch (InterruptedException ie){
                /* Do nothing */
            }
        }
        
        try{
            Dock availableDock = unoccupiedDocks.remove(0);
            availableDock.setCurrentShip(ship);
            System.out.println(ship.getName() + " added to dock " + availableDock.getName() + " of " + getName());
            System.out.println("number of available docks: " + unoccupiedDocks.size());
        } finally {
            System.out.println( Thread.currentThread().getName() +": unlocking portLock() from assignToAvailableDock()");

            portLock.unlock();
        }
        
    }
    public void serviceNextShipInQue(){
        /*
         * Take the ship from the front of the ship queue and take the first dock that's available from docks.
         * If no docks are available, will wait on a signal for condition docksAvailable.
         */
        System.out.println("service next ship in que at port " + getName());
        portLock.lock();
//        while(allDocksOccupied()){

//        }
        try{
            Ship nextShip = shipQueue.remove(0);
            
            assignToAvailableDock(nextShip);


        } finally {
            System.out.println( Thread.currentThread().getName() +": unlocking portLock() from serviceNextShipInQue()");
            portLock.unlock();
        }
    }

//    public void waitForAvailableDock(){
//        System.out.println("from waitForAvailableDock, obtain portLock");
//        portLock.lock();
//        try{
//            System.out.println("Guess all the docks are occupied!");
//            docksAvailable.await();
//            System.out.println("DocksAvailable signal received!");
//        } catch (InterruptedException ie){
//            /*Do nothing*/
//        } finally {
//            System.out.println( Thread.currentThread().getName() +": unlocking portLock() from waitForAvailableDock()");
//            portLock.unlock();
//        }
//    }

    public  void signalDockAvailable(Dock argDock){
        
        System.out.println("from signalDockAvailable, obtain portLock");
        portLock.lock();
        try{
            System.out.println(argDock.getName() + " has become available");
            unoccupiedDocks.add(argDock);
//            System.out.println("are all docks occupied? " + allDocksOccupied());
        } finally {
            System.out.println("signal for docksAvailable() next line...");
            docksAvailable.signal();
            portLock.unlock();
        }
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

    public ArrayList<String> getPortSkills(){
        /*
         * Return a list of all the skills that are available at the Port.
         */
        ArrayList<String> skillList =  new ArrayList<>();

        for(Person currentPerson: persons){
            if(!skillList.contains(currentPerson.getSkill())){
                skillList.add(currentPerson.getSkill());
            }
        }
        return skillList;
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

    public ArrayList<Dock> getDocks(){
        return docks;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setPortPanel(PortPanel portPanel){
        this.portPanel = portPanel;
    }

    public synchronized boolean allDocksOccupied(){ // Checks if all the docks at this port are currently occupied.  If any are not occupied, return false.
        System.out.println("checking if all docks are occupied");
        for(Dock currentDock: docks){
            System.out.println("checking dock " + currentDock.getName());
            if(!currentDock.isOccupied()){
                System.out.println("dock was not occupied! - allDocksOccupied");
                return false;
            }
        }
        System.out.println("all docks occupied - allDocksOccupied");
        return true;
    }

    public synchronized Dock getUnoccupiedDock(){
        for(Dock currentDock: docks){
            if(!currentDock.isOccupied()){
                return currentDock;
            }
        }
        return null;
    }
    
    public ArrayList<Person> getPersons(){
        return persons;
    }
}
