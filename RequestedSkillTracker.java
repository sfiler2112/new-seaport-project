import java.util.concurrent.locks.ReentrantLock;

/*
 *  Name: RequestedSkillTracker
 *  Date: 10/13/2019
 *  Author: Sean Filer
 *  Purpose: A data structure class that tracks how many skills have been acquired and how many are still needed for a job.
 */
public class RequestedSkillTracker {
    private Job job;
    private int skilledPersonsAcquired;
    private int skilledPersonsNeeded;
    private ReentrantLock rstLock;
    private JobRequestTrackerPanel requestTrackerPanel;

    public RequestedSkillTracker(Job job){
        this.job = job;
        skilledPersonsAcquired = 0;
        skilledPersonsNeeded = job.getRequirements().size();
        rstLock = new ReentrantLock();
    }

    public synchronized void setJobRequestTrackerPanel(JobRequestTrackerPanel jrtPanel){
        requestTrackerPanel = jrtPanel;
    }

    public void personAcquired(){
        rstLock.lock();

        try{
            skilledPersonsAcquired++;
            skilledPersonsNeeded--;
        } finally {
            requestTrackerPanel.trackerUpdate(skilledPersonsAcquired, skilledPersonsNeeded);
            rstLock.unlock();
        }
    }

    public void personReleased(){
        rstLock.lock();

        try{
            skilledPersonsAcquired--;
            skilledPersonsNeeded++;
        } finally {
            requestTrackerPanel.trackerUpdate(skilledPersonsAcquired, skilledPersonsNeeded);
            rstLock.unlock();
        }
    }
}
