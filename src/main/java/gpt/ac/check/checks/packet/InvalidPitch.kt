package gpt.ac.check.checks.packet

import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.apache.commons.math3.analysis.function.Max
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

private const val MIN_PITCH = -90
private const val MAX_PITCH = 90

class InvalidPitchCheck : Check(
    "Invalid Pitch",
    "Detects if a player has an invalid pitch",
    Category.MOVE,1) {

    override fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    )

    {
if(pitch> MAX_PITCH||pitch< MIN_PITCH){
    flag(1,perpetrator)
}
    }

}

