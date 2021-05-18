/*
 * Reagenchant
 * Copyright (c) 2019-2021 by LogicTechCorp
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

package logictechcorp.reagenchant.common.compatibility;

import net.minecraftforge.fml.ModList;

public class Compatibility {
    public static final boolean IS_JEI_LOADED = ModList.get().isLoaded("jei");
    public static final boolean IS_QUARK_ODDITIES_LOADED = ModList.get().isLoaded("quarkoddities");
    public static final boolean IS_APOTHEOSIS_LOADED = ModList.get().isLoaded("apotheosis");
}
