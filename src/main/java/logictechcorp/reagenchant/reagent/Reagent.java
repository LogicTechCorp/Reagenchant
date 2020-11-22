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

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.json.JsonFormat;
import logictechcorp.libraryex.utility.RandomHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;

import java.util.*;

/**
 * The base class for a Reagent.
 */
public class Reagent
{
    public static final Reagent EMPTY = new Reagent(Items.AIR);

    protected final Item item;
    protected final Map<Enchantment, ReagentEnchantData> enchantments;

    public Reagent(Item item)
    {
        this.item = item;
        this.enchantments = new HashMap<>();
    }

    public void addEnchantment(ReagentEnchantData reagentEnchantData)
    {
        this.enchantments.put(reagentEnchantData.getEnchantment(), reagentEnchantData);
    }

    public void removeEnchantment(Enchantment enchantment)
    {
        this.enchantments.remove(enchantment);
    }

    public void readFromConfig(Config config)
    {
        this.enchantments.clear();
        List<Config> enchantmentConfigs = config.getOrElse("enchantments", new ArrayList<>());

        for(Config enchantmentConfig : enchantmentConfigs)
        {
            this.addEnchantment(ReagentEnchantData.deserialize(enchantmentConfig));
        }
    }

    public void writeToConfig(Config config)
    {
        List<Config> enchantmentConfigs = new ArrayList<>();
        config.set("item", this.item.getRegistryName().toString());

        for(Map.Entry<Enchantment, ReagentEnchantData> entry : this.enchantments.entrySet())
        {
            ReagentEnchantData reagenchantmentData = entry.getValue();

            Config enchantmentConfig = JsonFormat.newConfig(LinkedHashMap::new);
            enchantmentConfig.add("enchantment", entry.getKey().getRegistryName().toString());
            enchantmentConfig.add("probability", reagenchantmentData.getEnchantmentProbability());
            enchantmentConfig.add("reagentCost", reagenchantmentData.getReagentCost());
            enchantmentConfig.add("minimumEnchantmentLevel", reagenchantmentData.getMinimumEnchantmentLevel());
            enchantmentConfig.add("maximumEnchantmentLevel", reagenchantmentData.getMaximumEnchantmentLevel());
            enchantmentConfigs.add(enchantmentConfig);
        }

        config.set("enchantments", enchantmentConfigs);
    }

    public List<EnchantmentData> compileEnchantmentList(ItemStack unenchantedStack, int enchantmentIndex, int enchantabilityLevel, Random random)
    {
        int itemEnchantability = unenchantedStack.getItem().getItemEnchantability(unenchantedStack);

        if(itemEnchantability <= 0)
        {
            return new ArrayList<>();
        }
        else
        {
            float enchantmentMultiplier = ((random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F);
            enchantabilityLevel = (enchantabilityLevel + 1 + random.nextInt((itemEnchantability / 4) + 1) + random.nextInt((itemEnchantability / 4) + 1));
            enchantabilityLevel = MathHelper.clamp(Math.round(enchantabilityLevel + (enchantmentMultiplier * enchantabilityLevel)), 1, Integer.MAX_VALUE);

            List<EnchantmentData> defaultEnchantments = new ArrayList<>();

            for(Enchantment enchantment : this.getApplicableEnchantments(unenchantedStack))
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

                    if(enchantmentIndex == 0)
                    {
                        maximumEnchantmentLevel = (minimumEnchantmentLevel + oneThird);
                    }
                    else if(enchantmentIndex == 1)
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
                    if(this.getEnchantmentProbability(enchantment) >= random.nextDouble())
                    {
                        defaultEnchantments.add(new EnchantmentData(enchantment, enchantmentLevel));
                    }
                }
            }

            List<EnchantmentData> refinedEnchantments = new ArrayList<>();

            while(!defaultEnchantments.isEmpty())
            {
                EnchantmentData removedEnchantment = WeightedRandom.getRandomItem(random, defaultEnchantments);
                refinedEnchantments.add(defaultEnchantments.remove(defaultEnchantments.indexOf(removedEnchantment)));
                EnchantmentHelper.removeIncompatible(defaultEnchantments, removedEnchantment);
            }

            return refinedEnchantments;
        }
    }

    public boolean canApplyEnchantments(ItemStack unenchantedStack)
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

    public boolean consumeReagent(ItemStack unenchantedStack, List<EnchantmentData> enchantments)
    {
        List<Enchantment> applicableEnchantments = this.getApplicableEnchantments(unenchantedStack);

        for(EnchantmentData enchantmentData : enchantments)
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
        return this == EMPTY || this.item == Items.AIR || this.item == null;
    }

    public Item getItem()
    {
        return this.item;
    }

    public Set<Enchantment> getEnchantments()
    {
        return Collections.unmodifiableSet(this.enchantments.keySet());
    }

    public ReagentEnchantData getReagentEnchantData(Enchantment enchantment)
    {
        return this.enchantments.getOrDefault(enchantment, ReagentEnchantData.EMPTY);
    }

    public List<Enchantment> getApplicableEnchantments(ItemStack unenchantedStack)
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

    public double getEnchantmentProbability(Enchantment enchantment)
    {
        return this.getReagentEnchantData(enchantment).getEnchantmentProbability();
    }

    public int getCost(Enchantment enchantment)
    {
        return this.getReagentEnchantData(enchantment).getReagentCost();
    }
}
