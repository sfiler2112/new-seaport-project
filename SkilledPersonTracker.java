import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/*
 *  Name: SkilledPersonTracker
 *  Date: 10/13/2019
 *  Author: Sean Filer
 *  Purpose: A data structure class that tracks the persons of each skill for use GUI's tracking resource usage and requests.
 */
public class SkilledPersonTracker {
    private ArrayList<Person> skilledPersonList = new ArrayList<>();  // Contains all the people at a port with a particular skill.
    private ArrayList<Person> busyPersonList = new ArrayList<>(); // Contains all the people of a particular skill that are currently assigned to a Job.
    private ArrayList<Person> availablePersonList = new ArrayList<>(); // Contains all the people of a particular skill that are currently available.
    private String skill; // The skill of all the people tracked by this SkilledPersonTracker.
    private ReentrantLock sptLock;
    private int busyPersonCount;
    private int availablePersonCount;

    public SkilledPersonTracker(String argSkill, ArrayList<Person> portPersons){
        skill = argSkill;

        for(Person currentPerson: portPersons){
            if(currentPerson.getSkill().equals(skill)){
                skilledPersonList.add(currentPerson);
                availablePersonList.add(currentPerson);
            }
        }
        busyPersonCount = 0;
        availablePersonCount = availablePersonList.size();
    }

    public ArrayList<Person> getAvailablePersonList(){
        return availablePersonList;
    }

    public ArrayList<Person> getBusyPersonList(){
        return busyPersonList;
    }

    public void personReserved(Person targetPerson){
        availablePersonList.remove(targetPerson);
        busyPersonList.add(targetPerson);
        availablePersonCount--;
        busyPersonCount++;
    }

    public void personReleased(Person targetPerson){
        availablePersonList.remove(targetPerson);
        busyPersonList.add(targetPerson);
        availablePersonCount--;
        busyPersonCount++;
    }

}
