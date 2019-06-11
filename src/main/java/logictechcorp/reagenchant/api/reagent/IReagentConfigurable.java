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

import com.electronwill.nightconfig.core.Config;

public interface IReagentConfigurable extends IReagent
{
    /**
     * Called when the server is starting to configure this Reagent.
     *
     * @param config The config that belongs to the Reagent.
     */
    void readFromConfig(Config config);

    /**
     * Called when the server is stopping to save this Reagent's data.
     *
     * @param config The config that belongs to the Reagent.
     */
    void writeToConfig(Config config);

    /**
     * Called to get the Reagents relative save file.
     *
     * @return The Reagents save relative save file.
     */
    String getRelativeSaveFile();
}
