package seaportproject;
import javax.swing.*;

public class JobWorker extends SwingWorker<Void, Void> {

    private int jobIndex;
    private double duration;
    private Job parentJob;


    public JobWorker(double duration, Job job){
        this.duration = duration;
        parentJob = job;
        System.out.println("job worker created");
    }


    public Void doInBackground() {
        int progress = 0;

        System.out.println("starting doInBackground");

        setProgress(progress);
        while(progress < 100){
            try {
                long sleepInterval = (long) duration * 100;
                System.out.println("sleeping for " + sleepInterval);
                Thread.sleep(sleepInterval);
            } catch (InterruptedException ignore) {
                /* Do nothing */
            }
            progress++;
            System.out.println("progress = " + progress);
        }
        return null;
    }

    public void done() {

    }
}
