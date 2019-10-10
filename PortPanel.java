/*
 *  Name: PortPanel
 *  Date: 9/29/2019
 *  Author: Sean Filer
 *  Purpose: This will contain the progress bars for the jobs currently being worked at a given port.
 */

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.ReentrantLock;
public class PortPanel  extends JPanel{
    private SeaPort port;
    private GridBagConstraints gbc = new GridBagConstraints();
    private ConcurrentHashMap<Integer, JobPanel> jobPanelMap;

    public PortPanel(SeaPort port){
        this.port = port;
        this.setBorder(new TitledBorder(port.getName()));
        this.port.setPortPanel(this);
        this.setLayout(new GridBagLayout());
        jobPanelMap = new ConcurrentHashMap<>();
    }




    public synchronized void update(ArrayList<Dock> portDocks){
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



                    if(!(currentShip == null)){
                        if(!currentShip.getJobs().isEmpty()){
                            JLabel dockLabel = new JLabel(currentDock.getName() + " jobs:");
                            gbc.gridx = 0;
                            gbc.gridy = yValue;
                            dockPanel.add(dockLabel, gbc);
                            yValue++;
                            Job[] jobsArray = currentShip.getJobs().toArray(new Job[currentShip.getJobs().size()]);
                            for(Job currentJob: jobsArray){
                                JobPanel currentJobPanel;
                                if(jobPanelMap.containsKey(currentJob.getIndex())){ // If this job's index is in the ConcurrentHashMap, get that jobPanel from the map.
                                    currentJobPanel = jobPanelMap.get(currentJob.getIndex());
                                } else {                                            // Otherwise, create a new jobPanel and add it to the map.
                                    currentJobPanel = new JobPanel(currentJob);
                                    jobPanelMap.put(currentJob.getIndex(), currentJobPanel);
                                }
                                gbc.gridy = yValue;
                                gbc.gridx = 1;
                                dockPanel.add(currentJobPanel, gbc);
                                yValue++;
                            }
                            gbc.gridx = 1;
                            System.out.println("current dockYValue: " + dockYValue);
                            gbc.gridy = dockYValue;
                            this.add(dockPanel, gbc);
                            dockYValue++;
                        }

                    }

                }
            }
        }
        this.revalidate();
    }


}