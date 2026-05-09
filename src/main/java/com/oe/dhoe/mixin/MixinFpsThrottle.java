package com.oe.dhoe.mixin;

import com.seibel.distanthorizons.core.generation.WorldGenerationQueue;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;

import com.seibel.distanthorizons.api.objects.data.DhApiChunk;
import com.seibel.distanthorizons.core.pos.DhChunkPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WorldGenerationQueue.class, remap = false)
public class MixinFpsThrottle {

    private static long lastFrameTime = 0;
    private static final long FRAME_BUDGET_NS = 16600000; // 16.6ms in nanoseconds

    @Inject(method = "startGenerationEvent", at = @At("HEAD"), cancellable = true, remap = false)
    private void dhoe$fpsThrottle(DhChunkPos chunkPosMin, byte targetDataDetail, byte granularity,
                                    Consumer<DhApiChunk> chunkDataConsumer,
                                    CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        try {
            long now = System.nanoTime();
            if (lastFrameTime != 0 && (now - lastFrameTime) < FRAME_BUDGET_NS) {
                // Frame budget exhausted — skip this generation event
                cir.setReturnValue(CompletableFuture.completedFuture(null));
            }
            lastFrameTime = now;
        } catch (Exception ignored) {}
    }
}
