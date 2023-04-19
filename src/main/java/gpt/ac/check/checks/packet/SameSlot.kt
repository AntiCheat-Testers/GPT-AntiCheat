package gpt.ac.check.checks.packet

import gpt.ac.check.Check
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent
import io.github.retrooper.packetevents.packetwrappers.WrappedPacket
import io.github.retrooper.packetevents.packetwrappers.play.`in`.helditemslot.WrappedPacketInHeldItemSlot
import org.bukkit.entity.Player

class SameSlot: Check("Same Slot","Detects if the Player sends 2 of the same slot",Category.PACKET,1) {
private var lastSelectedSlot=0;

    override fun onPacketSend(perpetrator : Player, wrappedpacket : WrappedPacket, event : PacketPlaySendEvent) {

        if(wrappedpacket is WrappedPacketInHeldItemSlot){
            if(wrappedpacket.currentSelectedSlot==lastSelectedSlot){
                flag(1,perpetrator)
            }
        lastSelectedSlot=wrappedpacket.currentSelectedSlot
        }

    }
}
