/*
 *  Name: Person
 *  Date: 8/30/2019
 *  Author: Sean Filer
 *  Purpose: Contained by a SeaPort, has a skill used for completing jobs.
 */
import java.util.*;
import java.util.concurrent.locks.*;

public class Person extends Thing{
    private String skill;
    private  boolean busy;
    private ReentrantLock personLock;
    
    public Person(String name, Scanner scannerLine){
        super(name, scannerLine);
        personLock = new ReentrantLock();
        busy = false;
        skill = scannerLine.next();

    }

    public String toString(){
        String personString = "Person: " + super.toString() + " " + skill;
        return personString;
    }

    public String getSkill(){
        return skill;
    }
    
    public void setBusy(){
        busy = true;
    }
    
    public void release(){
        personLock.lock();
        try{
            System.out.println(Thread.currentThread().getName() + ": attempting to release the person " + getName());
            if(busy){
                busy = false;
            } else {
                System.out.println(getName() + " was not busy already!");
            }
        } finally {
            personLock.unlock();
        }  
    }
    
    public void reserve(){
        personLock.lock();
        try{
            System.out.println(Thread.currentThread().getName() + ": attempting to reserve the person " + getName());
            if(!busy){
                busy = true;
            } else {
                System.out.println(getName() + " was actually busy!");
            }
        } finally {
            personLock.unlock();
        }  
    }
    
    public boolean isBusy(){
        return busy;
    }
}
