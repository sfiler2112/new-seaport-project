/*
 *  Name: SkillComparator
 *  Date: 9/13/2019
 *  Author: Sean Filer
 *  Purpose: For use with a collection that needs to be sorted by skill, a characteristic Persons and Jobs have. This comparator will only be used with Persons though.
 */
import java.util.Comparator;

public class SkillComparator implements Comparator<Person>{
     public int compare(Person person1, Person person2){
        
        String skill1 = person1.getSkill();
        String skill2 = person2.getSkill();
        
        return(skill1.compareTo(skill2));
    }
}
