package dev.vrba.minecraft.blockshuffle.command

import dev.vrba.minecraft.blockshuffle.BlockShuffle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BlockShuffleCommand(private val plugin: BlockShuffle) : CommandExecutor
{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        // If the sender is console (doesn't have access to world) or there is a game playing already
        // the command is considered invalid
        if (sender !is Player || plugin.manager.playing)
            return false

        val players = sender.world.players
        val game = plugin.manager.createNewGame(players)

        plugin.logger.info("Created a new game [$game]")

        return true
    }
}