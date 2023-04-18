package gpt.ac.check.checks.move

import gpt.ac.check.Check
import io.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.Location

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import sun.audio.AudioPlayer.player
import java.lang.Math.*
import kotlin.math.roundToInt

class Fly : Check("Fly", "Checks for invalid movement in air", Category.MOVE, 150) {

    private val playerMap: MutableMap<Player, Pair<Location, Double>> = mutableMapOf()

    override fun onJoin(event : PlayerJoinEvent) {
event.player.location.y=floor(event.player.location.y)
        if(playerMap.containsKey(event.player)){
            playerMap.remove(event.player)
        }
    }
    override fun onFlying(
        perpetrator: Player,
        yaw: Float,
        pitch: Float,
        posvec: Vector3d,
        onGround: Boolean,
        isMoving: Boolean,
        isRotating: Boolean
    ) {
        if (!perpetrator.isDead) {
            if (!playerMap.containsKey(perpetrator)) {
                playerMap[perpetrator] = Pair(perpetrator.location, 0.0)
            }

            val (lastLoc, lastPredictedMotionY) = playerMap[perpetrator]!!

            val predictedMotionY = predictNextMotionY(perpetrator, lastLoc)

            if (predictedMotionY < -1.0457237242233686 && predictedMotionY < perpetrator.location.y - lastLoc.y || (perpetrator.location.y - lastLoc.y).toFloat() == .42f && perpetrator.fallDistance > .42
            ) {
                flag(1, perpetrator,"Too Low Y Difference \n ACTUAL DELTA Y = ${perpetrator.location.y-lastLoc.y}   \n PREDICTED DELTA Y = $predictedMotionY \n DIF = ${(perpetrator.location.y-lastLoc.y)-predictedMotionY} ")
                PacketEvents.get().playerUtils.sendPacket(
                    perpetrator,
                    WrappedPacketOutEntity.WrappedPacketOutEntityLook(
                       perpetrator.entityId,
                lastLoc.yaw,
                        lastLoc.pitch,
                        true
                    )
                )
                PacketEvents.get().playerUtils.sendPacket(
                    perpetrator,
                    WrappedPacketOutEntityTeleport(perpetrator.entityId,playerMap[perpetrator]!!.first.add(0.0,predictedMotionY,0.0), false)
                )
            }else{
                playerMap[perpetrator] = Pair(perpetrator.location, predictedMotionY)

            }
        }

    }
    override fun onLeave(event : PlayerQuitEvent) {
        if(playerMap.containsKey(event.player)){
            playerMap.remove(event.player)
        }
    }
    private fun predictNextMotionY(player: Player, lastLoc: Location): Double {
        val momentumY = player.velocity.y
        var jumpBoostEffect = 0.0
        var predictedMotionY = momentumY - 0.08

        // Check if the player has a jump boost effect
        for (effect in player.activePotionEffects) {
            if (effect.type == PotionEffectType.JUMP) {
                jumpBoostEffect = (effect.amplifier + 1) *1.6
                break
            }
        }

        // Check if the player is jumping or falling
        val isGoingUp = player.location.y > lastLoc.y
        val isFalling = !isGoingUp && player.velocity.y < 0

        // Adjust predicted motionY based on jump boost effect and fall distance
        if (isGoingUp) {
            // Check if the player has a jump boost effect
            for (effect in player.activePotionEffects) {
                if (effect.type == PotionEffectType.JUMP) {
                    jumpBoostEffect = (effect.amplifier + 1) *1.6
                    break
                }
            }
            predictedMotionY = Math.max(predictedMotionY + jumpBoostEffect, 0.42)
        } else if (isFalling) {
            val fallDistance = player.fallDistance
            val fallTime = Math.sqrt(2 * fallDistance / 0.08).toLong()
            predictedMotionY += fallTime * 0.08
        }
        return predictedMotionY
    }



}