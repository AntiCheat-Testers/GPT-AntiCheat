package gpt.ac.check.checks.move

import gpt.ac.check.Check
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import kotlin.math.abs

class StrafeCheck : Check("Strafe", "Checks for invalid strafing behavior", Category.MOVE) {
    private var lastLocation: Location? = null

    override fun onMove(event: PlayerMoveEvent) {
        val player = event.player
        val location = player.location

        if (lastLocation != null && lastLocation!!.world == location.world) {
            val deltaX = abs(lastLocation!!.x - location.x)
            val deltaZ = abs(lastLocation!!.z - location.z)

            if (deltaX > 0.0 && deltaZ > 0.0) {
                val ratio = deltaZ / deltaX
                val speedEffect = player.getPotionEffect(PotionEffectType.SPEED)
                val speedMultiplier = if (speedEffect != null) (0.2 * speedEffect.amplifier) + 1.0 else 1.0
                val frictionMultiplier = if (player.isOnGround) player.location.block.state.blockData.material.slipperiness else 1.0
                if (ratio > 1.5 * speedMultiplier * frictionMultiplier.toFloat() || ratio < 0.5 * speedMultiplier * frictionMultiplier.toFloat()) {
                    flag(1L, player)
                }
            }
        }

        lastLocation = location.clone()
    }
}
