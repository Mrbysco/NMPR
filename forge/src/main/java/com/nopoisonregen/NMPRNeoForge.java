package com.nopoisonregen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@Mod(Constants.MOD_ID)
public class NMPRNeoForge {

	public NMPRNeoForge(IEventBus eventBus, Dist dist, ModContainer container) {
		container.registerConfig(ModConfig.Type.COMMON, NeoForgeNMPRConfig.commonSpec);
		eventBus.register(NeoForgeNMPRConfig.class);

		NeoForge.EVENT_BUS.addListener(this::isEffectApplicable);
		NeoForge.EVENT_BUS.addListener(this::onLivingTick);
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