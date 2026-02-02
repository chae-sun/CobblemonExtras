package dev.chasem.cobblemonextras.fabric

import dev.chasem.cobblemonextras.CobblemonExtras
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.level.ServerPlayer

class CobblemonFabric : ModInitializer {

    override fun onInitialize() {
        CobblemonExtras.initialize()

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            CobblemonExtras.registerCommands(dispatcher)
        }

        ServerLifecycleEvents.SERVER_STARTING.register {
            CobblemonExtras.permissions.register()
        }
    }
}
