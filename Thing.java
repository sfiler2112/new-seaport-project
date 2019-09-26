  
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class Thing implements Comparable<Thing> {

//    private int index;
    private String name;
    private int parent;
    
    public Thing(){
        /* Default constructor, does nothing */
    }

    public Thing(String name, Scanner scannerLine){
        this.name = name;
        this.parent = scannerLine.nextInt();
    }

    public int compareTo(Thing otherThing){
        return name.compareTo(otherThing.getName());
    }

    public DefaultMutableTreeNode getNode(){
        return new DefaultMutableTreeNode(name);
    }

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
