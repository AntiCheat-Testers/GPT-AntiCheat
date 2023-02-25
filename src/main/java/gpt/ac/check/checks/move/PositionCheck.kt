package gpt.ac.check.checks.move
import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.server.ServerUtils
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import kotlin.math.abs

class PositionCheck : Check("PositionCheck", "Predicts possible positions based on yaw", Check.Category.MOVE,5) {


    // Called when the player moves
    override fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    )

    {

        val player: Player = perpetrator

        // Get the current yaw of the player
        val currentYaw = player.location.yaw
        // Loop through all possible positions in a 3x3 square radius
        for (i in -1..1) {
            for (j in -1..1) {
                // Calculate the predicted position based on the current yaw
                val predictedX = player.location.x + i * abs(Math.cos(Math.toRadians(currentYaw.toDouble())))
                val predictedZ = player.location.z + j * abs(Math.sin(Math.toRadians(currentYaw.toDouble())))

                // Check if the player's actual position is within a certain range of the predicted position
                if (abs(predictedX - player.location.x) > 1 && abs(predictedZ - player.location.z) > 1) {
                    flag(1, player)


                }
            }
        }
    }

}