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

package logictechcorp.reagenchant.reagent;

import logictechcorp.reagenchant.api.reagent.IReagent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.*;

public class Reagent implements IReagent
{
    private final Item associatedItem;
    private final ResourceLocation name;
    private final Map<Integer, Tuple<Float, Integer>> enchantments = new HashMap<>();

    public Reagent(Item associatedItem, ResourceLocation name)
    {
        this.associatedItem = associatedItem;
        this.name = name;
    }

    @Override
    public void addEnchantment(Enchantment enchantment, float probability, int reagentCost)
    {
        this.enchantments.put(Enchantment.getEnchantmentID(enchantment), new Tuple<>(probability, reagentCost));
    }

    @Override
    public List<EnchantmentData> createEnchantmentList(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantmentLevel, boolean allowTreasure, Random random)
    {
        List<EnchantmentData> refinedEnchantmentData = new ArrayList<>();
        int enchantability = unenchantedStack.getItem().getItemEnchantability(unenchantedStack);

        if(enchantability <= 0)
        {
            return refinedEnchantmentData;
        }
        else
        {
            enchantmentLevel = enchantmentLevel + 1 + random.nextInt(enchantability / 4 + 1) + random.nextInt(enchantability / 4 + 1);
            float enchantmentMultiplier = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            enchantmentLevel = MathHelper.clamp(Math.round((float) enchantmentLevel + (float) enchantmentLevel * enchantmentMultiplier), 1, Integer.MAX_VALUE);

            List<EnchantmentData> aggregateEnchantmentData = new ArrayList<>();

            for(Enchantment enchantment : this.getApplicableEnchantments(world, pos, player, unenchantedStack, reagentStack, random))
            {
                if((!enchantment.isTreasureEnchantment() || allowTreasure) && (enchantment.canApplyAtEnchantingTable(unenchantedStack) || (unenchantedStack.getItem() == Items.BOOK && enchantment.isAllowedOnBooks())))
                {
                    for(int maxEnchantmentLevel = enchantment.getMaxLevel(); maxEnchantmentLevel > enchantment.getMinLevel() - 1; maxEnchantmentLevel--)
                    {
                        if(enchantmentLevel >= enchantment.getMinEnchantability(maxEnchantmentLevel) && enchantmentLevel <= enchantment.getMaxEnchantability(maxEnchantmentLevel))
                        {
                            EnchantmentData enchantmentData = new EnchantmentData(enchantment, maxEnchantmentLevel);

                            float probability = this.getEnchantmentProbability(world, pos, player, unenchantedStack, reagentStack, enchantmentData, random);
                            float randFloat = random.nextFloat();

                            if(probability < randFloat)
                            {
                                aggregateEnchantmentData.add(enchantmentData);
                            }

                            break;
                        }
                    }
                }

            }

            if(!aggregateEnchantmentData.isEmpty())
            {
                refinedEnchantmentData.add(WeightedRandom.getRandomItem(random, aggregateEnchantmentData));

                while(random.nextInt(50) <= enchantmentLevel)
                {
                    EnchantmentHelper.removeIncompatible(aggregateEnchantmentData, Util.getLastElement(refinedEnchantmentData));

                    if(aggregateEnchantmentData.isEmpty())
                    {
                        break;
                    }

                    refinedEnchantmentData.add(WeightedRandom.getRandomItem(random, aggregateEnchantmentData));
                    enchantmentLevel /= 2;
                }
            }

            return refinedEnchantmentData;
        }
    }

    @Override
    public boolean hasApplicableEnchantments(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, Random random)
    {
        for(Enchantment enchantment : this.getAssociatedEnchantments())
        {
            if(enchantment.canApplyAtEnchantingTable(unenchantedStack))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean consumeReagent(World world, BlockPos pos, EntityPlayer player, ItemStack enchantedStack, ItemStack reagentStack, List<EnchantmentData> enchantmentData, Random random)
    {
        return this.hasApplicableEnchantments(world, pos, player, enchantedStack, reagentStack, random);
    }

    @Override
    public Item getAssociatedItem()
    {
        return this.associatedItem;
    }

    @Override
    public ResourceLocation getName()
    {
        return this.name;
    }

    @Override
    public List<Enchantment> getAssociatedEnchantments()
    {
        List<Enchantment> associatedEnchantments = new ArrayList<>();

        for(Integer id : this.enchantments.keySet())
        {
            associatedEnchantments.add(Enchantment.getEnchantmentByID(id));
        }

        return associatedEnchantments;
    }

    @Override
    public List<Enchantment> getApplicableEnchantments(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, Random random)
    {
        List<Enchantment> enchantments = new ArrayList<>();

        for(Enchantment enchantment : this.getAssociatedEnchantments())
        {
            if(enchantment.canApplyAtEnchantingTable(unenchantedStack))
            {
                enchantments.add(enchantment);
            }
        }

        return enchantments;
    }

    @Override
    public float getBaseEnchantmentProbability(Enchantment enchantment)
    {
        Tuple<Float, Integer> data = this.enchantments.get(Enchantment.getEnchantmentID(enchantment));
        return data == null ? 0.0F : data.getFirst();
    }

    @Override
    public float getEnchantmentProbability(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        return this.getBaseEnchantmentProbability(enchantmentData.enchantment);
    }

    @Override
    public int getBaseReagentCost(Enchantment enchantment)
    {
        Tuple<Float, Integer> data = this.enchantments.get(Enchantment.getEnchantmentID(enchantment));
        return data == null ? 1 : data.getSecond();
    }

    @Override
    public int getReagentCost(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        return this.getBaseReagentCost(enchantmentData.enchantment);
    }
}
