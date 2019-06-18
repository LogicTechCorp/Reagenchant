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

import logictechcorp.libraryex.utility.NBTHelper;
import logictechcorp.reagenchant.Reagenchant;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Reagenchant.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class UnbreakingHandler
{
    @SubscribeEvent
    public static void onPlayerBreakSpeed(BreakSpeed event)
    {
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();

        if(!stack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0 && stack.getDamage() == stack.getMaxDamage())
        {
            event.setNewSpeed(0.5F);
            setItemUnbreakable(stack);
        }

    }

    @SubscribeEvent
    public static void onPlayerHarvestCheck(HarvestCheck event)
    {
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();

        if(!stack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0 && stack.getDamage() == stack.getMaxDamage())
        {
            event.setCanHarvest(false);
            setItemUnbreakable(stack);
        }

    }

    @SubscribeEvent
    public static void onBlockBreak(BreakEvent event)
    {
        ItemStack stack = event.getPlayer().getHeldItemMainhand();

        if(!stack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0 && stack.getDamage() == stack.getMaxDamage())
        {
            event.setExpToDrop(0);
            setItemUnbreakable(stack);
        }

    }

    @SubscribeEvent
    public static void onHarvestDrops(HarvestDropsEvent event)
    {
        ItemStack stack = event.getHarvester().getHeldItemMainhand();

        if(!stack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0 && stack.getDamage() == stack.getMaxDamage())
        {
            event.getDrops().clear();
            setItemUnbreakable(stack);
        }

    }

    @SubscribeEvent
    public static void onPlayerRightClick(RightClickBlock event)
    {
        ItemStack stack = event.getItemStack();

        if(!stack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0 && stack.getDamage() == stack.getMaxDamage())
        {
            event.setCanceled(true);
            setItemUnbreakable(stack);
        }

    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();

        if(attacker instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) attacker;
            ItemStack stack = player.getHeldItemMainhand();

            if(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0 && stack.getDamage() == stack.getMaxDamage())
            {
                event.setAmount(1.0F);
                setItemUnbreakable(stack);
            }
        }

    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event)
    {
        ItemStack inputStack = event.getLeft();
        ItemStack ingredientStack = event.getRight();
        if(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, inputStack) > 0)
        {
            NBTHelper.ensureTagExists(inputStack);

            if(inputStack.getTag().contains("Unbreakable"))
            {
                inputStack.getTag().remove("Unbreakable");
            }
        }

        if(EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, ingredientStack) > 0)
        {
            NBTHelper.ensureTagExists(ingredientStack);

            if(ingredientStack.getTag().contains("Unbreakable"))
            {
                ingredientStack.getTag().remove("Unbreakable");
            }
        }

    }

    private static void setItemUnbreakable(ItemStack stack)
    {
        NBTHelper.ensureTagExists(stack);

        if(!stack.getTag().getBoolean("Unbreakable"))
        {
            stack.getTag().putBoolean("Unbreakable", true);
        }

    }
}