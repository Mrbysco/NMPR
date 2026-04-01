package com.nopoisonregen;

import com.nopoisonregen.config.NMPRConfig;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;

public class ConfigHandler {

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		CommonClass.refreshCache(NMPRConfig.COMMON.cancelable.get());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		CommonClass.refreshCache(NMPRConfig.COMMON.cancelable.get());
	}
}
