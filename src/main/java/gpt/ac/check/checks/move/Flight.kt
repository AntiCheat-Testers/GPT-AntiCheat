package gpt.ac.check.checks.move

import gpt.ac.check.Check
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector

class Flight: Check("Flight", "Checks for invalid motion while in air", Category.MOVE,5) {

    private val slimeVelocityThreshold = 1.5 // adjust this value as needed
    private var lastOnGround: Long = 0
    private var lastSlimeVelocity: Vector? = null

    override fun bukkitOnMove(event: PlayerMoveEvent) {
        val player = event.player
        if (player.isFlying && !player.isInsideVehicle && !player.isGliding && !player.isRiptiding) {
            val from = event.from
            val to = event.to
            if (from.y < to!!.y) {
                // Player is moving upwards
                lastOnGround = System.currentTimeMillis()
            } else if (lastOnGround > 0) {
                // Player is moving downwards or horizontally
                val timeSinceLastOnGround = System.currentTimeMillis() - lastOnGround
                if (timeSinceLastOnGround < 1000) {
                    // Check if the player bounced on a slime block
                    val slimeVelocity = player.velocity
                    if (lastSlimeVelocity != null && slimeVelocity.y < 0 && slimeVelocity.y < -slimeVelocityThreshold &&
                        slimeVelocity.subtract(lastSlimeVelocity!!).lengthSquared() > 0.01) {
                        flag(1, player)
                        event.isCancelled=true;
                    }
                    lastSlimeVelocity = slimeVelocity
                }
            }
        }
    }
}