package gpt.ac.util

import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class FallTimePredictor(private val player: Player) {
    private val gravity = 0.08

    fun predictFallTime(): Int {
        var ticks = 0
        var velocity = player.velocity.clone()

        while (velocity.y > 0) {
            velocity = getNextVelocity(velocity)
            ticks++
        }

        val distance = max(0.0, player.location.y - getGroundLevel())
        val timeToFall = sqrt(2 * distance / gravity).toInt()

        return max(ticks, timeToFall)
    }

    private fun getNextVelocity(velocity: Vector): Vector {
        val y = max(0.0, velocity.y - gravity)
        return Vector(velocity.x, y, velocity.z)
    }

    private fun getGroundLevel(): Double {
        var groundLevel = player.location.blockY.toDouble()
        val maxSearchDistance = 5

        for (i in 1..maxSearchDistance) {
            val block = player.location.world!!.getBlockAt(
                player.location.blockX,
                player.location.blockY - i,
                player.location.blockZ
            )

            if (!block.isEmpty) {
                groundLevel = player.location.blockY - i + 1.0
                break
            }
        }

        return groundLevel
    }

}