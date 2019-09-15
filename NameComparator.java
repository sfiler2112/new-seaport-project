/*
 *  Name: NameComparator
 *  Date: 9/12/2019
 *  Author: Sean Filer
 *  Purpose: For use with a collection that needs to be sorted by name, a characteristic all Things have. 
 */
import java.util.Comparator;

public class NameComparator implements Comparator<Thing>{
     public int compare(Thing thing1, Thing thing2){
        
        String name1 = thing1.getName();
        String name2 = thing2.getName();
        
        return(name1.compareTo(name2));
    }
}
