package com.k_tten.tfih.fabric.features

import com.k_tten.tfih.fabric.TfihFabric
import com.k_tten.tfih.fabric.model.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft

object ShiningDiamond : Feature {
    private const val SHINING_MARKER = "SHINING! "
    private const val RIBBIT_MARKER = "RIBBIT! "
    private const val DIAMOND_MARKER = " caught their first DIAMOND "
    private const val BRILLIANT_MARKER = "BRILLIANT! "
    private const val ALL_DIAMOND_MARKER = " caught all DIAMOND "

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val isEnabled
        get() = TfihFabric.INSTANCE.configManager.config.autoGg

    override fun init(mc: Minecraft) {
        ClientReceiveMessageEvents.GAME.register { message, _ ->
            if (!isEnabled) return@register
            val text = message.string
            val shiningIndex = text.indexOf(SHINING_MARKER)
            val ribbitIndex = text.indexOf(RIBBIT_MARKER)
            val diamondIndex = text.indexOf(DIAMOND_MARKER)
            val brilliantIndex = text.indexOf(BRILLIANT_MARKER)
            val allDiamondIndex = text.indexOf(ALL_DIAMOND_MARKER)
            if ((shiningIndex == 0 && diamondIndex > shiningIndex) ||
                (ribbitIndex == 0 && diamondIndex > ribbitIndex) ||
                (brilliantIndex == 0 && allDiamondIndex > brilliantIndex)
            ) {
                sendDelayedGg(mc)
            }
        }
    }

    private fun sendDelayedGg(mc: Minecraft) {
        val connection = mc.connection ?: return
        scope.launch {
            delay(TfihFabric.RANDOM.nextLong(3_000L, 5_001L))
            mc.execute {
                if (mc.connection === connection && mc.player != null) {
                    connection.sendChat("gg")
                }
            }
        }
    }
}
