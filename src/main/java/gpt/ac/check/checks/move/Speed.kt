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
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import sun.audio.AudioPlayer.player
import java.lang.Math.abs

class Speed: Check("Speed Limit","Too Fast Movement",Category.MOVE,100) {
    private val playerMap: MutableMap<Player, Location> = mutableMapOf()
    private val playerMapTwo: MutableMap<Player, Boolean> = mutableMapOf()

    override fun onPacketSend(perpetrator : Player, wrappedpacket : WrappedPacket, event : PacketPlaySendEvent) {
        if(wrappedpacket is WrappedPacketOutEntityTeleport){
            playerMapTwo[perpetrator] = true

        }
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
        if (!playerMap.containsKey(perpetrator)) {
            playerMap[perpetrator] = perpetrator.location
        }
if(playerMapTwo[perpetrator]==true){
    playerMap[perpetrator] = perpetrator.location
    playerMapTwo[perpetrator]=false
    return

}
        // Check if player has been alive for at least 5 ticks
        if (perpetrator.ticksLived <= 10||perpetrator.isDead) {
            playerMap[perpetrator] = perpetrator.location
            return
        }

        if (isMovingTooFast(perpetrator, playerMap[perpetrator]!!).first &&
            !perpetrator.isSleeping &&
            !perpetrator.isFlying &&
            !perpetrator.isInsideVehicle &&
            !isRidableAtLocation(playerMap[perpetrator]!!, 3.0) &&
            !perpetrator.isDead
        ) {
            var cur = (perpetrator.location.x + perpetrator.location.z) - (playerMap[perpetrator]!!.x + playerMap[perpetrator]!!.z)
            if (cur < 0) {
                cur = abs(cur)
            }
            flag(1, perpetrator, "Went over the speed limit for their current scenario \n CURRENT= $cur  \n MAX=${isMovingTooFast(perpetrator, playerMap.get(perpetrator)!!).second}")
            PacketEvents.get().playerUtils.sendPacket(
                perpetrator,
                WrappedPacketOutEntity.WrappedPacketOutEntityLook(
                    perpetrator.entityId,
                    playerMap[perpetrator]!!.yaw,
                    playerMap[perpetrator]!!.pitch,
                    true
                )
            )

            PacketEvents.get().playerUtils.sendPacket(
                perpetrator,
                WrappedPacketOutEntityTeleport(perpetrator.entityId, Location(perpetrator.world, playerMap[perpetrator]!!.x, playerMap[perpetrator]!!.y, playerMap[perpetrator]!!.z), false)
            )
        } else {
            playerMap[perpetrator] = perpetrator.location
        }
    }
    private fun isMovingTooFast(player: Player,block:Location): Pair<Boolean,Double> {
        val currentBlock = player.location.subtract(0.0,1.0,0.0).block
        val blockAbove = player.location.add(0.0,.4,0.0).block

        val blockType = currentBlock.type
        var maxSpeed = 0.0

        // Determine the maximum allowed speed based on the block type
        when (blockType) {
            Material.ICE, Material.PACKED_ICE -> maxSpeed = if(!blockAbove.type.isSolid) 0.8579551377986263 else 1.085
            Material.SOUL_SAND -> maxSpeed = 0.4
            else -> maxSpeed = 1.68
        }

        // Check if the player has any speed-related potion effects and adjust the maximum allowed speed accordingly
        for (effect in player.activePotionEffects) {
            if (effect.type == PotionEffectType.SPEED) {
                maxSpeed += (effect.amplifier + 1) * 0.1
                break
            }
        }


        // Check if the player's current speed exceeds the maximum allowed speed
        var currentSpeed =(player.location.x+player.location.z)-(block.x+block.z)
if(currentSpeed<0){
    currentSpeed= abs(currentSpeed)
}
if(player.velocity.y>0){
    maxSpeed+=.06
}


        return (currentSpeed> maxSpeed &&block.y<=player.location.y )to maxSpeed
    }
    fun isRidableAtLocation(location: Location, radius: Double): Boolean {
        val world: World? = location!!.world
        val entities: List<Entity> = world!!.entities.toList()
        val ridables = entities.filter { it.type in arrayOf(EntityType.HORSE, EntityType.PIG,EntityType.BOAT,EntityType.MINECART) && it.location.distance(location) <= radius }
        return ridables.isNotEmpty()
    }
    
}