package gpt.ac.check.checks.move

import gpt.ac.check.Check
import io.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import sun.audio.AudioPlayer.player

class InvalidUp :Check("Invalid Upward Motion","Detects invalid upward motion",Category.MOVE,10) {
//I should deleter this check but it stop all step hacker so \_(-.-)_/
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

        if(!perpetrator.allowFlight&&predictedHeight!= 0.3423839977416992&&predictedHeight!=0.304736315543518&&predictedHeight!=0.46153890890323357&&predictedHeight<perpetrator.location.y-oldloc.y&&!perpetrator.location.add(0.0,-1.0,0.0).block.type.isSolid){

            PacketEvents.get().playerUtils.sendPacket(
                perpetrator,
                WrappedPacketOutEntity.WrappedPacketOutEntityLook(
                    perpetrator.entityId,
                    oldloc.yaw,
                    oldloc.pitch,
                    true
                )

            )
            if(oldloc.add(0.0,-predictedHeight,0.0).y >= oldloc.y&&oldloc.add(0.0,-predictedHeight,0.0).block.type==Material.AIR) {
                PacketEvents.get().playerUtils.sendPacket(
                    perpetrator,
                    WrappedPacketOutEntityTeleport(perpetrator.entityId, oldloc.add(0.0, -predictedHeight, 0.0), false)
                )
            }
        }else{
            playerMap[perpetrator] = Pair(perpetrator.location, predictedHeight)

        }

    }


    private fun predictMaximumJumpHeight(player: Player): Double {
        if (player.velocity.length() == 0.0||player.velocity.y<0) {
            return 0.42 // Return a default value for players not moving
        }
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

        // Check if the player is colliding with a slime block and bounced on it
        val currentBlock = player.location.subtract(0.0, 1.0, 0.0).block
        if (currentBlock.type == Material.SLIME_BLOCK) {
            val previousBlock = player.location.subtract(0.0, 2.0, 0.0).block
            if (previousBlock.type == Material.SLIME_BLOCK) {
                // Player just bounced on slime block
                slimeHeight = 0.4 + Math.abs(momentumY) * 1.6
println(slimeHeight)
    return slimeHeight*(momentumY+player.eyeHeight)
            }
        }

        // Calculate the predicted jump height based on the player's momentum, jump boost effect, stair height, and slime height
        predictedJumpHeight += momentumY * 0.5 + jumpBoostEffect * 0.1 + stairHeight

        return predictedJumpHeight
    }

}
