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
    
    public PassengerShip(String name, Scanner scannerLine){
        super(name, scannerLine);
        
        numberOfOccupiedRooms = scannerLine.nextInt();
        numberOfPassengers = scannerLine.nextInt();
        numberOfRooms = scannerLine.nextInt();
    }

    public String toString(){
        String pshipString = "Passenger Ship: " + super.toString();
        return pshipString;
    }
}
