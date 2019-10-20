/*  SkilledPersonObtainer
 *  Name:
 *  Date: 10/11/2019
 *  Author: Sean Filer
 *  Purpose: A runnable class that will be used with an executioner service in SeaPort to assign persons to job threads.
 */
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.*;

public class SkilledPersonObtainer implements Runnable{
    private Job job;
    private ArrayList<String> jobRequirements;
    private SeaPort port;
    
    private ReentrantLock obtainerLock;
    private Condition personsFinished;
    private boolean returningPersons;
    private Boolean successfulReservation;
    private ArrayList<Person> reservedPersonList;
    private ArrayList<Person> portPersons;
    private Thread spoThread;
    private ArrayList<Person> availablePersonList;
    private RequestedSkillTracker jobSkillRequestTracker;
    private boolean personWasReserved;
    private int reservationAttempts;
    
    public SkilledPersonObtainer(SeaPort argPort){
        
        port = argPort;
        obtainerLock = new ReentrantLock();
        returningPersons = false;
        personsFinished = obtainerLock.newCondition();
        portPersons = port.getPersons();
        successfulReservation = false;
        reservationAttempts = 0;
    }
    
    public void run(){
        reservePersons();
        System.out.println("finished with run for spo");
    }
    
    public void setJob(Job argJob){
        obtainerLock.lock();
        try{
            job = argJob;
            job = argJob;
            jobRequirements = argJob.getRequirements();
            jobSkillRequestTracker = argJob.getRequestedSkillTracker();
            spoThread = new Thread(this, "spo for " + job.getName());
            spoThread.start(); 
        } finally {
            System.out.println("starting spo for " + job.getName());
            obtainerLock.unlock();
        }
        
    }
    
    public void reserveRequiredSkills(){
        obtainerLock.lock();
        System.out.println("Thread " + Thread.currentThread().getName() + ": reserving the required skills...");

        successfulReservation = false;
        
        try{
            reservePersons();
        } finally {
            if(successfulReservation){
                System.out.println("reservation for " + job.getName() + " was successful.");
                waitForPersonsToFinish();
            } else {
                tryAgainLater();
            }
            obtainerLock.unlock();
        }
    }
    
    private void reservePersons(){
        /*
         * For each required skill, find a person to meet that requirement.  
         * If a required skill cannot reserve a person, return false.  Otherwise, return true after all required persons are reserved.
        */
        obtainerLock.lock();
        try{
            System.out.println(Thread.currentThread().getName() + " thread is trying to reserve persons...");
            boolean failedToReserveAll = false;
            ArrayList<Person> tempReservedPersonList = new ArrayList<>();
            for(int i = 0; i < jobRequirements.size(); i++){
                String currentRequirement = jobRequirements.get(i);
                for(int j = 0; j < portPersons.size(); j++){
                    System.out.println("checking portPerson " + j);
                    Person currentPerson = portPersons.get(j);
                    if(currentRequirement.equals(currentPerson.getSkill())){
                        if(!currentPerson.isBusy()){
                            System.out.println(currentPerson.getName() + " is not busy and has the required skill: " + currentRequirement );
                            currentPerson.reserve();
                            tempReservedPersonList.add(currentPerson);
                            jobSkillRequestTracker.personAcquired();
                            j = portPersons.size();
                            failedToReserveAll = false;
                        } else {
                            failedToReserveAll = true;
                            System.out.println(Thread.currentThread().getName() + " : person " + currentPerson.getName() +" was busy");
                        }
                    } else {
                        System.out.println(currentPerson.getName() + " does not have the required skill: " + currentRequirement );
                        failedToReserveAll = true;
                    }
                }   
                if(failedToReserveAll){
                    System.out.println(Thread.currentThread().getName() + " thread could not reserve all the required people.");
                    clearReservedPersons(tempReservedPersonList);
                    successfulReservation = false;
                    i = jobRequirements.size();
                } else {
                    System.out.println(currentRequirement + " requirement was met.");
                }
            } 
                
            if(tempReservedPersonList.size()==job.getRequirements().size()){
                System.out.println("reserved a person for every requirement. P/R: " + tempReservedPersonList.size() + "/" +  job.getRequirements().size());
                successfulReservation = true;
                reservedPersonList = tempReservedPersonList;
            }
            
        } finally {
            
            if(successfulReservation){
                System.out.println(Thread.currentThread().getName() + " thread reserved all the required people.");
                job.requiredSkillsReserved(reservedPersonList);
                waitForPersonsToFinish();
            } else {
                tryAgainLater();
            }
            obtainerLock.unlock();
        }
        
        
    }
    
    public void clearReservedPersons(ArrayList<Person> argReservedPersons){
        obtainerLock.lock();
        try{
            System.out.println(Thread.currentThread().getName() + " will now release the reserved person for each skill...");
            for(Person currentPerson: argReservedPersons){
                jobSkillRequestTracker.personReleased();
                currentPerson.release();
            }
        } finally {
            obtainerLock.unlock();
        }

    }
        

    
    public void tryAgainLater(){
        obtainerLock.lock();
        System.out.println(Thread.currentThread().getName() + " will need to try again later! ;p");
        long waitHandicap = 1000;
        try{
            reservationAttempts++;
            if(reservationAttempts > 10 ){
                waitHandicap = 2500;
            } else if (reservationAttempts > 100){
                waitHandicap = 4000;
            }
            System.out.println(Thread.currentThread().getName() + ": sleeping for " + String.valueOf(5000 - waitHandicap) + " millis" );
            Thread.currentThread().sleep(5000 -  waitHandicap);
        } catch (InterruptedException ie){
            /* Do nothing*/
        } finally {
            reserveRequiredSkills();
            obtainerLock.unlock(); 
        }
    }
    
    public void waitForPersonsToFinish(){
        obtainerLock.lock();
        System.out.println(Thread.currentThread().getName() + " waits for the job to finish now");
        try{
            while(!returningPersons){
                personsFinished.await();
            }
            
             for(Person currentPerson: reservedPersonList){
                currentPerson.release();
            }
        }catch (InterruptedException ie){
            /* Do nothing */
        } finally {
            System.out.println("finished with waitForPersonsToFinish() for  spo");
            obtainerLock.unlock();
        }
    }
    
    public void doneWithPersons(){
        obtainerLock.lock();
        try{
            returningPersons = true;
        } finally {
            personsFinished.signal();
            obtainerLock.unlock();
        }
    }
    
    public Job getJob(){
        return job;
    }
    
    
    public synchronized void setSuccessfulReservation(boolean state){
        successfulReservation = state;
    }


    public void setPersonWasReserved(boolean state){
        obtainerLock.lock();

        try{
            personWasReserved = state;
        } finally {
            obtainerLock.unlock();
        }

    }
}
