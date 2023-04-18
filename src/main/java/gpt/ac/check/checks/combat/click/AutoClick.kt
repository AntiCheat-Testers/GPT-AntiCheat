package gpt.ac.check.checks.combat.click

import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.player.Hand
import org.apache.commons.math3.distribution.ChiSquaredDistribution
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector
import java.lang.Math.abs
import java.util.*

import javax.annotation.Nullable
import kotlin.math.pow

class AutoClick: Check("AutoClicker","Checks for suspicious clicks", Category.COMBAT,5) {

    // Sets the threshold to 5 violations
    private val playerMap: MutableMap<Player, Deque<Long>> = mutableMapOf()
    private var consistentDeviations = 0

    private val clickTimes : MutableMap<Player, MutableList<Long>> = mutableMapOf()
    override fun bukkitOnInteract(event : PlayerInteractEvent) {
        clickTimes.clear()
    }

    override fun bukkitOnBreaking(event : BlockBreakEvent) {
        clickTimes.clear()
    }

    private val maxDeviation = 10 // Maximum deviation from average time between clicks
    private val maxConsistentDeviations = 10 // Maximum number of consistent deviations allowed

    @Nullable
    override fun onArmAnimation(perpetrator : Player, hand : Hand) {
        if (raycast(perpetrator, 5.0)==null&&perpetrator.ticksLived>60) {
            val player = perpetrator
            if (player.isBlocking) return // ignore if blocking
            if (!playerMap.containsKey(player)) {
                playerMap[player] = LinkedList()
            }
            val clicks = playerMap[player]!!
            val time = System.currentTimeMillis()
            clicks.addLast(time)
            if (clicks.size > 20) {
                clicks.removeFirst()
                val cps = clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000
                if (cps > 25.0) {
                    flag(1, player, "AutoClicker detected (high CPS)\n CPS= $cps")
                }
            }
            val averageTime = (clicks.last - clicks.first) / clicks.size.toDouble()
            if (averageTime < 60&&clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000!=Double.POSITIVE_INFINITY&&clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000!=Double.NEGATIVE_INFINITY) {
                flag(1, player, "AutoClicker detected (fast clicks) \n Average Time=$averageTime\n" +
                        " CPS=${clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000}")
            }
            if (clicks.size > 10&&clicks.toList().takeLast(50)!=null) {
                val last50 = clicks.toList().takeLast(50)
                val differences = mutableListOf<Long>()
                if (last50.size >= 2) {
                    for (i in 1 until last50.size) {
                        differences.add(last50[i] - last50[i - 1])
                    }
                }
                val stdDev = standardDeviation(differences)
                if (stdDev < 5) {
                    flag(1, player, "AutoClicker detected (low jitter) \n STD= $stdDev\n" +
                            " CPS=${clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000}")
                }
                val median = differences.sorted()[differences.size / 2]
                if (median < 10&&clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000>6) {
                    flag(1, player, "AutoClicker detected (low outliers) \n  MEDIAN=$median\n" +
                            " CPS=${clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000}")

                }
                val deviation = differences.map { abs(it - averageTime) }.average()
                if (deviation < maxDeviation) {
                    if (++consistentDeviations > maxConsistentDeviations) {
                        flag(1, player, "AutoClicker detected (consistent deviation)\n DEVIATION= $deviation \n CPS=${clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000}")
                    }
                } else {
                    consistentDeviations = 0
                }
                // Check for unnatural patterns
                val chiSq = chiSquaredTest(differences,25)
                if (chiSq>clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) &&(clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000)>3.2  ) {
                        flag(
                            1, player, "AutoClicker detected (unnatural pattern)\n CHI=$chiSq\n" +
                                    " CPS=${clicks.size.toDouble() / (clicks.last.toDouble() - clicks.first.toDouble()) * 1000}"
                        )

                }else{
                    if(this.threshold>0){
                        this.threshold--
                    }
                }


            }
        }
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
    private fun standardDeviation(list: List<Long>): Double {
        val mean = list.average()
        val variance = list.map { (it - mean) * (it - mean) }.sum() / (list.size - 1).toDouble()
        return Math.sqrt(variance)
    }

    fun chiSquaredTest(differences: List<Long>, numBins: Int): Double {
        val minValue = differences.minOrNull()!!
        val maxValue = differences.maxOrNull()!!
        val range = maxValue - minValue
        val binSize = range / numBins.toDouble()

        // Calculate expected frequency for each bin
        val expectedFrequency = DoubleArray(numBins) { differences.size.toDouble() / numBins }

        // Calculate observed frequency for each bin
        val observedFrequency = MutableList(numBins) { 0.0 }
        for (difference in differences) {
            val binIndex = ((difference - minValue) / binSize).toInt().coerceIn(0, numBins - 1)
            observedFrequency.set(binIndex, observedFrequency[binIndex] + 1)
        }

        // Calculate chi-squared statistic
        var chiSquared = 0.0
        for (i in 0 until numBins) {
            val numerator = (observedFrequency[i] - expectedFrequency[i]).pow(2)
            val denominator = expectedFrequency[i]
            chiSquared += numerator / denominator
        }

        // Calculate p-value using chi-squared distribution
        val degreesOfFreedom = numBins - 1.0
        val distribution = ChiSquaredDistribution(degreesOfFreedom)
        val pValue = 1 - distribution.cumulativeProbability(chiSquared)

        return pValue
    }
}

