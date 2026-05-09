package com.oe.dhoe.mixin;

import com.seibel.distanthorizons.core.generation.WorldGenerationQueue;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldGenerationQueue.class, remap = false)
public class MixinIdleGate {

    @Inject(method = "startWorldGenQueuingThread", at = @At("HEAD"), cancellable = true, remap = false)
    private void dhoe$idleGate(CallbackInfo ci) {
        try {
            net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;

            boolean anyPlayerOnline = false;
            for (ServerLevel level : server.getAllLevels()) {
                if (!level.players().isEmpty()) {
                    anyPlayerOnline = true;
                    break;
                }
            }

            if (!anyPlayerOnline) {
                ci.cancel();
            }
        } catch (Exception ignored) {}
    }
}
