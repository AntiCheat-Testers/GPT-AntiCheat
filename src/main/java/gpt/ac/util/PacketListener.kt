package gpt.ac.util


import io.github.retrooper.packetevents.event.PacketListenerAbstract
import io.github.retrooper.packetevents.event.PacketListenerPriority
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent

import io.github.retrooper.packetevents.packettype.PacketType
import io.github.retrooper.packetevents.packetwrappers.WrappedPacket
import io.github.retrooper.packetevents.packetwrappers.login.`in`.custompayload.WrappedPacketLoginInCustomPayload
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
import io.github.retrooper.packetevents.packetwrappers.play.out.abilities.WrappedPacketOutAbilities
import io.github.retrooper.packetevents.packetwrappers.play.out.animation.WrappedPacketOutAnimation
import io.github.retrooper.packetevents.packetwrappers.play.out.blockaction.WrappedPacketOutBlockAction
import io.github.retrooper.packetevents.packetwrappers.play.out.blockbreakanimation.WrappedPacketOutBlockBreakAnimation
import io.github.retrooper.packetevents.packetwrappers.play.out.blockchange.WrappedPacketOutBlockChange
import io.github.retrooper.packetevents.packetwrappers.play.out.camera.WrappedPacketOutCamera
import io.github.retrooper.packetevents.packetwrappers.play.out.chat.WrappedPacketOutChat
import io.github.retrooper.packetevents.packetwrappers.play.out.closewindow.WrappedPacketOutCloseWindow
import io.github.retrooper.packetevents.packetwrappers.play.out.collect.WrappedPacketOutCollect
import io.github.retrooper.packetevents.packetwrappers.play.out.custompayload.WrappedPacketOutCustomPayload
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity
import io.github.retrooper.packetevents.packetwrappers.play.out.entity.WrappedPacketOutEntity.WrappedPacketOutEntityLook
import io.github.retrooper.packetevents.packetwrappers.play.out.entitydestroy.WrappedPacketOutEntityDestroy
import io.github.retrooper.packetevents.packetwrappers.play.out.entityeffect.WrappedPacketOutEntityEffect
import io.github.retrooper.packetevents.packetwrappers.play.out.entityequipment.WrappedPacketOutEntityEquipment
import io.github.retrooper.packetevents.packetwrappers.play.out.entityheadrotation.WrappedPacketOutEntityHeadRotation
import io.github.retrooper.packetevents.packetwrappers.play.out.entitymetadata.WrappedPacketOutEntityMetadata
import io.github.retrooper.packetevents.packetwrappers.play.out.entitystatus.WrappedPacketOutEntityStatus
import io.github.retrooper.packetevents.packetwrappers.play.out.entityteleport.WrappedPacketOutEntityTeleport
import io.github.retrooper.packetevents.packetwrappers.play.out.entityvelocity.WrappedPacketOutEntityVelocity
import io.github.retrooper.packetevents.packetwrappers.play.out.experience.WrappedPacketOutExperience
import io.github.retrooper.packetevents.packetwrappers.play.out.explosion.WrappedPacketOutExplosion
import io.github.retrooper.packetevents.packetwrappers.play.out.gamestatechange.WrappedPacketOutGameStateChange
import io.github.retrooper.packetevents.packetwrappers.play.out.keepalive.WrappedPacketOutKeepAlive
import io.github.retrooper.packetevents.packetwrappers.play.out.namedentityspawn.WrappedPacketOutNamedEntitySpawn
import io.github.retrooper.packetevents.packetwrappers.play.out.namedsoundeffect.WrappedPacketOutNamedSoundEffect
import io.github.retrooper.packetevents.packetwrappers.play.out.openwindow.WrappedPacketOutOpenWindow
import io.github.retrooper.packetevents.packetwrappers.play.out.playerinfo.WrappedPacketOutPlayerInfo
import io.github.retrooper.packetevents.packetwrappers.play.out.removeentityeffect.WrappedPacketOutRemoveEntityEffect
import io.github.retrooper.packetevents.packetwrappers.play.out.spawnentity.WrappedPacketOutSpawnEntity
import io.github.retrooper.packetevents.packetwrappers.play.out.spawnentityliving.WrappedPacketOutSpawnEntityLiving
import io.github.retrooper.packetevents.packetwrappers.play.out.tabcomplete.WrappedPacketOutTabComplete
import io.github.retrooper.packetevents.packetwrappers.play.out.title.WrappedPacketOutTitle
import io.github.retrooper.packetevents.packetwrappers.play.out.unloadchunk.WrappedPacketOutUnloadChunk
import io.github.retrooper.packetevents.packetwrappers.play.out.updatehealth.WrappedPacketOutUpdateHealth
import io.github.retrooper.packetevents.packetwrappers.play.out.updatetime.WrappedPacketOutUpdateTime
import io.github.retrooper.packetevents.packetwrappers.play.out.windowitems.WrappedPacketOutWindowItems


