package dev.vrba.minecraft.blockshuffle

import dev.vrba.minecraft.blockshuffle.game.Difficulty
import dev.vrba.minecraft.blockshuffle.game.Game
import dev.vrba.minecraft.blockshuffle.game.Round
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask

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

    fun handlePlayerFoundBlock(player: Player)
    {
        if (!playing) return

        this.players.forEach { it.sendMessage("$AQUA ${it.displayName} $RESET found his block!") }

        val game = this.game ?: return
        val round = game.round ?: return
        val remaining = round.remainingBlocks - player

        game.round = round.copy(remainingBlocks = remaining)

        if (remaining.isEmpty())
        {
            this.players.forEach { it.sendMessage("$GREEN All players found their blocks!$RESET") }
            this.game = createNewRound(game)
        }
    }

    private fun handleEndOfRound()
    {
        if (!playing) return

        this.players.forEach { it.sendMessage("$RED The time is over! $RESET") }

        val game = this.game ?: return
        val round = game.round ?: return
        val bar = Bukkit.getBossBar(key) ?: return

        val losers = round.remainingBlocks.keys

        losers.forEach {
            bar.removePlayer(it)
            it.sendTitle("$RED Game over! $RESET", "You will not receive next blocks.", 20, 200, 20)
        }

        this.players.forEach {
            it.sendMessage(
                "Players who failed this round:\n" +
                losers.joinToString("\n", transform = Player::getDisplayName)
            )
        }

        val remaining = game.players - losers

        if (remaining.isEmpty())
        {
            this.players.forEach { it.sendMessage("$RED No players remaining, the game is over! $RESET") }
            this.game = null

            Bukkit.removeBossBar(key)

            return
        }

        createNewRound(game.copy(players = game.players - losers))
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
        handle?.let { Bukkit.getScheduler().cancelTask(it) }
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

            handleEndOfRound()

            return
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
                        "$RED ${it.name} $RESET",
                        "You have $AQUA $time $RESET remaining", 20, 200, 20
                    )
                    player.sendMessage("You have $AQUA $time $RESET to sneak (shift) on $RED ${it.name} $RESET")
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