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
    private World world;
    
    public Job(Scanner scannerLine, World world){
        super(scannerLine);
        
        duration = scannerLine.nextDouble();
        requirements = new ArrayList<>();
        while(scannerLine.hasNext()){
            requirements.add(scannerLine.next());
        }
        
        this.world = world;
    }
}
