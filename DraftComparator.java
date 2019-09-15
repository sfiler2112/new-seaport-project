/*
 *  Name: DraftComparator
 *  Date: 9/12/2019
 *  Author: Sean Filer
 *  Purpose: For use with a collection that needs to be sorted by draft, a Ship characteristic. The SeaPort class for the ships and shipQueue array lists.
 */
import java.util.Comparator;

public class DraftComparator implements Comparator<Ship>{
     public int compare(Ship ship1, Ship ship2){
        
        double draft1 = ship1.getDraft();
        double draft2 = ship2.getDraft();
        
        if(draft1 > draft2){
            return 1;
        } else if (draft1 < draft2) {
            return -1;
        } else {
            return 0;
        }
    }
}
