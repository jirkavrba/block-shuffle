package dev.vrba.minecraft.blockshuffle.game

import org.bukkit.entity.Player

data class Game(
    var players: List<Player>,
    var round: Round? = null
)