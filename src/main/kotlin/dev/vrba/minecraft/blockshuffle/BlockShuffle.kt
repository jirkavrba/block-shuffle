package dev.vrba.minecraft.blockshuffle

import dev.vrba.minecraft.blockshuffle.game.defaultConfiguration
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class BlockShuffle : JavaPlugin() {

    override fun onEnable() {
        loadConfiguration()
    }

    private fun loadConfiguration() {
        val file = File(dataFolder, "config.yml")

        val configuration = YamlConfiguration()

        // If the configuration file doesn't exist, create a new one
        if (!file.exists()) {
            createDefaultConfiguration(configuration, file)
        }

        try {
            configuration.load(file)
        }
        catch (exception: Exception) {
            // TODO: Use default configuration
            logger.severe("Cannot load / parse plugin configuration!")
        }
    }

    private fun createDefaultConfiguration(configuration: FileConfiguration, file: File) {
        file.parentFile.mkdirs()

        // Default time configurations
        defaultConfiguration.timeLimits
            .mapKeys { it.key.string }
            .forEach {
                val (difficulty, time) = it
                configuration.set("time-limits.$difficulty", time)
            }

        // Default block pools configurations
        defaultConfiguration.blockPools
            .mapKeys { it.key.string }
            .forEach {
                val (difficulty, blocks) = it
                configuration.set("block-pools.$difficulty", blocks.map(Material::name))
            }

        configuration.save(file)
    }
}