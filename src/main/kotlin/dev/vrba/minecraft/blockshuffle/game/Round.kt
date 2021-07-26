package dev.vrba.minecraft.blockshuffle.game

import org.bukkit.Material
import org.bukkit.entity.Player

data class Round(
    val difficulty: Difficulty,
    val remainingBlocks: Map<Player, Material>,
    val remainingSeconds: Int
)