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
    private World world;
    
    public CargoShip(Scanner scannerLine, World world){
        super(scannerLine);
        
        cargoValue = scannerLine.nextDouble();
        cargoVolume = scannerLine.nextDouble();
        cargoWeight = scannerLine.nextDouble();
        
        this.world = world;
    }
}
