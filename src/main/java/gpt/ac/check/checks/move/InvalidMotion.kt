package gpt.ac.check.checks.move

import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player

class InvalidMotion : Check("Invalid Motion", "Checks for invalid motion in air", Category.MOVE,5) {

    // Define a set of block types that should not trigger a flag
    private val allowedBlocks = setOf(
        Material.LADDER,
        Material.VINE,
        Material.SLIME_BLOCK
    )

    // Called when the player moves
    override fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    ) {

    val player= perpetrator
        // Get the player's current location and velocity
        val location = player.location
        val velocity = player.velocity

        // Check if the player is in the air
        if (!player.isOnGround) {
            // Get the block the player is standing on and the block above it
            val blockBelow = location.block.getRelative(BlockFace.DOWN)
            val blockAbove = location.block.getRelative(BlockFace.UP)

            // Check if the player is inside an allowed block
            if (allowedBlocks.contains(blockBelow.type) || allowedBlocks.contains(blockAbove.type)) {
                return // Player is not flagged if inside an allowed block
            }

            // Calculate the player's current speed
            val speed = velocity.length()
            val fallDistance = player.fallDistance
            val expectedVelocity = -Math.sqrt((2 * fallDistance) / 0.08)
            val actualvelocity = player.velocity.length()

            if (actualvelocity< expectedVelocity) {
                flag(0, player)
            }
            // Calculate the expected maximum speed based on the player's Y velocity
            val expectedSpeed = when {
                velocity.y < 0 -> 0.4
                velocity.y < 0.4 -> 0.4 + (velocity.y * 1.5)
                velocity.y < 0.75 -> 0.6 + (velocity.y * 1.5)
                else -> 0.7 + (velocity.y * 1.5)
            }

            // If the player's velocity is greater than the expected maximum velocity, flag the player

            // If the player's speed is greater than the expected maximum speed, flag the player
            if (speed > expectedSpeed&&velocity.y>=-.165) {
       flag(0, player)
                System.out.println(speed)
            }
        }
    }
}