package com.oe.dhoe.mixin;

import com.seibel.distanthorizons.core.generation.WorldGenerationQueue;
import com.seibel.distanthorizons.core.generation.tasks.IWorldGenTaskTracker;
import com.seibel.distanthorizons.core.generation.tasks.WorldGenResult;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.DhLodPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WorldGenerationQueue.class, remap = false)
public class MixinProximityGate {

    @Inject(method = "submitGenTask", at = @At("HEAD"), cancellable = true, remap = false)
    private void dhoe$proximityGate(long pos, byte requiredDataDetail, IWorldGenTaskTracker tracker,
                                     CallbackInfoReturnable<CompletableFuture<WorldGenResult>> cir) {
        try {
            net.minecraft.server.MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;

            int viewDist = server.getPlayerList().getViewDistance();
            double maxDist = viewDist * 16;

            DhLodPos lodPos = DhSectionPos.getSectionBBoxPos(pos);
            int blockX = lodPos.getCornerBlockPos().x;
            int blockZ = lodPos.getCornerBlockPos().z;

            double nearestDist = Double.MAX_VALUE;
            for (ServerLevel level : server.getAllLevels()) {
                for (Player player : level.players()) {
                    double dx = player.getX() - blockX;
                    double dz = player.getZ() - blockZ;
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    if (dist < nearestDist) nearestDist = dist;
                }
            }

            if (nearestDist > maxDist) {
                cir.setReturnValue(CompletableFuture.completedFuture(WorldGenResult.CreateFail()));
            }
        } catch (Exception ignored) {}
    }
}
