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

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "oldjava", name = "Old Java Warning", version = "@VERSION@", clientSideOnly = true, certificateFingerprint = "@FINGERPRINT@")
public class OldJavaWarning {
    
    private static final Logger LOG = LogManager.getLogger("Old Java");
    private static final File LOCK = new File("stop-java-detection.txt");
    
    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        
        Config.syncConfigData();
        new Thread(OldJavaWarning::checkJavaVersion).start();
    }
    
    private static void checkJavaVersion () {
        
        // Exit early if the user has a lock file.
        if (LOCK.exists()) {
            
            return;
        }
        
        // Warn users if they are not using 64bit JVM.
        if (Config.warn32 && !isJvm64bit()) {
            
            displayWarning(I18n.format("oldjava.notice.bit.body"), I18n.format("oldjava.notice.bit.title"), I18n.format("oldjava.url.bitinfo"));
        }
        
        // Warn users about using older versions of Java.
        String[] VersionConfig = Config.minVersion.split("_");
        String[] VersionSystem = System.getProperty("java.version").split("_");

        if(VersionConfig.length>1 && VersionSystem.length>1)
            if(VersionConfig[1].matches("[0-9]+") && VersionSystem[1].matches("[0-9]+"))
                if (Config.warnVersion && Integer.parseInt(Config.minVersion.split("_")[1])>Integer.parseInt(System.getProperty("java.version").split("_")[1]))
                    displayWarning(I18n.format("oldjava.notice.update.body"), I18n.format("oldjava.notice.update.title"), I18n.format("oldjava.url.updateinfo"));


    }
    
    private static void displayWarning(String message, String title, String url) {
        
        final String[] options = { I18n.format("oldjava.buttons.readmore"), I18n.format("oldjava.buttons.ignore"), I18n.format("oldjava.buttons.stop")};
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
