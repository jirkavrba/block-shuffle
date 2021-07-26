package dev.vrba.minecraft.blockshuffle.listener

import dev.vrba.minecraft.blockshuffle.GamesManager
import org.bukkit.Particle
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
        val player = event.player
        val location = player.location
        val block = player.world.getBlockAt(location.blockX, location.blockY - 1, location.blockZ)

        // Target block a player has to find
        val target = manager.game?.round?.remainingBlocks?.get(player) ?: return

        if (block.type == target)
        {
            player.world.spawnParticle(Particle.TOTEM, location, 1000)

            manager.foundBlock(player)
        }
    }

}