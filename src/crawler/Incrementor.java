package crawler;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

class Incrementor implements Runnable {

    private final long startTime;
    private final JLabel label;
    private String lastText;

    Incrementor(long startTime, JLabel label) {
        this.startTime = startTime;
        this.label = label;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            long millis = System.currentTimeMillis() - startTime;
            long MINUTES = TimeUnit.MILLISECONDS.toMinutes(millis);
            String text = String.format("%02d:%02d", MINUTES,
                TimeUnit.MILLISECONDS.toSeconds(millis) - 
                TimeUnit.MINUTES.toSeconds(MINUTES));
            if (!text.equals(lastText)) {                
                SwingUtilities.invokeLater(() -> label.setText(text));
                lastText = text;
            }
        }        
    }
}