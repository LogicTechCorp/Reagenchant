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
import logictechcorp.reagenchant.api.reagent.IReagent;
import logictechcorp.reagenchant.api.reagent.IReagentEnchantmentData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;

/**
 * The base class for a Reagent.
 */
public class Reagent implements IReagent
{
    protected final Item item;
    protected final Map<ResourceLocation, IReagentEnchantmentData> reagentEnchantmentData;

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

        this.reagentEnchantmentData = new HashMap<>();
    }

    public Reagent(ResourceLocation itemRegistryName)
    {
        this(ForgeRegistries.ITEMS.getValue(itemRegistryName));
    }

    @Override
    public void addReagentEnchantmentData(IReagentEnchantmentData reagentEnchantmentData)
    {
        this.reagentEnchantmentData.put(reagentEnchantmentData.getEnchantment().getRegistryName(), reagentEnchantmentData);
    }

    @Override
    public void removeReagentEnchantmentData(Enchantment enchantment)
    {
        this.reagentEnchantmentData.remove(enchantment.getRegistryName());
    }

    @Override
    public void readFromConfig(Config config)
    {
        this.reagentEnchantmentData.clear();
        List<Config> enchantmentConfigs = config.getOrElse("enchantments", new ArrayList<>());

        for(Config enchantmentConfig : enchantmentConfigs)
        {
            Enchantment enchantment = Enchantment.getEnchantmentByLocation(enchantmentConfig.getOrElse("enchantment", ""));

            if(enchantment != null)
            {
                double probability = enchantmentConfig.getOrElse("probability", 0.5D);
                int reagentCost = enchantmentConfig.getOrElse("reagentCost", 1);

                if(probability <= 0.0D)
                {
                    probability = 0.5D;
                }
                if(reagentCost < 0)
                {
                    reagentCost = 1;
                }

                int minimumEnchantmentLevel = enchantmentConfig.getOrElse("minimumEnchantmentLevel", enchantment.getMinLevel());
                int maximumEnchantmentLevel = enchantmentConfig.getOrElse("maximumEnchantmentLevel", enchantment.getMaxLevel());

                if(minimumEnchantmentLevel < 1)
                {
                    minimumEnchantmentLevel = 1;
                }
                if(maximumEnchantmentLevel > 100)
                {
                    maximumEnchantmentLevel = 100;
                }

                this.reagentEnchantmentData.put(enchantment.getRegistryName(), new ReagentEnchantmentData(enchantment, minimumEnchantmentLevel, maximumEnchantmentLevel, probability, reagentCost));
            }
        }
    }

    @Override
    public void writeToConfig(Config config)
    {
        List<Config> enchantmentConfigs = new ArrayList<>();
        config.set("item", this.item.getRegistryName().toString());

        for(Map.Entry<ResourceLocation, IReagentEnchantmentData> entry : this.reagentEnchantmentData.entrySet())
        {
            IReagentEnchantmentData reagenchantmentData = entry.getValue();

            Config enchantmentConfig = JsonFormat.newConfig(LinkedHashMap::new);
            enchantmentConfig.add("enchantment", entry.getKey().toString());
            enchantmentConfig.add("probability", reagenchantmentData.getEnchantmentProbability());
            enchantmentConfig.add("reagentCost", reagenchantmentData.getReagentCost());
            enchantmentConfig.add("minimumEnchantmentLevel", reagenchantmentData.getMinimumEnchantmentLevel());
            enchantmentConfig.add("maximumEnchantmentLevel", reagenchantmentData.getMaximumEnchantmentLevel());
            enchantmentConfigs.add(enchantmentConfig);
        }

        config.set("enchantments", enchantmentConfigs);
    }

    @Override
    public List<EnchantmentData> createEnchantmentList(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantabilityLevel, Random random)
    {
        int itemEnchantability = unenchantedStack.getItem().getItemEnchantability(unenchantedStack);

        if(itemEnchantability <= 0)
        {
            return new ArrayList<>();
        }
        else
        {
            enchantabilityLevel = enchantabilityLevel + 1 + random.nextInt(itemEnchantability / 4 + 1) + random.nextInt(itemEnchantability / 4 + 1);
            float enchantmentMultiplier = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            enchantabilityLevel = MathHelper.clamp(Math.round((float) enchantabilityLevel + (float) enchantabilityLevel * enchantmentMultiplier), 1, Integer.MAX_VALUE);

            List<EnchantmentData> aggregateEnchantmentData = new ArrayList<>();
            List<Enchantment> applicableEnchantments = this.getApplicableEnchantments(world, pos, player, unenchantedStack, reagentStack, random);
            Collections.shuffle(applicableEnchantments, random);

            for(Enchantment enchantment : applicableEnchantments)
            {
                int enchantmentLevel = this.getEnchantmentLevel(enchantment, enchantmentTier, enchantabilityLevel, random);

                if(enchantmentLevel > 0)
                {
                    EnchantmentData enchantmentData = new EnchantmentData(enchantment, enchantmentLevel);

                    if(this.getEnchantmentProbability(world, pos, player, unenchantedStack, reagentStack, enchantmentData, random) >= random.nextDouble())
                    {
                        aggregateEnchantmentData.add(enchantmentData);
                    }
                }
            }

            boolean addedOtherEnchantments = false;

            if(aggregateEnchantmentData.isEmpty())
            {
                aggregateEnchantmentData.addAll(EnchantmentHelper.getEnchantmentDatas(enchantabilityLevel, unenchantedStack, false));
                addedOtherEnchantments = true;
            }

            List<EnchantmentData> refinedEnchantmentData = new ArrayList<>();
            refinedEnchantmentData.add(WeightedRandom.getRandomItem(random, aggregateEnchantmentData));

            if(!addedOtherEnchantments)
            {
                aggregateEnchantmentData.addAll(EnchantmentHelper.getEnchantmentDatas(enchantabilityLevel, unenchantedStack, false));
            }

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

    @Override
    public boolean hasApplicableEnchantments(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, Random random)
    {
        for(Enchantment enchantment : this.getReagentEnchantmentData())
        {
            if(enchantment.canApplyAtEnchantingTable(unenchantedStack) || (unenchantedStack.getItem() == Items.BOOK && enchantment.isAllowedOnBooks()))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean consumeReagent(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, List<EnchantmentData> enchantmentList, Random random)
    {
        return true;
    }

    @Override
    public Item getItem()
    {
        return this.item;
    }

    @Override
    public List<Enchantment> getReagentEnchantmentData()
    {
        List<Enchantment> associatedEnchantments = new ArrayList<>();

        for(ResourceLocation registryName : this.reagentEnchantmentData.keySet())
        {
            associatedEnchantments.add(Enchantment.getEnchantmentByLocation(registryName.toString()));
        }

        return associatedEnchantments;
    }

    @Override
    public List<Enchantment> getApplicableEnchantments(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, Random random)
    {
        List<Enchantment> enchantments = new ArrayList<>();

        for(Enchantment enchantment : this.getReagentEnchantmentData())
        {
            if(enchantment.canApplyAtEnchantingTable(unenchantedStack) || (unenchantedStack.getItem() == Items.BOOK && enchantment.isAllowedOnBooks()))
            {
                enchantments.add(enchantment);
            }
        }

        return enchantments;
    }

    @Override
    public IReagentEnchantmentData getReagentEnchantmentData(Enchantment enchantment)
    {
        return this.reagentEnchantmentData.get(enchantment.getRegistryName());
    }

    @Override
    public int getEnchantmentLevel(Enchantment enchantment, int enchantmentTier, int enchantabilityLevel, Random random)
    {
        IReagentEnchantmentData reagentEnchantmentData = this.reagentEnchantmentData.get(enchantment.getRegistryName());
        int minimumEnchantmentLevel = reagentEnchantmentData.getMinimumEnchantmentLevel();
        int maximumEnchantmentLevel = reagentEnchantmentData.getMaximumEnchantmentLevel();
        int enchantmentLevel;

        if(minimumEnchantmentLevel == enchantment.getMinLevel() && maximumEnchantmentLevel == enchantment.getMaxLevel())
        {
            for(enchantmentLevel = maximumEnchantmentLevel; enchantmentLevel > minimumEnchantmentLevel - 1; enchantmentLevel--)
            {
                if(enchantabilityLevel >= enchantment.getMinEnchantability(enchantmentLevel) && enchantabilityLevel <= enchantment.getMaxEnchantability(enchantmentLevel))
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

        return enchantmentLevel;
    }

    @Override
    public double getEnchantmentProbability(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        ResourceLocation enchantmentRegistryName = enchantmentData.enchantment.getRegistryName();

        if(this.reagentEnchantmentData.containsKey(enchantmentRegistryName))
        {
            return this.reagentEnchantmentData.get(enchantmentRegistryName).getEnchantmentProbability();
        }

        return 0.5D;
    }

    @Override
    public int getReagentCost(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        ResourceLocation enchantmentRegistryName = enchantmentData.enchantment.getRegistryName();

        if(this.reagentEnchantmentData.containsKey(enchantmentRegistryName))
        {
            return this.reagentEnchantmentData.get(enchantmentRegistryName).getReagentCost();
        }

        return 1;
    }
}
