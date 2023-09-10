package gpt.ac.check.checks.move

import com.fasterxml.jackson.databind.Module
import gpt.ac.Main
import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.Location
import org.bukkit.entity.Player
import java.lang.Math.toRadians
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Strafe : Check("Invalid Strafe","Hacker",Category.MOVE,8) {
var loc : Location?=null
    override fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    ) {
        val player = perpetrator
        val currentLocation = perpetrator.location
if(loc!=null) {
    val deltaYaw = abs(currentLocation.yaw - loc!!.yaw)

    if (deltaYaw <= .01) return // Ignore small changes in yaw

    val deltaPosX = currentLocation.x - loc!!.x
    val deltaPosZ = currentLocation.z - loc!!.z
    val angle = atan2(deltaPosX, deltaPosZ)
    val strafeAngle = (angle - toRadians(currentLocation.yaw.toDouble())).toFloat()

    val strafeCos = cos(strafeAngle.toDouble())
    val strafeSin = sin(strafeAngle.toDouble())
    val forward = cos(toRadians(currentLocation.yaw.toDouble()))
    val right = sin(toRadians(currentLocation.yaw.toDouble()))

    val strafe = strafeCos * forward + strafeSin * right

    if (abs(strafe) > 1) {
        flag(1, perpetrator)
    }
}
        loc = perpetrator.location

    }
}