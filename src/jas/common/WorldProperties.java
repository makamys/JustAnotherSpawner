package jas.common;

import jas.common.config.LivingConfiguration;

import java.io.File;
import java.io.IOException;

import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class WorldProperties {

    /* Functional Universal Directory Settings, marks the Way the System should Sort */
    public boolean universalDirectory;
    /* Placeholder used to determine if the current directory needs to be deleted and changed */
    public boolean loadedUniversalDirectory;

    /* Functional Universal Directory Settings, marks the Way the System should Sort */
    public boolean savedSortCreatureByBiome;
    /* Placeholder used to determine if the current directory needs to be deleted and changed */
    public boolean loadedSortCreatureByBiome;

    public int despawnDist = 32;
    public int maxDespawnDist = 128;
    public int minDespawnTime = 600;

    public String saveName = "";
    public String importName = "";

    public WorldProperties(File configDirectory, World world) {
        loadWorldSaveConfiguration(configDirectory, world);
        importDefaultFiles(configDirectory);
        loadFileSaveConfiguration(configDirectory);
        loadWorldProperties(configDirectory);
    }

    /**
     * Load data related to how and where the files are desired to be saved
     */
    public void loadWorldSaveConfiguration(File configDirectory, World world) {
        Configuration worldGloablConfig = new Configuration(new File(configDirectory, DefaultProps.WORLDSETTINGSDIR
                + "SaveConfig.cfg"));
        String curWorldName = world.getWorldInfo().getWorldName();
        worldGloablConfig.load();

        /* Load Save Use Import_Name */
        Property importProp = worldGloablConfig.get("Save_Configuration", "Import_Name", "",
                "Folder name to Copy Missing Files From. Case Sensitive if OS allows. Beware invalid OS characters.");
        importName = importProp.getString();

        /* Load Save Use Global Save_Name */
        Property defaultsaveProp = worldGloablConfig
                .get("Save_Configuration",
                        "Default Save_Name",
                        "{$world}",
                        "Default name used for Save_Name. {$world} is replaced by world name. Case Sensitive if OS allows. Beware invalid OS characters.");
        saveName = defaultsaveProp.getString().replace("{$world}", curWorldName);

        /* Load Save Use Actual Save_Name */
        Property saveProp = worldGloablConfig
                .get("Save_Configuration." + curWorldName, "Save_Name", saveName,
                        "Folder name to look for and generate CFG files. Case Sensitive if OS allows. Beware invalid OS characters.");
        saveName = saveProp.getString().trim().equals("") ? "default" : saveProp.getString();

        /* Load Save Sort Creature By Biome */
        loadedSortCreatureByBiome = worldGloablConfig
                .get("Save_Configuration." + curWorldName, "Sort Creature By Biome - Setting", true,
                        "Determines if Entity CFGs are sorted internally by Entity or Biome. Change from TRUE to FALSE to alter sorting.")
                .getBoolean(true);

        /* Load Save/Use Universal Entity Directory */
        loadedUniversalDirectory = worldGloablConfig.get("Save_Configuration." + curWorldName,
                "Universal Entity CFG - Settings", false,
                "Specifies if the User wants the Entity CFG to Combined into a Universal CFG.").getBoolean(false);
        worldGloablConfig.save();
    }

    public void importDefaultFiles(File modConfigDirectoryFile) {
        if (importName.trim().equals("")) {
            return;
        }
        File worldFolder = new File(modConfigDirectoryFile, DefaultProps.WORLDSETTINGSDIR + saveName);
        File importFolder = new File(modConfigDirectoryFile, DefaultProps.WORLDSETTINGSDIR + importName);
        if (worldFolder.exists() || !importFolder.exists()) {
            return;
        }
        try {
            FileUtilities.copy(importFolder, worldFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load data related to how the files to be read were actually saved / formatted
     */
    public void loadFileSaveConfiguration(File configDirectory) {
        LivingConfiguration livingTempSettings = new LivingConfiguration(configDirectory, "temporarySaveSettings", this);
        livingTempSettings.load();
        savedSortCreatureByBiome = livingTempSettings.getSavedSortByBiome(
                JustAnotherSpawner.properties().globalSortCreatureByBiome).getBoolean(
                JustAnotherSpawner.properties().globalSortCreatureByBiome);
        universalDirectory = livingTempSettings.getSavedUseUniversalConfig(loadedUniversalDirectory).getBoolean(
                loadedUniversalDirectory);
        livingTempSettings.save();
    }

    public void loadWorldProperties(File configDirectory) {
        Configuration worldConfig = new Configuration(new File(configDirectory, DefaultProps.WORLDSETTINGSDIR
                + saveName + "/" + "WorldGlobalProperties" + ".cfg"));
        worldConfig.load();
        despawnDist = worldConfig.get("Properties.Spawning", "Min Despawn Distance", despawnDist).getInt(despawnDist);
        worldConfig.save();
    }
}
