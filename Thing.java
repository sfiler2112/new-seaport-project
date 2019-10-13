  
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class Thing implements Comparable<Thing>, Runnable {

    private int index;
    private String name;
    private int parent;
    
    public Thing(){
        /* Default constructor, does nothing */
    }

    public Thing(String name, Scanner scannerLine){
        this.name = name;
        this.parent = scannerLine.nextInt();
    }

    public void run(){
        /* do nothing */
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

    public void setIndex(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
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
