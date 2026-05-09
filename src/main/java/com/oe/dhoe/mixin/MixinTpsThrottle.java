package com.oe.dhoe.mixin;

import com.seibel.distanthorizons.core.generation.WorldGenerationQueue;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WorldGenerationQueue.class, remap = false)
public class MixinTpsThrottle {

    @Inject(method = "startNextWorldGenTask", at = @At("HEAD"), cancellable = true, remap = false)
    private void dhoe$tpsThrottle(CallbackInfoReturnable<Boolean> cir) {
        try {
            net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;

            double tps = 1000.0 / server.getAverageTickTime();
            if (tps < 20.0) {
                cir.setReturnValue(false);
            }
        } catch (Exception ignored) {}
    }
}
