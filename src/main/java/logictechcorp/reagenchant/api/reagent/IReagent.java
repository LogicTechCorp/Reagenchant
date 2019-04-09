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
     * @param enchantment                The Enchantment to be associated with the Reagent.
     * @param baseEnchantmentProbability The base probability for the Enchantment being applied.
     * @param baseReagentCost            The base Reagent cost required to apply the Enchantment.
     */
    void addEnchantment(Enchantment enchantment, float baseEnchantmentProbability, int baseReagentCost);

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
     * @param random           The random number generator.
     * @return A list of the Enchantments that are going to be applied.
     */
    List<EnchantmentData> createEnchantmentList(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantmentLevel, boolean allowTreasure, Random random);

    /**
     * Called to check if this Reagent has Enchantments that are applicable to the unenchantedStack.
     *
     * @param world            The world the Enchantment Table is in.
     * @param pos              The position of the Enchantment Table.
     * @param player           The player that is using the Enchantment Table.
     * @param unenchantedStack The Itemstack that is being enchanted.
     * @param reagentStack     The Itemstack that contains the Reagent.
     * @param random           The random number generator.
     * @return Whether the Reagent has Enchantments that are applicable to the unenchantedStack.
     */
    boolean hasApplicableEnchantments(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, Random random);

    /**
     * Called after the Enchantments are applied to determine if the Reagent is consumed.
     *
     * @param world           The world the Enchantment Table is in.
     * @param pos             The position of the Enchantment Table.
     * @param player          The player that is using the Enchantment Table.
     * @param enchantedStack  The Itemstack that is being enchanted.
     * @param reagentStack    The Itemstack that contains the Reagent.
     * @param enchantmentData The Enchantments that were applied.
     * @param random          The random number generator.
     * @return Whether the Reagent item is consumed.
     */
    boolean consumeReagent(World world, BlockPos pos, EntityPlayer player, ItemStack enchantedStack, ItemStack reagentStack, List<EnchantmentData> enchantmentData, Random random);

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
     * Returns a list containing the associated Enchantments.
     *
     * @return A list containing the associated Enchantments.
     */
    List<Enchantment> getAssociatedEnchantments();

    /**
     * Called when creating the list of Enchantments that can be applied to the unenchantedStack and allows for modification of said list.
     * Enchantments are removed from the list if they are not compatible with another Enchantment in the list.
     *
     * @param world            The world the Enchantment Table is in.
     * @param pos              The position of the Enchantment Table.
     * @param player           The player that is using the Enchantment Table.
     * @param unenchantedStack The Itemstack that is being enchanted.
     * @param reagentStack     The Itemstack that contains the Reagent.
     * @param random           The random number generator.
     * @return A list containing the Enchantments that can be applied to the unenchantedStack.
     */
    List<Enchantment> getApplicableEnchantments(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, Random random);

    /**
     * Returns the base probability for the Enchantment being applied.
     *
     * @param enchantment The Enchantment to get the probability for.
     * @return The base probability for the Enchantment being applied.
     */
    float getBaseEnchantmentProbability(Enchantment enchantment);

    /**
     * Called when creating the list of Enchantments that can be applied to the unenchantedStack and allows for modification of the base probability.
     * Enchantments are not added to the list if their base probability is not met.
     *
     * @param world            The world the Enchantment Table is in.
     * @param pos              The position of the Enchantment Table in the world.
     * @param player           The player that is using the Enchantment Table.
     * @param unenchantedStack The Itemstack that is being enchanted.
     * @param reagentStack     The Itemstack that contains the Reagent.
     * @param enchantmentData  The Enchantment that the probability is for.
     * @param random           The random number generator.
     * @return The probability of the Enchantment being applied.
     */
    float getEnchantmentProbability(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random);

    /**
     * The base Reagent cost required to apply the Enchantment.
     *
     * @param enchantment The Enchantment to get the Reagent cost for.
     * @return The base Reagent cost required to apply the Enchantment.
     */
    int getBaseReagentCost(Enchantment enchantment);

    /**
     * Called when applying Enchantments to the unenchantedStack and allows for modification of the base Reagent cost.
     * Enchantments are not applied if their Reagent cost is not met.
     *
     * @param world            The world the Enchantment Table is in.
     * @param pos              The position of the Enchantment Table.
     * @param player           The player that is using the Enchantment Table.
     * @param unenchantedStack The Itemstack that is being enchanted.
     * @param reagentStack     The Itemstack that contains the Reagent.
     * @param enchantmentData  The Enchantment that is going to be applied.
     * @param random           The random number generator.
     * @return The amount of Reagents required to apply the Enchantment.
     */
    int getReagentCost(World world, BlockPos pos, EntityPlayer player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random);
}
