 /*
 *  Name: PortPersonsController
 *  Date: 7/27/2019
 *  Author: Sean Filer
 *  Purpose: This class will be in charge of ordering access to the ArrayBlockingQueue 
 *  of persons at a SeaPort. 
 */
package seaportproject;

 import java.util.ArrayList;
 import java.util.concurrent.ArrayBlockingQueue;
 import java.util.concurrent.ThreadLocalRandom;
 import java.util.concurrent.locks.Condition;
 import java.util.concurrent.locks.ReentrantLock;
/**
 *
 * @author Sean
 */
public class PortPersonsController implements Runnable{
    private SeaPort port;
    private ArrayBlockingQueue<Person> personsBlockingQueue;
    private ArrayList<Person> portPersons;
    private ArrayList<String> availableSkills;
    private ReentrantLock portPCLock;
    private ArrayBlockingQueue<Person> returningPersonsList;
    private ArrayList<Person> selectedPersons;
    private ArrayList<String> requiredSkills;
    private Job currentJob;
    private Condition personsAreReturning;
    private Condition jobReservingPersons;
    private Condition jobWaitingOnReturn;
    private Condition nowSelectingPersons;
    private boolean haveReturnedPersons = false;
    private boolean haveJob = false;
    private boolean jobMustWait = false;
    private int reservationAttempts = 0;

    
    
    public void run(){
        reservePersons();
        selectRequiredPersons();
    }
    
    public PortPersonsController(SeaPort port){
        this.port =  port;

        portPCLock = new ReentrantLock();
        personsAreReturning = portPCLock.newCondition();
        jobReservingPersons = portPCLock.newCondition();
        jobWaitingOnReturn = portPCLock.newCondition();
        nowSelectingPersons = portPCLock.newCondition();
        availableSkills = new ArrayList<>();
        requiredSkills = new ArrayList<>();
        for(Person currentPerson: port.getPersons()){
            if(!availableSkills.contains(currentPerson.getSkill())) {
                availableSkills.add(currentPerson.getSkill());
            }
        }
        portPersons = port.getPersons();
        System.out.println("PortPersonsController created!");

    }

    public void setPersonsBlockingQueue(){
        personsBlockingQueue = port.getAvailablePersons();
    }
    
    public void reservePersons(){
        portPCLock.lock();
        boolean missingSkill = false;

        while(!haveJob){
            try{
                jobReservingPersons.await();
                System.out.println(Thread.currentThread().getName() + ": portPC receiving jobReservingPersonsSignal for " + currentJob.getName());
            } catch (InterruptedException ie){
                /*Do nothing*/
            }
        }
        try{

            System.out.println(currentJob.getName() + " requirements: " + currentJob.getRequirements());

            requiredSkills = currentJob.getRequirements();
            System.out.println("required skills has been set...");


        } finally {

            nowSelectingPersons.signal();
            portPCLock.unlock();
        }
    }

    public void personReturned(Person person){
        portPCLock.lock();

        try{
            if(!person.isReserved()){
                jobMustWait = false;
            }
        } finally {
            jobReservingPersons.signal();
        }


    }
    
    public void returnPersons(ArrayList<Person> returningPersons){
        portPCLock.lock();

        try{
            for(Person currentPerson: returningPersons){
                currentPerson.release();
            }
        } finally {
            jobWaitingOnReturn.signal();
            portPCLock.unlock();
        }
    }

//    public void waitForPersonsToReturn(){
//        portPCLock.lock();
//        while(jobMustWait){
//            try{
//                jobWaitingOnReturn.await();
//            } catch (InterruptedException ie){
//                /*Do nothing*/
//            }
//        }
//
//        try{
//            jobMustWait = false;
//        } finally {
//            jobReservingPersons.signal();
//            portPCLock.unlock();
//        }
//    }
    
//    public void setReturningPersons(ArrayBlockingQueue<Person> persons){
//        portPCLock.lock();
//
//        try{
//            returningPersonsList = persons;
//            haveReturnedPersons = true;
//        } finally {
//            returnPersons();
//            portPCLock.unlock();
//        }
//
//    }
    
    public void setJob(Job job){  // Called in Job.startWorking()
        portPCLock.lock();
        System.out.println(Thread.currentThread().getName() + "Setting portPC job to : " + job.getName());
        try{
            currentJob = job;
            haveJob = true;
            Thread portPCThread = new Thread(this, job.getName() + " portPC thread");
            System.out.println(portPCThread + " has been created for " + job.getName() +"!");
            portPCThread.start();
        } finally {
            System.out.println(Thread.currentThread().getName() + ": portPC signalling jobReservingPersons for " + currentJob.getName());
            jobReservingPersons.signal();
            portPCLock.unlock();
        }
        
    }
    
    
    
    public ArrayList<String> getSkills(){
        
        return availableSkills;
    }
    
    public void selectRequiredPersons(){
        portPCLock.lock();

        while(!haveJob){
            try{
                nowSelectingPersons.await();
            } catch (InterruptedException ie){
                /*Do nothing*/
            }
        }
        try{
            System.out.println(Thread.currentThread() + ": selecting required people for job " + currentJob.getName());
            
            int numberRequired = requiredSkills.size();
            selectedPersons = new ArrayList<>();

            
            for(int i = 0; i < portPersons.size() && numberRequired > 0; i++ ){
                Person currentPerson = portPersons.get(i);

                if(requiredSkills.contains(currentPerson.getSkill()) && !currentPerson.isReserved()){
                    selectedPersons.add(currentPerson);
                    numberRequired--;
                }
            }


            if(numberRequired == 0){
                System.out.println(Thread.currentThread() +": persons selected " + selectedPersons);
                jobMustWait = false;
                for(Person select: selectedPersons){
                    select.reserve(currentJob);
                }
            } else {
                System.out.println(Thread.currentThread() +": persons not reserved, not all required skills available. req skills: " + requiredSkills);
                jobMustWait = true;
            }
        }finally {
            if(jobMustWait){
                tryAgainLater();
            } else {
                
                currentJob.setReservedPersons(selectedPersons);
                
            }
            portPCLock.unlock();

        }
    }

    public void tryAgainLater(){ // Decreases the sleep time for every call of this method
        portPCLock.lock();
        System.out.println(Thread.currentThread().getName() + " will try again later...");
        try{
            long sleepTime = ThreadLocalRandom.current().nextLong((long)(1000-reservationAttempts), (long)(3000)-reservationAttempts);
            Thread.currentThread().sleep(sleepTime);
            reservationAttempts++;
        } catch (InterruptedException ie){
            /*Do nothing*/
        } finally {
            System.out.println(Thread.currentThread().getName() + ": reserveWorkers() attempt number " + reservationAttempts);
            selectRequiredPersons();
            portPCLock.unlock();
        }


    }

}
