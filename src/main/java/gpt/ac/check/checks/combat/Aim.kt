package gpt.ac.check.checks.combat

// Import necessary libraries and classes
import gpt.ac.check.Check
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import java.lang.Math.abs

// Define a class named Aim that extends the Check class
class Aim() : Check("Aimbot", "Checks for botted aim", Category.COMBAT,5) {



    // Define a mutable map to store the pitch and yaw data for each player
    private val aimData: MutableMap<Player, Pair<Float, Float>> = mutableMapOf()

    // Called when the player moves
    override fun BukkitonMove(event: PlayerMoveEvent) {
        val player = event.player
        val prevPitchYaw = aimData[player]

        // If the player has moved before, compare the previous pitch and yaw values with the current ones
        if (prevPitchYaw != null) {
            val prevPitch = prevPitchYaw.first
            val prevYaw = prevPitchYaw.second
            val currentPitch = player.location.pitch
            val currentYaw = player.location.yaw

            // Calculate the pitch difference between the current and last location
            val deltaPitch = abs(currentPitch - prevPitch)
            val deltaYaw = abs(currentYaw - prevYaw)

            // If the pitch or yaw difference is greater than 1.0, flag the player
            if (deltaPitch > 60.0 || deltaYaw > 30.0) {
                flag(1, player)
                event.isCancelled=true;
            }
        }

        // Update the aim data for the player
        aimData[player] = Pair(player.location.pitch, player.location.yaw)
    }
}