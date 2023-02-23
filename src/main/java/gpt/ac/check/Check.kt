package gpt.ac.check

import io.github.retrooper.packetevents.packetwrappers.play.`in`.useentity.WrappedPacketInUseEntity
import io.github.retrooper.packetevents.utils.player.Hand
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerVelocityEvent
import sun.rmi.transport.TransportConstants.Ping

abstract class Check(val name: String, val description: String,val category:Category,val threshold:Long): Listener {
    private val violations = mutableMapOf<Player, Long>()

    open fun BukkitonMove(event:PlayerMoveEvent){
    }
    open fun BukkitonAnimation(event:PlayerAnimationEvent){
    }
    open fun BukkitonVelocity(event:PlayerVelocityEvent){
    }
    open fun BukkitonInteract(event:PlayerInteractEvent){
    }
    open fun BukkitonInteractAtEntity(event:PlayerInteractAtEntityEvent){
    }
    open fun BukkitonInteractEntity(event:PlayerInteractEntityEvent){
    }
    open fun BukkitonBreaking(event:BlockBreakEvent){
    }

    open fun onUseEntity(perpetrator:Player,action:WrappedPacketInUseEntity.EntityUseAction, target:Player){

    }
    open fun onTransactionRecieve(perpetrator:Player,windowID:Int,actionNumber:Short){

    }
    open fun onChatRecieve(perpetrator:Player,message:String){

    }
    open fun onArmAnimation(perpetrator:Player,hand:Hand){

    }
open fun flag(increment:Long,player:Player){
    val ping = player.ping

    this.violations[player]=this.violations.getOrDefault(player,0L)+increment;
    when(this.category){
        Category.COMBAT->{
            combatviolations++;
            if(combatviolations>=5) {
                for (op in Bukkit.getServer().operators) {
                    if (op.isOnline) {
                        op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 has an unlikely amount of Combat Violations §7(Ping: $ping ms)")
                        combatviolations=0

                    }

                }
            }
        }
        Category.MOVE->{
            moveviolations++;
            if(moveviolations>=5) {
                for (op in Bukkit.getServer().operators) {
                    if (op.isOnline) {
                        op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 has an unlikely amount of Movement Violations §7(Ping: $ping ms)")

                        moveviolations=0
                    }

                }
            }
        }
        Category.PACKET->{
            packetviolations++;
            if(packetviolations>=5){
                for(op in Bukkit.getServer().operators){
                    if(op.isOnline){
                        op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 has an unlikely amount of Packet Violations §7(Ping: $ping ms)")

                        packetviolations=0

                    }

                }
            }
        }
    }
    for(op in Bukkit.getServer().operators){
if(op.isOnline){
    op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 failed ${getCategoryColor(this.category)}${this.name}§2 VL§a[${this.violations.getOrDefault(player,0L)}] §7(Ping: $ping ms)")
}

    }
    if(this.violations.getOrDefault(player,0L)>=this.threshold){
        player.kickPlayer("You Have Been Detected Cheating")
        this.violations.getOrDefault(player,0L)

        for(op in Bukkit.getServer().operators){
            if(op.isOnline){
                op.player!!.sendMessage(player.name + " was banned for Cheating")
            }

        }

    }
}
    enum class Category {
        COMBAT,
        MOVE,
        PACKET
    }
    private fun getCategoryColor(cat:Category):String{
        return when(cat){
            Category.COMBAT->{
                "§3"
            }
            Category.PACKET->{
                "§4"
            }

            Category.MOVE->{
                "§f"
            }
        }

    }
companion object{

    var combatviolations=0L
    var moveviolations=0L
    var packetviolations=0L

}
}