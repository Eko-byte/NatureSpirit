package net.hibiscus.naturespirit.blocks;

import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;

import javax.annotation.Nullable;
import java.util.Map;

public class PorcelainPotBlock extends Block {
   public static final float field_31095 = 3.0F;
   private static final Map <Block, Block> CONTENT_TO_POTTED = Maps.newHashMap();
   protected final VoxelShape SHAPE;
   private final Block content;
   private final Block type;

   public PorcelainPotBlock(Block content, VoxelShape shape, @Nullable Block type, AbstractBlock.Settings settings) {
      super(settings);
      this.SHAPE = shape;
      this.content = content;
      this.type = type;
      CONTENT_TO_POTTED.put(content, this);
   }

   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return SHAPE;
   }


   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      ItemStack itemStack = player.getStackInHand(hand);
      Item item = itemStack.getItem();
      BlockState blockState = (item instanceof BlockItem ? CONTENT_TO_POTTED.getOrDefault(((BlockItem) item).getBlock(), Blocks.AIR) : Blocks.AIR).getDefaultState();
      boolean bl = blockState.isOf(Blocks.AIR);
      boolean bl2 = this.isEmpty();
      if(bl != bl2) {
         if(bl2) {
            world.setBlockState(pos, blockState, 3);
            player.incrementStat(Stats.POT_FLOWER);
            if(!player.getAbilities().creativeMode) {
               itemStack.decrement(1);
            }
         }
         else {
            ItemStack itemStack2 = new ItemStack(this.content);
            if(itemStack.isEmpty()) {
               player.setStackInHand(hand, itemStack2);
            }
            else if(!player.giveItemStack(itemStack2)) {
               player.dropItem(itemStack2, false);
            }

            world.setBlockState(pos, type == null ? this.getDefaultState() : type.getDefaultState(), 3);
         }

         world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
         return ActionResult.success(world.isClient);
      }
      else {
         return ActionResult.CONSUME;
      }
   }

   public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
      return this.isEmpty() ? super.getPickStack(world, pos, state) : new ItemStack(this.content);
   }

   private boolean isEmpty() {
      return this.content == Blocks.AIR;
   }

   public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
      return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
   }

   public Block getContent() {
      return this.content;
   }

   public Block getType() {
      return this.type;
   }

   public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
      return false;
   }
}