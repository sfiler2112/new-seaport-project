
import java.util.*;

public class Thing implements Comparable<Thing> {

//    private int index;
    private String name;
    private int parent;
    
    public Thing(){
        /* Default constructor, does nothing */
    }

//    public Thing(Scanner scannerLine){
//        this.name = scannerLine.next();
//        this.index = scannerLine.nextInt();
//        this.parent = scannerLine.nextInt();
//    }
//
    public Thing(String name, Scanner scannerLine){
        this.name = name;
        this.parent = scannerLine.nextInt();
    }

    public int compareTo(Thing otherThing){
        return name.compareTo(otherThing.getName());
    }

//    public int getIndex(){
//        return index;
//    }

    public String getName(){
        return name;
    }

    public int getParent(){
        return parent;
    }

    public String toString(){
        return name;
    } // Edited to remove index from the return value.

    public boolean isSeaPort(){
        return false;
    }
}
