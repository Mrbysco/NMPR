package com.nopoisonregen;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.InteractionResult;

public class NMPRFabric implements ModInitializer {
	public static ConfigHolder<FabricNMPRConfig> config;

	@Override
	public void onInitialize() {
		config = AutoConfig.register(FabricNMPRConfig.class, JanksonConfigSerializer::new);
		config.registerLoadListener((holder, config) -> {
			CommonClass.refreshCache(config.general.cancelable);
			return InteractionResult.PASS;
		});
		config.registerSaveListener((holder, config) -> {
			CommonClass.refreshCache(config.general.cancelable);
			return InteractionResult.PASS;
		});

		ServerLifecycleEvents.SERVER_STARTING.register((server) -> {
			CommonClass.refreshCache(config.get().general.cancelable);
		});
	}
}
