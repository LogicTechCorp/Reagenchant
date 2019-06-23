/*
 * Reagenchant
 * Copyright (c) 2019 by LogicTechCorp
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

package logictechcorp.reagenchant.handler;

import logictechcorp.reagenchant.Reagenchant;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class UnbreakingHandler
{
    private static final ResourceLocation BROKEN_PROPERTY_KEY = Reagenchant.getResource("broken");
    private static final IItemPropertyGetter BROKEN_PROPERTY = new IItemPropertyGetter()
    {
        @Override
        @SideOnly(Side.CLIENT)
        public float apply(ItemStack stack, World world, EntityLivingBase entity)
        {
            return UnbreakingHandler.isItemBroken(stack) ? 1.0F : 0.0F;
        }
    };

    @SubscribeEvent
    public static void onPlayerBreakSpeed(BreakSpeed event)
    {
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setNewSpeed(0.5F);
        }
    }

    @SubscribeEvent
    public static void onPlayerHarvestCheck(HarvestCheck event)
    {
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanHarvest(false);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BreakEvent event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        Block block = event.getState().getBlock();
        ItemStack stack = event.getPlayer().getHeldItemMainhand();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            if(block instanceof IShearable)
            {
                IShearable shearable = (IShearable) block;

                if(shearable.isShearable(stack, world, pos))
                {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }

            event.setExpToDrop(0);
        }
    }

    @SubscribeEvent
    public static void onHarvestDrops(HarvestDropsEvent event)
    {
        EntityPlayer player = event.getHarvester();

        if(player != null)
        {
            ItemStack stack = event.getHarvester().getHeldItemMainhand();

            if(UnbreakingHandler.isItemBroken(stack))
            {
                event.getDrops().clear();
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event)
    {
        ItemStack stack = event.getItemStack();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(RightClickItem event)
    {
        ItemStack stack = event.getItemStack();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(EntityInteractSpecific event)
    {
        ItemStack stack = event.getItemStack();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(EntityInteract event)
    {
        ItemStack stack = event.getItemStack();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();

        if(attacker instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) attacker;
            ItemStack stack = player.getHeldItemMainhand();

            if(UnbreakingHandler.isItemBroken(stack))
            {
                event.setAmount(1.0F);
            }
        }
    }

    private static boolean isItemBroken(ItemStack stack)
    {
        int usesRemaining = (stack.getMaxDamage() - stack.getItemDamage());

        if(usesRemaining == 0)
        {
            stack.setItemDamage(stack.getMaxDamage() - 1);
        }

        return !stack.isEmpty() && !EnchantmentHelper.hasBindingCurse(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0 && usesRemaining <= 1;
    }

    public static void addBrokenPropertyToItems()
    {
        for(Item item : ForgeRegistries.ITEMS)
        {
            item.addPropertyOverride(BROKEN_PROPERTY_KEY, BROKEN_PROPERTY);
        }
    }
}
