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

package logictechcorp.reagenchant.api.internal.iface;

import logictechcorp.reagenchant.api.reagent.iface.IReagent;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public interface IReagentRegistry
{
    /**
     * Called to register a reagent.
     *
     * @param unregisteredReagent The reagent that is to be registered.
     */
    void registerReagent(IReagent unregisteredReagent);

    /**
     * Called to unregister a reagent.
     *
     * @param associatedItem The item that is associated with the reagent.
     */
    void unregisterReagent(Item associatedItem);

    /**
     * Called to check if an item is associated with a reagent.
     *
     * @param item The item to check against.
     * @return Whether the item is associated with a reagent.
     */
    boolean isReagentItem(Item item);

    /**
     * Called to get a reagent associated with an item.
     *
     * @param associatedItem The item to get the reagent for.
     * @return The reagent associated with the item.
     */
    IReagent getReagent(Item associatedItem);

    /**
     * Called to get a map containing reagent registry name's and instances.
     *
     * @return A map containing the reagent registry name's and instances.
     */
    Map<ResourceLocation, IReagent> getReagents();
}
