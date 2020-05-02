package com.tfar.debugstick;

import com.google.common.collect.ImmutableList;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.commands.CommandUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.tfar.debugstick.DebugStick.ITEM_ID;

@Mod.EventBusSubscriber(modid = DebugStick.MODID)
public class ItemDebugStick extends Item {

  public ItemDebugStick(String name) {
    setRegistryName(new ResourceLocation("minecraft"/* DebugStick.MODID */, name));
    setTranslationKey(ITEM_ID);
    setCreativeTab(CreativeTabs.MISC);
  }

  // called when right clicking a block

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
    if (nbt.getBoolean("isDebugStick")) {

      //fetches all possible block states of the targetted block
      ImmutableList<IBlockState> states = world.getBlockState(pos).getBlock().getBlockState().getValidStates();
      int index = states.indexOf(world.getBlockState(pos));
      int maxIndex = states.size();
      //go to the next or previous block state depending if the player is sneaking or not and eventually wrap around.
      int newIndex = (maxIndex + index + (player.isSneaking() ? -1 : 1) % maxIndex) % maxIndex;
      IBlockState newState = states.get(newIndex);
      //apply the new block state without triggering block updates
      world.setBlockState(pos, newState, 2);
      player.sendStatusMessage(new TextComponentString(TextFormatting.WHITE + newState.toString()), true);     
    }
    return EnumActionResult.SUCCESS;
  
  }

}
