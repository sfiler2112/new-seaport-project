/*
 *  Name: Person
 *  Date: 8/30/2019
 *  Author: Sean Filer
 *  Purpose: Contained by a SeaPort, has a skill used for completing jobs.
 */
import java.util.*;

public class Person extends Thing{
    private String skill;
    private boolean busy;
    
    public Person(String name, Scanner scannerLine){
        super(name, scannerLine);
        
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
}
