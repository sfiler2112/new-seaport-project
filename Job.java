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
    private JobSwingWorker jobSW;
    private boolean finished;
    private boolean shipNotDocked;
    private boolean allRequirementsMet;
    private Thread jobThread;
    private ReentrantLock jobLock = new ReentrantLock();
    private Condition shipDocked;
    private Condition jobFinished;
    private Condition personsObtained;
    private CountDownLatch doneSignal;
    private SeaPort currentPort;
    private volatile boolean canceled;
    private volatile boolean started;
    
    public Job(String name, int index, Scanner scannerLine){
        super(name, scannerLine);
        
        duration = scannerLine.nextDouble();
        requirements = new ArrayList<>();
        while(scannerLine.hasNext()){
            requirements.add(scannerLine.next());
        }

        shipDocked = jobLock.newCondition();
        jobFinished = jobLock.newCondition();
        personsObtained = jobLock.newCondition();
        this.setIndex(index);



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
            meetSkillRequirements();
            jobLock.unlock();
        }
    }

    public void prepareToStart(){
        jobLock.lock();

        try{
            shipNotDocked = false;
            System.out.println(this.getName() +"'s ship has docked!  ready to start working now...");
        } finally {
            shipDocked.signal();
            jobLock.unlock();
        }
    }


    public void obtainSkilledPersons(){
        jobLock.lock();

        try{
            allRequirementsMet = true;
            System.out.println(getName() + " has obtained the people needed to start progressing");
        } finally {
            personsObtained.signal();
            jobLock.unlock();
        }
    }

    public void meetSkillRequirements(){
        jobLock.lock();

        try{
            while(!allRequirementsMet){
                personsObtained.await();
            }
        } catch (InterruptedException ie) {
            /* Do nothing */
        } finally {
            jobSW.execute();
            waitForJobToFinish();
            jobLock.unlock();
        }
    }

    public void reserveRequiredSkills(){
        jobLock.lock();

        boolean successfulReservation;
        try{

            successfulReservation = currentPort.reservePersons(requirements);
        } finally {
            if(successfulReservation){

            }
        }
    }

    public void tryAgainLater(){

    }

    public void waitForJobToFinish(){
        jobLock.lock();

        try{
            while(!finished){
                jobFinished.await();
            }
        } catch (InterruptedException ie){
            /*Do nothing*/
        }


        try{
//            releaseReservedPersons();  Release the persons at teh current port that had to be reserved while the job was progressing.
            System.out.println(getName() + " finished! using countDown()...");
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

    public void setCountDownLatch(CountDownLatch doneSignal){
        try{
            this.doneSignal = doneSignal;
        } finally {
            prepareToStart();
        }
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
}
