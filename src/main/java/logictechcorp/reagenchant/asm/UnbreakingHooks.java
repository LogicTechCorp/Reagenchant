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

package logictechcorp.reagenchant.asm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import logictechcorp.reagenchant.ReagenchantConfig;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class UnbreakingHooks
{
    public static boolean attemptDamageItem(ItemStack stack, int amount, Random random, ServerPlayerEntity damager)
    {
        if(!stack.isDamageable())
        {
            return false;
        }
        else
        {
            if(amount > 0)
            {
                int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
                int negatedAmount = 0;

                for(int i = 0; enchantmentLevel > 0 && i < amount; i++)
                {
                    if(negateDamage(stack, enchantmentLevel, random))
                    {
                        negatedAmount++;
                    }
                }

                amount -= negatedAmount;

                if(amount <= 0)
                {
                    return false;
                }
            }

            if(damager != null && amount != 0)
            {
                CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(damager, stack, stack.getDamage() + amount);
            }

            return setDamage(stack, stack.getDamage() + amount) >= stack.getMaxDamage();
        }
    }

    public static boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entityLiving)
    {
        if(ReagenchantConfig.ENCHANTMENT.unbreakingPreventsItemDestruction.get() && UnbreakingHandler.isItemBroken(stack))
        {
            return false;
        }
        else
        {
            return stack.getItem().onBlockDestroyed(stack, world, state, pos, entityLiving);
        }
    }

    private static boolean negateDamage(ItemStack stack, int level, Random rand)
    {
        if(ReagenchantConfig.ENCHANTMENT.unbreakingPreventsItemDestruction.get() && UnbreakingHandler.isItemBroken(stack))
        {
            return true;
        }
        if(stack.getItem() instanceof ArmorItem && rand.nextFloat() < 0.6F)
        {
            return false;
        }
        else
        {
            return rand.nextInt(level + 1) > 0;
        }
    }

    public static int getHarvestLevel(ItemStack stack, int harvestLevel)
    {
        if(ReagenchantConfig.ENCHANTMENT.unbreakingPreventsItemDestruction.get() && UnbreakingHandler.isItemBroken(stack))
        {
            return -1;
        }
        else
        {
            return harvestLevel;
        }
    }

    public static Multimap<String, AttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlotType slot)
    {
        Item item = stack.getItem();

        if(ReagenchantConfig.ENCHANTMENT.unbreakingPreventsItemDestruction.get() && UnbreakingHandler.isItemBroken(stack))
        {
            return HashMultimap.create();
        }
        else
        {
            return item.getAttributeModifiers(slot, stack);
        }
    }

    private static int setDamage(ItemStack stack, int amount)
    {
        if(ReagenchantConfig.ENCHANTMENT.unbreakingPreventsItemDestruction.get() && UnbreakingHandler.canItemBeBroken(stack, amount))
        {
            amount = stack.getMaxDamage() - 1;
            UnbreakingHandler.breakItem(null, stack, null);
        }

        stack.getItem().setDamage(stack, amount);
        return amount;
    }
}
