package dev.vrba.minecraft.blockshuffle.game

import org.bukkit.Material
import org.bukkit.entity.Player

data class Round(
    val difficulty: Difficulty,
    val remainingBlocks: Map<Player, Material>,
    val timeLimit: Int,
    val timeElapsed: Int = 0
)