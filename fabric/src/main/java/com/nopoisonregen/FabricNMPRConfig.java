package com.nopoisonregen;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.List;

@Config(name = Constants.MOD_ID)
public class FabricNMPRConfig implements ConfigData {
	@ConfigEntry.Gui.CollapsibleObject
	public General general = new General();

	public static class General {
		@Comment("A list of cancelable effects in the format \"effect1,effect2\"")
		public List<String> cancelable = List.of("minecraft:regeneration,minecraft:poison");
	}
}