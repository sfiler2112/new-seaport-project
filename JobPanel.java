/*
 *  Name: JobPanel
 *  Date: 9/29/2019
 *  Author: Sean Filer
 *  Purpose: This will contain the progress bar for a job with buttons to play/pause or cancel the job.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class JobPanel extends JPanel implements PropertyChangeListener, ActionListener {
    private JProgressBar jobProgressBar;
    private JobSwingWorker jobSW;


    public JobPanel(Job job){
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.setLayout(new GridBagLayout());
        jobProgressBar = new JProgressBar(0,100);
        jobSW = job.getJobSW();
        jobProgressBar.setValue(jobSW.getProgress());
        jobSW.addPropertyChangeListener(this);
        System.out.println("new progress bar created for " + job.getName());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JLabel jobLabel = new JLabel(job.getName() + ":");
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(jobLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        topPanel.add(jobProgressBar, gbc);

        /*
         * Add the buttons to the JobPanel below the jobLabel and progress bar as a button panel
         */
//        pauseButton = new JButton("Pause");
//        pauseButton.setActionCommand("pause");
//        pauseButton.addActionListener(this);
//
//        playButton = new JButton("Play");
//        playButton.setActionCommand("play");
//        playButton.addActionListener(this);
//
//        cancelButton = new JButton("Cancel");
//        cancelButton.setActionCommand("cancel");
//        cancelButton.addActionListener(this);
//
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.add(pauseButton);
//        buttonPanel.add(cancelButton);

        JobRequestTrackerPanel requestTrackerPanel = new JobRequestTrackerPanel(job.getRequestedSkillTracker());
        job.getRequestedSkillTracker().setJobRequestTrackerPanel(requestTrackerPanel);
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(topPanel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(requestTrackerPanel, gbc);
    }


    public void propertyChange(PropertyChangeEvent event){
        if(event.getPropertyName().equals("progress")){
            /* Obtain the correct progress bar by using the event source's index
             * and get the value that corresponds to that key in the ConcurrentHashMap
             */
            jobProgressBar.setValue((Integer)event.getNewValue());
        }
    }

    public void actionPerformed(ActionEvent e){
        String actionCommand = e.getActionCommand();

        switch(actionCommand){
            case "pause":
                System.out.println("pause button for " + jobSW.getIndex() + " pressed.");
                break;
            case "play":
                System.out.println("play button for " + jobSW.getIndex() + " pressed.");
            case "cancel":
                System.out.println("cancel button for " + jobSW.getIndex() + " pressed.");
            default:
                break;
        }
    }


}
