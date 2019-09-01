/*
 *  Name: PassengerShip
 *  Date: 8/30/2019
 *  Author: Sean Filer
 *  Purpose: A ship that also has a number of passengers, rooms, and occupied rooms.
 */
import java.util.*;

public class PassengerShip extends Ship{
    private int numberOfOccupiedRooms;
    private int numberOfPassengers;
    private int numberOfRooms;
    private World world;
    
    public PassengerShip(Scanner scannerLine, World world){
        super(scannerLine);
        
        numberOfOccupiedRooms = scannerLine.nextInt();
        numberOfPassengers = scannerLine.nextInt();
        numberOfRooms = scannerLine.nextInt();
        
        this.world = world;
    }
}
