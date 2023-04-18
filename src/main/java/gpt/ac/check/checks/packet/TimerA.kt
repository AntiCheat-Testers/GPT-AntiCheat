package gpt.ac.check.checks.packet

import gpt.ac.Main
import gpt.ac.check.Check
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent
import io.github.retrooper.packetevents.packetwrappers.WrappedPacket
import io.github.retrooper.packetevents.packetwrappers.play.`in`.armanimation.WrappedPacketInArmAnimation
import io.github.retrooper.packetevents.packetwrappers.play.`in`.blockdig.WrappedPacketInBlockDig
import io.github.retrooper.packetevents.packetwrappers.play.`in`.blockplace.WrappedPacketInBlockPlace
import io.github.retrooper.packetevents.packetwrappers.play.`in`.boatmove.WrappedPacketInBoatMove
import io.github.retrooper.packetevents.packetwrappers.play.`in`.chat.WrappedPacketInChat
import io.github.retrooper.packetevents.packetwrappers.play.`in`.entityaction.WrappedPacketInEntityAction
import io.github.retrooper.packetevents.packetwrappers.play.`in`.flying.WrappedPacketInFlying
import io.github.retrooper.packetevents.packetwrappers.play.`in`.helditemslot.WrappedPacketInHeldItemSlot
import io.github.retrooper.packetevents.packetwrappers.play.`in`.settings.WrappedPacketInSettings
import io.github.retrooper.packetevents.packetwrappers.play.`in`.steervehicle.WrappedPacketInSteerVehicle
import io.github.retrooper.packetevents.packetwrappers.play.`in`.transaction.WrappedPacketInTransaction
import io.github.retrooper.packetevents.packetwrappers.play.`in`.useentity.WrappedPacketInUseEntity
import io.github.retrooper.packetevents.packetwrappers.play.`in`.useitem.WrappedPacketInUseItem
import io.github.retrooper.packetevents.packetwrappers.play.`in`.vehiclemove.WrappedPacketInVehicleMove
import io.github.retrooper.packetevents.packetwrappers.play.`in`.windowclick.WrappedPacketInWindowClick
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive
import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType.W
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent

class TimerA : Check("Timer A", "This is a Check That Checks if a perpetrator sends too many packets before the server sends a keepalive packet", Category.PACKET, 2) {
    private var counter = mutableMapOf<Player, Double>()
    // Counter to keep track of the number of packets received
    override fun onLeave(event : PlayerQuitEvent) {
        counter.remove(event.player)
    }
    override fun onPacketRecieve(perpetrator: Player, wrappedpacket: WrappedPacket,event:PacketPlayReceiveEvent) {



        // Increment the counter based on acceptable packets
        if(wrappedpacket is WrappedPacketInFlying||wrappedpacket is WrappedPacketOutKeepAlive||wrappedpacket is WrappedPacketInTransaction) {
            counter[perpetrator] = counter.getOrDefault(perpetrator, 0.0) + 1
if(wrappedpacket is WrappedPacketInFlying && Location(perpetrator.world,wrappedpacket.x,wrappedpacket.y,wrappedpacket.z).block.type.isSolid){
    counter[perpetrator] = counter.getOrDefault(perpetrator, 0.0) -1

}
        }

        // Debug print the counter
      //  println(counter.get(perpetrator))

        // Check if the counter is greater than 45, and flag the player if it is
        if (counter.getOrDefault(perpetrator,0.0) > 45) {
            event.isCancelled=true
         flag(1, perpetrator,"Sent ${counter.getOrDefault(perpetrator,0.0)} Packets Before Server KeepAlive, Max allowed is 45")
        }
    }

    override fun onPacketSend(perpetrator: Player, wrappedpacket: WrappedPacket,event: PacketPlaySendEvent) {
        // Reset the counter if a keep alive packet is sent
        if(wrappedpacket is WrappedPacketOutEntityTeleport){
            counter[perpetrator]=counter.getOrDefault(perpetrator,0.0)-3

        }
        if (wrappedpacket is WrappedPacketOutKeepAlive) {
            counter[perpetrator]=counter.getOrDefault(perpetrator,0.0)- counter.getOrDefault(perpetrator,0.0)

        }
        if (counter.getOrDefault(perpetrator,0.0) > 45) {
            event.isCancelled=true
        }
    }
}