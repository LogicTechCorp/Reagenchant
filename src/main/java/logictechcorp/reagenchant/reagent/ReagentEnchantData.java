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

import com.mojang.datafixers.Dynamic;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ReagentEnchantData
{
    public static final ReagentEnchantData EMPTY = new ReagentEnchantData(null, 0, 0, 0.0F, 0);

    private final Enchantment enchantment;
    private final int minimumEnchantmentLevel;
    private final int maximumEnchantmentLevel;
    private final float enchantmentProbability;
    private final int reagentCost;

    public ReagentEnchantData(Enchantment enchantment, int minimumEnchantmentLevel, int maximumEnchantmentLevel, float enchantmentProbability, int reagentCost)
    {
        this.enchantment = enchantment;
        this.minimumEnchantmentLevel = minimumEnchantmentLevel;
        this.maximumEnchantmentLevel = maximumEnchantmentLevel;
        this.enchantmentProbability = enchantmentProbability;
        this.reagentCost = reagentCost;
    }

    public static <T> ReagentEnchantData deserialize(Dynamic<T> dynamic)
    {
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(dynamic.get("enchantment").asString("null")));

        if(enchantment != null)
        {
            float probability = dynamic.get("probability").asFloat(0.5F);

            if(probability < 0.0F)
            {
                probability = 0.5F;
            }

            if(probability > 0.0F)
            {
                int minimumEnchantmentLevel = dynamic.get("minimumEnchantmentLevel").asInt(enchantment.getMinLevel());
                int maximumEnchantmentLevel = dynamic.get("maximumEnchantmentLevel").asInt(enchantment.getMaxLevel());

                if(minimumEnchantmentLevel < 1)
                {
                    minimumEnchantmentLevel = 1;
                }
                if(maximumEnchantmentLevel > 100)
                {
                    maximumEnchantmentLevel = 100;
                }

                int reagentCost = dynamic.get("reagentCost").asInt(1);

                if(reagentCost < 0)
                {
                    reagentCost = 1;
                }

                return new ReagentEnchantData(enchantment, minimumEnchantmentLevel, maximumEnchantmentLevel, probability, reagentCost);
            }
        }

        return ReagentEnchantData.EMPTY;
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

    public float getEnchantmentProbability()
    {
        return this.enchantmentProbability;
    }

    public int getReagentCost()
    {
        return this.reagentCost;
    }
}
