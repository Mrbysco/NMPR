package com.nopoisonregen;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Main.MOD_ID)
public class Main {
	public static final String MOD_ID = "nopoisonregen";

	public Main() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void playerTickEvent(PlayerTickEvent event) {
		Player player = event.player;
		if (player.hasEffect(MobEffects.REGENERATION) && player.hasEffect(MobEffects.POISON)) {
			int regenTime = player.getEffect(MobEffects.REGENERATION).getDuration();
			int poisonTime = player.getEffect(MobEffects.POISON).getDuration();
			int remainingTime = regenTime - poisonTime;
			if (remainingTime < 0) {
				player.removeEffectNoUpdate(MobEffects.POISON);
				player.removeEffectNoUpdate(MobEffects.REGENERATION);
				player.addEffect(new MobEffectInstance(MobEffects.POISON, -remainingTime));
			} else if (remainingTime > 0) {
				player.removeEffectNoUpdate(MobEffects.POISON);
				player.removeEffectNoUpdate(MobEffects.REGENERATION);
				player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, remainingTime));
			} else {
				player.removeEffectNoUpdate(MobEffects.POISON);
				player.removeEffectNoUpdate(MobEffects.REGENERATION);
			}
		}
	}
}