import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.type.Slab
import org.bukkit.block.data.type.Stairs
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import kotlin.math.abs

class Speed : Check("Strafe", "Checks for invalid strafing behavior", Category.MOVE, 5) {
    var oldLocation: Location? = null

    override fun onFlying(
        perpetrator: Player,
        yaw: Float,
        pitch: Float,
        posvec: Vector3d,
        onGround: Boolean,
        isMoving: Boolean,
        isRotating: Boolean
    ) {
        val player = perpetrator

        val blockBelow = player.location.subtract(0.0, -1.0, 0.0).block.blockData

        val maxDeltaXZ = if (blockBelow is Slab || blockBelow is Stairs ) {
            // Adjust maximum delta for steppable blocks
            getMaxDeltaXZ(player) * 2
        } else {
            getMaxDeltaXZ(player)
        }

        if (oldLocation != null) {
            val deltaXZ = player.location.distance(oldLocation!!)

            if (deltaXZ > maxDeltaXZ) {
                flag(1, player)
            }
        }
        oldLocation = player.location
    }

    private fun getMaxDeltaXZ(player: Player): Double {
        val speedModifier = getSpeedModifier(player)
        val sprintModifier = if (player.isSprinting) 1.3 else 1.0
        val baseDeltaXZ = 0.23

        return baseDeltaXZ * speedModifier * sprintModifier
    }

    private fun getSpeedModifier(player: Player): Double {
        return when {
            player.hasPotionEffect(PotionEffectType.SPEED) -> {
                val speedEffect = player.getPotionEffect(PotionEffectType.SPEED)
                val speedLevel = speedEffect?.amplifier ?: 0
                1.0 + (speedLevel * 0.2)
            }
            else -> 1.0
        }
    }
}