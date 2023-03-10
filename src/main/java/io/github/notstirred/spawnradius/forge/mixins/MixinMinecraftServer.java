package io.github.notstirred.spawnradius.forge.mixins;

import io.github.notstirred.spawnradius.forge.SpawnRadius;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    private static int requiredChunksCount = 441;

    @Redirect(method = "prepareLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerChunkCache;addRegionTicket(Lnet/minecraft/server/level/TicketType;Lnet/minecraft/world/level/ChunkPos;ILjava/lang/Object;)V"), require = 1)
    private <T> void replaceRegionTicketRadius(ServerChunkCache instance, TicketType<T> ticketType, ChunkPos pos, int radius, T unit) {
        if (SpawnRadius.RADIUS > 0) {
            instance.addRegionTicket(ticketType, pos, SpawnRadius.RADIUS, unit);
        }
        SpawnRadius.LOGGER.info("Replaced SPAWN ticket with radius: " + SpawnRadius.RADIUS);

        requiredChunksCount = calculateRequiredChunksCount(SpawnRadius.RADIUS);
    }

    @ModifyConstant(method = "prepareLevels", constant = @Constant(intValue = 441), require = 1)
    private int replaceRequiredChunksCount(int original) {
        return requiredChunksCount;
    }

    private static int calculateRequiredChunksCount(int radius) {
        if (radius <= 0) {
            SpawnRadius.LOGGER.info("Replaced required chunks to load with: " + 0);
            return 0;
        }
        int entityTickingDiameter = (radius - 1)*2 + 1;
        int chunksCount = entityTickingDiameter * entityTickingDiameter;
        SpawnRadius.LOGGER.info("Replaced required chunks to load with: " + chunksCount);
        return chunksCount;
    }
}
