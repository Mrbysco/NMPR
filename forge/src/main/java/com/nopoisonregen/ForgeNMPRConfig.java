package com.nopoisonregen;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class ForgeNMPRConfig {
	public static class Common {
		public final ForgeConfigSpec.ConfigValue<List<? extends String>> cancelable;

		Common(ForgeConfigSpec.Builder builder) {
			//General settings
			builder.comment("General settings")
					.push("general");

			cancelable = builder
					.comment("A list of cancelable effects in the format \"effect1,effect2\"")
					.defineList(List.of("cancelable"), () -> List.of("minecraft:regeneration,minecraft:poison"), o -> (o instanceof String));

			builder.pop();
		}
	}

	public static final ForgeConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		CommonClass.refreshCache(ForgeNMPRConfig.COMMON.cancelable.get());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		CommonClass.refreshCache(ForgeNMPRConfig.COMMON.cancelable.get());
	}
}
