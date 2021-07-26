package dev.vrba.minecraft.blockshuffle.listener

import dev.vrba.minecraft.blockshuffle.GamesManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent

class GameListener(private val manager: GamesManager) : Listener
{
    @EventHandler
    fun onPlayerSneak(event: PlayerToggleSneakEvent)
    {
        // Only handle move events if the game is in play
        if (!manager.playing) return

        // Block that the player is standing on
        val location = event.player.location
        val block = event.player.world.getBlockAt(location.blockX, location.blockY - 1, location.blockZ)
        val material = block.type

        println(material)
    }
}