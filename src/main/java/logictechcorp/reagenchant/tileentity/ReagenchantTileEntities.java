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

package logictechcorp.reagenchant.tileentity;

import logictechcorp.reagenchant.Reagenchant;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ReagenchantTileEntities
{
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, Reagenchant.MOD_ID);

    public static final RegistryObject<TileEntityType<ReagentTableTileEntity>> REAGENT_TABLE_TILE_ENTITY = TILE_ENTITIES.register("reagent_table_tile_entity", () -> TileEntityType.Builder.create(ReagentTableTileEntity::new, Blocks.ENCHANTING_TABLE).build(null));
}
