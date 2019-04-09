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

package logictechcorp.reagenchant.registry;

import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.api.reagent.IReagent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.HashMap;
import java.util.Map;

public class ReagentRegistry
{
    private static final Map<ResourceLocation, IReagent> REAGENTS = new HashMap<>();
    private static final Marker MARKER = MarkerManager.getMarker("ReagentRegistry");

    public static void registerReagent(IReagent reagent)
    {
        ResourceLocation associatedItem = reagent.getAssociatedItem().getRegistryName();

        if(associatedItem == null)
        {
            Reagenchant.LOGGER.warn(MARKER, "The {} Reagent was not able to registered because it has an invalid associated item.", reagent.getName().toString());
            return;
        }

        if(REAGENTS.containsKey(associatedItem))
        {
            IReagent registeredReagent = REAGENTS.get(associatedItem);

            for(Enchantment enchantment : reagent.getAssociatedEnchantments())
            {
                if(registeredReagent.getAssociatedEnchantments().contains(enchantment))
                {
                    continue;
                }

                float baseEnchantmentProbability = reagent.getBaseEnchantmentProbability(enchantment);
                int baseReagentCost = reagent.getBaseReagentCost(enchantment);

                registeredReagent.addEnchantment(enchantment, baseEnchantmentProbability, baseReagentCost);
            }

            return;
        }

        REAGENTS.put(associatedItem, reagent);
    }

    public static boolean isReagentItem(Item item)
    {
        return REAGENTS.containsKey(item.getRegistryName());
    }

    public static IReagent getReagent(Item item)
    {
        return REAGENTS.get(item.getRegistryName());
    }
}
