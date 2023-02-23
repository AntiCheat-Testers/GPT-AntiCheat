package gpt.ac.util

import io.github.retrooper.packetevents.event.PacketListenerAbstract
import io.github.retrooper.packetevents.event.PacketListenerPriority
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent
import io.github.retrooper.packetevents.packettype.PacketType
import io.github.retrooper.packetevents.packettype.PacketType.Play
import io.github.retrooper.packetevents.packetwrappers.WrappedPacket
import io.github.retrooper.packetevents.packetwrappers.play.`in`.abilities.WrappedPacketInAbilities
import io.github.retrooper.packetevents.packetwrappers.play.`in`.armanimation.WrappedPacketInArmAnimation
import io.github.retrooper.packetevents.packetwrappers.play.`in`.blockdig.WrappedPacketInBlockDig
import io.github.retrooper.packetevents.packetwrappers.play.`in`.blockplace.WrappedPacketInBlockPlace
import io.github.retrooper.packetevents.packetwrappers.play.`in`.chat.WrappedPacketInChat
import io.github.retrooper.packetevents.packetwrappers.play.`in`.clientcommand.WrappedPacketInClientCommand
import io.github.retrooper.packetevents.packetwrappers.play.`in`.closewindow.WrappedPacketInCloseWindow
import io.github.retrooper.packetevents.packetwrappers.play.`in`.custompayload.WrappedPacketInCustomPayload
import io.github.retrooper.packetevents.packetwrappers.play.`in`.enchantitem.WrappedPacketInEnchantItem
import io.github.retrooper.packetevents.packetwrappers.play.`in`.entityaction.WrappedPacketInEntityAction
import io.github.retrooper.packetevents.packetwrappers.play.`in`.flying.WrappedPacketInFlying
import io.github.retrooper.packetevents.packetwrappers.play.`in`.helditemslot.WrappedPacketInHeldItemSlot
import io.github.retrooper.packetevents.packetwrappers.play.`in`.keepalive.WrappedPacketInKeepAlive
import io.github.retrooper.packetevents.packetwrappers.play.`in`.setcreativeslot.WrappedPacketInSetCreativeSlot
import io.github.retrooper.packetevents.packetwrappers.play.`in`.settings.WrappedPacketInSettings
import io.github.retrooper.packetevents.packetwrappers.play.`in`.steervehicle.WrappedPacketInSteerVehicle
import io.github.retrooper.packetevents.packetwrappers.play.`in`.tabcomplete.WrappedPacketInTabComplete
import io.github.retrooper.packetevents.packetwrappers.play.`in`.transaction.WrappedPacketInTransaction
import io.github.retrooper.packetevents.packetwrappers.play.`in`.updatesign.WrappedPacketInUpdateSign
import io.github.retrooper.packetevents.packetwrappers.play.`in`.useentity.WrappedPacketInUseEntity
import io.github.retrooper.packetevents.packetwrappers.play.`in`.useitem.WrappedPacketInUseItem
import io.github.retrooper.packetevents.packetwrappers.play.`in`.windowclick.WrappedPacketInWindowClick
import org.bukkit.entity.Entity
import org.bukkit.entity.Player


class PacketEventsPacketListener : PacketListenerAbstract(PacketListenerPriority.HIGHEST) {
    val clientBoundPackets = mapOf(
        PacketType.Play.Client.ARM_ANIMATION to WrappedPacketInArmAnimation::class.java,
        PacketType.Play.Client.BLOCK_DIG to WrappedPacketInBlockDig::class.java,
        PacketType.Play.Client.BLOCK_PLACE to WrappedPacketInBlockPlace::class.java,
        PacketType.Play.Client.CHAT to WrappedPacketInChat::class.java,
        PacketType.Play.Client.CLIENT_COMMAND to WrappedPacketInClientCommand::class.java,
        PacketType.Play.Client.CLOSE_WINDOW to WrappedPacketInCloseWindow::class.java,
        PacketType.Play.Client.CUSTOM_PAYLOAD to WrappedPacketInCustomPayload::class.java,
        PacketType.Play.Client.ENCHANT_ITEM to WrappedPacketInEnchantItem::class.java,
        PacketType.Play.Client.ENTITY_ACTION to WrappedPacketInEntityAction::class.java,
        PacketType.Play.Client.FLYING to WrappedPacketInFlying::class.java,
        PacketType.Play.Client.HELD_ITEM_SLOT to WrappedPacketInHeldItemSlot::class.java,
        PacketType.Play.Client.KEEP_ALIVE to WrappedPacketInKeepAlive::class.java,
        PacketType.Play.Client.SET_CREATIVE_SLOT to WrappedPacketInSetCreativeSlot::class.java,
        PacketType.Play.Client.SETTINGS to WrappedPacketInSettings::class.java,
        PacketType.Play.Client.STEER_VEHICLE to WrappedPacketInSteerVehicle::class.java,
        PacketType.Play.Client.TAB_COMPLETE to WrappedPacketInTabComplete::class.java,
        PacketType.Play.Client.TRANSACTION to WrappedPacketInTransaction::class.java,
        PacketType.Play.Client.UPDATE_SIGN to WrappedPacketInUpdateSign::class.java,
        PacketType.Play.Client.USE_ENTITY to WrappedPacketInUseEntity::class.java,
        PacketType.Play.Client.USE_ITEM to WrappedPacketInUseItem::class.java,
        PacketType.Play.Client.WINDOW_CLICK to WrappedPacketInWindowClick::class.java)
    override fun onPacketPlayReceive(event: PacketPlayReceiveEvent) {
        val player = event.player
        val wrappedPacketout = clientBoundPackets[event.packetId] ?: return

        when(event.packetId){

            Play.Client.ARM_ANIMATION->{
                for(Check in CheckManager.checkClasses) {
                  Check.onArmAnimation  (player,(wrappedPacketout.getDeclaredConstructor().newInstance(event.nmsPacket)as WrappedPacketInArmAnimation).hand)
                }
            }



        }        }
}
