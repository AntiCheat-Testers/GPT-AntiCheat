package gpt.ac.check.checks.combat


import gpt.ac.check.Check
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import kotlin.math.abs

class RoundedAim : Check("Rounded Aim", "Checks for rounded yaw behavior", Category.COMBAT) {
    private val lastYaws = IntArray(10)
    private var lastIndex = 0

    override fun onMove(event : PlayerMoveEvent) {
        val player : Player = event.player
        val roundedYaw : Int = abs(player.location.yaw).toInt() % 360
        lastYaws[lastIndex] = roundedYaw
        lastIndex = (lastIndex + 1) % 10
        for (i in -180..180 step 1) {
            if (lastYaws.all { it % i == 0 }) {
                // The last 10 yaws are divisible by the current divisor, so flag
                flag(1, player)
                return

            } else {
                // The last 10 yaws are not divisible by the current divisor, so continue checking
                continue
            }
        }
    }
}
