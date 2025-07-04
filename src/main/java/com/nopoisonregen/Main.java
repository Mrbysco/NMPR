package com.nopoisonregen;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = Main.MOD_ID, name = Main.NAME, version = Main.VERSION)
public class Main {
	public static final String MOD_ID = "nopoisonregen";
	public static final String NAME = "No Poison with Regeneration";
	public static final String VERSION = "1.0";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);


	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new NMPRConfig());

		MinecraftForge.EVENT_BUS.register(new ModEventHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// some example code

	}

	@EventHandler
	public void init(FMLPostInitializationEvent event) {
		ModEventHandler.refreshCache(NMPRConfig.general.cancelable);
	}
}
