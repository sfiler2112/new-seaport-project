/*
 *  Name: PortTime
 *  Date: 8/30/2019
 *  Author: Sean Filer
 *  Purpose: Holds the time of initialization.  Can return that time or the current time as an int value.
 */
import java.time.LocalDateTime;

public class PortTime {
    private int startTime;

    public PortTime()
    {
        startTime = LocalDateTime.now().toLocalTime().toSecondOfDay();
    }

    public int getStartTime()
    {
        return startTime;
    }


    public int getCurrentTime()
    {
        return LocalDateTime.now().toLocalTime().toSecondOfDay();
    }
}