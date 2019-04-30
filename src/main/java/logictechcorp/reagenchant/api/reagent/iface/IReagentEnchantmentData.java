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

package logictechcorp.reagenchant.api.reagent.iface;

import net.minecraft.enchantment.Enchantment;

public interface IReagentEnchantmentData
{
    /**
     * The enchantment this data is for.
     *
     * @return The enchantment this data is for.
     */
    Enchantment getEnchantment();

    /**
     * The minimum level for the enchantment.
     *
     * @return The minimum level for the enchantment.
     */
    int getMinimumEnchantmentLevel();

    /**
     * The maximum level for the enchantment.
     *
     * @return The maximum level for the enchantment.
     */
    int getMaximumEnchantmentLevel();

    /**
     * Returns the base probability for the enchantment being applied.
     *
     * @return The base probability for the enchantment being applied.
     */
    double getEnchantmentProbability();

    /**
     * The base reagent cost required to apply the enchantment.
     *
     * @return The base reagent cost required to apply the enchantment.
     */
    int getReagentCost();
}
