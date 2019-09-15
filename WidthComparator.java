/*
 *  Name: WidthComparator
 *  Date: 9/13/2019
 *  Author: Sean Filer
 *  Purpose: For use with a collection that needs to be sorted by width, a Ship characteristic. The SeaPort class for the ships and shipQueue array lists.
 */
import java.util.Comparator;

public class WidthComparator implements Comparator<Ship>{
     public int compare(Ship ship1, Ship ship2){
        
        double width1 = ship1.getWidth();
        double width2 = ship2.getWidth();
        
        if(width1 > width2){
            return 1;
        } else if (width1 < width2) {
            return -1;
        } else {
            return 0;
        }
    }
}
