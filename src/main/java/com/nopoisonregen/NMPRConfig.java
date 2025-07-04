package com.nopoisonregen;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Main.MOD_ID)
@Config.LangKey("nopoisonregen.config.title")
public class NMPRConfig {

	@Config.Comment({"General settings"})
	public static final General general = new General();

	public static class General {

		@Config.Comment("Disables all functional statue tiers (tiers that usually drop items) [Default: false]")
		public final String[] cancelable = new String[] {
				"minecraft:regeneration,minecraft:poison"
		};
	}

	@Mod.EventBusSubscriber(modid = Main.MOD_ID)
	private static class EventHandler {

		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Main.MOD_ID)) {
				ConfigManager.sync(Main.MOD_ID, Config.Type.INSTANCE);
				ModEventHandler.refreshCache(general.cancelable);
			}
		}
	}
}
