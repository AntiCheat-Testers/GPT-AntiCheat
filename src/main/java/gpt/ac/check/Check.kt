package gpt.ac.check

import org.bukkit.Bukkit
import org.bukkit.entity.Cat
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerVelocityEvent

abstract class Check(val name: String, val description: String,category:Category): Listener {
      var threshold: Long = 0
     private var violations: Long = 0


     open fun onMove(event:PlayerMoveEvent){
    }
    open fun onAnimation(event:PlayerAnimationEvent){
    }
    open fun onVelocity(event:PlayerVelocityEvent){
    }
    open fun onInteract(event:PlayerInteractEvent){
    }
    open fun onInteractAtEntity(event:PlayerInteractAtEntityEvent){
    }
    open fun onInteractEntity(event:PlayerInteractEntityEvent){
    }
fun flag(increment:Long,player:Player){
    this.violations+=increment;
    for(op in Bukkit.getServer().operators){
if(op.isOnline){
    op.player!!.sendMessage(player.name + "failed " + this.name +"VL "+this.violations)
}

    }
    if(this.violations>this.threshold){
        player.kickPlayer("You Have Been Detected Cheating")
        this.violations=0L

        for(op in Bukkit.getServer().operators){
            if(op.isOnline){
                op.player!!.sendMessage(player.name + "was banned for Cheating")
            }

        }

    }
}
    enum class Category {
        COMBAT,
        MOVE,
        PACKET
    }
}