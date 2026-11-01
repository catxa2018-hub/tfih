package com.k_tten.tfih.fabric.features

import com.k_tten.tfih.fabric.TfihFabric
import com.k_tten.tfih.fabric.Utilities.screen
import com.k_tten.tfih.fabric.model.Feature
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.Items
import java.util.UUID

object Umberella : Feature {
    private const val MIN_INTERVAL_MILLIS = 4 * 60 * 1000L + 45 * 1000L
    private const val MAX_INTERVAL_MILLIS = 4 * 60 * 1000L + 55 * 1000L
    private const val MAX_JUMP_TICKS = 30

    private enum class ActionState {
        IDLE,
        RELEASE_JUMP,
        WAIT_FOR_PEAK,
        USE_ITEM,
        RESTORE_SLOT,
        CAST_ROD
    }

    private var wasEnabled = false
    private var nextActionAtNanos = 0L
    private var state = ActionState.IDLE
    private var stateTicks = 0
    private var originalSlot = -1
    private var activePlayerId: UUID? = null
    private var testRunRequested = false
    private var testRunActive = false

    private val config
        get() = TfihFabric.INSTANCE.configManager.config

    fun requestTestRun(): Boolean {
        if (state != ActionState.IDLE || testRunRequested) return false
        testRunRequested = true
        nextActionAtNanos = 0L
        return true
    }

    override fun init(mc: Minecraft) {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick(::onTick))
    }

    private fun onTick(mc: Minecraft) {
        if (!config.umberella && !testRunRequested && !testRunActive) {
            if (wasEnabled || state != ActionState.IDLE) resetAction(mc)
            wasEnabled = false
            return
        }

        if (!wasEnabled && !testRunRequested) {
            wasEnabled = true
            scheduleNextAction()
            return
        }
        if (testRunRequested) {
            wasEnabled = true
            nextActionAtNanos = 0L
        }

        val player = mc.player
        if (state != ActionState.IDLE && (player == null || player.uuid != activePlayerId)) {
            resetAction(mc)
            scheduleNextAction()
            return
        }
        if (state != ActionState.IDLE && (mc.screen() != null || mc.isPaused)) {
            resetAction(mc)
            scheduleNextAction()
            return
        }

        when (state) {
            ActionState.IDLE -> {
                if (System.nanoTime() < nextActionAtNanos) return
                if (player == null || mc.level == null || mc.screen() != null || mc.isPaused ||
                    !player.onGround() || player.mainHandItem.item != Items.FISHING_ROD
                ) {
                    scheduleNextAction()
                    return
                }

                activePlayerId = player.uuid
                originalSlot = player.inventory.selectedSlot
                stateTicks = 0
                testRunActive = testRunRequested
                testRunRequested = false
                mc.options.keyJump.setDown(true)
                state = ActionState.RELEASE_JUMP
            }

            ActionState.RELEASE_JUMP -> {
                mc.options.keyJump.setDown(false)
                stateTicks = 0
                state = ActionState.WAIT_FOR_PEAK
            }

            ActionState.WAIT_FOR_PEAK -> {
                stateTicks++
                if (player == null) return
                if (!player.onGround() && player.deltaMovement.y <= 0.0) {
                    val matchingSlot = findUmberellaSlot(player)
                    if (matchingSlot == null) {
                        finishAction()
                        return
                    }
                    player.inventory.selectedSlot = matchingSlot
                    state = ActionState.USE_ITEM
                } else if (stateTicks >= MAX_JUMP_TICKS) {
                    finishAction()
                }
            }

            ActionState.USE_ITEM -> {
                mc.startUseItem()
                state = ActionState.RESTORE_SLOT
            }

            ActionState.RESTORE_SLOT -> {
                player?.inventory?.selectedSlot = originalSlot
                state = ActionState.CAST_ROD
            }

            ActionState.CAST_ROD -> {
                if (player?.mainHandItem?.item == Items.FISHING_ROD) mc.startUseItem()
                finishAction()
            }
        }
    }

    private fun findUmberellaSlot(player: net.minecraft.client.player.LocalPlayer): Int? {
        for (slot in 0 until 9) {
            val stack = player.inventory.getItem(slot)
            val hasMatchingName = stack.hoverName.string.contains("Umberella", ignoreCase = true)
            val hasMatchingLore = stack.get(DataComponents.LORE)?.lines()?.any {
                it.string.contains("Umberella", ignoreCase = true)
            } == true
            if (!stack.isEmpty && (hasMatchingName || hasMatchingLore)) return slot
        }
        return null
    }

    private fun finishAction() {
        state = ActionState.IDLE
        stateTicks = 0
        originalSlot = -1
        activePlayerId = null
        testRunActive = false
        scheduleNextAction()
    }

    private fun resetAction(mc: Minecraft) {
        if (state == ActionState.RELEASE_JUMP) mc.options.keyJump.setDown(false)
        val player = mc.player
        if (player != null && player.uuid == activePlayerId && originalSlot >= 0) {
            player.inventory.selectedSlot = originalSlot
        }
        state = ActionState.IDLE
        stateTicks = 0
        originalSlot = -1
        activePlayerId = null
        testRunRequested = false
        testRunActive = false
    }

    private fun scheduleNextAction() {
        val intervalMillis = TfihFabric.RANDOM.nextLong(MIN_INTERVAL_MILLIS, MAX_INTERVAL_MILLIS + 1)
        nextActionAtNanos = System.nanoTime() + intervalMillis * 1_000_000L
    }
}
