package dev.vrba.minecraft.blockshuffle.game

import dev.vrba.minecraft.blockshuffle.game.Difficulty.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.configuration.file.FileConfiguration
import java.io.File

private fun Int.minutes(): Int = this * 60

data class Configuration(
    val timeLimits: Map<Difficulty, Int>,
    val blockPools: Map<Difficulty, Collection<Material>>,
)

private val defaultConfiguration = Configuration(
    // TODO: Balance time limits
    timeLimits = mapOf(
        Easy     to 5.minutes(),
        Normal   to 7.minutes(),
        Hard     to 10.minutes(),
        Hardcore to 10.minutes(),
        Brutal   to 15.minutes(),
    ),
    // TODO: Add more blocks
    blockPools = mapOf(
        Easy     to setOf(COBBLESTONE, STONE, GRASS_BLOCK, DIRT, WATER, SAND),
        Normal   to setOf(COAL_ORE, SANDSTONE),
        Hard     to setOf(STONE_BRICKS),
        Hardcore to setOf(NETHERRACK),
        Brutal   to setOf(DIAMOND_BLOCK)
    )
)

fun loadGameConfiguration(configuration: FileConfiguration, file: File): Configuration =
    try
    {
        configuration.load(file)

        val difficulties = Difficulty.values()

        Configuration(
            difficulties.associateWith {
                configuration.get("time-limits.${it.string}") as? Int
                    ?: throw Exception("Missing / invalid time limit value of difficulty [${it.string}]")
            },
            difficulties.associateWith {
                val keys = configuration.get("block-pools.${it.string}") as? Collection<*>
                    ?: throw Exception("Missing / invalid block pool of difficulty [${it.string}]")

                keys.map { key ->
                    getMaterial(key as String)
                        ?: throw Exception("Invalid material [$key] in block pool of difficulty [${it.string}]")
                }
            }
        )
    }
    catch (exception: Exception)
    {
        Bukkit.getLogger().severe("Cannot load / parse plugin configuration!")
        Bukkit.getLogger().severe(exception.message)

        // Fallback to loading default configuration
        defaultConfiguration
    }

fun createDefaultGameConfiguration(configuration: FileConfiguration, file: File): Configuration
{
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

    return defaultConfiguration
}
