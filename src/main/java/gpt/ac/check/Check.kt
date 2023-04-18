package gpt.ac.check


import gpt.ac.Main
import gpt.ac.check.checks.move.Fly
import gpt.ac.check.checks.move.InvalidUp
import io.github.retrooper.packetevents.PacketEvents
import io.github.retrooper.packetevents.event.PacketListenerAbstract
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent
import io.github.retrooper.packetevents.packetwrappers.WrappedPacket
import io.github.retrooper.packetevents.packetwrappers.play.`in`.entityaction.WrappedPacketInEntityAction
import io.github.retrooper.packetevents.packetwrappers.play.`in`.settings.WrappedPacketInSettings
import io.github.retrooper.packetevents.packetwrappers.play.`in`.useentity.WrappedPacketInUseEntity
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity.WrappedPacketOutEntityLook
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport
import io.github.retrooper.packetevents.utils.player.Direction
import io.github.retrooper.packetevents.utils.player.Hand
import io.github.retrooper.packetevents.utils.vector.Vector3d
import io.github.retrooper.packetevents.utils.vector.Vector3f
import io.github.retrooper.packetevents.utils.vector.Vector3i
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerVelocityEvent
import org.bukkit.inventory.ItemStack
import java.util.*

abstract class Check(val name: String, val description: String, val category:Category, var threshold:Long): Listener,
    PacketListenerAbstract() {
    private val violations = mutableMapOf<Player, Long>()
    private val iskicked = mutableMapOf<Player, Boolean>()
    private val validposition = mutableMapOf<Player, Location>()

    //Bukkit Events
    open fun onJoin(event : PlayerJoinEvent) {
        iskicked[event.player] = false

    }

    open fun bukkitOnMove(event : PlayerMoveEvent) {
    }

    open fun bukkitOnAnimation(event : PlayerAnimationEvent) {
    }

    open fun bukkitOnVelocity(event : PlayerVelocityEvent) {
    }

    open fun bukkitOnInteract(event : PlayerInteractEvent) {
    }

    open fun bukkitOnInteractAtEntity(event : PlayerInteractAtEntityEvent) {
    }

    open fun bukkitOnInteractEntity(event : PlayerInteractEntityEvent) {
    }

    open fun bukkitOnBreaking(event : BlockBreakEvent) {
    }

    open fun onLeave(event : PlayerQuitEvent) {
        this.violations.remove(event.player)
        combatviolations = 0
        moveviolations = 0
        packetviolations = 0
    }

    //ClientBound Packet based Events
    open fun onUseEntity(
        perpetrator : Player,
        action : WrappedPacketInUseEntity.EntityUseAction,
        target : Optional<Vector3d>,
        entity:Entity?
    ) {
    }

    open fun onPacketRecieve(perpetrator : Player, wrappedpacket : WrappedPacket,event:PacketPlayReceiveEvent) {
    }

    open fun onPacketSend(perpetrator : Player, wrappedpacket : WrappedPacket,event : PacketPlaySendEvent) {
    }

    open fun onTransactionRecieve(perpetrator : Player, windowID : Int, actionNumber : Short) {

    }

    open fun onKeepAliveRecieve(perpetrator : Player, id : Long) {

    }

    open fun onArmAnimation(perpetrator : Player, hand : Hand) {

    }

    open fun onFlying(
        perpetrator : Player,
        yaw : Float,
        pitch : Float,
        posvec : Vector3d,
        onGround : Boolean,
        isMoving : Boolean,
        isRotating : Boolean
    ) {
        if (perpetrator.location.subtract(0.0, 1.0, 0.0).block.type.isSolid && perpetrator.ticksLived>10) {
            this.validposition[perpetrator] = this.validposition.getOrDefault(perpetrator, perpetrator.location)

        }
    }
    open fun onSteer(perpetrator:Player,foward:Float,strafe:Float,dismounting:Boolean,jumping:Boolean){

    }
    open fun onPlace(perpetrator:Player, hand:Hand, direction:Direction, blockpos: Vector3i, cursorpos:Optional<Vector3f>, itemstack:Optional<ItemStack>){

    }
    open fun onItemSwitch(perpetrator:Player,slot:Int){

    }
    open fun onRecieveChat(perpetrator:Player,message:String){

    }
    open fun onItemUse(perpetrator:Player,hand:Hand,blockpos: Vector3i){

    }
    open fun onAction(player : Player, action : WrappedPacketInEntityAction.PlayerAction?, jumpBoost : Int, entityId : Int, entity : Entity?) {

    }
  open  fun onSettings(player : Player, chatVisibility : WrappedPacketInSettings.ChatVisibility?, viewDistance : Int, displaySkinPartsMask : Byte, chatColored : Boolean, serverListingsAllowed : Boolean, locale : String?, displayedSkinParts : Set<WrappedPacketInSettings.DisplayedSkinPart>) {

    }
    open fun onWindowClick(
        perpetrator : Player,
        actionNumber : Int,
        id : Int,
        mode : Int,
        windowButton : Int,
        windowSlot : Int,
        clickedItemStack : ItemStack,){

    }
open fun flag(increment:Long,player:Player){
    Thread{
        if(validposition[player]!=null) {
            PacketEvents.get().playerUtils.sendPacket(
                player,
                WrappedPacketOutEntityLook(
                    player.entityId,
                 validposition[player]!!.yaw,
                    validposition.get(player)!!.pitch,
                    true
                )
            )
            PacketEvents.get().playerUtils.sendPacket(
                player,
                WrappedPacketOutEntityTeleport(player.entityId, validposition.get(player)!!.subtract(0.0,if(this is Fly || this is InvalidUp).165 else 0.0,0.0), false)
            )
            this.validposition.remove(player)
        }

      violations[player]=violations.getOrDefault(player,0L)+increment;
    when(this.category){
        Category.COMBAT->{
            combatviolations++;
            if(combatviolations>=5) {
                for (op in Bukkit.getServer().operators) {
                    if (op.isOnline) {
                        op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 has an unlikely amount of ${getCategoryColor(this.category)}Combat Violations§6 ")
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
                        op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 has an unlikely amount of ${getCategoryColor(this.category)}Movement Violations§6 ")

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
                        op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 has an unlikely amount of ${getCategoryColor(this.category)}Packet Violations")

                        packetviolations=0

                    }

                }
            }
        }
    }
    for(op in Bukkit.getServer().operators){
if(op.isOnline){
    op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 failed ${getCategoryColor(this.category)}${this.name}§2 VL§a[${this.violations.getOrDefault(player,0L)}] §7(Ver ${PacketEvents.get().playerUtils.getClientVersion(player).toString().substring(2).replace("_",".")})")
}

    }
    if(this.violations.getOrDefault(player,0L)>=this.threshold&&!iskicked.getOrDefault(player,false)) {
        iskicked[player] = true

        Bukkit.getScheduler().runTask(Main.plugin!!, Runnable{
if(this.category==Category.PACKET){
    player.kickPlayer("Cheating")
}
        })
        this.violations.getOrDefault(player, 0L)

        for (op in Bukkit.getServer().operators) {
            if (op.isOnline) {
                op.player!!.sendMessage(player.name + " was banned for Cheating")
            }

        }
    }
    }.start()
}
    open fun flag(increment:Long,player:Player,debug:String){
        Thread{
            if(validposition[player]!=null) {
                PacketEvents.get().playerUtils.sendPacket(
                    player,
                    WrappedPacketOutEntityLook(
                        player.entityId,
                        validposition[player]!!.yaw,
                        validposition.get(player)!!.pitch,
                        true
                    )
                )
                PacketEvents.get().playerUtils.sendPacket(
                    player,
                    WrappedPacketOutEntityTeleport(player.entityId, validposition.get(player)!!.subtract(0.0,if(this is Fly || this is InvalidUp).165 else 0.0,0.0), false)
                )
                this.validposition.remove(player)
            }

            violations[player]=violations.getOrDefault(player,0L)+increment;
            when(this.category){
                Category.COMBAT->{
                    combatviolations++;
                    if(combatviolations>=5) {
                        for (op in Bukkit.getServer().operators) {
                            if (op.isOnline) {
                                op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 has an unlikely amount of ${getCategoryColor(this.category)}Combat Violations§6 ")
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
                                op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 has an unlikely amount of ${getCategoryColor(this.category)}Movement Violations§6 ")

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
                                op.player!!.sendMessage("§2[❀GPT] §6${player.name}§2 has an unlikely amount of ${getCategoryColor(this.category)}Packet Violations")

                                packetviolations=0

                            }

                        }
                    }
                }
            }

            val message="§2[❀GPT] §6${player.name}§2 failed ${getCategoryColor(this.category)}${this.name}§2 VL§a[${
                this.violations.getOrDefault(
                    player,
                    0L
                )
            }] §7(Ver ${
                PacketEvents.get().playerUtils.getClientVersion(player).toString().substring(2).replace("_", ".")
            })"
            val text= net.md_5.bungee.api.chat.TextComponent(message);
            text.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(this.description+"\n >"+debug).create())

            for(op in Bukkit.getServer().operators){
                if(op.isOnline){
                    op.player!!.spigot().sendMessage(text)
                }

            }
            if(this.violations.getOrDefault(player,0L)>=this.threshold&&!iskicked.getOrDefault(player,false)) {
                iskicked[player] = true

                Bukkit.getScheduler().runTask(Main.plugin!!, Runnable{

                        if(this.category==Category.PACKET){
                            player.kickPlayer("Cheating")
                        }
                })
                this.violations.getOrDefault(player, 0L)

                for (op in Bukkit.getServer().operators) {
                    if (op.isOnline) {
                        op.player!!.sendMessage(player.name + " was banned for Cheating")
                    }

                }
            }
        }.start()
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