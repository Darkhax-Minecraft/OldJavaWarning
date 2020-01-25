package net.darkhax.oldjava;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This class contains common utilities and methods for the mod. Everything in
 * this class must be safe to access or class load from any Java environment.
 */
public final class Utils {

	private static final File LOCK = new File("stop-java-detection.txt");

	/**
	 * Checks if the locked file {@link #LOCK} has been created. If this returns
	 * true the mod should completely disable itself.
	 * 
	 * @return Whether or not the lock file has been created.
	 */
	public static boolean isLocked() {

		return LOCK.exists();
	}

	/**
	 * Writes the {@link #LOCK} file which will disable the mod on future runs.
	 */
	public static void writeLock() {

		try (FileWriter writer = new FileWriter(LOCK)) {

			writer.write(
					"If this file exists, the old java version popup will not be shown. This should not be included with modpacks by default!");
		}

		catch (final IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Checks if the JVM is 64bit. This is done by checking various properties for
	 * the 64x flag.
	 * 
	 * @return Whether or not the system is a 64bit JVM.
	 */
	public static boolean isJvm64bit() {

		final String[] propertyStrings = new String[] { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };

		for (final String property : propertyStrings) {

			final String value = System.getProperty(property);

			if (value != null && value.contains("64")) {

				return true;
			}
		}

		return false;
	}
	
	public static boolean hasRam(long recommended) {
		
		long maxMemory = Runtime.getRuntime().maxMemory();
		return recommended <= maxMemory;
	}
	
	public static boolean compareJava(String target) {
		
		return target.compareTo(System.getProperty("java.version")) > 0;
	}
	
	public static String getJavaVersion() {
		
		return System.getProperty("java.version");
	}
	
	public static long getMemory() {
		
		return Runtime.getRuntime().maxMemory();
	}
	
	public static void createWarning(String message, String title, String url, String readMore, String ignore, String stop, Path file) {
		
		try {
			
			ProcessBuilder builder = new ProcessBuilder(System.getProperty("java.home") + "/bin/java", "-jar", file.toString(), message, title, url, readMore, ignore, stop);
			builder.inheritIO();
			builder.start();
		} 
		
		catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}