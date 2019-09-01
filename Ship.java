/*
 *  Name: Ship
 *  Date: 8/29/2019
 *  Author: Sean Filer
 *  Purpose: Ancestor class for CargoShip and PassengerShip.  Contained by a 
 */
import java.util.*;

public class Ship extends Thing{
    private PortTime arrivalTime;
    private PortTime dockTime;
    private double draft;
    private double length;
    private double weight;
    private double width;
    private ArrayList<Job> jobs;
    private World world;
    
    public Ship(Scanner scannerLine){
        super(scannerLine);
        
        draft = scannerLine.nextDouble();
        length = scannerLine.nextDouble();
        weight = scannerLine.nextDouble();
        width = scannerLine.nextDouble();
            
        jobs = new ArrayList<>();
    }
    
    public void addJob(Job newJob){
        jobs.add(newJob);
    }
    
    public Thing searchForIndex(int targetIndex){
        /*
         * If a job's index matches the targetIndex, return that job.  
        */
        for(Job currentJob: jobs){
            if(currentJob.getIndex() == targetIndex){
                return currentJob;
            }
        }
        
        return null; // If a match was not found, return null.
    }
}