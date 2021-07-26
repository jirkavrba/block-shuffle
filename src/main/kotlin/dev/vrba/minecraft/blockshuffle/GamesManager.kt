package dev.vrba.minecraft.blockshuffle

import dev.vrba.minecraft.blockshuffle.game.Configuration
import dev.vrba.minecraft.blockshuffle.game.Game
import org.bukkit.entity.Player

data class GamesManager(
    var playing: Boolean = false,
    var game: Game? = null
) {
    fun createNewGame(players: List<Player>): Game
    {
        playing = true
        game = Game(players)

        return game as Game
    }
}