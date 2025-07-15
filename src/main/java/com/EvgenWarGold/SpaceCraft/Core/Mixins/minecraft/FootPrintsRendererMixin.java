package com.EvgenWarGold.SpaceCraft.Core.Mixins.minecraft;

import com.EvgenWarGold.SpaceCraft.Proxy.ClientProxy;
import net.minecraft.client.particle.EffectRenderer;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EffectRenderer.class)
public class FootPrintsRendererMixin {
    @Inject(method = "renderParticles", at = @At("HEAD"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, require = 1)
    private void spacecraft$onRenderParticles(Entity entity, float partialTicks, CallbackInfo callbackInfo) {
        ClientProxy.renderFootprints(partialTicks);
    }
}
