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

package logictechcorp.reagenchant.api;

import com.google.common.collect.ImmutableList;
import logictechcorp.reagenchant.api.reagent.IReagent;
import net.minecraft.item.Item;

public interface IReagenchantAPI
{
    /**
     * Returns false if the actual mod is loaded.
     *
     * @return Whether this API instance is created by the mod.
     */
    boolean isStub();

    /**
     * Called to register a Reagent.
     *
     * @param reagent The reagent that is to be registered.
     */
    void registerReagent(IReagent reagent);

    /**
     * Called to check if an item is associated with a Reagent.
     *
     * @param item The item to check against.
     * @return Whether the item is associated with a Reagent.
     */
    boolean isReagentItem(Item item);

    /**
     * Called to get a Reagent associated with an item.
     *
     * @param associatedItem The item to get the Reagent for.
     * @return The Reagent associated with the item.
     */
    IReagent getReagent(Item associatedItem);

    /**
     * Called to get a list of all registered Reagents.
     *
     * @return A list of the registered Reagents.
     */
    ImmutableList<IReagent> getReagents();
}
