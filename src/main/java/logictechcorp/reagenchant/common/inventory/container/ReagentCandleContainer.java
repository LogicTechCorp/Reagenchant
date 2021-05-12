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

package logictechcorp.reagenchant.common.inventory.container;

import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.registry.ReagenchantBlocks;
import logictechcorp.reagenchant.core.registry.ReagenchantContainers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;


public class ReagentCandleContainer extends Container {
    private final IWorldPosCallable worldPosCallable;

    public ReagentCandleContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY, new ItemStackHandler(1));
    }

    public ReagentCandleContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable, ItemStackHandler itemStackHandler) {
        super(ReagenchantContainers.REAGENT_CANDLE_CONTAINER.get(), id);
        this.worldPosCallable = worldPosCallable;

        this.addSlot(new SlotItemHandler(itemStackHandler, 0, 80, 35) {
            @Override
            public void onSlotChanged() {
                ReagentCandleContainer.this.onContentsChanged();
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return Reagenchant.REAGENT_MANAGER.isReagent(stack.getItem());
            }
        });

        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for(int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }
    }

    public void onContentsChanged() {
        this.detectAndSendChanges();
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return isWithinUsableDistance(this.worldPosCallable, player, ReagenchantBlocks.REAGENT_CANDLE.get());
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack()) {
            ItemStack oldStack = slot.getStack();
            newStack = oldStack.copy();

            if(index == 0) {
                if(!this.mergeItemStack(oldStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(oldStack, newStack);
            }
            else if(!this.mergeItemStack(oldStack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }

            if(oldStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }

            if(oldStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, oldStack);
        }

        return newStack;
    }
}
