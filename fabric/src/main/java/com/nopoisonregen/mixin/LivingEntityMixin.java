package com.nopoisonregen.mixin;

import com.nopoisonregen.CommonClass;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(method = "canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
			at = @At("HEAD"), cancellable = true)
	public void canBeAffected(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
		boolean applicable = CommonClass.isEffectApplicable(mobEffectInstance, ((LivingEntity) (Object) this));
		if (!applicable) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		CommonClass.onLivingTick((LivingEntity) (Object) this);
	}
}