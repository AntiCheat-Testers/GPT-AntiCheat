package gpt.ac.check.checks.combat.click

import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.player.Hand
import org.bukkit.entity.Player
import kotlin.math.pow
import kotlin.math.sqrt

class Kuriotosis : Check("AutoClicker Kuriotosis", "yes", Category.COMBAT,5) {
    private val maxSampleSize: Int = 20
    private val maxStdDev: Double = 0.5
    private val maxKurtosis: Double = 5.0
    private val samples = mutableMapOf<Player, MutableList<Long>>()

    override fun onArmAnimation(perpetrator : Player, hand : Hand) {
        val player = perpetrator
        val time = System.currentTimeMillis()

        // Add the time between clicks to the player's sample list
        val sampleList = samples.getOrPut(player) { mutableListOf() }
        if (sampleList.size >= maxSampleSize) {
            sampleList.removeFirst()
        }
        if (sampleList.isNotEmpty()) {
            sampleList.add(time - sampleList.last())
        }
        sampleList.add(0L)

        // Check if the player's sample data indicates autoclicking
        if (sampleList.size == maxSampleSize) {
            val mean = sampleList.average()
            val variance = sampleList.map { (it - mean).pow(2) }.average()
            val stdDev = sqrt(variance)
            val kurtosis = sampleList.map { (it - mean).pow(4) }.average() / stdDev.pow(4)
println("MEAN > $mean variance > $variance STD>$stdDev Kurtosis > $kurtosis")
            if (stdDev < maxStdDev && kurtosis > maxKurtosis) {
             flag(1,player)
            }
        }
    }
}