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

import logictechcorp.reagenchant.api.reagent.IReagentEnchantmentData;
import net.minecraft.enchantment.Enchantment;

public class ReagentEnchantmentData implements IReagentEnchantmentData
{
    private Enchantment enchantment;
    private int minimumEnchantmentLevel;
    private int maximumEnchantmentLevel;
    private double enchantmentProbability;
    private int reagentCost;

    public ReagentEnchantmentData(Enchantment enchantment, int minimumEnchantmentLevel, int maximumEnchantmentLevel, double enchantmentProbability, int reagentCost)
    {
        this.enchantment = enchantment;
        this.minimumEnchantmentLevel = minimumEnchantmentLevel;
        this.maximumEnchantmentLevel = maximumEnchantmentLevel;
        this.enchantmentProbability = enchantmentProbability;
        this.reagentCost = reagentCost;
    }

    public ReagentEnchantmentData(Enchantment enchantment, double enchantmentProbability, int reagentCost)
    {
        this(enchantment, enchantment.getMinLevel(), enchantment.getMaxLevel(), enchantmentProbability, reagentCost);
    }

    @Override
    public Enchantment getEnchantment()
    {
        return this.enchantment;
    }

    @Override
    public int getMinimumEnchantmentLevel()
    {
        return this.minimumEnchantmentLevel;
    }

    @Override
    public int getMaximumEnchantmentLevel()
    {
        return this.maximumEnchantmentLevel;
    }

    @Override
    public double getEnchantmentProbability()
    {
        return this.enchantmentProbability;
    }

    @Override
    public int getReagentCost()
    {
        return this.reagentCost;
    }
}
