import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class Aim : Check("Aimbot", "Checks for botted aim", Category.COMBAT, 5) {

    private val aimData: MutableMap<Player, Vector3d> = mutableMapOf()

    override fun onFlying(perpetrator: Player, yaw: Float, pitch: Float, posVec: Vector3d, onGround: Boolean, isMoving: Boolean, isRotating: Boolean) {
        val player = perpetrator
        val prevPosVec = aimData[player]

        if (prevPosVec != null) {
            val currentPosVec = fromBukkitVector(player.location.toVector())
            val deltaPosVec = currentPosVec.subtract(prevPosVec)

            // Calculate the pitch and yaw differences between the current and previous position vectors
            val deltaPitch = Math.toDegrees(Math.atan2(deltaPosVec.y, deltaPosVec.x)).toFloat() - pitch
            val deltaYaw = Math.toDegrees(Math.atan2(-deltaPosVec.z, deltaPosVec.x)).toFloat() - yaw

            // Normalize the pitch and yaw differences to [-180, 180)
            val normDeltaPitch = normalizeAngle(deltaPitch)
            val normDeltaYaw = normalizeAngle(deltaYaw)

            // If the pitch or yaw difference is too large, flag the player
            if (Math.abs(normDeltaPitch) > 60.0f || Math.abs(normDeltaYaw) > 90.0f) {
                flag(1, player)
            }
        }

        aimData[player] = fromBukkitVector(player.location.toVector())
    }

    private fun normalizeAngle(angle: Float): Float {
        var normalized = angle % 360.0f
        if (normalized >= 180.0f) {
            normalized -= 360.0f
        } else if (normalized < -180.0f) {
            normalized += 360.0f
        }
        return normalized
    }
    fun fromBukkitVector(bukkitVec: Vector): Vector3d {
        return Vector3d(bukkitVec.x, bukkitVec.y, bukkitVec.z)
    }
}