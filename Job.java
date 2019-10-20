/*
 *  Name: Job
 *  Date: 8/30/2019
 *  Author: Sean Filer
 *  Purpose: Contained by a ship, has a duration and a list of requirements.  Requirements are skills that a Person must have to start the job.
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Job extends Thing {
    private double duration;
    private ArrayList<String> requirements;
    private ArrayList<Person> reservedPersonList;
    private JobSwingWorker jobSW;
    private boolean finished;
    private boolean shipNotDocked;
    private boolean allRequirementsMet;
    private boolean successfulReservation;
    private Thread jobThread;
    private ReentrantLock jobLock = new ReentrantLock();
    private Condition shipDocked;
    private Condition jobFinished;
    private Condition personsObtained;
    private CountDownLatch doneSignal;
    private SeaPort currentPort;
    private volatile boolean canceled;
    private volatile boolean started;
    private SkilledPersonObtainer spo;
    private RequestedSkillTracker rst;
    
    public Job(String name, int index, Scanner scannerLine){
        super(name, scannerLine);
        
        duration = scannerLine.nextDouble();
        requirements = new ArrayList<>();
        reservedPersonList = new ArrayList<>();
        while(scannerLine.hasNext()){
            requirements.add(scannerLine.next());
        }

        shipDocked = jobLock.newCondition();
        jobFinished = jobLock.newCondition();
        personsObtained = jobLock.newCondition();
        this.setIndex(index);

        rst = new RequestedSkillTracker(this);


        finished = false;
        shipNotDocked = true;
        allRequirementsMet = false;
//        jobThread = new Thread(this, this.getName());
//        jobThread.start();
    }

    public void run(){
        System.out.println(Thread.currentThread().getName() + ": thread started, will wait for ship to dock");
        jobSW = new JobSwingWorker(this);
        waitForShipToDock();
    }

    public void waitForShipToDock(){
        jobLock.lock();

        try{
            while(shipNotDocked){
                System.out.println(Thread.currentThread().getName() + ": still not docked, waiting...");
                shipDocked.await();
            }

        } catch (InterruptedException ie){
            /*Do nothing*/
        } finally {
            /*
             * Job has docked, needs to obtain its skilled workers from the port.
             */
            spo.setJob(this);
            waitForPersonsObtained();
            jobLock.unlock();
        }
    }

    public void prepareToStart(CountDownLatch doneSignal){
        jobLock.lock();
        try{
            System.out.println(this.getName() +"'s ship has docked!  ready to start working now...");
            this.doneSignal = doneSignal;
            spo = new SkilledPersonObtainer(currentPort);
            shipNotDocked = false;
        } finally {
            shipDocked.signal();
            jobLock.unlock();
        }
    }


    public void waitForPersonsObtained(){
        jobLock.lock();
        
        try{
            while(!successfulReservation){
                personsObtained.await();
            } 
        } catch(InterruptedException ie){
            /* Do nothing */
        } finally {
            jobSW.execute();
            waitForJobToFinish();
            jobLock.unlock();
        }
    }

    public void requiredSkillsReserved(ArrayList<Person> argReservedPersonList){ // Called by the SkilledPersonObtainer, which the ship created for this job to get the skilled persons.
        jobLock.lock();
        try{
            successfulReservation = true;
            reservedPersonList = argReservedPersonList;
        } finally {
            personsObtained.signal();
            jobLock.unlock();
        }
    }


    public void waitForJobToFinish(){
        jobLock.lock();
        while(!finished){
            try{
                jobFinished.await();
            } catch (InterruptedException ie){
                /*Do nothing*/
            }
        }
        try{
            System.out.println(getName() + " finished! using countDown() and telling the spo to release the reserved persons");
            spo.doneWithPersons();
            doneSignal.countDown();
        } finally {
            jobLock.unlock();
        }

    }

    public void finish(){
        jobLock.lock();

        try{
            System.out.println(getName() + " has finished, signaling jobFinished...");
            finished = true;
        } finally {
            jobFinished.signal();
            jobLock.unlock();
        }

    }

    
    public synchronized void setSkilledPersonObtainer(SkilledPersonObtainer argSPO){
        spo = argSPO;
    }

    public String toString(){
        String jobString = "Job: " + super.toString();
        for(String currentRequirement: requirements){
            jobString = jobString + " " + currentRequirement;
        }
        return jobString;
    }

    public String searchForSkill(String targetSkill){
        /*
         * If any of the requirements match the target skill, return the job's toString.
         */
        for(String currentRequirement: requirements){
            if(currentRequirement.equals(targetSkill)){
                return toString() + "\n";
            }
        }
        return "";
    }

    public void attachJobSwingWorker(JobSwingWorker jobSW){
        this.jobSW = jobSW;
        System.out.println("attaching jobSW to job! index: " + this.getIndex());
        System.out.println("jobSW index: " + jobSW.getIndex());
    }

    public RequestedSkillTracker getRequestedSkillTracker(){
        return rst;
    }

    public JobSwingWorker getJobSW(){
        return jobSW;
    }

    public double getDuration(){
        return duration;
    }

    public ArrayList<String> getRequirements(){
        return requirements;
    }

    public synchronized void setPort(SeaPort port){
        currentPort = port;
    }

    public synchronized void setCanceled(boolean argState){
        canceled = argState;
    }

    public synchronized void setStarted(){
        started = true;
    }
    
    public synchronized void reservedSuccessfully(){
        successfulReservation = true;
    }
    

}
