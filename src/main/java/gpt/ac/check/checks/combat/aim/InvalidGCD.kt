package gpt.ac.check.checks.combat.aim

import gpt.ac.check.Check
import io.github.retrooper.packetevents.packetwrappers.play.`in`.useentity.WrappedPacketInUseEntity
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

class InvalidYawGCD : Check("Invalid Yaw Division Angle", "Detects when a player's yaw is an invalid Golden-Circle Division angle.", Category.COMBAT, 5) {

    override fun onUseEntity(
        perpetrator : Player,
        action : WrappedPacketInUseEntity.EntityUseAction,
        target : Optional<Vector3d>,
        entity : Entity?
    ) {
        if (entity != null) {
            val yaw = perpetrator.location.yaw % 360
            val gcd = 360.0 / (360.0 / 1.61803398875)
            val closestGCD = roundToNearest(yaw, gcd)
            val deviation = abs(yaw - closestGCD)
//nogga wtf

            if (deviation > .808) {
                flag(1, perpetrator)
            }

        }
    }
        private fun roundToNearest(value : Float, nearest : Double) : Double {
            return (value / nearest).roundToInt() * nearest
        }

}