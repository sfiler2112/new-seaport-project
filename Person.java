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
    private World world;
    
    public Person(Scanner scannerLine, World world){
        super(scannerLine);
        
        busy = false;
        skill = scannerLine.next();
        
        this.world = world;
    }
}
