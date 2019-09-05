import java.util.*;

public class Thing implements Comparable<Thing> {

    private int index;
    private String name;
    private int parent;
    private World world;
    
    public Thing(){
        /* Default constructor, does nothing */
    }

    public Thing(Scanner scannerLine){
        this.name = scannerLine.next();
        this.index = scannerLine.nextInt();
        this.parent = scannerLine.nextInt();
    }

    public int compareTo(Thing otherThing){
        if(index < otherThing.getIndex()){
            return -1;
        } else if(index > otherThing.getIndex()){
            return 1;
        } else {
            return 0;
        }
    }

    public int getIndex(){
        return index;
    }

    public String getName(){
        return name;
    }

    public int getParent(){
        return parent;
    }

    public String toString(){
        return name + " " + index;
    }

    public boolean isSeaPort(){
        return false;
    }
}
