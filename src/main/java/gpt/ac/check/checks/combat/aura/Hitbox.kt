// This package contains a check for detecting if a player has hit outside an entity hitbox
package gpt.ac.check.checks.combat.aura

import gpt.ac.check.Check
import io.github.retrooper.packetevents.packetwrappers.play.`in`.useentity.WrappedPacketInUseEntity
import io.github.retrooper.packetevents.utils.vector.Vector3d

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

class Hitbox : Check("Hitbox", "Player Hit outside of an entity hitbox", Category.COMBAT, 5) {

    // This function is called when a player uses an entity
    override fun onUseEntity(
        perpetrator: Player,
        action: WrappedPacketInUseEntity.EntityUseAction,
        target: Optional<Vector3d>,
        entity: Entity?
    ) {
        // Check if the action is an attack
        if (action == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {
            // Check if there is an entity present
            if (entity != null) {
                // Check if the player has hit outside of the entity hitbox
                if (raycast(perpetrator, 3.0)!!.location.distance(entity.location) > 3.001 || raycast(perpetrator, 4.0)!!.location.block.type.isSolid) {
                    // Flag the perpetrator if the hit is outside of the entity hitbox
                    flag(1, perpetrator)
                }
            }
        }
    }

    // This function is used to perform a raycast to detect blocks in front of a player
    fun raycast(player: Player, maxDistance: Double): org.bukkit.block.Block? {
        val eyeLoc: Location = player.eyeLocation
        val dir: Vector = eyeLoc.direction
        val loc: Location = eyeLoc.clone()

        // Loop through a certain number of blocks
        for (i in 0 until maxDistance.toInt()) {
            // Move the location in the direction of the player's view
            loc.add(dir)

            // Return the block at the location
            return loc.block
        }

        return null
    }
}