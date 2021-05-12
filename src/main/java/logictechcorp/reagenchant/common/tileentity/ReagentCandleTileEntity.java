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

package logictechcorp.reagenchant.common.tileentity;

import logictechcorp.reagenchant.common.inventory.container.ReagentCandleContainer;
import logictechcorp.reagenchant.core.registry.ReagenchantTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.TranslationTextComponent;

public class ReagentCandleTileEntity extends InventoryTileEntity {
    public ReagentCandleTileEntity() {
        super(ReagenchantTileEntityTypes.REAGENT_CANDLE_TILE_ENTITY.get(), new TranslationTextComponent("container.reagenchant.reagent_candle"), 1);
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ReagentCandleContainer(windowId, playerInventory, IWorldPosCallable.of(this.world, this.pos), this.itemStackHandler);
    }
}
