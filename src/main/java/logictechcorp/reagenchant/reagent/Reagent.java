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

package logictechcorp.reagenchant.reagent;

import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class Reagent
{
    protected final Item item;
    protected final Map<ResourceLocation, ReagentEnchantmentData> enchantments = new HashMap<>();

    public Reagent(Item item)
    {
        if(item != null)
        {
            this.item = item;
        }
        else
        {
            this.item = Items.AIR;
        }
    }

    public void addEnchantment(ReagentEnchantmentData reagentEnchantmentData)
    {
        this.enchantments.put(reagentEnchantmentData.getEnchantment().getRegistryName(), reagentEnchantmentData);
    }

    public void removeEnchantment(Enchantment enchantment)
    {
        this.enchantments.remove(enchantment.getRegistryName());
    }

    public List<EnchantmentData> createEnchantmentList(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantabilityLevel, Random random)
    {
        int enchantability = unenchantedStack.getItem().getItemEnchantability(unenchantedStack);

        if(enchantability <= 0)
        {
            return new ArrayList<>();
        }
        else
        {
            enchantabilityLevel = enchantabilityLevel + 1 + random.nextInt(enchantability / 4 + 1) + random.nextInt(enchantability / 4 + 1);
            float enchantmentMultiplier = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            enchantabilityLevel = MathHelper.clamp(Math.round((float) enchantabilityLevel + (float) enchantabilityLevel * enchantmentMultiplier), 1, Integer.MAX_VALUE);

            List<EnchantmentData> aggregateEnchantmentData = new ArrayList<>();
            List<Enchantment> applicableEnchantments = this.getApplicableEnchantments(world, pos, player, unenchantedStack, reagentStack, random);
            Collections.shuffle(applicableEnchantments, random);

            for(Enchantment enchantment : applicableEnchantments)
            {
                ReagentEnchantmentData reagentEnchantmentData = this.getReagentEnchantmentData(enchantment);
                int minimumEnchantmentLevel = reagentEnchantmentData.getMinimumEnchantmentLevel();
                int maximumEnchantmentLevel = reagentEnchantmentData.getMaximumEnchantmentLevel();
                int enchantmentLevel;

                if(minimumEnchantmentLevel == enchantment.getMinLevel() && maximumEnchantmentLevel == enchantment.getMaxLevel())
                {
                    for(enchantmentLevel = maximumEnchantmentLevel; enchantmentLevel > minimumEnchantmentLevel - 1; enchantmentLevel--)
                    {
                        if(enchantabilityLevel >= enchantment.getMinEnchantability(enchantmentLevel))
                        {
                            break;
                        }
                    }
                }
                else
                {
                    int oneThird = (maximumEnchantmentLevel - minimumEnchantmentLevel) / 3;

                    if(enchantmentTier == 0)
                    {
                        maximumEnchantmentLevel = (minimumEnchantmentLevel + oneThird);
                    }
                    else if(enchantmentTier == 1)
                    {
                        minimumEnchantmentLevel += oneThird;
                        maximumEnchantmentLevel -= oneThird;
                    }
                    else
                    {
                        minimumEnchantmentLevel = (maximumEnchantmentLevel - oneThird);
                    }

                    enchantmentLevel = RandomHelper.getNumberInRange(minimumEnchantmentLevel, maximumEnchantmentLevel, random);
                }

                if(enchantmentLevel > 0)
                {
                    EnchantmentData enchantmentData = new EnchantmentData(enchantment, enchantmentLevel);

                    if(this.getEnchantmentProbability(world, pos, player, unenchantedStack, reagentStack, enchantmentData, random) >= random.nextDouble())
                    {
                        aggregateEnchantmentData.add(enchantmentData);
                    }
                }
            }

            if(aggregateEnchantmentData.isEmpty())
            {
                return EnchantmentHelper.buildEnchantmentList(random, unenchantedStack, enchantabilityLevel, false);
            }

            List<EnchantmentData> refinedEnchantmentData = new ArrayList<>();
            refinedEnchantmentData.add(WeightedRandom.getRandomItem(random, aggregateEnchantmentData));
            aggregateEnchantmentData.addAll(EnchantmentHelper.getEnchantmentDatas(enchantabilityLevel, unenchantedStack, false));

            while(random.nextInt(50) <= enchantabilityLevel)
            {
                EnchantmentHelper.removeIncompatible(aggregateEnchantmentData, refinedEnchantmentData.get(0));

                if(aggregateEnchantmentData.isEmpty())
                {
                    break;
                }

                refinedEnchantmentData.add(WeightedRandom.getRandomItem(random, aggregateEnchantmentData));
                enchantabilityLevel /= 2;
            }

            return refinedEnchantmentData;
        }
    }

    public boolean hasApplicableEnchantments(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, Random random)
    {
        for(Enchantment enchantment : this.getEnchantments())
        {
            if(enchantment.canApplyAtEnchantingTable(unenchantedStack) || (unenchantedStack.getItem() == Items.BOOK && enchantment.isAllowedOnBooks()))
            {
                return true;
            }
        }

        return false;
    }

    public boolean consumeReagent(World world, BlockPos pos, PlayerEntity player, ItemStack enchantedStack, ItemStack reagentStack, List<EnchantmentData> enchantmentList, Random random)
    {
        List<Enchantment> applicableEnchantments = this.getApplicableEnchantments(world, pos, player, enchantedStack, reagentStack, random);

        for(EnchantmentData enchantmentData : enchantmentList)
        {
            if(applicableEnchantments.contains(enchantmentData.enchantment))
            {
                return true;
            }
        }

        return false;
    }

    public Item getItem()
    {
        return this.item;
    }

    public List<Enchantment> getEnchantments()
    {
        List<Enchantment> associatedEnchantments = new ArrayList<>();

        for(ResourceLocation registryName : this.enchantments.keySet())
        {
            associatedEnchantments.add(ForgeRegistries.ENCHANTMENTS.getValue(registryName));
        }

        return associatedEnchantments;
    }

    public List<Enchantment> getApplicableEnchantments(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, Random random)
    {
        List<Enchantment> enchantments = new ArrayList<>();

        for(Enchantment enchantment : this.getEnchantments())
        {
            if(enchantment.canApplyAtEnchantingTable(unenchantedStack) || (unenchantedStack.getItem() == Items.BOOK && enchantment.isAllowedOnBooks()))
            {
                enchantments.add(enchantment);
            }
        }

        return enchantments;
    }

    public ReagentEnchantmentData getReagentEnchantmentData(Enchantment enchantment)
    {
        return this.enchantments.get(enchantment.getRegistryName());
    }

    public double getEnchantmentProbability(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        Enchantment enchantment = enchantmentData.enchantment;

        if(this.enchantments.containsKey(enchantment.getRegistryName()))
        {
            return this.getReagentEnchantmentData(enchantment).getEnchantmentProbability();
        }

        return 0.5D;
    }

    public int getCost(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        Enchantment enchantment = enchantmentData.enchantment;

        if(this.enchantments.containsKey(enchantment.getRegistryName()))
        {
            return this.getReagentEnchantmentData(enchantment).getReagentCost();
        }

        return 1;
    }
}
