package net.darkhax.oldjava;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class Config {
    
    public static Configuration cfg = new Configuration(new File("config/oldjavawarning.cfg"));
    
    public static boolean warnVersion = true;
    public static boolean warn32 = false;
    public static String minVersion = "1.8.0_162";
    
    public static void syncConfigData () {
        
        warnVersion = cfg.getBoolean("warnVersion", "general", true, "Should users with lower versions of Java be warned?");
        warn32 = cfg.getBoolean("warn32", "general", false, "Should users with 32x versions of Java be warned?");
        minVersion = cfg.getString("minJavaVersion", "general", "1.8.0_162", "The lowest Java version users can use. This must follow the Java version format!");
        
        if (cfg.hasChanged()) {
            
            cfg.save();
        }
    }
}