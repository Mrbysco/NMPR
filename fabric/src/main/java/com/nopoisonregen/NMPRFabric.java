package com.nopoisonregen;

import com.nopoisonregen.config.NMPRConfig;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;

public class NMPRFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		ConfigRegistry.INSTANCE.register(Constants.MOD_ID, ModConfig.Type.COMMON, NMPRConfig.commonSpec);

		ModConfigEvents.loading(Constants.MOD_ID).register((config) -> {
			CommonClass.refreshCache(NMPRConfig.COMMON.cancelable.get());
		});
		ModConfigEvents.reloading(Constants.MOD_ID).register((config) -> {
			CommonClass.refreshCache(NMPRConfig.COMMON.cancelable.get());
		});
	}
}
