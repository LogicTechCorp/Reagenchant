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

package logictechcorp.reagenchant;

import logictechcorp.reagenchant.api.internal.IReagentRegistry;
import logictechcorp.reagenchant.api.reagent.IReagent;
import logictechcorp.reagenchant.api.reagent.IReagentConfigurable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class ReagentRegistry implements IReagentRegistry
{
    static final IReagentRegistry INSTANCE = new ReagentRegistry();

    private final Map<ResourceLocation, IReagent> reagents = new HashMap<>();
    private final Marker marker = MarkerManager.getMarker("ReagentRegistry");

    private ReagentRegistry()
    {
    }

    @Override
    public void registerReagent(IReagent unregisteredReagent)
    {
        Item associatedItem = unregisteredReagent.getAssociatedItem();

        if(associatedItem == Items.AIR)
        {
            Reagenchant.LOGGER.warn(this.marker, "The {} Reagent was not able to registered because it has an invalid associated item.", unregisteredReagent.getName().toString());
            return;
        }

        ResourceLocation associatedItemRegistryName = associatedItem.getRegistryName();

        if(this.reagents.containsKey(associatedItemRegistryName))
        {
            IReagent registeredReagent = this.reagents.get(associatedItemRegistryName);

            for(Enchantment enchantment : unregisteredReagent.getAssociatedEnchantments())
            {
                if(registeredReagent.getAssociatedEnchantments().contains(enchantment) && !(registeredReagent instanceof IReagentConfigurable))
                {
                    continue;
                }

                double baseEnchantmentProbability = unregisteredReagent.getBaseEnchantmentProbability(enchantment);
                int baseReagentCost = unregisteredReagent.getBaseReagentCost(enchantment);

                registeredReagent.addEnchantment(enchantment, baseEnchantmentProbability, baseReagentCost);
            }

            return;
        }

        this.reagents.put(associatedItemRegistryName, unregisteredReagent);
    }

    @Override
    public void unregisterReagent(Item associatedItem)
    {
        this.reagents.remove(associatedItem.getRegistryName());
    }

    @Override
    public boolean isReagentItem(Item item)
    {
        return item instanceof IReagent || this.reagents.containsKey(item.getRegistryName());
    }

    @Override
    public IReagent getReagent(Item associatedItem)
    {
        return this.reagents.get(associatedItem.getRegistryName());
    }

    @Override
    public Map<ResourceLocation, IReagent> getReagents()
    {
        return Collections.unmodifiableMap(this.reagents);
    }
}
