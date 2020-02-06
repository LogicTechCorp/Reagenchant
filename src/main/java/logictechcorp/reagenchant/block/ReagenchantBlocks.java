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

package logictechcorp.reagenchant.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ReagenchantBlocks
{
    public static final DeferredRegister<Block> BLOCK_OVERRIDES = new DeferredRegister<>(ForgeRegistries.BLOCKS, "minecraft");

    static
    {
        BLOCK_OVERRIDES.register("enchanting_table", () -> new ReagentTableBlock(Block.Properties.from(Blocks.ENCHANTING_TABLE)));
    }
}
