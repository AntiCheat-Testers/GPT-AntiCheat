package gpt.ac.check.checks.combat.aim

import gpt.ac.Main
import gpt.ac.check.Check
import io.github.retrooper.packetevents.packetwrappers.play.`in`.entityaction.WrappedPacketInEntityAction
import io.github.retrooper.packetevents.utils.vector.Vector3d
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.*

class AimLock: Check("AimLock","Checks for suspicous aim",Category.COMBAT,190) {
    private val mappedhitpositions: MutableMap<Player, MutableList<Pair<Double, Double>>> = mutableMapOf()

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

        if (isMoving&&isRotating&&(doesPlayerRaycastHitAnyEntity(perpetrator)!=null&&doesPlayerRaycastHitAnyEntity(perpetrator)!!.velocity.length()>0||raycast(perpetrator,2.0)!=null)) {
            val hitX = perpetrator.location.x - 0.5
            val hitY = perpetrator.location.y - 0.5
            if (!mappedhitpositions.containsKey(perpetrator)) {
              mappedhitpositions[perpetrator] = mutableListOf(hitX to hitY)
            }
            mappedhitpositions[perpetrator]!!.add(hitX to hitY)

            val hitPositions= mappedhitpositions[perpetrator]

            if (hitPositions!!.size >= 35) {
                val angles = mutableListOf<Double>()
                val distances = mutableListOf<Double>()
                for (i in 0 until hitPositions.size - 1) {
                    val (x1, y1) = hitPositions[i]
                    val (x2, y2) = hitPositions[i + 1]
                    val dx = x2 - x1
                    val dy = y2 - y1
                    val distance = hypot(dx, dy)
if(distance==0.21585510030058686){
    flag(10,perpetrator,"Zeroday Client detected")
    return
}

                    if(distance>.2) {


                        distances.add(distance)
                        val angle = atan2(dy, dx) * 180 / PI
                        angles.add(angle)
                    }
                }
                val meanAngle = angles.average()
                val angleStdDev =
                    angles.fold(0.0) { acc, angle -> acc + (angle - meanAngle).pow(2) } / (angles.size - 1)


                val meanDistance = distances.average()
                val distanceStdDev =
                    distances.fold(0.0) { acc, distance -> acc + (distance - meanDistance).pow(2) } / (distances.size - 1)
                val diffs = mutableListOf<Double>()



                // Apply heuristics and set violation level
                if (( meanDistance!=0.0&&distanceStdDev < 0.2 * meanDistance&&abs(distanceStdDev- 0.2 * meanDistance)>=0.00971706585713259&&distanceStdDev<=0.00937387558262262&&distanceStdDev<0.0060224826354349&&distanceStdDev>0.004088022198609647&&angleStdDev<8299.91938491934)||angleStdDev<=0.0&&meanDistance!=0.0) {

                    flag(1,perpetrator,"Impossible Standard Deviation Distance\n MIN STD DISTANCE ${0.2 * meanDistance} \n ACTUAL STD DISTANCE $distanceStdDev \n AVG ANGLE STD DISTANCE $angleStdDev")
                }
                hitPositions.removeFirst()


            }

        }
    }
    fun doesPlayerRaycastHitAnyEntity(player: Player): Entity? {
        val eyeLocation = player.eyeLocation
        val playerDirection = eyeLocation.direction

        val maxDistance = 6 // Maximum distance to check for entity hit

        val ray = playerDirection.normalize().multiply(0.1) // Adjust the ray step for more accurate checks

        var currentPosition = eyeLocation.toVector()

        for (i in 0..(maxDistance.toInt() * 10)) {
            currentPosition = currentPosition.add(ray)
            val nearbyEntities = player.world.getNearbyEntities(currentPosition.toLocation(player.world), 0.1, 0.1, 0.1)

            for (entity in nearbyEntities) {
                if (entity is Entity && entity != player) {
                    return entity
                }
            }
        }

        return null
    }
    fun raycast(player: Player, maxDistance: Double): org.bukkit.block.Block? {
        val eyeLoc: Location = player.eyeLocation
        val dir: Vector = eyeLoc.direction
        var loc: Location = eyeLoc.clone()

        for (i in 0 until maxDistance.toInt()) {
            loc.add(dir)

            val block: Block = loc.block
            if (block.type.isSolid) {
                return block
            }
        }

        return null
    }
}

