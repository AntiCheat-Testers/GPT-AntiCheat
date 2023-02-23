package gpt.ac.check.checks.combat.click

import gpt.ac.check.Check
import io.github.retrooper.packetevents.utils.player.Hand
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.Vector

import javax.annotation.Nullable

class AutoClick() : Check("AutoClicker A","Checks if the player's CPS hit over a specific amount", Category.COMBAT,5) {

    // Sets the threshold to 5 violations

    private val clickTimes : MutableMap<Player, MutableList<Long>> = mutableMapOf()
    override fun bukkitOnInteract(event : PlayerInteractEvent) {
        clickTimes.clear()
    }

    override fun bukkitOnBreaking(event : BlockBreakEvent) {
        clickTimes.clear()
    }


    @Nullable
    override fun onArmAnimation(perpetrator : Player, hand : Hand) {
        if (raycast(perpetrator, 5.0)==null) {
            val times = clickTimes.getOrDefault(perpetrator, mutableListOf())
            val currentTime = System.currentTimeMillis()

            // Remove all click times that are more than 1 second old
            times.removeIf { currentTime - it > 1000 }

            // Add the current click time
            times.add(currentTime)

            clickTimes[perpetrator] = times

            if (times.size > 25) {
                flag(1, perpetrator)

            } else {
                System.out.println(perpetrator.name + "'s" + " CPS is " + times.size)

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
}

