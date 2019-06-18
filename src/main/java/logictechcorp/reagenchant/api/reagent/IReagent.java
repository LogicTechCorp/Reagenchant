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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * An interface for an enchanting reagent. Contains various functions related to enchanting.
 */
public interface IReagent
{
    /**
     * Called to associate an enchantment with this reagent.
     *
     * @param enchantment            The enchantment to be associated with this reagent.
     * @param reagentEnchantmentData The data that represents the enchantment.
     */
    void addEnchantment(Enchantment enchantment, IReagentEnchantmentData reagentEnchantmentData);

    /**
     * Called to unassociated an enchantment with this reagent.
     *
     * @param enchantment The enchantment to be unassociated with this reagent.
     */
    void removeEnchantment(Enchantment enchantment);

    /**
     * Returns a list of the enchantments that are to be applied to the unenchantedStack.
     *
     * @param world               The world the enchantment table is in.
     * @param pos                 The position of the enchantment table in the world.
     * @param player              The player that is using the enchantment table.
     * @param unenchantedStack    The itemstack that is being enchanted.
     * @param reagentStack        The itemstack that contains the reagent.
     * @param enchantmentTier     The tier of the enchantment from 1 to 3.
     * @param enchantabilityLevel The level of experience required to unlock the enchantmentTier.
     * @param random              The random number generator.
     * @return A list of the enchantments that are going to be applied.
     */
    List<EnchantmentData> createEnchantmentList(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantabilityLevel, Random random);

    /**
     * Called to check if this reagent has enchantments that are applicable to the unenchantedStack.
     *
     * @param world            The world the enchantment table is in.
     * @param pos              The position of the enchantment table.
     * @param player           The player that is using the enchantment table.
     * @param unenchantedStack The itemstack that is being enchanted.
     * @param reagentStack     The itemstack that contains the reagent.
     * @param random           The random number generator.
     * @return Whether the reagent has enchantments that are applicable to the unenchantedStack.
     */
    boolean hasApplicableEnchantments(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, Random random);

    /**
     * Called after the enchantments are applied to determine if the reagent is consumed.
     *
     * @param world           The world the enchantment table is in.
     * @param pos             The position of the enchantment table.
     * @param player          The player that is using the enchantment table.
     * @param enchantedStack  The itemstack that is being enchanted.
     * @param reagentStack    The itemstack that contains the reagent.
     * @param enchantmentList The enchantments that were applied.
     * @param random          The random number generator.
     * @return Whether the reagent item is consumed.
     */
    boolean consumeReagent(World world, BlockPos pos, PlayerEntity player, ItemStack enchantedStack, ItemStack reagentStack, List<EnchantmentData> enchantmentList, Random random);

    /**
     * Returns the name of the reagent as a resource location.
     *
     * @return The name of the reagent as a resource location.
     */
    ResourceLocation getName();

    /**
     * Returns the item that is associated with this reagent.
     *
     * @return The item that is associated with this reagent.
     */
    Item getAssociatedItem();

    /**
     * Returns a list containing the associated enchantments.
     *
     * @return A list containing the associated enchantments.
     */
    List<Enchantment> getAssociatedEnchantments();

    /**
     * Called when creating the list of enchantments that can be applied to the unenchantedStack and allows for modification of said list.
     * Enchantments are removed from the list if they are not compatible with another enchantment in the list.
     *
     * @param world            The world the enchantment table is in.
     * @param pos              The position of the enchantment table.
     * @param player           The player that is using the enchantment table.
     * @param unenchantedStack The itemstack that is being enchanted.
     * @param reagentStack     The itemstack that contains the reagent.
     * @param random           The random number generator.
     * @return A list containing the enchantments that can be applied to the unenchantedStack.
     */
    List<Enchantment> getApplicableEnchantments(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, Random random);

    /**
     * Called to get the data associated with the enchantment.
     *
     * @param enchantment The enchantment to get the data for.
     * @return The data associated with the enchantment.
     */
    IReagentEnchantmentData getReagentEnchantmentData(Enchantment enchantment);

    /**
     * Called when creating the list of enchantments that can be applied to the unenchantedStack and allows for modification of the base probability.
     * enchantments are not added to the list if their base probability is not met.
     *
     * @param world            The world the enchantment table is in.
     * @param pos              The position of the enchantment table in the world.
     * @param player           The player that is using the enchantment table.
     * @param unenchantedStack The itemstack that is being enchanted.
     * @param reagentStack     The itemstack that contains the reagent.
     * @param enchantmentData  The enchantment that the probability is for.
     * @param random           The random number generator.
     * @return The probability of the enchantment being applied.
     */
    double getEnchantmentProbability(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random);

    /**
     * Called when applying enchantments to the unenchantedStack and allows for modification of the base reagent cost.
     * enchantments are not applied if their reagent cost is not met.
     *
     * @param world            The world the enchantment table is in.
     * @param pos              The position of the enchantment table.
     * @param player           The player that is using the enchantment table.
     * @param unenchantedStack The itemstack that is being enchanted.
     * @param reagentStack     The itemstack that contains the reagent.
     * @param enchantmentData  The enchantment that is going to be applied.
     * @param random           The random number generator.
     * @return The amount of reagents required to apply the enchantment.
     */
    int getReagentCost(World world, BlockPos pos, PlayerEntity player, ItemStack unenchantedStack, ItemStack reagentStack, EnchantmentData enchantmentData, Random random);
}
