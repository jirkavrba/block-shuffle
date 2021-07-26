package dev.vrba.minecraft.blockshuffle

import dev.vrba.minecraft.blockshuffle.game.Difficulty
import dev.vrba.minecraft.blockshuffle.game.Game
import dev.vrba.minecraft.blockshuffle.game.Round
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player

data class GamesManager(
    val plugin: BlockShuffle,
    var players: List<Player> = listOf(),
    var game: Game? = null,
    var handle: Int? = null
)
{
    val playing: Boolean
    get() = game != null

    private val key = NamespacedKey(plugin, "game_bar")

    fun createNewGame(players: List<Player>): Game
    {
        this.players = players
        this.game = createNewRound(Game(players))

        return game as Game
    }

    // TODO: Better naming
    fun foundBlock(player: Player)
    {
        if (!playing) return

        this.players.forEach { it.sendMessage("${ChatColor.AQUA}${it.displayName}${ChatColor.RESET} found his block!") }

        val game = this.game ?: return
        val round = game.round ?: return
        val remaining = round.remainingBlocks - player

        game.round = round.copy(remainingBlocks = remaining)

        if (remaining.isEmpty())
        {
            this.players.forEach { it.sendMessage("${ChatColor.GREEN}All players found their blocks!${ChatColor.RESET}") }
            this.game = createNewRound(game)
        }
    }

    private fun createNewRound(game: Game): Game
    {
        // TODO: Make this configurable
        val difficulty = Difficulty.Easy

        val limit = plugin.gameConfiguration.timeLimits[difficulty] ?: throw IllegalStateException()
        val round = Round(
            difficulty,
            assignBlocksToPlayers(difficulty),
            limit
        )

        broadcastRound(game.players, round)
        startSchedulerTask()

        return game.copy(
            players = players,
            round = round,
        )
    }

    private fun startSchedulerTask()
    {
        handle = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 0, 20)
    }

    private fun tick()
    {
        val round = game?.round ?: return
        val players = round.remainingBlocks.keys.toList()

        round.timeElapsed++

        if (round.timeElapsed >= round.timeLimit)
        {
            // Handle end of the round
            handle?.let { Bukkit.getScheduler().cancelTask(it) }
            handle = null
        }

        updateBossBar(players, round)
    }

    private fun assignBlocksToPlayers(difficulty: Difficulty): Map<Player, Material>
    {
        val pool = plugin.gameConfiguration.blockPools[difficulty] ?: throw IllegalStateException()

        return players.associateWith { pool.random() }
    }

    private fun broadcastRound(players: List<Player>, round: Round)
    {
        val seconds = round.timeLimit
        val time = if (seconds > 60) "${seconds / 60} minutes"
        else "$seconds seconds"

        players.forEach { player ->
            round.remainingBlocks[player]
                ?.let {
                    player.sendTitle(
                        "${ChatColor.RED} ${it.name} ${ChatColor.RESET}",
                        "You have ${ChatColor.AQUA}$time${ChatColor.RESET} remaining", 20, 200, 20
                    )
                    player.sendMessage("You have $time to sneak (shift) on ${it.name}")
                }
        }

        updateBossBar(players, round)
    }

    private fun updateBossBar(players: List<Player>, round: Round)
    {
        val bar: BossBar = Bukkit.getBossBar(key) ?: Bukkit.createBossBar(
            key,
            "Remaining time",
            BarColor.GREEN,
            BarStyle.SOLID,
            BarFlag.DARKEN_SKY
        )

        players.map { player -> bar.addPlayer(player) }

        val progress = 1.0 - (round.timeElapsed.toFloat() / round.timeLimit.toFloat())

        bar.progress = progress
        bar.isVisible = true
        bar.color = when (progress)
        {
            in 0.0..0.25 -> BarColor.RED
            in 0.25..0.5 -> BarColor.YELLOW
            else -> BarColor.GREEN
        }
    }
}