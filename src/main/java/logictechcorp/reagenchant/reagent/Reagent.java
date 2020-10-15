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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class Reagent
{
    public static final Reagent EMPTY = new Reagent(Items.AIR);

    protected final Item item;
    protected final Map<Enchantment, ReagentEnchantData> enchantments = new HashMap<>();

    public Reagent(Item item)
    {
        this.item = item;
    }

    public void addEnchantment(ReagentEnchantData reagentEnchantData)
    {
        if(!this.isEmpty())
        {
            this.enchantments.put(reagentEnchantData.getEnchantment(), reagentEnchantData);
        }
    }

    public void removeEnchantment(Enchantment enchantment)
    {
        if(!this.isEmpty())
        {
            this.enchantments.remove(enchantment);
        }
    }

    public List<EnchantmentData> createEnchantmentList(ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantabilityLevel, Random random)
    {
        int itemEnchantability = unenchantedStack.getItemEnchantability();

        if(itemEnchantability <= 0)
        {
            return new ArrayList<>();
        }
        else
        {
            float enchantmentMultiplier = ((random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F);
            enchantabilityLevel = (enchantabilityLevel + 1 + random.nextInt((itemEnchantability / 4) + 1) + random.nextInt((itemEnchantability / 4) + 1));
            enchantabilityLevel = MathHelper.clamp(Math.round(enchantabilityLevel + (enchantmentMultiplier * enchantabilityLevel)), 1, Integer.MAX_VALUE);

            List<EnchantmentData> aggregateEnchantmentData = new ArrayList<>();

            for(Enchantment enchantment : this.getApplicableEnchantments(unenchantedStack, reagentStack, random))
            {
                ReagentEnchantData reagentEnchantData = this.getReagentEnchantData(enchantment);
                int minimumEnchantmentLevel = reagentEnchantData.getMinimumEnchantmentLevel();
                int maximumEnchantmentLevel = reagentEnchantData.getMaximumEnchantmentLevel();
                int enchantmentLevel;

                if(minimumEnchantmentLevel == enchantment.getMinLevel() && maximumEnchantmentLevel == enchantment.getMaxLevel())
                {
                    for(enchantmentLevel = maximumEnchantmentLevel; enchantmentLevel > (minimumEnchantmentLevel - 1); enchantmentLevel--)
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

                    if(this.getEnchantmentProbability(unenchantedStack, reagentStack, enchantmentData, random) >= random.nextFloat())
                    {
                        aggregateEnchantmentData.add(enchantmentData);
                    }
                }
            }

            List<EnchantmentData> refinedEnchantmentData = new ArrayList<>();

            if(!aggregateEnchantmentData.isEmpty())
            {
                refinedEnchantmentData.add(WeightedRandom.getRandomItem(random, aggregateEnchantmentData));

                while(random.nextInt(50) <= enchantabilityLevel)
                {
                    EnchantmentHelper.removeIncompatible(aggregateEnchantmentData, Util.getLast(refinedEnchantmentData));

                    if(aggregateEnchantmentData.isEmpty())
                    {
                        break;
                    }

                    refinedEnchantmentData.add(WeightedRandom.getRandomItem(random, aggregateEnchantmentData));
                    enchantabilityLevel /= 2;
                }
            }

            return refinedEnchantmentData;
        }
    }

    public boolean hasApplicableEnchantments(ItemStack unenchantedStack, ItemStack reagentStack, Random random)
    {
        for(Enchantment enchantment : this.enchantments.keySet())
        {
            if(enchantment.canApplyAtEnchantingTable(unenchantedStack) || (unenchantedStack.getItem() == Items.BOOK && enchantment.isAllowedOnBooks()))
            {
                return true;
            }
        }

        return false;
    }

    public boolean consumeReagent(ItemStack unenchantedStack, ItemStack enchantedStack, ItemStack reagentStack, List<EnchantmentData> enchantmentList, Random random)
    {
        List<Enchantment> applicableEnchantments = this.getApplicableEnchantments(unenchantedStack, reagentStack, random);

        for(EnchantmentData enchantmentData : enchantmentList)
        {
            if(applicableEnchantments.contains(enchantmentData.enchantment))
            {
                return true;
            }
        }

        return false;
    }

    public boolean containsEnchantment(Enchantment enchantment)
    {
        return this.enchantments.containsKey(enchantment);
    }

    public boolean isEmpty()
    {
        return this == EMPTY || this.item == null;
    }

    public Item getItem()
    {
        return this.isEmpty() ? Items.AIR : this.item;
    }

    public Set<Enchantment> getEnchantments()
    {
        return Collections.unmodifiableSet(this.enchantments.keySet());
    }

    public ReagentEnchantData getReagentEnchantData(Enchantment enchantment)
    {
        return this.enchantments.getOrDefault(enchantment, ReagentEnchantData.EMPTY);
    }

    public List<Enchantment> getApplicableEnchantments(ItemStack unenchantedStack, ItemStack reagentStack, Random random)
    {
        List<Enchantment> enchantments = new ArrayList<>();

        for(Enchantment enchantment : this.enchantments.keySet())
        {
            if(enchantment.canApplyAtEnchantingTable(unenchantedStack) || (unenchantedStack.getItem() == Items.BOOK && enchantment.isAllowedOnBooks()))
            {
                enchantments.add(enchantment);
            }
        }

        return enchantments;
    }

    public float getEnchantmentProbability(ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        Enchantment enchantment = enchantmentData.enchantment;

        if(this.enchantments.containsKey(enchantment))
        {
            return this.enchantments.get(enchantment).getEnchantmentProbability();
        }

        return 0.0F;
    }

    public int getCost(ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        Enchantment enchantment = enchantmentData.enchantment;

        if(this.enchantments.containsKey(enchantment))
        {
            return this.enchantments.get(enchantment).getReagentCost();
        }

        return 0;
    }
}
