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

import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.init.ReagenchantContainerTypes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.SlotItemHandler;

public class ReagentTableContainer extends Container
{
    private final ReagentTableManager reagentTableManager;

    public ReagentTableContainer(int id)
    {
        this(null, id);
    }

    public ReagentTableContainer(ReagentTableManager reagentTableManager, int id)
    {
        super(ReagenchantContainerTypes.REAGENT_TABLE_CONTAINER, id);
        this.reagentTableManager = reagentTableManager;

        this.addSlot(new SlotItemHandler(this.reagentTableManager.getInventory(), 0, 6, 47)
        {
            @Override
            public void onSlotChanged()
            {
                ReagentTableContainer.this.reagentTableManager.onContentsChanged(ReagentTableContainer.this);
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.isEnchantable();
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });
        this.addSlot(new SlotItemHandler(this.reagentTableManager.getInventory(), 1, 24, 47)
        {
            @Override
            public void onSlotChanged()
            {
                ReagentTableContainer.this.reagentTableManager.onContentsChanged(ReagentTableContainer.this);
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem().isIn(Tags.Items.GEMS_LAPIS);
            }

            @Override
            public int getSlotStackLimit()
            {
                return 64;
            }
        });
        this.addSlot(new SlotItemHandler(this.reagentTableManager.getInventory(), 2, 42, 47)
        {
            @Override
            public void onSlotChanged()
            {
                ReagentTableContainer.this.reagentTableManager.onContentsChanged(ReagentTableContainer.this);
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return ReagenchantAPI.getInstance().getReagentRegistry().isReagentItem(stack.getItem());
            }

            @Override
            public int getSlotStackLimit()
            {
                return 64;
            }
        });

        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(this.reagentTableManager.getUser().inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(this.reagentTableManager.getUser().inventory, x, 8 + x * 18, 142));
        }

        this.reagentTableManager.onContentsChanged(this);
        this.func_216958_a(IntReferenceHolder.create(this.reagentTableManager.getEnchantmentLevels(), 0));
        this.func_216958_a(IntReferenceHolder.create(this.reagentTableManager.getEnchantmentLevels(), 1));
        this.func_216958_a(IntReferenceHolder.create(this.reagentTableManager.getEnchantmentLevels(), 2));
        this.func_216958_a(IntReferenceHolder.single()).set(this.reagentTableManager.getXpSeed());
        this.func_216958_a(IntReferenceHolder.create(this.reagentTableManager.getEnchantments(), 0));
        this.func_216958_a(IntReferenceHolder.create(this.reagentTableManager.getEnchantments(), 1));
        this.func_216958_a(IntReferenceHolder.create(this.reagentTableManager.getEnchantments(), 2));
        this.func_216958_a(IntReferenceHolder.create(this.reagentTableManager.getEnchantabilityLevels(), 0));
        this.func_216958_a(IntReferenceHolder.create(this.reagentTableManager.getEnchantabilityLevels(), 1));
        this.func_216958_a(IntReferenceHolder.create(this.reagentTableManager.getEnchantabilityLevels(), 2));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if(id >= 0 && id <= 2)
        {
            this.reagentTableManager.getEnchantabilityLevels()[id] = data;
        }
        else if(id == 3)
        {
            this.reagentTableManager.setXpSeed(data);
        }
        else if(id >= 4 && id <= 6)
        {
            this.reagentTableManager.getEnchantments()[id - 4] = data;
        }
        else if(id >= 7 && id <= 9)
        {
            this.reagentTableManager.getEnchantmentLevels()[id - 7] = data;
        }
        else
        {
            super.updateProgressBar(id, data);
        }
    }

    @Override
    public void onContainerClosed(PlayerEntity player)
    {
        super.onContainerClosed(player);

        if(!this.reagentTableManager.getWorld().isRemote)
        {
            if(this.reagentTableManager.getInventory().getStackInSlot(0).isEnchanted())
            {
                if(!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).hasDisconnected())
                {
                    player.dropItem(this.reagentTableManager.getInventory().extractItem(0, 64, false), false);
                }
                else
                {
                    player.inventory.placeItemBackInInventory(this.reagentTableManager.getWorld(), this.reagentTableManager.getInventory().extractItem(0, 64, false));
                }
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if(index == 0)
            {
                if(!this.mergeItemStack(slotStack, 3, 38, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index == 1)
            {
                if(!this.mergeItemStack(slotStack, 3, 38, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index == 2)
            {
                if(!this.mergeItemStack(slotStack, 3, 38, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(slotStack.getItem() == Items.LAPIS_LAZULI)
            {
                if(!this.mergeItemStack(slotStack, 1, 2, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(ReagenchantAPI.getInstance().getReagentRegistry().isReagentItem(slotStack.getItem()))
            {
                if(!this.mergeItemStack(slotStack, 2, 3, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if(this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(slotStack))
                {
                    return ItemStack.EMPTY;
                }

                if(slotStack.hasTag())
                {
                    this.inventorySlots.get(0).putStack(slotStack.split(1));
                }
                else if(!slotStack.isEmpty())
                {
                    this.inventorySlots.get(0).putStack(new ItemStack(slotStack.getItem(), 1));
                    slotStack.shrink(1);
                }
            }

            if(slotStack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }

            if(slotStack.getCount() == stack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return stack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        BlockPos pos = this.reagentTableManager.getPos();

        if(this.reagentTableManager.getWorld().getBlockState(pos).getBlock() != Blocks.ENCHANTING_TABLE)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public boolean enchantItem(PlayerEntity player, int enchantmentTier)
    {
        return this.reagentTableManager.enchantItem(player, enchantmentTier, this);
    }

    public ReagentTableManager getReagentTableManager()
    {
        return this.reagentTableManager;
    }
}
