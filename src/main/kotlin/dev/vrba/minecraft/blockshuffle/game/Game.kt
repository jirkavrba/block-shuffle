package dev.vrba.minecraft.blockshuffle.game

import org.bukkit.entity.Player

data class Game(
    val players: List<Player>,
    val playing: Boolean,
    val round: Round
)