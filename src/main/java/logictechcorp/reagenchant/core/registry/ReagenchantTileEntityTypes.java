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

import com.minecraftabnormals.abnormals_core.core.util.registry.TileEntitySubRegistryHelper;
import logictechcorp.reagenchant.common.tileentity.CustomAnvilTileEntity;
import logictechcorp.reagenchant.common.tileentity.ReagentAltarTileEntity;
import logictechcorp.reagenchant.common.tileentity.ReagentEnchantingTableTileEntity;
import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class ReagenchantTileEntityTypes {
    public static final TileEntitySubRegistryHelper REGISTRY_HELPER = Reagenchant.REGISTRY_HELPER.getTileEntitySubHelper();

    public static final RegistryObject<TileEntityType<ReagentEnchantingTableTileEntity>> REAGENT_ENCHANTING_TABLE_TILE_ENTITY = REGISTRY_HELPER.createTileEntity("reagent_enchanting_table_tile_entity", ReagentEnchantingTableTileEntity::new, () -> new Block[]{ Blocks.ENCHANTING_TABLE });
    public static final RegistryObject<TileEntityType<ReagentAltarTileEntity>> REAGENT_ALTAR_TILE_ENTITY = REGISTRY_HELPER.createTileEntity("reagent_altar_tile_entity", ReagentAltarTileEntity::new, () -> new Block[]{ ReagenchantBlocks.REAGENT_ALTAR.get() });
    public static final RegistryObject<TileEntityType<CustomAnvilTileEntity>> CUSTOM_ANVIL_TILE_ENTITY = REGISTRY_HELPER.createTileEntity("custom_anvil_tile_entity", CustomAnvilTileEntity::new, () -> new Block[]{ ReagenchantBlocks.CUSTOM_ANVIL.get(), ReagenchantBlocks.CUSTOM_CHIPPED_ANVIL.get(), ReagenchantBlocks.CUSTOM_DAMAGED_ANVIL.get() });
}
