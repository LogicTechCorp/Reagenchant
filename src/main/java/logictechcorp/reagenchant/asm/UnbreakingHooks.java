/*
 * Reagenchant
 * Copyright (c) 2019-2020 by LogicTechCorp
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
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UnbreakingHooks
{
    public static int adjustSetDamage(ItemStack stack, int damage, LivingEntity livingEntity)
    {
        if(ReagenchantConfig.ENCHANTMENT.unbreakingPreventsItemDestruction.get() && UnbreakingHandler.canItemBeBroken(stack, damage))
        {
            if(damage >= stack.getMaxDamage())
            {
                damage = stack.getMaxDamage() - 1;
                UnbreakingHandler.breakItem(stack);

                if(livingEntity != null)
                {
                    UnbreakingHandler.sendBreakEffect(livingEntity, stack, EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.HAND, livingEntity.getActiveHand().ordinal()));
                }
            }
        }

        return damage;
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

        if(ReagenchantConfig.ENCHANTMENT.unbreakingPreventsItemDestruction.get() && UnbreakingHandler.isItemBroken(stack))
        {
            return HashMultimap.create();
        }
        else
        {
            return stack.getItem().getAttributeModifiers(slot, stack);
        }
    }
}
