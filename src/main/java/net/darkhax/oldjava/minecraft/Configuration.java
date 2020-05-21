package net.darkhax.oldjava.minecraft;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class Configuration {
    
    private final ForgeConfigSpec spec;
    
    private final BooleanValue checkJavaVersion;
    private final ConfigValue<String> minJavaVersion;
    
    private final BooleanValue check32Bit;
    
    private final BooleanValue checkMinMemory;
    private final ConfigValue<Integer> recommendedRam;
    
    public Configuration() {
        
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        // Java Version
        builder.comment("Settings related to checking for a very specific Minecraft version.");
        builder.push("java-version");
        
        builder.comment("Whether or not the Java version should be checked.");
        this.checkJavaVersion = builder.define("enabled", true);
        
        builder.comment("What is the minimum version of Java to look for?");
        this.minJavaVersion = builder.define("minVersion", "1.8.0_162");
        builder.pop();
        
        // JVM Arch
        builder.comment("Settings related to the Java architecture.");
        builder.push("jvm-architecture");
        
        builder.comment("Should people using 32 bit Java be warned and asked to use 64 bit Java instead?");
        this.check32Bit = builder.define("warn32Bit", true);
        builder.pop();
        
        // System Memory
        builder.comment("Settings related to Java's memory management.");
        builder.push("memory");
        
        builder.comment("Should users with low RAM be warned?");
        this.checkMinMemory = builder.define("warnLowMemory", true);
        
        builder.comment("The amount of recommended ram to play the pack. This is in megabytes.");
        this.recommendedRam = builder.define("recommendedRam", 1024);
        builder.pop();
        
        this.spec = builder.build();
    }
    
    public ForgeConfigSpec getSpec () {
        
        return this.spec;
    }
    
    public boolean checkJavaVersion () {
        
        return this.checkJavaVersion.get();
    }
    
    public String getMinJavaVersion () {
        
        return this.minJavaVersion.get();
    }
    
    public boolean check32Bit () {
        
        return this.check32Bit.get();
    }
    
    public boolean checkLowMemory () {
        
        return this.checkMinMemory.get();
    }
    
    public long getRecommenedMemory () {
        
        return this.recommendedRam.get() * 1000000L;
    }
    
    public void forceLoad (Path path) {
        
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        this.spec.setConfig(configData);
    }
}