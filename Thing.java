public class Thing implements Comparable<Thing> {

    private int index;
    private String name;
    private int parent;

    public Thing(){
        /* Default constructor, does nothing */
    }

    public Thing(int index, String name, int parent){
        this.index = index;
        this.name = name;
        this.parent = parent;
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
        return name + ":" + index;
    }
}
