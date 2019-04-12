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
import logictechcorp.reagenchant.reagent.ReagentTableManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
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
            List<ItemStack> ores = OreDictionary.getOres("gemLapis");

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
                return ReagenchantAPI.getInstance().getReagentRegistry().isReagentItem(stack.getItem());
            }

            @Override
            public int getSlotStackLimit()
            {
                return 64;
            }
        });

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new Slot(reagentTableManager.getPlayer().inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; k++)
        {
            this.addSlotToContainer(new Slot(reagentTableManager.getPlayer().inventory, k, 8 + k * 18, 142));
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

    private void broadcastData(IContainerListener listener)
    {
        listener.sendWindowProperty(this, 0, this.reagentTableManager.getEnchantabilityLevels()[0]);
        listener.sendWindowProperty(this, 1, this.reagentTableManager.getEnchantabilityLevels()[1]);
        listener.sendWindowProperty(this, 2, this.reagentTableManager.getEnchantabilityLevels()[2]);
        listener.sendWindowProperty(this, 3, this.reagentTableManager.getXpSeed() & -16);
        listener.sendWindowProperty(this, 4, this.reagentTableManager.getEnchantments()[0]);
        listener.sendWindowProperty(this, 5, this.reagentTableManager.getEnchantments()[1]);
        listener.sendWindowProperty(this, 6, this.reagentTableManager.getEnchantments()[2]);
        listener.sendWindowProperty(this, 7, this.reagentTableManager.getEnchantmentLevels()[0]);
        listener.sendWindowProperty(this, 8, this.reagentTableManager.getEnchantmentLevels()[1]);
        listener.sendWindowProperty(this, 9, this.reagentTableManager.getEnchantmentLevels()[2]);
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
                if(!this.mergeItemStack(slotStack, 2, 38, true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if(index == 1)
            {
                if(!this.mergeItemStack(slotStack, 2, 38, true))
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
        if(this.reagentTableManager.getWorld().getBlockState(this.reagentTableManager.getPos()).getBlock() != Blocks.ENCHANTING_TABLE)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq((double) this.reagentTableManager.getPos().getX() + 0.5D, (double) this.reagentTableManager.getPos().getY() + 0.5D, (double) this.reagentTableManager.getPos().getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int enchantmentTier)
    {
        return this.reagentTableManager.enchantItem(player, enchantmentTier, this);
    }

    public ReagentTableManager getReagentTableManager()
    {
        return this.reagentTableManager;
    }
}
