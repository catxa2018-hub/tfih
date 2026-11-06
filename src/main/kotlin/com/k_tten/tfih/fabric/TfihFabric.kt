package com.k_tten.tfih.fabric

import com.k_tten.tfih.fabric.features.Fishing
import com.k_tten.tfih.fabric.features.ShiningDiamond
import com.k_tten.tfih.fabric.features.Umberella
import com.k_tten.tfih.fabric.config.Config
import com.k_tten.tfih.fabric.config.ConfigManager
import com.k_tten.tfih.fabric.config.ConfigCommands
import com.k_tten.tfih.fabric.helper.ChatHelper
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import kotlin.random.Random
import kotlin.reflect.KProperty

class TfihFabric: ModInitializer {

    companion object {
        val LOGGER = LogManager.getLogger(TfihFabric::class.java)!!

        lateinit var INSTANCE: TfihFabric

        operator fun getValue(thisRef: Any?, property: KProperty<*>): Config {
            return INSTANCE.configManager.config
        }
        val RANDOM = Random(System.currentTimeMillis())

        val FEATURE_LIST = listOf(Fishing, Umberella, ShiningDiamond)
    }

    lateinit var configManager: ConfigManager

    override fun onInitialize() {
        INSTANCE = this
        LOGGER.info("Initializing tfih...")

        // Initialize Helpers
        ChatHelper.init()

        val fabricLoader = FabricLoader.getInstance()
        val configPath = fabricLoader.configDir.resolve("tfih.json")
     
        configManager = ConfigManager(configPath)

        ClientLifecycleEvents.CLIENT_STARTED.register { client: Minecraft ->
            for (feature in FEATURE_LIST) {
                feature.init(client)
            }
        }

        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher, registryAccess ->
            for (feature in FEATURE_LIST) {
                feature.registerCommands(dispatcher, registryAccess)
            }
            ConfigCommands.register(dispatcher, configManager)
        })
    }
}