package gpt.ac.check.checks.packet

import gpt.ac.check.Check
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

private const val MIN_PITCH = -90.0
private const val MAX_PITCH = 90.0

class InvalidPitch : Check(
    "Invalid Pitch",
    "Detects if a player has an invalid pitch",
    Category.MOVE
), Listener {

    init {
        threshold = 1
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val pitch = event.to?.pitch ?: return

        if (pitch > 90.0 || pitch < -90.0) {
            flag(1, player)
        }
    }
}

