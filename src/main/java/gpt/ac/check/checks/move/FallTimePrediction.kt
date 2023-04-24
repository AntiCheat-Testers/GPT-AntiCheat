package gpt.ac.check.checks.move

import gpt.ac.check.Check
import gpt.ac.check.checks.move.FallTimePrediction.Gravity.GRAVITY
import gpt.ac.util.FallTimePredictor
import io.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import kotlin.math.*

class FallTimePrediction: Check("Fall Time Prediction","Checks if the players fall time in ticks is > than the predicted fall time",Category.MOVE,10) {
    object Gravity {
        const val GRAVITY = 0.08
    }
    override fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    ) {
        if (!onGround) {
            val fallDistance = perpetrator.fallDistance.toDouble()
            val velocity = perpetrator.velocity.clone()

            // Predict how many ticks it would take to reach the ground
            val predictor = FallTimePredictor(perpetrator)
            val predictedFallTime = predictor.predictFallTime()

            // Calculate how many ticks it actually took to fall
            val actualFallTime = calculateActualFallTime(fallDistance, velocity)

            if (!perpetrator.isFlying&&predictedFallTime ==6 && actualFallTime> 11||predictedFallTime==3&&actualFallTime==0&&!perpetrator.location.subtract(0.3,0.2,0.3).block.type.isSolid&&!perpetrator.location.subtract(0.3,0.5,0.3).block.isLiquid&&perpetrator.velocity.y>=0) {
                flag(
                    1,
                    perpetrator,
                    "Flight violation detected. Predicted fall time: $predictedFallTime. Actual fall time: $actualFallTime."
                )

                PacketEvents.get().playerUtils.sendPacket(
                    perpetrator,
                    WrappedPacketOutEntity.WrappedPacketOutEntityLook(
                        perpetrator.entityId,
                        perpetrator.location.yaw,
                       perpetrator.location.pitch,
                        true
                    )
                )
                PacketEvents.get().playerUtils.sendPacket(
                    perpetrator,
                    WrappedPacketOutEntityTeleport(perpetrator.entityId,perpetrator.location.subtract(perpetrator.velocity.x,0.08*perpetrator.fallDistance,perpetrator.velocity.z), false)
                )
            }
        }
    }
    private fun calculateActualFallTime(fallDistance : Double, velocity : Vector) : Int {
        // Calculate how long it would take to fall if starting from rest
        val timeToFallRest = (-velocity.y - sqrt(velocity.y.pow(2.0) - 2 * GRAVITY * fallDistance)) / (-GRAVITY)

        // Calculate how long it would take to fall from the current velocity
        val timeToFallCurrent = -2 * velocity.y / GRAVITY

        // Return the larger of the two times
        return ceil(max(timeToFallRest, timeToFallCurrent)).toInt()
    }
}

