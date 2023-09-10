package gpt.ac.check.checks.move

import gpt.ac.Main
import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.player.Direction
import io.github.retrooper.packetevents.utils.player.Hand
import io.github.retrooper.packetevents.utils.vector.Vector3f
import io.github.retrooper.packetevents.utils.vector.Vector3i
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import org.apache.commons.math3.distribution.ChiSquaredDistribution
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.util.Vector
import kotlin.math.abs

class BlockPlacementCheck : Check("Scaffold", "Hacker", Category.MOVE, 12) {
    private val placementTimes = mutableMapOf<Player, LinkedList<Long>>()

    override fun onPlace(
        perpetrator: Player,
        hand: Hand,
        direction: Direction,
        blockPos: Vector3i,
        cursorPos: Optional<Vector3f>,
        itemStack: Optional<ItemStack>
    ) {
        val currentTime = System.currentTimeMillis()
        val placementHistory = placementTimes.getOrPut(perpetrator) { LinkedList() }

        // Check if player has placed blocks recently
        if (isSuspiciousPlacement(perpetrator, blockPos, itemStack)) {
            placementHistory.addLast(currentTime)
        }
if(cursorPos.isPresent&&(cursorPos.get().x>1.0||cursorPos.get().y>1.0||cursorPos.get().z>1.0||cursorPos.get().x<-1.0||cursorPos.get().y<-1.0||cursorPos.get().z<-1.0)){
    flag(Long.MAX_VALUE,perpetrator,"Impossible Placment offset vector ${cursorPos.get()}")
}
        val raycastdata=raycast(perpetrator,5.0)
        val raycastdata1=raycastType(perpetrator,5.0)

        if( raycastdata==null&&blockPos.x!=-1&& perpetrator.location.y.toInt()-blockPos.y==1&&perpetrator.world.getBlockAt(blockPos.x,blockPos.y-2,blockPos.z)!!.type==Material.AIR&&perpetrator.world.getBlockAt(blockPos.x,blockPos.y+2,blockPos.z).type==Material.AIR&&perpetrator.world.getBlockAt(blockPos.x,blockPos.y-2,blockPos.z).type==Material.AIR&& perpetrator.world.getBlockAt(blockPos.x,blockPos.y+1,blockPos.z).type==Material.AIR&&perpetrator.velocity.y<=0){
           flag(0,perpetrator,"Raycast (experimental)")
            println("${blockPos.x} ${blockPos.y} ${blockPos.z}")
        }

        if (placementHistory.size > 7) {
            val violationDetected = detectSuspiciousPattern(placementHistory,perpetrator)
            if (violationDetected) {
                // Suspicious block placement pattern detected
                val message = "Suspicious block placement pattern detected for player ${perpetrator.name}"
                // Handle the violation (e.g., kick, notify staff, etc.)
                flag(1, perpetrator)

            }
            placementHistory.clear()

        }
    }

    private fun isSuspiciousPlacement(perpetrator: Player, blockPos: Vector3i, itemStack: Optional<ItemStack>): Boolean {
        // Check if the player is placing a block while looking at the same block
        if (itemStack.isPresent && blockPos != null) {
            val targetBlock = perpetrator.location.add(0.0, -1.0, 0.0).block
            if (perpetrator.location.add(0.0,-2.0,0.0).block.type==Material.AIR&&targetBlock.type == Material.AIR&&perpetrator.location.block.type==Material.AIR &&(perpetrator.velocity.y==-0.0784000015258789||perpetrator.velocity.y>0)) {
                return true
            }
        }
        return false
    }

    private fun detectSuspiciousPattern(placementHistory: LinkedList<Long>,p:Player): Boolean {

        // Calculate the time intervals between placements
        val intervals = mutableListOf<Long>()
        var previousTime = placementHistory.first
        for (time in placementHistory.drop(1)) {
            val interval = time - previousTime
            intervals.add(interval)
            previousTime = time
        }

        // Perform statistical analysis on the intervals
        val averageInterval = intervals.average()
        // Apply Chi-Square distribution to detect suspicious patterns
        val chiSquaredValue = calculateChiSquaredValue(intervals, averageInterval)
        val suffix = "E-5"
        val suffix2 = "E-4"

        val isChiSquaredE5 = chiSquaredValue.toString().endsWith(suffix)
        val isChiSquaredE4 = chiSquaredValue.toString().endsWith(suffix2)

        if(isChiSquaredE5||isChiSquaredE4){
            flag(50,p,"Augustus Client Detected   (Too Precise)  \n nCHI SQ VALUE = $chiSquaredValue")
            return false
        }
        println("$chiSquaredValue   $averageInterval")
        return chiSquaredValue <.15||chiSquaredValue<10&&averageInterval>120&&averageInterval<155
    }

    private fun calculateChiSquaredValue(intervals: List<Long>, averageInterval: Double): Double {
        val observedFrequencies = intervals.map { it / averageInterval }
        val expectedFrequency = intervals.size.toDouble() / observedFrequencies.size
        val chiSquared = observedFrequencies.map { (it - expectedFrequency) * (it - expectedFrequency) / expectedFrequency }.sum()
        return chiSquared
    }

    private fun getChiSquaredThreshold(degreesOfFreedom: Int): Double {
        val chiSquaredDistribution = ChiSquaredDistribution(degreesOfFreedom.toDouble())
        return chiSquaredDistribution.inverseCumulativeProbability(95.0) // 95% confidence level
    }
    fun raycastType(player: Player, maxDistance: Double): Material {
        val eyeLoc: Location = player.eyeLocation
        val dir: Vector = eyeLoc.direction
        var loc: Location = eyeLoc.clone()

        for (i in 0 until maxDistance.toInt()) {
            loc.add(dir)

            val block: Block = loc.block
            if (block.type.isSolid) {
                return block.type
            }
        }

        return Material.AIR
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
    fun getPlayerCardinalDirection(player: Player): Direction {
        val yaw = player.location.yaw

        if (yaw < 0) {
            return Direction.NORTH // Default to North if an error occurs
        }

        val directions = Direction.values()
        val index = ((yaw + 45) % 360 / 90).toInt()
        return directions[index]
    }
}
