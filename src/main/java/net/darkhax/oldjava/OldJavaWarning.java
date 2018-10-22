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

@Mod(modid = "oldjava", name = "Old Java Warning", version = "@VERSION@", clientSideOnly = true, certificateFingerprint = "@FINGERPRINT@")
public class OldJavaWarning {
    
    private static final Logger LOG = LogManager.getLogger("Old Java");
    private static final String URL_UPDATE_JAVA = "https://gist.github.com/darkhax/2d82494a88327327cec6d5172b1db239#file-oldjava-md";
    private static final String URL_BIT_SIZE = "https://www.java.com/en/download/faq/java_win64bit.xml#Java%20for%2064-bit";
    private static final String INFO_UPDATE_JAVA = "<html><p>Warning: An outdated version of Java has been detected. This can cause the following issues with your game.<br>It is highly recommended to update Java to a newer version of Java 8.</p><ul><li>Poor performance and stuttering.</li><li>Crashes with newer mods.</li><li>Various security exploits.</li></ul>";
    private static final String INFO_BIT_SIZE = "A x32 installation of Java was detected. Using the x64 version of Java is recommended.";
    private static final File LOCK = new File("stop-java-detection.txt");
    
    public OldJavaWarning() {
        
        Config.syncConfigData();
        new Thread(OldJavaWarning::checkJavaVersion).start();
    }
    
    private static void checkJavaVersion () {
        
        // Exit early if the user has a lock file.
        if (LOCK.exists()) {
            
            return;
        }
        
        // Warn users if they are not using 64bit JVM and have less than 4gb available.
        if (Config.warn32 && isJvm64bit()) {
            
            displayWarning(INFO_BIT_SIZE, "Minecraft: Java Bit Type", URL_BIT_SIZE);
        }
        
        // Warn users about using older versions of Java.
        if (Config.warnVersion && Config.minVersion.compareTo(System.getProperty("java.version")) > 0) {

            displayWarning(INFO_UPDATE_JAVA, "Minecraft: Old Java Version", URL_UPDATE_JAVA);
        }
    }
    
    private static void displayWarning(String message, String title, String url) {
        
        final String[] options = { "Read More", "Ignore", "Stop Reminding" };
        final int response = JOptionPane.showOptionDialog(getPopupFrame(), message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        
        // Read More
        if (response == 0) {
            
            displayUpdateInfo(url);
        }
        
        // Stop Reminding Me
        else if (response == 2) {
            
            writeLock();
        }
        
        // -1 and 1 are close/ignore. No code needed.
    }
    
    private static void displayUpdateInfo (String url) {
        
        try {
            
            // Open the web page for updating Java.
            Desktop.getDesktop().browse(new URI(url));
        }
        
        catch (IOException | URISyntaxException e) {
            
            LOG.catching(e);
            JOptionPane.showMessageDialog(getPopupFrame(), "Could not access update guide. Do you have Internet?", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void writeLock () {
        
        try {
            
            FileUtils.write(LOCK, "If this file exists, the old java version popup will not be shown.", StandardCharsets.UTF_8);
        }
        
        catch (final IOException e) {
            
            LOG.catching(e);
        }
    }
    
    private static JFrame getPopupFrame () {
        
        final JFrame parent = new JFrame();
        parent.setAlwaysOnTop(true);
        return parent;
    }
    
    private static boolean isJvm64bit () {
        
        final String[] propertyStrings = new String[] { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };
        
        for (final String property : propertyStrings) {
            
            final String value = System.getProperty(property);
            
            if (value != null && value.contains("64")) {
                
                return true;
            }
        }
        
        return false;
    }
}