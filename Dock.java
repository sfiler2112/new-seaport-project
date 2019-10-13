/*
 *  Name: Dock
 *  Date: 8/29/2019
 *  Author: Sean Filer
 *  Purpose: Contained by a SeaPort.  Contains a ship if occupied, contains nothing otherwise.
 */

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Scanner;
import java.util.concurrent.locks.*;

public class Dock extends Thing{
    private Ship currentShip;
    private boolean occupied;
    private World world;
    private SeaPort port;
    private ReentrantLock dockLock = new ReentrantLock();
    
//    public Dock(Scanner scannerLine, World world){
//        super(scannerLine);
//        this.world = world;
//        port = (SeaPort) world.searchForIndex(super.getParent()); //Set the port for this dock by searching the World for the parent index.
//        currentShip = null;
//        occupied = false;
//    }

    public Dock(String name, Scanner scannerLine, World world){
        super(name, scannerLine);
        this.world = world;
        port = (SeaPort) world.getWorldHashMap().get(this.getParent());  // Get the parent SeaPort from the world hash map using the dock's parent index value.
        currentShip = null;
        occupied = false;
    }
    
    public void addShip(Ship ship){ // Only used when building the world.  Otherwise, setShip should be used.
        /*
         * If the dock is not occupied, add ship.  Otherwise, add this ship to the SeaPort that contains this Dock.
        */
        if(!occupied){
            currentShip = ship;
            port.addShip(this);
            occupied = true;
        } else {
            port.addShip(ship);
        }
    }
    
    public Ship getShip(){
        return currentShip;
    }
    
    public String displayDockString(){
        String dockString = "      Dock: " + super.toString();
        if(occupied){
            dockString = dockString  + "\n        Current Ship: " + currentShip.toString();
        }
        return dockString;
    }

    public DefaultMutableTreeNode getDockNode(){
        DefaultMutableTreeNode dockNode = new DefaultMutableTreeNode(getName());
        if(occupied){
            dockNode.add(currentShip.getNode());
        }

        return dockNode;
    }

    public JPanel getDockJPPanel(){
        JPanel dockPanel = new JPanel();
        dockPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        TitledBorder dockBorder = new TitledBorder(getName());
        dockPanel.setBorder(dockBorder);
        if(occupied){
            dockBorder.setTitle(getName() + ": " + currentShip.getName());
            if(currentShip.hasJobs()){
                int jobCounter = 0;
                for(Job currentJob: currentShip.getJobs()){
                    gbc.gridx = 0;
                    gbc.gridy = jobCounter;
                    dockPanel.add(new JLabel(currentJob.getName()));
                    jobCounter++;
                }
            } else {
                dockPanel.add(new JLabel("No jobs to work"));
            }
        } else {
            dockPanel.add(new JLabel("Dock not occupied"));
        }
        return dockPanel;
    }

    public String toString(){
        String dockString = "Dock: " + super.toString();
        return dockString;
    }

    public synchronized boolean isOccupied(){
        return occupied;
    }

    public  void setCurrentShip(Ship newShip){
        dockLock.lock();
        try{
            currentShip = newShip;
            occupied = true;
        } finally {
            currentShip.removeJobsWithUnmetRequirements(port.getPersons());
            currentShip.setCurrentDock(this);
            dockLock.unlock();
        }

    }

    public void makeAvailable(){
        dockLock.lock();
        try{
            System.out.println(getName() + " is removing a ship: " + currentShip.getName());
            currentShip = null;
            occupied = false;
        } finally {
            port.signalDockAvailable(this);
            dockLock.unlock();
        }
    }

    public SeaPort getPort(){
        return port;
    }


}
