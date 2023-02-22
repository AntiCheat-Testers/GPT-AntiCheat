package gpt.ac.check.checks.combat

import gpt.ac.check.Check
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerAnimationEvent

class AutoClick() : Check("AutoClicker A","Checks if the player's CPS hit over a specific amount", Category.COMBAT) {

    // Sets the threshold to 25 violations
    init {
        this.threshold = 25
    }

    private val clickTimes: MutableMap<Player, MutableList<Long>> = mutableMapOf()

    override fun onAnimation(event: PlayerAnimationEvent) {
        val player = event.player
        val times = clickTimes.getOrDefault(player, mutableListOf())
        val currentTime = System.currentTimeMillis()

        // Remove all click times that are more than 1 second old
        times.removeIf { currentTime - it > 1000 }

        // Add the current click time
        times.add(currentTime)

        clickTimes[player] = times

        if (times.size > 25) {
            flag(1, player)
        }
    }
}
