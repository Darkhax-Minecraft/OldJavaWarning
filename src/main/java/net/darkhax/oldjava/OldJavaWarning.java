package net.darkhax.oldjava;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = "oldjava", name = "Old Java Warning", version = "@VERSION@", clientSideOnly=true, certificateFingerprint = "@FINGERPRINT@")
public class OldJavaWarning {
    
    private static final Logger LOG = LogManager.getLogger("Old Java");
    private static final String INSTRUCTIONS_LINK = "https://gist.github.com/darkhax/2d82494a88327327cec6d5172b1db239#file-oldjava-md";
    private static final File LOCK = new File("stop-java-detection.txt");
    
    public OldJavaWarning () {
        
        new Thread(OldJavaWarning::checkJavaVersion).start();
    }
    
    private static void checkJavaVersion () {
        
        // Check if Java version is a bad one and the lock file does not exist.
        // TODO Replace with a config to allow better version detection.
        if (System.getProperty("java.version").equalsIgnoreCase("1.8.0_25") && !LOCK.exists()) {
            
            final String[] options = { "Read More", "Ignore", "Stop Reminding" };
            
            final String message = "<html><p>Warning: A very outdated version of Java has been detected. This can cause the following issues with your game.<br>It is highly recommended to update Java to a newer version of Java 8.</p><ul><li>Poor performance and stuttering.</li><li>Crashes with newer mods.</li><li>Various security exploits.</li></ul>";
            int response = JOptionPane.showOptionDialog(getPopupFrame(), message, "Minecraft: Old Java Version", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            
            // Read More
            if (response == 0) {
                
                displayUpdateInfo();
            }
            
            // Stop Reminding Me
            else if (response == 2) {
                
                writeLock();
            }
            
            // -1 and 1 are close/ignore. No code needed.
        }
    }
    
    private static void displayUpdateInfo() {
        
        try {
            
            // Open the web page for updating Java.
            Desktop.getDesktop().browse(new URI(INSTRUCTIONS_LINK));
        }
        
        catch (IOException | URISyntaxException e) {

            LOG.catching(e);                    
            JOptionPane.showMessageDialog(getPopupFrame(), "Could not access update guide. Do you have Internet?", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void writeLock() {
        
        try {
            
            FileUtils.write(LOCK, "If this file exists, the old java version popup will not be shown.", StandardCharsets.UTF_8);
        }
        
        catch (IOException e) {
            
            LOG.catching(e);
        }
    }
    
    private static JFrame getPopupFrame() {
        
        final JFrame parent = new JFrame();
        parent.setAlwaysOnTop(true);
        return parent;
    }
}