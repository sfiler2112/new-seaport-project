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
    JProgressBar progressBar;
    private int progress;
    private volatile boolean paused;
    private volatile boolean canceled;

    public Void doInBackground(){
        System.out.println("Starting JobSwingWorker.doInBackground() for " + jobThing.getName());
        setProgress(progress);
        while(progress < 100){

            if(isCancelled()){
                progress = 101;
            }
            try{
                Thread.sleep((long) (duration * 10));
            } catch (InterruptedException ie){
                /*Do nothing*/
                System.out.println("is do in background being interrupted?");
            }

            if(!paused){
                progress++;
            }

            setProgress(progress);
        }

        return null;
    }


    public void done(){
        jobThing.finish();
        System.out.println("finished the job: " + jobThing.getName());
        System.out.println(Thread.currentThread().getName() + ": done with job " + jobThing.getName());
    }

    public void pause(){
        System.out.println("pause called from jobSW-" + index);
        paused = true;
    }
    
    public void play(){
        System.out.println("play called from jobSW-" + index);
        paused = false;
    }
    

    public JobSwingWorker(Job job){
        jobThing = job;
        jobThing.attachJobSwingWorker(this);
        index = jobThing.getIndex();
        System.out.println("JobSwingWorker created! index: " + index);
        duration = jobThing.getDuration();
        progress = 0;
    }

    public int getIndex(){
        return index;
    }


    public boolean isPaused(){
        return paused;
    }
}