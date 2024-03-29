/*
 *  Name: Ship
 *  Date: 8/29/2019
 *  Author: Sean Filer
 *  Purpose: Ancestor class for CargoShip and PassengerShip.  Contained by a 
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class Ship extends Thing{
    private PortTime arrivalTime;
    private PortTime dockTime;
    private double draft;
    private double length;
    private double weight;
    private double width;
    private ArrayList<Job> jobs;
    private CountDownLatch doneSignal;
    private Dock currentDock;
    private ReentrantLock shipLock = new ReentrantLock();
    private volatile boolean allJobsStarted;
    
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
        weight = scannerLine.nextDouble();
        length = scannerLine.nextDouble();
        width = scannerLine.nextDouble();
        draft = scannerLine.nextDouble();

        jobs = new ArrayList<>();
    }

    public void run(){
        System.out.println("Thread " + Thread.currentThread().getName() + " started.");
        System.out.println("initial number number of jobs for " + getName() +": " + jobs.size());
        SeaPort port = currentDock.getPort();
        removeJobsWithUnmetRequirements(port.getPersons());
        if(!jobs.isEmpty()){

            doneSignal = new CountDownLatch(jobs.size());
            for(Job currentJob: jobs){
                currentJob.setPort(port);
                currentJob.prepareToStart(doneSignal);
            }
            waitForJobsToFinish();

        } else {
            System.out.println("ship had no jobs to start with: " + getName());
            currentDock.makeAvailable();
        }
        
        System.out.println("Thread " + Thread.currentThread().getName() + " finished.");
    }

//    public void reserveRequiredSkills(){
//        shipLock.lock();
//
//
//        try{
//            while(!allJobsStarted){
//                if(port.reserveSkilledPersons())
//            }
//        }
//    }

    public void removeJobsWithUnmetRequirements(ArrayList<Person> portPersons){
        shipLock.lock();

        try{
            ArrayList<Job> removeJobList = new ArrayList<>();

            for(Job currentJob: jobs){
                if(!hasRequiredSkills(portPersons, currentJob.getRequirements())){
                    System.out.println(Thread.currentThread().getName() + ": " + currentJob.getName() + " added to remove list, skills not met.");
                    removeJobList.add(currentJob);
                }
            }

            for(Job removedJob: removeJobList){
                jobs.remove(removedJob);
            }
        } finally {
            shipLock.unlock();
        }

    }

    public synchronized boolean hasRequiredSkills(ArrayList<Person> portPersons, ArrayList<String> requirements){
        boolean skillMissing;

        ArrayList<Person> foundPortPersons = new ArrayList<>();


        for(String currentRequirement: requirements){
            skillMissing = true;
            for( Person currentPerson: portPersons){
                if(currentPerson.getSkill().equals(currentRequirement) && !foundPortPersons.contains(currentPerson)){
                    skillMissing = false;
                    foundPortPersons.add(currentPerson);
                    System.out.println(Thread.currentThread().getName() + ": " + currentPerson.getName() + " has the skill " + currentRequirement);
                }
            }

            if(skillMissing){
                System.out.println(getName() + " could not find the required skills for it's job! ");
                return false;
            }
        }
        System.out.println(getName() + " found the required skills for it's job! ");
        return true;
    }

    public void waitForJobsToFinish(){
        System.out.println("Waiting for " + getName() + " jobs to finish");
        shipLock.lock();
        try{
            doneSignal.await();
        } catch (InterruptedException ie) {
            /*Do nothing*/
        } finally {
            currentDock.makeAvailable();
            shipLock.unlock();
        }
    }
    
    public void clearRequiredPersonIndex(Job finishedJob){
        
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
    
    public void sortJobsByName(){
        Collections.sort(jobs, new NameComparator());
    }
    
    public double getDraft(){
        return draft;
    }
    public double getLength(){
        return length;
    }
    public double getWeight(){
        return weight;
    }
    public double getWidth(){
        return width;
    }

    public ArrayList<Job> getJobs(){
        return jobs;
    }

    public void setCurrentDock(Dock dock){
        this.currentDock = dock;
        Thread shipThread = new Thread(this, this.getName());
        shipThread.start();
    }
}