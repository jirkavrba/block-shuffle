package dev.vrba.minecraft.blockshuffle

import dev.vrba.minecraft.blockshuffle.command.BlockShuffleCommand
import dev.vrba.minecraft.blockshuffle.game.Configuration
import dev.vrba.minecraft.blockshuffle.game.createDefaultGameConfiguration
import dev.vrba.minecraft.blockshuffle.game.loadGameConfiguration
import dev.vrba.minecraft.blockshuffle.listener.GameListener
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class BlockShuffle : JavaPlugin()
{
    val manager: GamesManager = GamesManager(this)

    lateinit var gameConfiguration: Configuration

    override fun onEnable()
    {
        loadConfiguration()
        registerCommandExecutors()
    }

    private fun loadConfiguration()
    {
        val file = File(dataFolder, "config.yml")
        val configuration = YamlConfiguration()

        // If the configuration file doesn't exist, create a new one
        gameConfiguration = if (file.exists()) loadGameConfiguration(configuration, file)
                            else createDefaultGameConfiguration(configuration, file)
    }

    private fun registerCommandExecutors()
    {
        getCommand("block-shuffle")?.setExecutor(BlockShuffleCommand(this))
        server.pluginManager.registerEvents(GameListener(manager), this)
    }
}