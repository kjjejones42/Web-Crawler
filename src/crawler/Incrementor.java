package crawler;

import javax.swing.*;

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
            String text = WebCrawler.formatDuration(System.currentTimeMillis() - startTime);
            if (!text.equals(lastText)) {                
                SwingUtilities.invokeLater(() -> label.setText(text));
                lastText = text;
            }
        }        
    }
}