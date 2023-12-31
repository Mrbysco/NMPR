package com.nopoisonregen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(Main.MOD_ID)
public class Main {
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "nopoisonregen";

	public Main(IEventBus eventBus) {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
		eventBus.register(Config.class);

		NeoForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void isEffectApplicable(MobEffectEvent.Applicable event) {
		MobEffectInstance effectInstance = event.getEffectInstance();
		MobEffect applicableEffect = effectInstance.getEffect();
		LivingEntity livingEntity = event.getEntity();
		Level level = livingEntity.level();
		if (!level.isClientSide && !cancelableEffectMap.isEmpty()) {
			for (Map.Entry<MobEffect, MobEffect> entry : cancelableEffectMap.entrySet()) {
				MobEffect effect1 = entry.getKey();
				MobEffect effect2 = entry.getValue();
				if (effect1 != null && effect2 != null) {
					if (effect1 == applicableEffect && livingEntity.hasEffect(effect2)) {
						MobEffectInstance instance2 = livingEntity.getEffect(effect2);
						if (instance2 != null) {
							int firstEffectTime = instance2.getDuration() * (instance2.getAmplifier() + 1);
							int secondEffectTime = effectInstance.getDuration() * (effectInstance.getAmplifier() + 1);
							int remainingTime = firstEffectTime - secondEffectTime;
							if (remainingTime == 0) {
								livingEntity.removeEffect(effect2);
								event.setResult(Event.Result.DENY);
							}
						}
					} else if (effect2 == applicableEffect && livingEntity.hasEffect(effect1)) {
						MobEffectInstance instance1 = livingEntity.getEffect(effect1);
						if (instance1 != null) {
							int firstEffectTime = instance1.getDuration() * (instance1.getAmplifier() + 1);
							int secondEffectTime = effectInstance.getDuration() * (effectInstance.getAmplifier() + 1);
							int remainingTime = firstEffectTime - secondEffectTime;
							if (remainingTime == 0) {
								livingEntity.removeEffect(effect1);
								event.setResult(Event.Result.DENY);
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingTick(LivingEvent.LivingTickEvent event) {
		LivingEntity livingEntity = event.getEntity();
		Level level = livingEntity.level();
		if (!level.isClientSide && level.getGameTime() % 20 == 0 && !cancelableEffectMap.isEmpty()) {
			for (Map.Entry<MobEffect, MobEffect> entry : cancelableEffectMap.entrySet()) {
				MobEffect effect1 = entry.getKey();
				MobEffect effect2 = entry.getValue();
				if (effect1 != null && effect2 != null && livingEntity.hasEffect(effect1) && livingEntity.hasEffect(effect2)) {
					MobEffectInstance instance1 = livingEntity.getEffect(effect1);
					MobEffectInstance instance2 = livingEntity.getEffect(effect2);
					if (instance1 != null && instance2 != null) {
						int firstEffectTime = instance1.getDuration() * (instance1.getAmplifier() + 1);
						int secondEffectTime = instance2.getDuration() * (instance2.getAmplifier() + 1);
						int remainingTime = firstEffectTime - secondEffectTime;
						livingEntity.removeEffect(effect1);
						livingEntity.removeEffect(effect2);
						if (remainingTime < 0) {
							livingEntity.addEffect(new MobEffectInstance(effect2, -remainingTime));
						} else if (remainingTime > 0) {
							livingEntity.addEffect(new MobEffectInstance(effect1, remainingTime));
						}
					}
				}
			}
		}
	}

	public static final Map<MobEffect, MobEffect> cancelableEffectMap = new HashMap<>();

	public static void refreshCache() {
		LOGGER.info("Refreshing NMPR canceling effects");
		cancelableEffectMap.clear();
		for (String configValue : Config.COMMON.cancelable.get()) {
			if (!configValue.contains(","))
				continue;

			String[] values = configValue.split(",");
			if (values.length == 2) {
				String value1 = values[0];
				ResourceLocation location1 = ResourceLocation.tryParse(value1);
				String value2 = values[1];
				ResourceLocation location2 = ResourceLocation.tryParse(value2);
				if (location1 != null && location2 != null) {
					MobEffect effect1 = BuiltInRegistries.MOB_EFFECT.get(location1);
					MobEffect effect2 = BuiltInRegistries.MOB_EFFECT.get(location2);
					if (effect1 != null && effect2 != null) {
						cancelableEffectMap.put(effect1, effect2);
					}
				}
			}
		}
	}
}