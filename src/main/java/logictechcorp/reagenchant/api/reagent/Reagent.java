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

package logictechcorp.reagenchant.api.reagent;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
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
    protected final ResourceLocation name;
    protected final Item associatedItem;
    protected final Map<ResourceLocation, Tuple<Double, Integer>> enchantments = new HashMap<>();

    public Reagent(ResourceLocation name, Item associatedItem)
    {
        this.name = name;

        if(associatedItem != null)
        {
            this.associatedItem = associatedItem;
        }
        else
        {
            this.associatedItem = Items.AIR;
        }
    }

    public Reagent(ResourceLocation name, ResourceLocation associatedItemRegistryName)
    {
        this.name = name;

        Item associatedItem = ForgeRegistries.ITEMS.getValue(associatedItemRegistryName);

        if(associatedItem != null)
        {
            this.associatedItem = associatedItem;
        }
        else
        {
            this.associatedItem = Items.AIR;
        }
    }

    @Override
    public void addEnchantment(Enchantment enchantment, double probability, int reagentCost)
    {
        this.enchantments.put(enchantment.getRegistryName(), new Tuple<>(probability, reagentCost));
    }

    @Override
    public void removeEnchantment(Enchantment enchantment)
    {
        this.enchantments.remove(enchantment.getRegistryName());
    }

    @Override
    public List<EnchantmentData> createEnchantmentList(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantabilityLevel, Random random)
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
                if(enchantment.canApplyAtEnchantingTable(unenchantedStack) || (unenchantedStack.getItem() == Items.BOOK && enchantment.isAllowedOnBooks()))
                {
                    for(int enchantmentLevel = enchantment.getMaxLevel(); enchantmentLevel > enchantment.getMinLevel() - 1; enchantmentLevel--)
                    {
                        if(enchantabilityLevel >= enchantment.getMinEnchantability(enchantmentLevel) && enchantabilityLevel <= enchantment.getMaxEnchantability(enchantmentLevel))
                        {
                            EnchantmentData enchantmentData = new EnchantmentData(enchantment, enchantmentLevel);

                            if(this.getEnchantmentProbability(world, pos, player, unenchantedStack, reagentStack, enchantmentData, random) >= random.nextDouble())
                            {
                                aggregateEnchantmentData.add(enchantmentData);
                            }

                            break;
                        }
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
    public boolean consumeReagent(World world, BlockPos pos, EntityPlayer player, ItemStack enchantedStack, ItemStack reagentStack, List<EnchantmentData> enchantmentList, Random random)
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

        for(ResourceLocation registryName : this.enchantments.keySet())
        {
            associatedEnchantments.add(Enchantment.getEnchantmentByLocation(registryName.toString()));
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
    public double getBaseEnchantmentProbability(Enchantment enchantment)
    {
        Tuple<Double, Integer> data = this.enchantments.get(enchantment.getRegistryName());
        return data == null ? 0.0D : data.getFirst();
    }

    @Override
    public double getEnchantmentProbability(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        return this.getBaseEnchantmentProbability(enchantmentData.enchantment);
    }

    @Override
    public int getBaseReagentCost(Enchantment enchantment)
    {
        Tuple<Double, Integer> data = this.enchantments.get(enchantment.getRegistryName());
        return data == null ? 0 : data.getSecond();
    }

    @Override
    public int getReagentCost(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random)
    {
        return this.getBaseReagentCost(enchantmentData.enchantment);
    }
}
