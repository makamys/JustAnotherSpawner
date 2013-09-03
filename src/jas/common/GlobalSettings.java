package jas.common;

import java.io.File;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class GlobalSettings {
    public boolean debugMode = false;
    public int spawnerTickSpacing = 0;

    public boolean globalSortCreatureByBiome = true;

    public boolean turnGameruleSpawningOff = false;
    public boolean emptyVanillaSpawnLists = true;

    GlobalSettings(File configDirectory) {
        loadProperties(configDirectory);
    }

    public void loadProperties(File configDirectory) {
        Configuration config = new Configuration(
                new File(configDirectory, DefaultProps.MODDIR + "GlobalProperties.cfg"));
        config.load();
        turnGameruleSpawningOff = config.get("Properties.Vanilla Controls", "Gamerule doSpawning Off on Start",
                turnGameruleSpawningOff).getBoolean(turnGameruleSpawningOff);
        emptyVanillaSpawnLists = config.get("Properties.Vanilla Controls", "Empty Vanilla SpawnLists on Start", true)
                .getBoolean(true);

        globalSortCreatureByBiome = config.get("Properties.Spawning", "Sort Creature By Biome",
                globalSortCreatureByBiome).getBoolean(globalSortCreatureByBiome);
        Property resultTickSpacing = config.get("Properties.Spawning", "Spawner Tick Spacing", spawnerTickSpacing);
        if (resultTickSpacing.getInt(spawnerTickSpacing) < 0) {
            JASLog.severe(
                    "Error with spawnerTickSpacing is %s. spawnerTickSpacing cannot be less than zero. Setting to 0.",
                    resultTickSpacing.getInt(spawnerTickSpacing));
            resultTickSpacing.set(spawnerTickSpacing);
        } else {
            spawnerTickSpacing = resultTickSpacing.getInt(spawnerTickSpacing);
        }
        config.save();
    }
}