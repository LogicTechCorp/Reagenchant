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

import logictechcorp.reagenchant.api.reagent.IReagent;
import logictechcorp.reagenchant.registry.ReagentRegistry;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.*;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Random;

public class ContainerReagenchantTable extends Container
{
    private final World world;
    private final BlockPos pos;
    private final EntityPlayer player;
    private final Random rand;
    public IInventory inventory;
    public int xpSeed;
    public int[] enchantLevels;
    public int[] enchantClue;
    public int[] worldClue;

    @SideOnly(Side.CLIENT)
    public ContainerReagenchantTable(InventoryPlayer playerInventory, World world)
    {
        this(playerInventory, world, BlockPos.ORIGIN);
    }

    public ContainerReagenchantTable(InventoryPlayer playerInventory, World world, BlockPos pos)
    {
        this.world = world;
        this.pos = pos;
        this.player = playerInventory.player;
        this.rand = new Random();
        this.inventory = new InventoryBasic("Reagenchant", true, 3)
        {
            @Override
            public int getInventoryStackLimit()
            {
                return 64;
            }

            @Override
            public void markDirty()
            {
                super.markDirty();
                ContainerReagenchantTable.this.onCraftMatrixChanged(this);
            }
        };
        this.xpSeed = playerInventory.player.getXPSeed();
        this.enchantLevels = new int[3];
        this.enchantClue = new int[]{-1, -1, -1};
        this.worldClue = new int[]{-1, -1, -1};
        this.addSlotToContainer(new Slot(this.inventory, 0, 6, 47)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return true;
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });
        this.addSlotToContainer(new Slot(this.inventory, 1, 24, 47)
        {
            List<ItemStack> ores = OreDictionary.getOres("gemLapis");

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                for(ItemStack ore : ores)
                {
                    if(OreDictionary.itemMatches(ore, stack, false))
                    {
                        return true;
                    }
                }
                return false;
            }
        });
        this.addSlotToContainer(new Slot(this.inventory, 2, 32, 47)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return ReagentRegistry.isReagentItem(stack.getItem());
            }
        });

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 9; j++)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; k++)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    protected void broadcastData(IContainerListener listener)
    {
        listener.sendWindowProperty(this, 0, this.enchantLevels[0]);
        listener.sendWindowProperty(this, 1, this.enchantLevels[1]);
        listener.sendWindowProperty(this, 2, this.enchantLevels[2]);
        listener.sendWindowProperty(this, 3, this.xpSeed & -16);
        listener.sendWindowProperty(this, 4, this.enchantClue[0]);
        listener.sendWindowProperty(this, 5, this.enchantClue[1]);
        listener.sendWindowProperty(this, 6, this.enchantClue[2]);
        listener.sendWindowProperty(this, 7, this.worldClue[0]);
        listener.sendWindowProperty(this, 8, this.worldClue[1]);
        listener.sendWindowProperty(this, 9, this.worldClue[2]);
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
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if(id >= 0 && id <= 2)
        {
            this.enchantLevels[id] = data;
        }
        else if(id == 3)
        {
            this.xpSeed = data;
        }
        else if(id >= 4 && id <= 6)
        {
            this.enchantClue[id - 4] = data;
        }
        else if(id >= 7 && id <= 9)
        {
            this.worldClue[id - 7] = data;
        }
        else
        {
            super.updateProgressBar(id, data);
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        if(this.inventory == inventory)
        {
            ItemStack unenchantedStack = inventory.getStackInSlot(0);
            ItemStack reagentStack = inventory.getStackInSlot(2);

            if(!unenchantedStack.isEmpty() && unenchantedStack.isItemEnchantable())
            {
                if(!this.world.isRemote)
                {
                    float power = 0;

                    for(int z = -1; z <= 1; z++)
                    {
                        for(int x = -1; x <= 1; x++)
                        {
                            if((z != 0 || x != 0) && this.world.isAirBlock(this.pos.add(x, 0, z)) && this.world.isAirBlock(this.pos.add(x, 1, z)))
                            {
                                power += ForgeHooks.getEnchantPower(world, pos.add(x * 2, 0, z * 2));
                                power += ForgeHooks.getEnchantPower(world, pos.add(x * 2, 1, z * 2));

                                if(x != 0 && z != 0)
                                {
                                    power += ForgeHooks.getEnchantPower(world, pos.add(x * 2, 0, z));
                                    power += ForgeHooks.getEnchantPower(world, pos.add(x * 2, 1, z));
                                    power += ForgeHooks.getEnchantPower(world, pos.add(x, 0, z * 2));
                                    power += ForgeHooks.getEnchantPower(world, pos.add(x, 1, z * 2));
                                }
                            }
                        }
                    }

                    this.rand.setSeed((long) this.xpSeed);

                    for(int i = 0; i < 3; i++)
                    {
                        this.enchantLevels[i] = EnchantmentHelper.calcItemStackEnchantability(this.rand, i, (int) power, unenchantedStack);
                        this.enchantClue[i] = -1;
                        this.worldClue[i] = -1;

                        if(this.enchantLevels[i] < i + 1)
                        {
                            this.enchantLevels[i] = 0;
                        }
                        this.enchantLevels[i] = ForgeEventFactory.onEnchantmentLevelSet(world, pos, i, (int) power, unenchantedStack, enchantLevels[i]);
                    }

                    for(int i = 0; i < 3; i++)
                    {
                        if(this.enchantLevels[i] > 0)
                        {
                            List<EnchantmentData> enchantmentList = this.getEnchantmentList(unenchantedStack, reagentStack, i, this.enchantLevels[i]);

                            if(enchantmentList != null && !enchantmentList.isEmpty())
                            {
                                EnchantmentData enchantmentData = enchantmentList.get(this.rand.nextInt(enchantmentList.size()));
                                this.enchantClue[i] = Enchantment.getEnchantmentID(enchantmentData.enchantment);
                                this.worldClue[i] = enchantmentData.enchantmentLevel;
                            }
                        }
                    }

                    this.detectAndSendChanges();
                }
            }
            else
            {
                for(int i = 0; i < 3; i++)
                {
                    this.enchantLevels[i] = 0;
                    this.enchantClue[i] = -1;
                    this.worldClue[i] = -1;
                }
            }
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer player, int enchantmentTier)
    {
        ItemStack unenchantedStack = this.inventory.getStackInSlot(0);
        ItemStack lapisStack = this.inventory.getStackInSlot(1);
        ItemStack reagentStack = this.inventory.getStackInSlot(2);
        int i = enchantmentTier + 1;

        if((lapisStack.isEmpty() || lapisStack.getCount() < i) && !player.capabilities.isCreativeMode)
        {
            return false;
        }
        else if(this.enchantLevels[enchantmentTier] > 0 && !unenchantedStack.isEmpty() && (player.experienceLevel >= i && player.experienceLevel >= this.enchantLevels[enchantmentTier] || player.capabilities.isCreativeMode))
        {
            if(!this.world.isRemote)
            {
                List<EnchantmentData> enchantmentList = this.getEnchantmentList(unenchantedStack, reagentStack, enchantmentTier, this.enchantLevels[enchantmentTier]);

                if(!enchantmentList.isEmpty())
                {
                    player.onEnchant(unenchantedStack, i);
                    boolean flag = unenchantedStack.getItem() == Items.BOOK;

                    if(flag)
                    {
                        unenchantedStack = new ItemStack(Items.ENCHANTED_BOOK);
                        this.inventory.setInventorySlotContents(0, unenchantedStack);
                    }

                    for(EnchantmentData enchantmentData : enchantmentList)
                    {
                        if(flag)
                        {
                            ItemEnchantedBook.addEnchantment(unenchantedStack, enchantmentData);
                        }
                        else
                        {
                            unenchantedStack.addEnchantment(enchantmentData.enchantment, enchantmentData.enchantmentLevel);
                        }
                    }

                    if(!player.capabilities.isCreativeMode)
                    {
                        lapisStack.shrink(i);

                        if(lapisStack.isEmpty())
                        {
                            this.inventory.setInventorySlotContents(1, ItemStack.EMPTY);
                        }

                        if(!reagentStack.isEmpty())
                        {
                            IReagent reagent = ReagentRegistry.getReagent(reagentStack.getItem());

                            if(reagent.consumeReagent(world, pos, player, unenchantedStack, reagentStack, enchantmentTier, this.enchantLevels[enchantmentTier], enchantmentList, this.rand))
                            {
                                reagentStack.shrink(i);

                                if(reagentStack.isEmpty())
                                {
                                    this.inventory.setInventorySlotContents(2, ItemStack.EMPTY);
                                }
                            }
                        }
                    }

                    player.addStat(StatList.ITEM_ENCHANTED);

                    if(player instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP) player, unenchantedStack, i);
                    }

                    this.inventory.markDirty();
                    this.xpSeed = player.getXPSeed();
                    this.onCraftMatrixChanged(this.inventory);
                    this.world.playSound(null, this.pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F);
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    private List<EnchantmentData> getEnchantmentList(ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantmentLevel)
    {
        this.rand.setSeed((long) (this.xpSeed + enchantmentTier));
        List<EnchantmentData> enchantmentData = EnchantmentHelper.buildEnchantmentList(this.rand, unenchantedStack, enchantmentLevel, false);
        ;

        if(!reagentStack.isEmpty())
        {
            IReagent reagent = ReagentRegistry.getReagent(reagentStack.getItem());

            if(reagent.hasApplicableEnchantments(this.world, this.pos, this.player, unenchantedStack, reagentStack, enchantmentTier, enchantmentLevel, this.rand))
            {
                enchantmentData = reagent.buildEnchantmentList(this.world, this.pos, this.player, unenchantedStack, reagentStack, enchantmentTier, enchantmentLevel, false, this.rand);
            }
        }

        if(unenchantedStack.getItem() == Items.BOOK && enchantmentData.size() > 1)
        {
            enchantmentData.remove(this.rand.nextInt(enchantmentData.size()));
        }

        return enchantmentData;
    }

    @SideOnly(Side.CLIENT)
    public int getLapisAmount()
    {
        ItemStack stack = this.inventory.getStackInSlot(1);
        return stack.isEmpty() ? 0 : stack.getCount();
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if(!this.world.isRemote)
        {
            this.clearContainer(playerIn, playerIn.world, this.inventory);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        if(this.world.getBlockState(this.pos).getBlock() != Blocks.ENCHANTING_TABLE)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
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
}
