/*
 *  Name:  JobSwingWorker
 *  Date: 9/29/2019
 *  Author: Sean Filer
 *  Purpose: To extend SwingWorker in a way that is useful for Job to use when its progress must be displayed with a JProgressbar.
 */


import javax.swing.SwingWorker;
import javax.swing.*;



public class JobSwingWorker extends SwingWorker<Void, Long>{

    private Job jobThing;
    private int index;
    private double duration;
    private JPanel panel;
    JProgressBar progressBar;

    public Void doInBackground(){
        System.out.println("Starting JobSwingWorker.doInBackground() for " + jobThing.getName());
        int progress = 0;
        setProgress(progress);
        while(progress < 100){
            try{
                if(progress%10 == 0){
                    System.out.println(jobThing.getName() + "progress = " + progress);
                }

                Thread.sleep((long) (duration * 10));
            } catch (InterruptedException ie){
                /*Do nothing*/
                System.out.println("is do in background being interrupted?");
            }
            progress++;
            setProgress(progress);
        }

        return null;
    }

    public boolean getDockLock(){
        return true;
    }

    public void done(){
        jobThing.finish();
        System.out.println("finished the job: " + jobThing.getName());
        System.out.println(Thread.currentThread().getName() + ": done with job " + jobThing.getName());
    }

    public JobSwingWorker(Job job){
        jobThing = job;
        jobThing.attachJobSwingWorker(this);
        index = jobThing.getIndex();
        System.out.println("JobSwingWorker created! index: " + index);
        duration = jobThing.getDuration();
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
    }

    public int getIndex(){
        return index;
    }

    public void setPanel(JPanel panel){
        this.panel = panel;
    }

    public JProgressBar getProgressBar(){
        return progressBar;
    }
}