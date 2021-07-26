package dev.vrba.minecraft.blockshuffle

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class BlockShuffle : JavaPlugin() {
    override fun onEnable() {
        Bukkit.getLogger().info("Register BlockShuffle plugin")
    }
}