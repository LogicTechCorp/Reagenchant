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

package logictechcorp.reagenchant.core.registry;

import com.minecraftabnormals.abnormals_core.core.util.registry.BlockSubRegistryHelper;
import logictechcorp.reagenchant.common.block.ReagentEnchantingTableBlock;
import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class ReagenchantBlocks {
    public static final BlockSubRegistryHelper OVERRIDE_HELPER = Reagenchant.OVERRIDE_REGISTRY_HELPER.getBlockSubHelper();

    public static final RegistryObject<Block> REAGENT_ENCHANTING_TABLE = OVERRIDE_HELPER.createBlock("enchanting_table", () -> new ReagentEnchantingTableBlock(Block.Properties.from(Blocks.ENCHANTING_TABLE)), ItemGroup.DECORATIONS);
}
