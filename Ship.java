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
    
//    public Ship(Scanner scannerLine){
//        super(scannerLine);
//
//        draft = scannerLine.nextDouble();
//        length = scannerLine.nextDouble();
//        weight = scannerLine.nextDouble();
//        width = scannerLine.nextDouble();
//
//        jobs = new ArrayList<>();
//    }

    public Ship(String name, Scanner scannerLine){
        super(name, scannerLine);
        draft = scannerLine.nextDouble();
        length = scannerLine.nextDouble();
        weight = scannerLine.nextDouble();
        width = scannerLine.nextDouble();

        jobs = new ArrayList<>();
    }
    
    public void addJob(Job newJob){
        jobs.add(newJob);
    }

    public boolean hasJobs(){
        /*
         * Return true if the ship has an jobs, false if it does not
         */
        if(jobs.isEmpty()){
            return false;
        } else {
            return true;
        }
    }

    public String searchForName(String targetName){
        /*
         * Check the jobs assigned to this ship for a name match.
         */
        String searchResult = "";

        for(Job currentJob: jobs){
            if(currentJob.getName().equals(targetName)){
                searchResult = searchResult + currentJob.toString() + "\n";
            }
        }

        return searchResult;
    }
    
//    public Thing searchForIndex(int targetIndex){
//        /*
//         * If a job's index matches the targetIndex, return that job.
//        */
//        for(Job currentJob: jobs){
//            if(currentJob.getIndex() == targetIndex){
//                return currentJob;
//            }
//        }
//
//        return null; // If a match was not found, return null.
//    }

    public String searchForSkill(String targetSkill){
        /*
         * If a job has a requirement that matches the target skill, add that job to the search results
         */
        String searchResult = "";

        for(Job currentJob: jobs){
            searchResult = searchResult + currentJob.searchForSkill(targetSkill);
        }

        return searchResult;
    }
}