package gpt.ac.check.checks.combat.aim

import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.entity.Player
import kotlin.math.abs
import kotlin.math.roundToInt

class InvalidYawGCD : Check("Invalid Yaw Division Angle", "Detects when a player's yaw is an invalid Golden-Circle Division angle.", Category.COMBAT, 5) {

    override fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    ) {
        val yaw = perpetrator.location.yaw % 360
        val gcd = 360.0 / (360.0 / 1.61803398875)
        val closestGCD = roundToNearest(yaw, gcd)
        val deviation = abs(yaw - closestGCD)
      //  System.out.println(deviation)
        //System.out.println(closestGCD)

        if (deviation >.808) {
          flag(1, perpetrator)
        }

    }

    private fun roundToNearest(value: Float, nearest: Double): Double {
        return (value / nearest).roundToInt() * nearest
    }
}