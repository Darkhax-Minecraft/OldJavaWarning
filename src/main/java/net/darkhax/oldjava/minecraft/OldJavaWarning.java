package net.darkhax.oldjava.minecraft;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.oldjava.Utils;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("oldjavawarning")
public class OldJavaWarning {
    
	public static final Logger LOG = LogManager.getLogger("Old Java Warning");
	
	private static final String OLD_JAVA_TITLE = "Minecraft: Outdated Java Version";
	private static final String OLD_JAVA_WARNING = "<html><div><p>Warning: You started Minecraft with Java %java_version%. This is an outdated version of Java. It is highly recommended to update to a newer version. Continuing to use an outdated version can cause the following issues.</p><p>&nbsp;</p></div><div><ul><li>Lag, stuttering, and other performance issues.</li><li>Crashes with some mods.</li><li>Various security issues.</li></ul></div>";
	private static final String OLD_JAVA_LINK = "https://gist.github.com/Darkhax/2d82494a88327327cec6d5172b1db239";
	
	private static final String ARCH_32_TITLE = "Minecraft: 32bit Install";
	private static final String ARCH_32_WARNING = "Warning: You started Minecraft with a 32x version of Java. Minecraft runs best on 64x versions of Java. It is highly recommended to switch to 64x Java if possible.";
	private static final String ARCH_32_LINK = "https://www.java.com/en/download/faq/java_win64bit.xml#Java%20for%2064-bit";
	
	private static final String MEMORY_TITLE = "Minecraft: System Memory";
	private static final String MEMORY_WARNING = "Warning: You started Minecraft with %s MB RAM. It is recommended that you use at least %s MB RAM for this pack.";
	private static final String MEMORY_LINK = "https://gist.github.com/Darkhax/95c1cd3aae090271134243f94150ba0d";
	
	public OldJavaWarning() {

		Configuration config = new Configuration();
		
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, config.getSpec());
        config.forceLoad(FMLPaths.CONFIGDIR.get().resolve("oldjavawarning-client.toml"));
        
        Path modFile = ModList.get().getModFileById("oldjavawarning").getFile().getFilePath();
        
		if (!Utils.isLocked()) {
			
			if (config.checkJavaVersion() && !Utils.compareJava(config.getMinJavaVersion())) {
				
				LOG.info("User has an outdated Java. Their version is {} and pack is configured to use {}.", Utils.getJavaVersion(), config.getMinJavaVersion());
				Utils.createWarning(OLD_JAVA_WARNING.replace("%java_version%", Utils.getJavaVersion()), OLD_JAVA_TITLE, OLD_JAVA_LINK, "Read More", "Ignore", "Stop Reminding", modFile);
			}
			
			if (config.check32Bit() && !Utils.isJvm64bit()) {
				
				LOG.info("User is using a 32x version of Java. 64x is recommended for this pack.");
				Utils.createWarning(ARCH_32_WARNING, ARCH_32_TITLE, ARCH_32_LINK, "Read More", "Ignore", "Stop Reminding", modFile);
			}
			
			if (config.checkLowMemory() && !Utils.hasRam(config.getRecommenedMemory())) {
				
				LOG.info("User has less than the recommended amount of ram. They have {}mb and the pack recommends {}mb.", format(Utils.getMemory()), format(config.getRecommenedMemory()));
				Utils.createWarning(String.format(MEMORY_WARNING, format(Utils.getMemory()), format(config.getRecommenedMemory())), MEMORY_TITLE, MEMORY_LINK, "Read More", "Ignore", "Stop Reminding", modFile);
			}
		}
		
		else {
			
			LOG.info("Mod has been disabled by the user.");
		}
	}
	
	private static String format(long bytes) {
		
		return Long.toString(bytes / 1000000L);
	}
}