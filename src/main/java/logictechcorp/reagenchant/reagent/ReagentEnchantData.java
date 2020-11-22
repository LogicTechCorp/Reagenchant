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
import net.minecraft.enchantment.Enchantment;

public class ReagentEnchantData
{
    public static final ReagentEnchantData EMPTY = new ReagentEnchantData(null, 0, 0, 0.0D, 0);

    private final Enchantment enchantment;
    private final int minimumEnchantmentLevel;
    private final int maximumEnchantmentLevel;
    private final double enchantmentProbability;
    private final int reagentCost;

    public ReagentEnchantData(Enchantment enchantment, int minimumEnchantmentLevel, int maximumEnchantmentLevel, double enchantmentProbability, int reagentCost)
    {
        this.enchantment = enchantment;
        this.minimumEnchantmentLevel = minimumEnchantmentLevel;
        this.maximumEnchantmentLevel = maximumEnchantmentLevel;
        this.enchantmentProbability = enchantmentProbability;
        this.reagentCost = reagentCost;
    }

    public ReagentEnchantData(Enchantment enchantment, float enchantmentProbability, int reagentCost)
    {
        this(enchantment, enchantment.getMinLevel(), enchantment.getMaxLevel(), enchantmentProbability, reagentCost);
    }

    public static ReagentEnchantData deserialize(Config config)
    {
        Enchantment enchantment = Enchantment.getEnchantmentByLocation(config.getOrElse("enchantment", ""));

        if(enchantment != null)
        {
            double probability = config.getOrElse("probability", 0.5D);
            int reagentCost = config.getOrElse("reagentCost", 1);

            if(probability <= 0.0F)
            {
                probability = 0.5F;
            }
            if(reagentCost < 0)
            {
                reagentCost = 1;
            }

            int minimumEnchantmentLevel = config.getOrElse("minimumEnchantmentLevel", enchantment.getMinLevel());
            int maximumEnchantmentLevel = config.getOrElse("maximumEnchantmentLevel", enchantment.getMaxLevel());

            if(minimumEnchantmentLevel < 1)
            {
                minimumEnchantmentLevel = 1;
            }
            if(maximumEnchantmentLevel > 100)
            {
                maximumEnchantmentLevel = 100;
            }

            return new ReagentEnchantData(enchantment, minimumEnchantmentLevel, maximumEnchantmentLevel, probability, reagentCost);
        }

        return EMPTY;
    }

    public boolean isEmpty()
    {
        return this == EMPTY;
    }

    public Enchantment getEnchantment()
    {
        return this.enchantment;
    }

    public int getMinimumEnchantmentLevel()
    {
        return this.minimumEnchantmentLevel;
    }

    public int getMaximumEnchantmentLevel()
    {
        return this.maximumEnchantmentLevel;
    }

    public double getEnchantmentProbability()
    {
        return this.enchantmentProbability;
    }

    public int getReagentCost()
    {
        return this.reagentCost;
    }
}
