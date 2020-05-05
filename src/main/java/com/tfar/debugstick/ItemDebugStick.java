package com.tfar.debugstick;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.IProperty;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import static com.tfar.debugstick.DebugStick.ITEM_ID;

@Mod.EventBusSubscriber(modid = DebugStick.MODID)
public class ItemDebugStick extends Item {

  private static final String TAG_SELECTED = "selectedProperty";
  private static final String TAG_IS_DEBUG_STICK = "isDebugStick";

  public ItemDebugStick(String name) {
    setRegistryName(new ResourceLocation("minecraft"/* DebugStick.MODID */, name));
    setTranslationKey(ITEM_ID);
    setCreativeTab(CreativeTabs.MISC);
  }

  // called when right-clicking a block

  @Override
  @Nonnull
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
      float hitX, float hitY, float hitZ) {

    if (world.isRemote)
      return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

    ItemStack stack = player.getHeldItem(hand);
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null) {
      NBTTagCompound empty = new NBTTagCompound();
      stack.setTagCompound(empty);
    }
    if (nbt.getBoolean(TAG_IS_DEBUG_STICK)) {

      IBlockState blockstate = world.getBlockState(pos);
      IProperty selected = getSelectedProperty(blockstate, nbt, player);

      if (selected == null) {
        player.sendStatusMessage(new TextComponentString(TextFormatting.WHITE + "This block has no properites"), true);
      } else {

        blockstate = blockstate.withProperty(selected,
            cycle(selected.getAllowedValues(), blockstate.getValue(selected), player.isSneaking() ? -1 : 1));

        player.sendStatusMessage(new TextComponentString(TextFormatting.WHITE + "'" + selected.getName() + "' is now '"
            + blockstate.getValue(selected).toString() + "'"), true);
        world.setBlockState(pos, blockstate, 2);
      }

    }
    return EnumActionResult.SUCCESS;

  }

  // called when left-clicking a block
  @Override
  public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {

    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null) {
      NBTTagCompound empty = new NBTTagCompound();
      stack.setTagCompound(empty);

    }

    if (nbt.getBoolean(TAG_IS_DEBUG_STICK)) {
      if (world.isRemote)
        return false;

      IBlockState blockstate = world.getBlockState(pos);
      IProperty selected = getSelectedProperty(blockstate, nbt, player);

      if (selected == null) {
        player.sendStatusMessage(new TextComponentString(TextFormatting.WHITE + "This block has no properites"), true);

      } else {

        selected = cycle(blockstate.getBlock().getBlockState().getProperties(), selected, player.isSneaking() ? -1 : 1);
        nbt.setString(TAG_SELECTED, selected.getName());
        player.sendStatusMessage(
            new TextComponentString(TextFormatting.WHITE + "Selected property '" + selected.getName() + "'"), true);

      }
      return false;
    }

    return true;
  }

  // returns the property selected in the TAG_SELECTED NBT field, or the
  // first property or null if there are no properties
  @Nullable
  private static IProperty<?> getSelectedProperty(IBlockState blockstate, NBTTagCompound nbt, EntityPlayer player) {
    if (blockstate.getBlock().getBlockState().getProperties().isEmpty()) {

      return null;
    } else if (!nbt.hasKey(TAG_SELECTED)) {

      return blockstate.getBlock().getBlockState().getProperties().iterator().next();
    } else {

      String name = nbt.getString(TAG_SELECTED);
      ArrayList<IProperty<?>> properties = new ArrayList<>(blockstate.getBlock().getBlockState().getProperties());
      for (IProperty<?> property : properties) {
        if (property.getName().equals(name)) {

          return property;
        }
      }

      return properties.get(0);
    }

  }

  // go to the next or previous elemnt depending on the movement
  // not and eventually wrap around.
  @Nonnull
  private static <T> T cycle(Collection<T> possibleValues, T current, int move) {
    ArrayList<T> elements = new ArrayList<>(possibleValues);
    int index = elements.indexOf(current);
    int maxIndex = elements.size();

    int newIndex = (maxIndex + index + move % maxIndex) % maxIndex;
    return elements.get(newIndex);
  }

}
