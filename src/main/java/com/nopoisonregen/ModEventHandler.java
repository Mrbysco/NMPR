package com.nopoisonregen;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 21/03/2019.
 */
public class ModEventHandler {
	public static final Map<ResourceLocation, ResourceLocation> cancelableEffectMap = new HashMap<>();

	/**
	 * Refresh cancelable effects cache
	 *
	 * @param cancelableEffects The configured list of cancelable effects
	 */
	public static void refreshCache(String[] cancelableEffects) {
		Main.LOGGER.info("Refreshing NMPR canceling effects");
		cancelableEffectMap.clear();
		for (String configValue : cancelableEffects) {
			if (!configValue.contains(","))
				continue;

			String[] values = configValue.split(",");
			if (values.length == 2) {
				String value1 = values[0];
				ResourceLocation location1 = new ResourceLocation(value1);
				String value2 = values[1];
				ResourceLocation location2 = new ResourceLocation(value2);
				if (location1 != null && location2 != null) {
					cancelableEffectMap.put(location1, location2);
				}
			}
		}
	}

	/**
	 * Check if the effect is applicable to the living entity
	 *
	 * @param applicableEffect The effect instance to check
	 * @param livingEntity     The living entity to check
	 * @return True if the effect is applicable, false otherwise
	 */
	private boolean isEffectApplicable(PotionEffect applicableEffect, EntityLivingBase livingEntity) {
		World level = livingEntity.getEntityWorld();
		if (!level.isRemote && !cancelableEffectMap.isEmpty()) {
			for (Map.Entry<ResourceLocation, ResourceLocation> entry : cancelableEffectMap.entrySet()) {
				Potion effect1 = ForgeRegistries.POTIONS.getValue(entry.getKey());
				Potion effect2 = ForgeRegistries.POTIONS.getValue(entry.getValue());
				if (effect1 != null && effect2 != null) {
					if (effect1 == applicableEffect.getPotion() && livingEntity.getActivePotionEffect(effect2) != null) {
						PotionEffect instance2 = livingEntity.getActivePotionEffect(effect2);
						if (instance2 != null) {
							int firstEffectTime = instance2.getDuration() * (instance2.getAmplifier() + 1);
							int secondEffectTime = applicableEffect.getDuration() * (applicableEffect.getAmplifier() + 1);
							int remainingTime = firstEffectTime - secondEffectTime;
							if (remainingTime == 0) {
								livingEntity.removePotionEffect(effect2);
								return false;
							}
						}
					} else if (effect2 == applicableEffect.getPotion() && livingEntity.getActivePotionEffect(effect1) != null) {
						PotionEffect instance1 = livingEntity.getActivePotionEffect(effect1);
						if (instance1 != null) {
							int firstEffectTime = instance1.getDuration() * (instance1.getAmplifier() + 1);
							int secondEffectTime = applicableEffect.getDuration() * (applicableEffect.getAmplifier() + 1);
							int remainingTime = firstEffectTime - secondEffectTime;
							if (remainingTime == 0) {
								livingEntity.removePotionEffect(effect1);
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	@SubscribeEvent
	public void playerTickEvent(PotionEvent.PotionApplicableEvent event) {
		boolean isApplicable = isEffectApplicable(event.getPotionEffect(), event.getEntityLiving());
		if (!isApplicable) {
			event.setResult(Result.DENY);
		}
	}

	/**
	 * Check if the player has 2 effects that cancel each other out
	 */
	@SubscribeEvent
	public void onLivingTick(TickEvent.PlayerTickEvent event) {
		EntityPlayer livingEntity = event.player;
		World level = livingEntity.getEntityWorld();
		if (!level.isRemote && level.getTotalWorldTime() % 20 == 0 && !cancelableEffectMap.isEmpty()) {
			for (Map.Entry<ResourceLocation, ResourceLocation> entry : cancelableEffectMap.entrySet()) {
				Potion effect1 = ForgeRegistries.POTIONS.getValue(entry.getKey());
				Potion effect2 = ForgeRegistries.POTIONS.getValue(entry.getValue());
				if (effect1 != null && effect2 != null) {
					PotionEffect instance1 = livingEntity.getActivePotionEffect(effect1);
					PotionEffect instance2 = livingEntity.getActivePotionEffect(effect2);
					if (instance1 != null && instance2 != null) {
						int firstEffectTime = instance1.getDuration() * (instance1.getAmplifier() + 1);
						int secondEffectTime = instance2.getDuration() * (instance2.getAmplifier() + 1);
						int remainingTime = firstEffectTime - secondEffectTime;
						livingEntity.removePotionEffect(effect1);
						livingEntity.removePotionEffect(effect2);
						if (remainingTime < 0) {
							livingEntity.addPotionEffect(new PotionEffect(effect2, -remainingTime));
						} else if (remainingTime > 0) {
							livingEntity.addPotionEffect(new PotionEffect(effect1, remainingTime));
						}
					}
				}
			}
		}
	}
}

