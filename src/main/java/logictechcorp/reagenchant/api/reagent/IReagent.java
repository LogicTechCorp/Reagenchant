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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * An interface for an Enchanting Reagent. Contains various functions related to Enchanting.
 */
public interface IReagent
{
    /**
     * Called to associate an Enchantment with this Reagent.
     *
     * @param enchantment The Enchantment to be associated with the Reagent
     * @param probability The probability of the Enchantment being picked
     */
    void addEnchantment(Enchantment enchantment, float probability);

    /**
     * Returns a list of the Enchantments that are to be applied to the unenchantedStack.
     *
     * @param world            The world the Enchantment Table is in.
     * @param pos              The position of the Enchantment Table in the world.
     * @param player           The player that is using the Enchantment Table.
     * @param unenchantedStack The Itemstack that is being enchanted.
     * @param reagentStack     The Itemstack that contains the Reagent.
     * @param enchantmentTier  The tier of the enchantment from 1 to 3.
     * @param enchantmentLevel The level of the enchantment from 1 to 30.
     * @param allowTreasure    Whether treasure enchantments can be applied to the unenchantedStack.
     * @param rand             The random number generator.
     * @return A list of the Enchantments that are going to be applied.
     */
    List<EnchantmentData> buildEnchantmentList(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantmentLevel, boolean allowTreasure, Random rand);

    /**
     * Called to check if this Reagent has Enchantments that are applicable to the unenchantedStack.
     *
     * @param world            The world the Enchantment Table is in.
     * @param pos              The position of the Enchantment Table.
     * @param player           The player that is using the Enchantment Table.
     * @param unenchantedStack The Itemstack that is being enchanted.
     * @param reagentStack     The Itemstack that contains the Reagent.
     * @param enchantmentTier  The tier of the enchantment from 1 to 3.
     * @param enchantmentLevel The level of the enchantment from 1 to 30.
     * @param rand             The random number generator.
     * @return Whether the Reagent has Enchantments that are applicable to the unenchantedStack.
     */
    boolean hasApplicableEnchantments(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantmentLevel, Random rand);

    /**
     * Called after the Enchantments are applied and right before the Reagent is consumed.
     *
     * @param world            The world the Enchantment Table is in.
     * @param pos              The position of the Enchantment Table.
     * @param player           The player that is using the Enchantment Table.
     * @param enchantedStack   The Itemstack that is being enchanted.
     * @param reagentStack     The Itemstack that contains the Reagent.
     * @param enchantmentTier  The tier of the enchantment from 1 to 3.
     * @param enchantmentLevel The level of the enchantment from 1 to 30.
     * @param enchantmentData  The Enchantments that were applied.
     * @param rand             The random number generator.
     * @return Whether the Reagent item is consumed.
     */
    boolean consumeReagent(World world, BlockPos pos, EntityPlayer player, ItemStack enchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantmentLevel, List<EnchantmentData> enchantmentData, Random rand);

    /**
     * Returns the item that is associated with this Reagent.
     *
     * @return The item that is associated with this Reagent.
     */
    Item getAssociatedItem();

    /**
     * Returns the name of the Reagent as a ResourceLocation.
     *
     * @return The name of the Reagent as a ResourceLocation.
     */
    ResourceLocation getName();

    /**
     * Returns a map containing the associated Enchantments and their base probabilities.
     *
     * @return A map containing the associated Enchantments and their base probabilities.
     */
    ImmutableMap<Enchantment, Float> getAssociatedEnchantments();

    /**
     * Returns a list containing the Enchantments that can be applied to the unenchantedStack.
     *
     * @param world            The world the Enchantment Table is in.
     * @param pos              The position of the Enchantment Table.
     * @param player           The player that is using the Enchantment Table.
     * @param unenchantedStack The Itemstack that is being enchanted.
     * @param reagentStack     The Itemstack that contains the Reagent.
     * @param enchantmentTier  The tier of the enchantment from 1 to 3.
     * @param enchantmentLevel The level of the enchantment from 1 to 30.
     * @param rand             The random number generator.
     * @return A list containing the Enchantments that can be applied to the unenchantedStack.
     */
    ImmutableList<Enchantment> getApplicableEnchantments(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantmentLevel, Random rand);

    /**
     * Returns the probability of the Enchantment being applied.
     *
     * @param world       The world the Enchantment Table is in.
     * @param pos         The position of the Enchantment Table in the world.
     * @param player      The player that is using the Enchantment Table.
     * @param enchantment The Enchantment that the probability is for.
     * @return The probability of the Enchantment being applied.
     */
    float getEnchantmentProbability(World world, BlockPos pos, EntityPlayer player, Enchantment enchantment);
}
