/*
 *  Name: PortPanel
 *  Date: 9/29/2019
 *  Author: Sean Filer
 *  Purpose: This will contain the progress bars for the jobs currently being worked at a given port.
 */

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.ReentrantLock;
public class PortPanel  extends JPanel implements  PropertyChangeListener {
    private SeaPort port;
    private GridBagConstraints gbc = new GridBagConstraints();
    private ConcurrentHashMap<Integer, JProgressBar> progressBarMap;

    public PortPanel(SeaPort port){
        this.port = port;
        this.setBorder(new TitledBorder(port.getName()));
        this.port.setPortPanel(this);
        this.setLayout(new GridBagLayout());
        progressBarMap = new ConcurrentHashMap<>();
    }

    public void propertyChange(PropertyChangeEvent event){
        if(event.getPropertyName().equals("progress")){
            /* Obtain the correct progress bar by using the event source's index
             * and get the value that corresponds to that key in the ConcurrentHashMap
             */
            JobSwingWorker jobSW = (JobSwingWorker) event.getSource();
            JProgressBar progressBar = progressBarMap.get(jobSW.getIndex());
            progressBar.setValue((Integer)event.getNewValue());

        }
    }


    public void update(ArrayList<Dock> portDocks){
        System.out.println("Updateing portPanel, current occupied docks: " + portDocks);
        this.removeAll();
        if(!portDocks.isEmpty()){
            int dockYValue = 0;
            JPanel dockPanel;
            for(Dock currentDock: portDocks){
                if(currentDock.isOccupied()){
                    System.out.println("updating dock panel for: " + currentDock.getName());
                    dockPanel = new JPanel();
                    dockPanel.setLayout(new GridBagLayout());

                    int yValue = 0; // used for setting the grid bag constraints as the for-loop progresses.

                    Ship currentShip = currentDock.getShip();

                    JLabel dockLabel = new JLabel(currentDock.getName() + " jobs:");
                    gbc.gridx = 0;
                    gbc.gridy = yValue;
                    dockPanel.add(dockLabel, gbc);
                    yValue++;

                    if(!(currentShip == null)){
                        if(!currentShip.getJobs().isEmpty()){
                            Job[] jobsArray = currentShip.getJobs().toArray(new Job[currentShip.getJobs().size()]);
                            for(Job currentJob: jobsArray){
                                gbc.gridy = yValue;
                                gbc.gridx = 0;
                                dockPanel.add(new JLabel(currentJob.getName() + ":"), gbc);
                                JProgressBar jobProgressBar;
                                if(progressBarMap.containsKey(currentJob.getIndex())){
                                    jobProgressBar = progressBarMap.get(currentJob.getIndex());
                                } else {
                                    jobProgressBar = createNewProgressBar(currentJob);
                                }
                                gbc.gridy = yValue;
                                gbc.gridx = 1;
                                dockPanel.add(jobProgressBar, gbc);
                                yValue++;
                            }
                        }

                    }
                    gbc.gridx = 1;
                    System.out.println("current dockYValue: " + dockYValue);
                    gbc.gridy = dockYValue;
                    this.add(dockPanel, gbc);
                    dockYValue++;
                }
            }
        }
        this.revalidate();
    }


    public JProgressBar createNewProgressBar(Job currentJob){
        JProgressBar newProgressBar = new JProgressBar(0,100);
        newProgressBar.setValue(0);

        JobSwingWorker jobSW = currentJob.getJobSW();
        jobSW.addPropertyChangeListener(this);

        int pbIndex = currentJob.getIndex();

        progressBarMap.put(pbIndex, newProgressBar);
        return newProgressBar;
    }
}