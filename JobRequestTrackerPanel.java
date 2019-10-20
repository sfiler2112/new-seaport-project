import javax.swing.*;
import java.awt.*;
import java.util.concurrent.locks.ReentrantLock;

/*
 *  Name: JobRequestTrackerPanel
 *  Date: 10/13/2019
 *  Author: Sean Filer
 *  Purpose: A data structure class that tracks how many skills have been acquired and how many are still needed for a job.
 */
public class JobRequestTrackerPanel extends JPanel {
    private RequestedSkillTracker requestTracker;
    private ReentrantLock jrtPanelLock;
    private JTextArea acquiredNumberText = new JTextArea("0");
    private JTextArea neededNumberText = new JTextArea();

    public JobRequestTrackerPanel(RequestedSkillTracker requestTracker){
        this.requestTracker = requestTracker;
        jrtPanelLock = new ReentrantLock();
        this.setLayout(new GridBagLayout());

        JPanel acquiredPanel = new JPanel();
        acquiredPanel.setLayout(new GridBagLayout());
        acquiredPanel.add(new JLabel("People Acquired:"));
        acquiredPanel.add(acquiredNumberText);

        JPanel neededPanel = new JPanel();
        neededPanel.setLayout(new GridBagLayout());
        neededPanel.add(new JLabel("Skills Needed:"));
        neededPanel.add(neededNumberText);

        acquiredNumberText.setText("?");
        neededNumberText.setText("?");

        this.add(acquiredPanel);
        this.add(neededPanel);
    }

    public void trackerUpdate(int acquiredNum, int neededNum){
        jrtPanelLock.lock();

        try{
            acquiredNumberText.setText(String.valueOf(acquiredNum));
            neededNumberText.setText(String.valueOf(neededNum));
        } finally {
            this.revalidate();
            jrtPanelLock.unlock();
        }
    }
}
