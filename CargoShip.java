/*
 *  Name: CargoShip
 *  Date: 8/30/2019
 *  Author: Sean Filer
 *  Purpose: A ship that also has a cargo value, cargo volume, and cargo weight.
 */
import java.util.*;

public class CargoShip extends Ship{
    private double cargoValue;
    private double cargoVolume;
    private double cargoWeight;
    
    public CargoShip(String name, Scanner scannerLine){
        super(name, scannerLine);
        
        cargoValue = scannerLine.nextDouble();
        cargoVolume = scannerLine.nextDouble();
        cargoWeight = scannerLine.nextDouble();
    }

    public String toString(){
        String cshipString = "Cargo Ship: " + super.toString();
        return cshipString;
    }
}
