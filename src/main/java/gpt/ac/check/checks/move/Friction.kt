package gpt.ac.check.checks.move

import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class Friction : Check("Friction","Checks for invalid block friction",Category.MOVE,23){

    override fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    ) {
        if(!isPlayerFollowingFriction(perpetrator,perpetrator.location.subtract(0.3,0.05,0.3).block)&&isMoving){

            if(perpetrator.location.subtract(0.3,0.05,0.3).block.type.isSolid&&perpetrator.velocity.y== -0.0784000015258789&&onGround) {
                flag(1, perpetrator)
            }
        }
    }


    fun isPlayerFollowingFriction(player: Player, block: Block): Boolean {
        val material = block.type
        val defaultSlipperiness = 0.6

        // Check if the block is ice or packed ice, which have a slipperiness of 0.98 and 0.99 respectively.
        val isIceBlock = (material == Material.ICE || material == Material.PACKED_ICE)




        // Get the slipperiness of the block below the player
        val blockSlipperiness = when {
            isIceBlock -> if (material == Material.PACKED_ICE) 0.99 else 0.98

            else -> defaultSlipperiness
        }

        // Get the player's velocity vector and normalize it
        val playerVelocity = player.velocity.clone().normalize()

        // Get the direction of the block's friction vector based on its orientation
        val blockDirection = when (block.data) {
            0.toByte(), 1.toByte() -> Vector(0, 0, -1)
            2.toByte(), 3.toByte() -> Vector(-1, 0, 0)
            4.toByte(), 5.toByte() -> Vector(0, 0, 1)
            6.toByte(), 7.toByte() -> Vector(1, 0, 0)
            else -> Vector(0, 0, 0)
        }

        // Calculate the dot product of the player's velocity and the block's friction direction
        val dotProduct = playerVelocity.dot(blockDirection)

        // Check if the player is following the friction of the block below them
        return dotProduct >= 0
    }
}