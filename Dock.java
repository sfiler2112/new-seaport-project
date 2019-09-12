/*
 *  Name: Dock
 *  Date: 8/29/2019
 *  Author: Sean Filer
 *  Purpose: Contained by a SeaPort.  Contains a ship if occupied, contains nothing otherwise.
 */
import java.util.*;

public class Dock extends Thing{
    private Ship currentShip;
    private boolean occupied;
    private World world;
    private SeaPort port;
    
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

    public String toString(){
        String dockString = "Dock: " + super.toString();
        return dockString;
    }
}
