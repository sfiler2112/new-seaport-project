/*
 *  Name: WeightComparator
 *  Date: 9/13/2019
 *  Author: Sean Filer
 *  Purpose: For use with a collection that needs to be sorted by weight, a Ship characteristic. The SeaPort class for the ships and shipQueue array lists.
 */
import java.util.Comparator;

public class WeightComparator implements Comparator<Ship>{
     public int compare(Ship ship1, Ship ship2){
        
        double weight1 = ship1.getWeight();
        double weight2 = ship2.getWeight();
        
        if(weight1 > weight2){
            return 1;
        } else if (weight1 < weight2) {
            return -1;
        } else {
            return 0;
        }
    }
}
