package gpt.ac.check.checks.move

import gpt.ac.check.Check
import io.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent
import io.github.retrooper.packetevents.packetwrappers.WrappedPacket
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SpeedLimitCheck : Check("Speed Limit", "Detection of Excessive Movement Speed", Category.MOVE, 100) {
    private val playerLocationMap: MutableMap<Player, Location> = mutableMapOf()
    private val playerTeleportedMap: MutableMap<Player, Boolean> = mutableMapOf()

    override fun onPacketSend(perpetrator: Player, wrappedPacket: WrappedPacket, event: PacketPlaySendEvent) {
        if (wrappedPacket is WrappedPacketOutEntityTeleport) {
            playerTeleportedMap[perpetrator] = true
        }
    }

    override fun onFlying(
        perpetrator: Player,
        yaw: Float,
        pitch: Float,
        posVec: Vector3d,
        onGround: Boolean,
        isMoving: Boolean,
        isRotating: Boolean
    ) {
   if(isMovingTooFast(perpetrator,perpetrator.location.subtract(0.0,-1.0,0.0)).first){
       flag(1,perpetrator)
   }
    }

    private fun isMovingTooFast(player: Player, block: Location): Pair<Boolean, Double> {

        val maxSpeed = 0.0


var strafe=player.velocity.x
        var forward=abs(player.velocity.z)
        var yaw = abs( player.location.yaw)
        var friction=0.6f
        var f = strafe * strafe + forward * forward

        if (f !=0.0) {
            f = sqrt(f)
            if (f < 1.0F) {
                f = 1.0
            }
            f = friction / f
            strafe *= f
            forward *= f
            val f1 =sin(yaw * (Math.PI.toFloat() / 180.0F))
            val f2 =cos(yaw * (Math.PI.toFloat() / 180.0F))

            val deltaXZ = sqrt(forward * forward + strafe * strafe)
            val actualXZ= sqrt(player.velocity.z*player.velocity.z+player.velocity.x*player.velocity.x)
            return (deltaXZ <actualXZ&&actualXZ-deltaXZ>.1) to actualXZ
        }

        return false to maxSpeed
    }

    private fun isRidableAtLocation(location: Location, radius: Double): Boolean {
        val world: World? = location.world
        val entities: List<Entity> = world!!.entities.toList()
        val ridables = entities.filter { it.type in arrayOf(EntityType.HORSE, EntityType.PIG, EntityType.BOAT, EntityType.MINECART) && it.location.distance(location) <= radius }
        return ridables.isNotEmpty()
    }
}
