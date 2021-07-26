package dev.vrba.minecraft.blockshuffle.game

import dev.vrba.minecraft.blockshuffle.game.Difficulty.*
import org.bukkit.Material
import org.bukkit.Material.*

private fun Int.seconds(): Int = this * 1000
private fun Int.minutes(): Int = this * 60 * 1000

val defaultConfiguration = Configuration(
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

data class Configuration(
    val timeLimits: Map<Difficulty, Int>,
    val blockPools: Map<Difficulty, Collection<Material>>,
)