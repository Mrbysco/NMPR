package com.nopoisonregen;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class NeoForgeNMPRConfig {
	public static class Common {
		public final ModConfigSpec.ConfigValue<List<? extends String>> cancelable;

		Common(ModConfigSpec.Builder builder) {
			//General settings
			builder.comment("General settings")
					.push("general");

			cancelable = builder
					.comment("A list of cancelable effects in the format \"effect1,effect2\"")
					.defineList(List.of("cancelable"), () -> List.of("minecraft:regeneration,minecraft:poison"), o -> (o instanceof String));

			builder.pop();
		}
	}

	public static final ModConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		CommonClass.refreshCache(NeoForgeNMPRConfig.COMMON.cancelable.get());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		CommonClass.refreshCache(NeoForgeNMPRConfig.COMMON.cancelable.get());
	}
}
