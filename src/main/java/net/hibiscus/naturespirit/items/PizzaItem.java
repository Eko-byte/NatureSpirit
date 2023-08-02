package net.hibiscus.naturespirit.items;

import net.hibiscus.naturespirit.NatureSpirit;
import net.hibiscus.naturespirit.blocks.HibiscusBlocks;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PizzaItem extends AliasedBlockItem {
   public PizzaItem(Block block, Settings settings) {
      super(block, settings);
   }

   private static String[] forEachTopping(ItemStack pizza) {
      NbtCompound nbtCompound = pizza.getOrCreateSubNbt("BlockStateTag");
      assert nbtCompound != null;
      String[] toppings = new String[9];
      toppings[0] = nbtCompound.getString("mushroom_topping");
      toppings[1] = nbtCompound.getString("green_olives_topping");
      toppings[2] = nbtCompound.getString("black_olives_topping");
      toppings[3] = nbtCompound.getString("beetroot_topping");
      toppings[4] = nbtCompound.getString("carrot_topping");
      toppings[5] = nbtCompound.getString("cod_topping");
      toppings[6] = nbtCompound.getString("chicken_topping");
      toppings[7] = nbtCompound.getString("pork_topping");
      toppings[8] = nbtCompound.getString("rabbit_topping");
      return toppings;
   }

   public void addBitesToPizza(ItemStack pizza) {
      NbtCompound nbtCompound = pizza.getOrCreateSubNbt("BlockStateTag");
      assert nbtCompound != null;
      int pizzaSlice = this.asItem() == HibiscusBlocks.WHOLE_PIZZA ? 0 : this.asItem() == HibiscusBlocks.THREE_QUARTERS_PIZZA ? 1 : this.asItem() == HibiscusBlocks.HALF_PIZZA ? 2 : 3;
      nbtCompound.putInt("pizza_bites", pizzaSlice);
   }

   public void getAllToppings(ItemStack pizza) {
      NbtCompound nbtCompound = pizza.getOrCreateSubNbt("BlockStateTag");
      assert nbtCompound != null;
      String[] toppings = forEachTopping(pizza);
      int j = 0;
      for(String topping : toppings) {
         if(topping.equals("true")) {
            j++;
         }
      }
      nbtCompound.putInt("toppings", j);
   }

   @Override public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
      addBitesToPizza(stack);
      getAllToppings(stack);
   }

   public void appendTooltip(ItemStack stack, @Nullable World world, List <Text> tooltip, TooltipContext context) {
      super.appendTooltip(stack, world, tooltip, context);
      String[] toppingBooleans = forEachTopping(stack);
      String[] toppingStrings = new String[9];
      toppingStrings[0] = "mushroom_topping";
      toppingStrings[1] = "green_olives_topping";
      toppingStrings[2] = "black_olives_topping";
      toppingStrings[3] = "beetroot_topping";
      toppingStrings[4] = "carrot_topping";
      toppingStrings[5] = "cod_topping";
      toppingStrings[6] = "chicken_topping";
      toppingStrings[7] = "pork_topping";
      toppingStrings[8] = "rabbit_topping";
      for(int i = 0; i < toppingBooleans.length; ++i) {
         if(toppingBooleans[i].equals("true")) {
            tooltip.add(Text.translatable("block.natures_spirit.pizza." + toppingStrings[i])
                    .formatted(Formatting.GRAY));
         }
      }
   }

   public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
      ItemStack itemStack = super.finishUsing(stack, world, user);
      Item pizzaSlice = this.asItem() == HibiscusBlocks.WHOLE_PIZZA ? HibiscusBlocks.THREE_QUARTERS_PIZZA : this.asItem() == HibiscusBlocks.THREE_QUARTERS_PIZZA ? HibiscusBlocks.HALF_PIZZA : this.asItem() == HibiscusBlocks.HALF_PIZZA ? HibiscusBlocks.QUARTER_PIZZA : Items.AIR;

      PlayerEntity holder = (PlayerEntity) user;
      holder.incrementStat(NatureSpirit.EAT_PIZZA_SLICE);
      int foodAmount = 0;
      float saturationModifier = 0F;
      String[] toppings = forEachTopping(stack);
      if(toppings[4].equals("true")) {
         foodAmount = foodAmount + 1;
         saturationModifier = saturationModifier + 0.1F;
      }
      if(toppings[7].equals("true")) {
         foodAmount = foodAmount + 2;
         saturationModifier = saturationModifier + 0.2F;
      }
      if(toppings[2].equals("true")) {
         foodAmount = foodAmount + 1;
         saturationModifier = saturationModifier + 0.1F;
      }
      if(toppings[1].equals("true")) {
         foodAmount = foodAmount + 2;
         saturationModifier = saturationModifier + 0.1F;
      }
      if(toppings[3].equals("true")) {
         foodAmount = foodAmount + 2;
         saturationModifier = saturationModifier + 0.1F;
      }
      if(toppings[6].equals("true")) {
         foodAmount = foodAmount + 2;
         saturationModifier = saturationModifier + 0.2F;
      }
      if(toppings[8].equals("true")) {
         foodAmount = foodAmount + 2;
         saturationModifier = saturationModifier + 0.2F;
      }
      if(toppings[5].equals("true")) {
         foodAmount = foodAmount + 2;
         saturationModifier = saturationModifier + 0.2F;
      }
      if(toppings[0].equals("true")) {
         foodAmount = foodAmount + 2;
         saturationModifier = saturationModifier + 0.1F;
      }
      holder.getHungerManager().add(foodAmount, saturationModifier);

      if(((PlayerEntity) user).getAbilities().creativeMode) {
         return itemStack;
      }
      else {
         assert itemStack.getNbt() != null;
         ItemStack itemStack1 = new ItemStack(pizzaSlice, 1);
         itemStack1.setNbt(stack.getNbt());
         return itemStack1;
      }
   }
}
