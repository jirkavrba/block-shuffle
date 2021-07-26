package dev.vrba.minecraft.blockshuffle.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class BlockShuffleCommand(private val plugin: Plugin) : CommandExecutor
{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        sender.sendMessage("Starting")
        return sender is Player
    }
}