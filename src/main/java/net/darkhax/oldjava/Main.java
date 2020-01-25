package net.darkhax.oldjava;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {

	public static void main(String... args) {
		
		displayWarning(args[0], args[1], args[2], args[3], args[4], args[5]);
	}
	
    private static void displayWarning(String message, String title, String url, String readMore, String ignore, String stop) {
        
        final String[] options = { readMore, ignore, stop };
        final int response = JOptionPane.showOptionDialog(getPopupFrame(), message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        
        // Read More
        if (response == 0) {
            
            displayUpdateInfo(url);
        }
        
        // Stop Reminding Me
        else if (response == 2) {
            
            Utils.writeLock();
        }
        
        // -1 and 1 are close/ignore. No code needed.
        
        System.exit(0);
    }
    
    private static void displayUpdateInfo (String url) {
        
        try {
            
            // Open the web page for updating Java.
            Desktop.getDesktop().browse(new URI(url));
        }
        
        catch (IOException | URISyntaxException e) {
            
        	e.printStackTrace();
            JOptionPane.showMessageDialog(getPopupFrame(), "Could not access update guide. Do you have Internet?", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static JFrame getPopupFrame () {
        
        final JFrame parent = new JFrame();
        parent.setAlwaysOnTop(true);
        return parent;
    }
}