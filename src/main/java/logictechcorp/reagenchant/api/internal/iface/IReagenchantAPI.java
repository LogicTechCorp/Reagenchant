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

public interface IReagenchantAPI
{
    /**
     * Returns false if the actual mod is loaded.
     *
     * @return Whether this API instance is created by the mod.
     */
    boolean isStub();

    /**
     * Returns the reagent registry.
     *
     * @return The reagent registry.
     */
    IReagentRegistry getReagentRegistry();

    /**
     * Returns the reagent manager.
     *
     * @return The reagent manager
     */
    IReagentManager getReagentManager();
}
