package gpt.ac.check.checks.move
import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import kotlin.math.roundToInt

class PredictFallDamage : Check("Predict Fall Damage", "Checks if the player takes less fall damage than expected", Category.MOVE, 5) {

    override fun onFlying(
        perpetrator: Player,
        yaw: Float,
        pitch: Float,
        posvec: Vector3d,
        onGround: Boolean,
        isMoving: Boolean,
        isRotating: Boolean
    ) {
        // Check if the player is in the air and has fallen more than 3 blocks
        if (!perpetrator.isOnGround && posvec.y < -0.6) {
            // Calculate the expected fall damage based on the player's fall distance
            val expectedDamage = calculateExpectedDamage(perpetrator.fallDistance)
if(onGround&&!isCollidedWithCollidableBlock(perpetrator)&&perpetrator.location.y>=0){
    flag(1, perpetrator,"EXPECTED=${expectedDamage} \n GOT=${perpetrator.lastDamage} ")

}
            // Check if the player's actual fall damage is less than the expected damage
            val actualDamage = perpetrator.lastDamage
            if (actualDamage < expectedDamage&&perpetrator.location.y>0) {
                flag(1, perpetrator,"EXPECTED=${expectedDamage} \n GOT=${perpetrator.lastDamage}" )
                // Apply the predicted fall damage to the player
                perpetrator.damage(expectedDamage.toDouble())
            }
        }
    }

    private fun calculateExpectedDamage(fallDistance: Float): Int {
        // Calculate the expected fall damage based on the player's fall distance
        val fallDamage = (fallDistance - 3.0).coerceAtLeast(0.0) / 2.0
        val damage = fallDamage.roundToInt()
        return damage
    }
    private fun isCollidedWithCollidableBlock(player: Player): Boolean {
        val blockBelow = player.location.block.getRelative(BlockFace.DOWN)
        val blockMaterial = blockBelow.type
        return blockMaterial.isAir
    }
}