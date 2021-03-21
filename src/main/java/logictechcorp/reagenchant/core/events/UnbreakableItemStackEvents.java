/*
 * Reagenchant
 * Copyright (c) 2019-2021 by LogicTechCorp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package logictechcorp.reagenchant.core.events;

import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.util.item.UnbreakableItemStackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class UnbreakableItemStackEvents {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        List<ITextComponent> tooltips = event.getToolTip();
        CompoundNBT compound = stack.getTag();

        if(compound != null && compound.contains(UnbreakableItemStackUtil.DISABLED_ENCHANTMENTS_KEY)) {
            for(int tooltipIndex = 0; tooltipIndex < tooltips.size(); tooltipIndex++) {
                ITextComponent tooltip = tooltips.get(tooltipIndex);

                if(tooltip.getString().isEmpty()) {
                    ListNBT disabledEnchantments = compound.getList(UnbreakableItemStackUtil.DISABLED_ENCHANTMENTS_KEY, 10);

                    int enchantmentCount = disabledEnchantments.size();

                    for(int tagIndex = 0; tagIndex < enchantmentCount; tagIndex++) {
                        CompoundNBT enchantmentCompound = disabledEnchantments.getCompound(tagIndex);
                        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantmentCompound.getString("id")));
                        int index = ((tooltipIndex + tagIndex));

                        if(enchantment != null) {
                            tooltips.add(index, ((IFormattableTextComponent) enchantment.getDisplayName(enchantmentCompound.getInt("lvl"))).mergeStyle(TextFormatting.GOLD));
                        }

                        if((tagIndex + 1) == enchantmentCount) {
                            tooltips.add((index + 1), new TranslationTextComponent("tooltip." + Reagenchant.MOD_ID + ":item.broken").mergeStyle(TextFormatting.GOLD));
                        }
                    }

                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntityEvent(AttackEntityEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack stack = player.getHeldItemMainhand();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerHarvestCheck(PlayerEvent.HarvestCheck event) {
        PlayerEntity player = event.getPlayer();
        BlockState state = event.getTargetBlock();
        ItemStack stack = player.getHeldItemMainhand();

        if(UnbreakableItemStackUtil.isBroken(stack) && state.getRequiresTool()) {
            event.setCanHarvest(false);
        }
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        ItemStack stack = player.getHeldItemMainhand();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            event.setNewSpeed(0.5F);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        World world = (World) event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        PlayerEntity player = event.getPlayer();
        Block block = state.getBlock();
        ItemStack stack = player.getHeldItemMainhand();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            if(block instanceof IForgeShearable) {
                if(((IForgeShearable) block).isShearable(stack, world, pos)) {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }

            if(state.getRequiresTool()) {
                event.setExpToDrop(0);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockToolInteractEvent(BlockEvent.BlockToolInteractEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack stack = player.getHeldItemMainhand();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        ItemStack stack = event.getItemStack();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack stack = event.getItemStack();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            event.clearModifiers();
        }
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack inputStack = event.getItemInput();
        ItemStack outputStack = event.getItemResult();

        if(UnbreakableItemStackUtil.isBroken(inputStack) && (outputStack.getDamage() < inputStack.getDamage())) {
            UnbreakableItemStackUtil.fixItem(outputStack);
        }
    }
}
