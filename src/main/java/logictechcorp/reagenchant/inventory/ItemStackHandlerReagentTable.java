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

package logictechcorp.reagenchant.inventory;

import logictechcorp.reagenchant.tileentity.TileEntityReagentTable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerReagentTable extends ItemStackHandler
{
    private TileEntityReagentTable reagentTable;
    private NonNullList<ItemStack> stacks;

    public ItemStackHandlerReagentTable(TileEntityReagentTable reagentTable)
    {
        this.reagentTable = reagentTable;
        this.stacks = NonNullList.withSize(2, ItemStack.EMPTY);
    }
}
