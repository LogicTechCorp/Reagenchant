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

package logictechcorp.reagenchant.item;

import logictechcorp.libraryex.item.ItemBlockMod;
import logictechcorp.reagenchant.init.ReagenchantBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ItemBlockReagentTable extends ItemBlockMod
{
    public ItemBlockReagentTable()
    {
        super(Blocks.ENCHANTING_TABLE, ReagenchantBlocks.getDefaultItemBlockBuilder());
    }

    @Override
    public String getTranslationKey()
    {
        return "tile.enchantmentTable";
    }

    @Override
    public String getTranslationKey(ItemStack stack)
    {
        return this.getTranslationKey();
    }
}
