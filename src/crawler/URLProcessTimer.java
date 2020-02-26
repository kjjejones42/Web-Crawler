package crawler;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

class URLProcessTimer extends Timer {    

    private final long startTime;
    private final JLabel label;

    URLProcessTimer(long startTime, JLabel label) {
        this.startTime = startTime;
        this.label = label;
    }

    private TimerTask task = new TimerTask() {    
        @Override
        public void run() {     
            String text = WebCrawler.formatDuration(System.currentTimeMillis() - startTime);
            SwingUtilities.invokeLater(() -> label.setText(text));
        }
    };

    public void run() {        
        scheduleAtFixedRate(task, 0, 1000);  
    }    
}