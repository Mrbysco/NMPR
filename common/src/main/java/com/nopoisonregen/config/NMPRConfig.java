package com.nopoisonregen.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class NMPRConfig {
	public static class Common {
		public final ModConfigSpec.ConfigValue<List<? extends String>> cancelable;

		Common(ModConfigSpec.Builder builder) {
			//General settings
			builder.comment("General settings")
					.push("general");

			cancelable = builder
					.comment("A list of cancelable effects in the format \"effect1,effect2\"")
					.defineListAllowEmpty("cancelable", () -> List.of("minecraft:regeneration,minecraft:poison"), String::new, o -> (o instanceof String));

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
}