class PacketListener : PacketListenerAbstract(PacketListenerPriority.HIGHEST) {
    private val clientBoundPackets = mapOf(
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
        PacketType.Play.Client.LOOK to WrappedPacketInFlying::class.java,
        PacketType.Play.Client.POSITION to WrappedPacketInFlying::class.java,
        PacketType.Play.Client.POSITION_LOOK to WrappedPacketInFlying::class.java,

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
    private val serverBoundPackets = mapOf(
        PacketType.Play.Server.ABILITIES to WrappedPacketOutAbilities::class.java,
        PacketType.Play.Server.ANIMATION to WrappedPacketOutAnimation::class.java,
        PacketType.Play.Server.BLOCK_ACTION to WrappedPacketOutBlockAction::class.java,
        PacketType.Play.Server.BLOCK_BREAK_ANIMATION to WrappedPacketOutBlockBreakAnimation::class.java,
        PacketType.Play.Server.BLOCK_CHANGE to WrappedPacketOutBlockChange::class.java,
        PacketType.Play.Server.CHAT to WrappedPacketOutChat::class.java,
        PacketType.Play.Server.CLOSE_WINDOW to WrappedPacketOutCloseWindow::class.java,
        PacketType.Play.Server.COLLECT to WrappedPacketOutCollect::class.java,
        PacketType.Play.Server.CUSTOM_PAYLOAD to WrappedPacketOutCustomPayload::class.java,
        PacketType.Play.Server.ENTITY to WrappedPacketOutEntity::class.java,
        PacketType.Play.Server.ENTITY_DESTROY to WrappedPacketOutEntityDestroy::class.java,
        PacketType.Play.Server.ENTITY_EFFECT to WrappedPacketOutEntityEffect::class.java,
        PacketType.Play.Server.ENTITY_EQUIPMENT to WrappedPacketOutEntityEquipment::class.java,
        PacketType.Play.Server.ENTITY_HEAD_ROTATION to WrappedPacketOutEntityHeadRotation::class.java,
        PacketType.Play.Server.ENTITY_LOOK to WrappedPacketOutEntityLook::class.java,
        PacketType.Play.Server.ENTITY_METADATA to WrappedPacketOutEntityMetadata::class.java,
        PacketType.Play.Server.ENTITY_STATUS to WrappedPacketOutEntityStatus::class.java,
        PacketType.Play.Server.ENTITY_TELEPORT to WrappedPacketOutEntityTeleport::class.java,
        PacketType.Play.Server.ENTITY_VELOCITY to WrappedPacketOutEntityVelocity::class.java,
        PacketType.Play.Server.EXPERIENCE to WrappedPacketOutExperience::class.java,
        PacketType.Play.Server.EXPLOSION to WrappedPacketOutExplosion::class.java,
        PacketType.Play.Server.GAME_STATE_CHANGE to WrappedPacketOutGameStateChange::class.java,
        PacketType.Play.Server.KEEP_ALIVE to WrappedPacketOutKeepAlive::class.java,
        PacketType.Play.Server.NAMED_ENTITY_SPAWN to WrappedPacketOutNamedEntitySpawn::class.java,
        PacketType.Play.Server.NAMED_SOUND_EFFECT to WrappedPacketOutNamedSoundEffect::class.java,
        PacketType.Play.Server.OPEN_WINDOW to WrappedPacketOutOpenWindow::class.java,
        PacketType.Play.Server.PLAYER_INFO to WrappedPacketOutPlayerInfo::class.java,

        PacketType.Play.Server.SPAWN_ENTITY to WrappedPacketOutSpawnEntity::class.java,
        PacketType.Play.Server.SPAWN_ENTITY_LIVING to WrappedPacketOutSpawnEntityLiving::class.java,
        PacketType.Play.Server.TAB_COMPLETE to WrappedPacketOutTabComplete::class.java,
        PacketType.Play.Server.TITLE to WrappedPacketOutTitle::class.java,
        PacketType.Play.Server.UNLOAD_CHUNK to WrappedPacketOutUnloadChunk::class.java,
        PacketType.Play.Server.UPDATE_HEALTH to WrappedPacketOutUpdateHealth::class.java,
        PacketType.Play.Server.UPDATE_TIME to WrappedPacketOutUpdateTime::class.java,
        PacketType.Play.Server.WINDOW_ITEMS to WrappedPacketOutWindowItems::class.java,
        PacketType.Play.Server.CAMERA to WrappedPacketOutCamera::class.java,
        PacketType.Play.Server.REMOVE_ENTITY_EFFECT to WrappedPacketOutRemoveEntityEffect::class.java,
    )
    override fun onPacketPlayReceive(event: PacketPlayReceiveEvent) {
        Thread {
            val player = event.player
            val packetID = event.packetId
            val packetClass = clientBoundPackets[packetID] ?: return@Thread
            val constructor = packetClass.getDeclaredConstructor(event.nmsPacket::class.java)
            val wrappedPacket = constructor.newInstance(event.nmsPacket) as WrappedPacket
            for(check in CheckManager.checkClasses){
                check.onPacketRecieve(player,wrappedPacket,event)
            }
            when (wrappedPacket) {
                is WrappedPacketLoginInCustomPayload -> {
                }

                is WrappedPacketInArmAnimation -> {
                    for (check in CheckManager.checkClasses) {
                        check.onArmAnimation(player, wrappedPacket.hand)
                    }
                }

                is WrappedPacketInKeepAlive -> {
                    for (check in CheckManager.checkClasses) {
                        check.onKeepAliveRecieve(player, wrappedPacket.id)
                    }
                }

                is WrappedPacketInUseEntity -> {
                    for (check in CheckManager.checkClasses) {
                        check.onUseEntity(player, wrappedPacket.action, wrappedPacket.target,wrappedPacket.entity)
                    }
                }

                is WrappedPacketInTransaction -> {
                    for (check in CheckManager.checkClasses) {
                        check.onTransactionRecieve(player, wrappedPacket.windowId, wrappedPacket.actionNumber)
                    }
                }

                is WrappedPacketInFlying -> {
            for(check in CheckManager.checkClasses)
                check.onFlying(player,wrappedPacket.yaw,wrappedPacket.pitch,wrappedPacket.position,wrappedPacket.isOnGround,wrappedPacket.isRotating,wrappedPacket.isMoving)
                }

                is WrappedPacketInSteerVehicle -> {
                    for (check in CheckManager.checkClasses) {
                        check.onSteer(
                            player,
                            wrappedPacket.forwardValue,
                            wrappedPacket.sideValue,
                            wrappedPacket.isDismount,
                            wrappedPacket.isJump
                        )
                    }
                }

                is WrappedPacketInBlockPlace -> {
                    for (check in CheckManager.checkClasses) {
                        check.onPlace(
                            player,
                            wrappedPacket.hand,
                            wrappedPacket.direction,
                            wrappedPacket.blockPosition,
                            wrappedPacket.cursorPosition,
                            wrappedPacket.itemStack
                        )
                    }
                }

                is WrappedPacketInHeldItemSlot -> {
                    for (check in CheckManager.checkClasses) {
                        check.onItemSwitch(player, wrappedPacket.currentSelectedSlot)
                    }
                }

                is WrappedPacketInChat -> {
                    for (check in CheckManager.checkClasses) {
                        check.onRecieveChat(player, wrappedPacket.message)
                    }
                }

                is WrappedPacketInUseItem -> {

                    for (check in CheckManager.checkClasses) {
                        check.onItemUse(player, wrappedPacket.hand, wrappedPacket.blockPosition)
                    }
                }

                is WrappedPacketInWindowClick -> {
                    for (check in CheckManager.checkClasses) {
                        check.onWindowClick(
                            player,
                            wrappedPacket.actionNumber,
                            wrappedPacket.windowId,
                            wrappedPacket.mode,
                            wrappedPacket.windowButton,
                            wrappedPacket.windowSlot,
                            wrappedPacket.clickedItemStack
                        )
                    }
                }

                is WrappedPacketInEntityAction -> {
                    for (check in CheckManager.checkClasses) {
                        check.onAction(
                            player,
                            wrappedPacket.action,
                            wrappedPacket.jumpBoost,
                            wrappedPacket.entityId,
                            wrappedPacket.entity
                        )
                    }
                }

                is WrappedPacketInSettings -> {
                    for (check in CheckManager.checkClasses) {
                        check.onSettings(
                            player,
                            wrappedPacket.chatVisibility,
                            wrappedPacket.viewDistance,
                            wrappedPacket.displaySkinPartsMask,
                            wrappedPacket.isChatColored,
                            wrappedPacket.isServerListingsAllowed,
                            wrappedPacket.locale,
                            wrappedPacket.displayedSkinParts
                        )
                    }
                }
            }


        }.start()
    }

    override fun onPacketPlaySend(event : PacketPlaySendEvent) {
        val packetID = event.packetId
        val packetClass = serverBoundPackets[packetID] ?: return
        val constructor = packetClass.getDeclaredConstructor(event.nmsPacket::class.java)
        val wrappedPacket = constructor.newInstance(event.nmsPacket) as WrappedPacket
        for(check in CheckManager.checkClasses){
            check.onPacketSend(event.player,wrappedPacket,event)
        }
    }


    }

