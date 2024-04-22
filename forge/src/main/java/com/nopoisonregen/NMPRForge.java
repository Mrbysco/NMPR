package com.nopoisonregen;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class NMPRForge {

	public NMPRForge() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ForgeNMPRConfig.commonSpec);
		eventBus.register(ForgeNMPRConfig.class);

		MinecraftForge.EVENT_BUS.addListener(this::isEffectApplicable);
		MinecraftForge.EVENT_BUS.addListener(this::onLivingTick);
	}

	private void isEffectApplicable(MobEffectEvent.Applicable event) {
		boolean isApplicable = CommonClass.isEffectApplicable(event.getEffectInstance(), event.getEntity());
		if (!isApplicable) {
			event.setResult(Event.Result.DENY);
		}
	}

	private void onLivingTick(LivingEvent.LivingTickEvent event) {
		CommonClass.onLivingTick(event.getEntity());
	}
}