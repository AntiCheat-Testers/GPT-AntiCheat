package gpt.ac.check.checks.packet

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
import io.github.retrooper.packetevents.packetwrappers.play.`in`.helditemslot.WrappedPacketInHeldItemSlot
import io.github.retrooper.packetevents.packetwrappers.play.`in`.steervehicle.WrappedPacketInSteerVehicle
import io.github.retrooper.packetevents.packetwrappers.play.`in`.useentity.WrappedPacketInUseEntity
import io.github.retrooper.packetevents.packetwrappers.play.`in`.useitem.WrappedPacketInUseItem
import io.github.retrooper.packetevents.packetwrappers.play.`in`.vehiclemove.WrappedPacketInVehicleMove
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent

class TimerA : Check("Timer A", "Player Speed Up Time", Category.PACKET, 2) {
    private var counter = mutableMapOf<Player, Double>()
    // Counter to keep track of the number of packets received
    override fun onLeave(event : PlayerQuitEvent) {
        counter.remove(event.player)
    }
    override fun onPacketRecieve(perpetrator: Player, wrappedpacket: WrappedPacket,event:PacketPlayReceiveEvent) {
        // Check if the packet is a block place or arm animation packet


        // Increment the counter
        if(!(wrappedpacket is WrappedPacketInChat||wrappedpacket is WrappedPacketInBlockPlace || wrappedpacket is WrappedPacketInArmAnimation||wrappedpacket is WrappedPacketInEntityAction||(wrappedpacket is WrappedPacketInUseEntity||wrappedpacket is WrappedPacketInUseItem||wrappedpacket is WrappedPacketInBlockDig||wrappedpacket is WrappedPacketInHeldItemSlot||wrappedpacket is WrappedPacketInSteerVehicle||wrappedpacket is WrappedPacketInVehicleMove||wrappedpacket is WrappedPacketInBoatMove))) {
            counter[perpetrator] = counter.getOrDefault(perpetrator, 0.0) + 1

        }
        // Debug print the counter
      //  println(counter.get(perpetrator))

        // Check if the counter is greater than 46, and flag the player if it is
        if (counter.getOrDefault(perpetrator,0.0) > 45) {
            event.isCancelled=true
         flag(1, perpetrator)
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