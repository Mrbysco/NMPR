package com.nopoisonregen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonClass {
	public static final Map<MobEffect, MobEffect> cancelableEffectMap = new HashMap<>();

	/**
	 * Refresh cancelable effects cache
	 *
	 * @param cancelableEffects The configured list of cancelable effects
	 */
	public static void refreshCache(List<? extends String> cancelableEffects) {
		Constants.LOGGER.info("Refreshing NMPR canceling effects");
		cancelableEffectMap.clear();
		for (String configValue : cancelableEffects) {
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

	/**
	 * Check if the effect is applicable to the living entity
	 *
	 * @param effectInstance The effect instance to check
	 * @param livingEntity   The living entity to check
	 * @return True if the effect is applicable, false otherwise
	 */
	public static boolean isEffectApplicable(MobEffectInstance effectInstance, LivingEntity livingEntity) {
		MobEffect applicableEffect = effectInstance.getEffect();
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
								return false;
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
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Check if the entity has 2 effects that cancel each other out
	 *
	 * @param livingEntity The living entity to check
	 */
	public static void onLivingTick(LivingEntity livingEntity) {
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
}