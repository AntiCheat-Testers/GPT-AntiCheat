package gpt.ac.check.checks.move
import gpt.ac.check.Check
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.abs

class PositionCheck : Check("PositionCheck", "Predicts possible positions based on yaw", Check.Category.MOVE) {

    // Sets the threshold to 10 violations
    init {
        threshold = 10
    }

    // Called when the player moves
    override fun onMove(event: PlayerMoveEvent) {
        val player: Player = event.player

        // Get the current yaw of the player
        val currentYaw = player.location.yaw

        // Loop through all possible positions in a 3x3 square radius
        for (i in -1..1) {
            for (j in -1..1) {
                // Calculate the predicted position based on the current yaw
                val predictedX = player.location.x + i * abs(Math.cos(Math.toRadians(currentYaw.toDouble())))
                val predictedZ = player.location.z + j * abs(Math.sin(Math.toRadians(currentYaw.toDouble())))

                // Check if the player's actual position is within a certain range of the predicted position
                if (abs(predictedX - player.location.x) < 1 && abs(predictedZ - player.location.z) < 1) {
                    flag(1, player)
                }
            }
        }
    }
}