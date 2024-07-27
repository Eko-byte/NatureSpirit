package net.hibiscus.naturespirit.world.tree_decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.hibiscus.naturespirit.world.HibiscusWorldGen;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

import java.util.List;
import java.util.function.Predicate;

public class SnowTreeDecorator extends TreeDecorator {
   public static final MapCodec <SnowTreeDecorator> CODEC = MapCodec.unit(SnowTreeDecorator::new);

   public SnowTreeDecorator() {}

   protected TreeDecoratorType <?> getType() {
      return HibiscusWorldGen.SNOW_DECORATOR;
   }

   public void generate(Generator generator) {
      List <BlockPos> list2 = generator.getLeavesPositions();
      list2.stream().filter((pos) -> generator.isAir(pos.up()))
           .forEach((pos) -> generator.replace(pos.up(), Blocks.SNOW.getDefaultState()));

      list2.stream().filter((pos) -> generator.isAir(pos.down()) && generator.isAir(pos.down().down()))
           .forEach((pos) -> {
              BlockPos blockPos = generator.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
              if (generator.isAir(blockPos)) {
                 generator.replace(blockPos, Blocks.SNOW.getDefaultState());
              }});
   }
}
