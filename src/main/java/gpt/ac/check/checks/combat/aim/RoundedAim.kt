package gpt.ac.check.checks.combat.aim


import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.abs

class RoundedAim : Check("Rounded Aim", "Checks for rounded yaw behavior", Category.COMBAT,5) {
    private val lastYaws = IntArray(10)
    private var lastIndex = 0

    override fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    ) {
        if(isRotating) {
            val player : Player = perpetrator
            val roundedYaw : Int = abs(player.location.yaw).toInt() % 360
            lastYaws[lastIndex] = roundedYaw
            lastIndex = (lastIndex + 1) % 4
            for (i in -180..180 step 1) {
                if (lastYaws.all { it % i == 0 }) {
                    // The last 10 yaws are divisible by the current divisor, so flag
       //             flag(1, player)
                    return

                } else {
                    // The last 10 yaws are not divisible by the current divisor, so continue checking
                    continue
                }
            }
        }
    }
}
