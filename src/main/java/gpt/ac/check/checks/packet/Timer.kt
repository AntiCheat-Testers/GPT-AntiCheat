package gpt.ac.check.checks.packet

import gpt.ac.check.Check
import io.github.retrooper.packetevents.packetwrappers.WrappedPacket
import io.github.retrooper.packetevents.packetwrappers.play.`in`.armanimation.WrappedPacketInArmAnimation
import io.github.retrooper.packetevents.packetwrappers.play.`in`.blockplace.WrappedPacketInBlockPlace
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive
import org.bukkit.entity.Player

class Timer : Check("Timer A", "Player Speed Up Time", Category.PACKET, 5) {

    // Counter to keep track of the number of packets received
    var counter = 0.0

    override fun onPacketRecieve(perpetrator: Player, wrappedpacket: WrappedPacket) {
        // Check if the packet is a block place or arm animation packet
        if (wrappedpacket is WrappedPacketInBlockPlace || wrappedpacket is WrappedPacketInArmAnimation) {
            counter -= 2
        }

        // Increment the counter
        counter++

        // Debug print the counter
        System.out.println(counter)

        // Check if the counter is greater than 46, and flag the player if it is
        if (counter > 46) {
            flag(1, perpetrator)
        }
    }

    override fun onPacketSend(perpetrator: Player, wrappedpacket: WrappedPacket) {
        // Reset the counter if a keep alive packet is sent
        if (wrappedpacket is WrappedPacketOutKeepAlive) {
            counter = 0.0
        }
    }
}