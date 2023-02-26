package gpt.ac.check.checks.move

import gpt.ac.check.Check
import io.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.potion.PotionEffectType

class InvalidUp :Check("Invalid Upward Motion","Detects invalid upward motion",Category.MOVE,10) {

    private val playerMap: MutableMap<Player, Pair<Location, Double>> = mutableMapOf()

    override fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    ) {
        if (!perpetrator.isDead) {
            if (!playerMap.containsKey(perpetrator)) {
                playerMap[perpetrator] = Pair(perpetrator.location, 0.0)
            }
        }
        val (oldloc) = playerMap[perpetrator]!!

            val predictedHeight = predictMaximumJumpHeight(perpetrator)
            playerMap[perpetrator] = Pair(perpetrator.location, predictedHeight)
        if(predictedHeight<perpetrator.location.y-oldloc.y&&!perpetrator.location.add(0.0,-1.0,0.0).block.type.isSolid){
            flag(1,perpetrator)
            PacketEvents.get().playerUtils.sendPacket(
                perpetrator,
                WrappedPacketOutEntity.WrappedPacketOutEntityLook(
                    perpetrator.entityId,
                    oldloc.yaw,
                    oldloc.pitch,
                    true
                )
            )
            PacketEvents.get().playerUtils.sendPacket(
                perpetrator,
                WrappedPacketOutEntityTeleport(perpetrator.entityId,oldloc, false)
            )
        }

    }


    private fun predictMaximumJumpHeight(player: Player): Double {
        val momentumY = player.velocity.y
        var jumpBoostEffect = 0.0
        var predictedJumpHeight = 0.42
        val stairBlock = player.location.block
        val isStair = stairBlock.type.name.contains("STAIRS")
        val stairHeight = if (isStair) 0.5 else 0.0
        var slimeHeight = 0.0

        // Check if the player has a jump boost effect
        for (effect in player.activePotionEffects) {
            if (effect.type == PotionEffectType.JUMP) {
                jumpBoostEffect = (effect.amplifier + 1) * 0.1
                break
            }
        }

        // Check if the player is colliding with a slime block
        val currentBlock = player.location.subtract(0.0,-4.0,0.0).block
        if (currentBlock.type == Material.SLIME_BLOCK) {
            slimeHeight = 0.4 + Math.abs(momentumY) * 0.2

        }

        // Calculate the predicted jump height based on the player's momentum, jump boost effect, stair height, and slime height
        predictedJumpHeight += momentumY * 0.5 + jumpBoostEffect * 0.1 + stairHeight * slimeHeight

        return predictedJumpHeight
    }
}