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

import logictechcorp.reagenchant.Reagenchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class ContainerReagentTable extends Container
{
    private final ReagentTableManager reagentTableManager;

    public ContainerReagentTable(ReagentTableManager reagentTableManager)
    {
        this.reagentTableManager = reagentTableManager;

        this.addSlotToContainer(new SlotItemHandler(this.reagentTableManager.getInventory(), 0, 6, 47)
        {
            @Override
            public void onSlotChanged()
            {
                reagentTableManager.onContentsChanged(ContainerReagentTable.this);
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.isItemEnchantable();
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });
        this.addSlotToContainer(new SlotItemHandler(this.reagentTableManager.getInventory(), 1, 24, 47)
        {
            private final List<ItemStack> ores = OreDictionary.getOres("gemLapis");

            @Override
            public void onSlotChanged()
            {
                reagentTableManager.onContentsChanged(ContainerReagentTable.this);
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                for(ItemStack ore : this.ores)
                {
                    if(OreDictionary.itemMatches(ore, stack, false))
                    {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public int getSlotStackLimit()
            {
                return 64;
            }
        });
        this.addSlotToContainer(new SlotItemHandler(this.reagentTableManager.getInventory(), 2, 42, 47)
        {
            @Override
            public void onSlotChanged()
            {
                reagentTableManager.onContentsChanged(ContainerReagentTable.this);
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return Reagenchant.REAGENT_MANAGER.hasReagent(stack.getItem());
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
                this.addSlotToContainer(new Slot(reagentTableManager.getReagentTable().getUser().inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlotToContainer(new Slot(reagentTableManager.getReagentTable().getUser().inventory, x, 8 + x * 18, 142));
        }

        this.reagentTableManager.onContentsChanged(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
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
            this.reagentTableManager.getEnchantmentHints()[id - 4] = data;
        }
        else if(id >= 7 && id <= 9)
        {
            this.reagentTableManager.getEnchantmentLevels()[id - 7] = data;
        }
        else if(id >= 10 && id <= 12)
        {
            this.reagentTableManager.getReagentCosts()[id - 10] = data;
        }
        else
        {
            super.updateProgressBar(id, data);
        }
    }

    @Override
    public void addListener(IContainerListener listener)
    {
        super.addListener(listener);
        this.broadcastData(listener);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for(IContainerListener listener : this.listeners)
        {
            this.broadcastData(listener);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        this.reagentTableManager.getReagentTable().setUser(null);

        if(!this.reagentTableManager.getWorld().isRemote)
        {
            if(this.reagentTableManager.getInventory().getStackInSlot(0).isItemEnchanted())
            {
                if(!player.isEntityAlive() || player instanceof EntityPlayerMP && ((EntityPlayerMP) player).hasDisconnected())
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
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();

            if(index == 0)
            {
                if(!this.mergeItemStack(slotStack, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index == 1)
            {
                if(!this.mergeItemStack(slotStack, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index == 2)
            {
                if(!this.mergeItemStack(slotStack, 3, 39, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(slotStack.getItem() == Items.DYE && EnumDyeColor.byDyeDamage(slotStack.getMetadata()) == EnumDyeColor.BLUE)
            {
                if(!this.mergeItemStack(slotStack, 1, 2, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(Reagenchant.REAGENT_MANAGER.hasReagent(slotStack.getItem()))
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

                if(slotStack.hasTagCompound())
                {
                    this.inventorySlots.get(0).putStack(slotStack.splitStack(1));
                }
                else if(!slotStack.isEmpty())
                {
                    this.inventorySlots.get(0).putStack(new ItemStack(slotStack.getItem(), 1, slotStack.getMetadata()));
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
    public boolean canInteractWith(EntityPlayer player)
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
    public boolean enchantItem(EntityPlayer player, int enchantmentIndex)
    {
        return this.reagentTableManager.enchantItem(player, enchantmentIndex, this);
    }

    private void broadcastData(IContainerListener listener)
    {
        listener.sendWindowProperty(this, 0, this.reagentTableManager.getEnchantabilityLevels()[0]);
        listener.sendWindowProperty(this, 1, this.reagentTableManager.getEnchantabilityLevels()[1]);
        listener.sendWindowProperty(this, 2, this.reagentTableManager.getEnchantabilityLevels()[2]);
        listener.sendWindowProperty(this, 3, this.reagentTableManager.getXpSeed() & -16);
        listener.sendWindowProperty(this, 4, this.reagentTableManager.getEnchantmentHints()[0]);
        listener.sendWindowProperty(this, 5, this.reagentTableManager.getEnchantmentHints()[1]);
        listener.sendWindowProperty(this, 6, this.reagentTableManager.getEnchantmentHints()[2]);
        listener.sendWindowProperty(this, 7, this.reagentTableManager.getEnchantmentLevels()[0]);
        listener.sendWindowProperty(this, 8, this.reagentTableManager.getEnchantmentLevels()[1]);
        listener.sendWindowProperty(this, 9, this.reagentTableManager.getEnchantmentLevels()[2]);
        listener.sendWindowProperty(this, 10, this.reagentTableManager.getReagentCosts()[0]);
        listener.sendWindowProperty(this, 11, this.reagentTableManager.getReagentCosts()[1]);
        listener.sendWindowProperty(this, 12, this.reagentTableManager.getReagentCosts()[2]);
    }

    public ReagentTableManager getReagentTableManager()
    {
        return this.reagentTableManager;
    }
}
