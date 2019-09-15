/*
 *  Name: LengthComparator
 *  Date: 9/13/2019
 *  Author: Sean Filer
 *  Purpose: For use with a collection that needs to be sorted by length, a Ship characteristic. The SeaPort class for the ships and shipQueue array lists.
 */
import java.util.Comparator;

public class LengthComparator implements Comparator<Ship>{
     public int compare(Ship ship1, Ship ship2){
        
        double length1 = ship1.getLength();
        double length2 = ship2.getLength();
        
        if(length1 > length2){
            return 1;
        } else if (length1 < length2) {
            return -1;
        } else {
            return 0;
        }
    }
}
