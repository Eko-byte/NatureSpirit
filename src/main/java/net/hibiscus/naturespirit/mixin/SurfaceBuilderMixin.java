package net.hibiscus.naturespirit.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.hibiscus.naturespirit.registration.NSBiomes;
import net.hibiscus.naturespirit.registration.NSWorldGen;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.RandomSplitter;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.BlockColumn;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.noise.NoiseParametersKeys;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true) @Mixin(SurfaceBuilder.class)
public class SurfaceBuilderMixin {
   @Final @Shadow
   private BlockState defaultState;
   @Unique private DoublePerlinNoiseSampler sugiPillarNoise;
   @Unique private DoublePerlinNoiseSampler sugiPillarRoofNoise;
   @Unique private DoublePerlinNoiseSampler sugiSurfaceNoise;

   @Inject(method = "<init>", at = @At(value = "TAIL"))
   private void injectSugiNoise(NoiseConfig noiseConfig, BlockState defaultState, int seaLevel, RandomSplitter randomDeriver, CallbackInfo ci)  {
      sugiPillarNoise = noiseConfig.getOrCreateSampler(NSWorldGen.SUGI_PILLAR);
      sugiPillarRoofNoise = noiseConfig.getOrCreateSampler(NSWorldGen.SUGI_PILLAR_ROOF);
      sugiSurfaceNoise = noiseConfig.getOrCreateSampler(NSWorldGen.SUGI_SURFACE);
   }

   @Inject(method = "buildSurface", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/entry/RegistryEntry;matchesKey(Lnet/minecraft/registry/RegistryKey;)Z", ordinal = 0))
   private void injectSugiPillars(NoiseConfig noiseConfig, BiomeAccess biomeAccess, Registry <Biome> biomeRegistry, boolean useLegacyRandom, HeightContext heightContext,
           Chunk chunk, ChunkNoiseSampler chunkNoiseSampler, MaterialRules.MaterialRule materialRule,
           CallbackInfo ci,
           @Local RegistryEntry <Biome> registryEntry, @Local(ordinal = 4) int m, @Local(ordinal = 5) int n, @Local(ordinal = 6) int o, @Local BlockColumn blockColumn)  {
      if (registryEntry.matchesKey(NSBiomes.SUGI_FOREST)) {
         this.placeSugiPillar(blockColumn, m, n, o, chunk);
      }
   }

   @Unique private void placeSugiPillar(BlockColumn column, int x, int z, int surfaceY, HeightLimitView chunk) {
      double e = Math.min(Math.abs(sugiSurfaceNoise.sample(x, 0.0, z) * 8.25), sugiPillarNoise.sample((double)x * 0.2, 0.0, (double)z * 0.2) * 15.0);
      if (!(e <= 0.0)) {
         double h = Math.abs(sugiPillarRoofNoise.sample((double)x * 0.75, 0.0, (double)z * 0.75) * 2.25);
         double i = 64.0 + Math.min(e * e * 5.5, Math.ceil(h * 50.0) + 12.0);
         int j = MathHelper.floor(i);
         if (surfaceY <= j) {
            int k;
            for(k = j; k >= chunk.getBottomY(); --k) {
               BlockState blockState = column.getState(k);
               if (blockState.isOf(this.defaultState.getBlock())) {
                  break;
               }

               if (blockState.isOf(Blocks.WATER)) {
                  return;
               }
            }

            for(k = j; k >= chunk.getBottomY() && column.getState(k).isAir(); --k) {
               column.setState(k, this.defaultState);
            }

         }
      }
   }

}
