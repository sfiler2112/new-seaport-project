/*
 *  Name: Job
 *  Date: 8/30/2019
 *  Author: Sean Filer
 *  Purpose: Contained by a ship, has a duration and a list of requirements.  Requirements are skills that a Person must have to start the job.
 */
import java.util.*;

public class Job extends Thing{
    private double duration;
    private ArrayList<String> requirements;
    
    public Job(String name, Scanner scannerLine){
        super(name, scannerLine);
        
        duration = scannerLine.nextDouble();
        requirements = new ArrayList<>();
        while(scannerLine.hasNext()){
            requirements.add(scannerLine.next());
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
}
