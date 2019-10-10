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
    private Thread jobThread;
    private ReentrantLock jobLock = new ReentrantLock();
    private Condition shipDocked;
    private Condition jobFinished;
    private CountDownLatch doneSignal;
    
    public Job(String name, int index, Scanner scannerLine){
        super(name, scannerLine);
        
        duration = scannerLine.nextDouble();
        requirements = new ArrayList<>();
        while(scannerLine.hasNext()){
            requirements.add(scannerLine.next());
        }

        shipDocked = jobLock.newCondition();
        jobFinished = jobLock.newCondition();
        this.setIndex(index);



        finished = false;
        shipNotDocked = true;
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

        while(shipNotDocked){
            try{
                System.out.println(Thread.currentThread().getName() + ": still not docked, waiting...");
                shipDocked.await();
            } catch (InterruptedException ie){
                /*Do nothing*/
            }
        }
        try{
            jobSW.execute();
        } finally {
            waitForJobToFinish();
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
}
