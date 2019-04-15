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

package logictechcorp.reagenchant.reagent;

import logictechcorp.libraryex.utility.RandomHelper;
import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.api.reagent.IReagent;
import logictechcorp.reagenchant.inventory.ContainerReagentTable;
import logictechcorp.reagenchant.tileentity.TileEntityReagentTable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;
import java.util.Random;

public class ReagentTableManager
{
    private final World world;
    private final BlockPos pos;
    private final EntityPlayer player;
    private final TileEntityReagentTable reagentTable;
    private final ItemStackHandler inventory;
    private final Random random;
    private int xpSeed;
    private int[] enchantments;
    private int[] enchantmentLevels;
    private int[] enchantabilityLevels;

    public ReagentTableManager(World world, BlockPos pos, EntityPlayer player, TileEntityReagentTable reagentTable)
    {
        this.world = world;
        this.pos = pos;
        this.player = player;
        this.reagentTable = reagentTable;
        this.inventory = reagentTable.getInventory();
        this.random = new Random();
        this.xpSeed = player.inventory.player.getXPSeed();
        this.enchantments = new int[]{-1, -1, -1};
        this.enchantmentLevels = new int[]{-1, -1, -1};
        this.enchantabilityLevels = new int[3];
    }

    public void onContentsChanged(ContainerReagentTable containerReagentTable)
    {
        ItemStack unenchantedStack = this.inventory.getStackInSlot(0);

        if(unenchantedStack.isItemEnchantable() && !this.inventory.getStackInSlot(1).isEmpty())
        {
            if(!this.world.isRemote)
            {
                float power = 0;

                for(int z = -1; z <= 1; z++)
                {
                    for(int x = -1; x <= 1; x++)
                    {
                        if((z != 0 || x != 0) && !this.world.getBlockState(this.pos.add(x, 0, z)).isOpaqueCube() && !this.world.getBlockState(this.pos.add(x, 1, z)).isOpaqueCube())
                        {
                            power += ForgeHooks.getEnchantPower(this.world, this.pos.add(x * 2, 0, z * 2));
                            power += ForgeHooks.getEnchantPower(this.world, this.pos.add(x * 2, 1, z * 2));

                            if(x != 0 && z != 0)
                            {
                                power += ForgeHooks.getEnchantPower(this.world, this.pos.add(x * 2, 0, z));
                                power += ForgeHooks.getEnchantPower(this.world, this.pos.add(x * 2, 1, z));
                                power += ForgeHooks.getEnchantPower(this.world, this.pos.add(x, 0, z * 2));
                                power += ForgeHooks.getEnchantPower(this.world, this.pos.add(x, 1, z * 2));
                            }
                        }
                    }
                }

                this.random.setSeed((long) this.xpSeed);

                for(int i = 0; i < 3; i++)
                {
                    this.enchantments[i] = -1;
                    this.enchantmentLevels[i] = -1;
                    this.enchantabilityLevels[i] = EnchantmentHelper.calcItemStackEnchantability(this.random, i, (int) power, unenchantedStack);

                    if(this.enchantabilityLevels[i] < i + 1)
                    {
                        this.enchantabilityLevels[i] = 0;
                    }

                    this.enchantabilityLevels[i] = ForgeEventFactory.onEnchantmentLevelSet(this.world, this.pos, i, (int) power, unenchantedStack, this.enchantabilityLevels[i]);
                }

                for(int i = 0; i < 3; i++)
                {
                    if(this.enchantabilityLevels[i] > 0)
                    {
                        List<EnchantmentData> enchantmentList = this.createEnchantmentList(i);

                        if(enchantmentList != null && !enchantmentList.isEmpty())
                        {
                            EnchantmentData enchantmentData = enchantmentList.get(this.random.nextInt(enchantmentList.size()));
                            this.enchantments[i] = Enchantment.getEnchantmentID(enchantmentData.enchantment);
                            this.enchantmentLevels[i] = enchantmentData.enchantmentLevel;
                        }
                    }
                }

                containerReagentTable.detectAndSendChanges();
            }
        }
        else
        {
            for(int i = 0; i < 3; i++)
            {
                this.enchantments[i] = -1;
                this.enchantmentLevels[i] = -1;
                this.enchantabilityLevels[i] = 0;
            }
        }
    }

    private List<EnchantmentData> createEnchantmentList(int enchantmentTier)
    {
        ItemStack unenchantedStack = this.inventory.getStackInSlot(0);
        ItemStack reagentStack = this.inventory.getStackInSlot(2);
        int enchantabilityLevel = this.enchantabilityLevels[enchantmentTier];

        this.random.setSeed((long) (this.xpSeed + enchantmentTier));
        List<EnchantmentData> enchantmentData = EnchantmentHelper.buildEnchantmentList(this.random, unenchantedStack, enchantabilityLevel, false);
        boolean usedReagentEnchantments = false;

        if(!reagentStack.isEmpty())
        {
            IReagent reagent = ReagenchantAPI.getInstance().getReagentRegistry().getReagent(reagentStack.getItem());

            if(reagent.hasApplicableEnchantments(this.world, this.pos, this.player, unenchantedStack, reagentStack, this.random))
            {
                enchantmentData = reagent.createEnchantmentList(this.world, this.pos, this.player, unenchantedStack, reagentStack, enchantmentTier, enchantabilityLevel, this.random);
                usedReagentEnchantments = true;
            }
        }

        if(unenchantedStack.getItem() == Items.BOOK && enchantmentData.size() > 1)
        {
            if(usedReagentEnchantments)
            {
                enchantmentData.remove(RandomHelper.getNumberInRange(1, enchantmentData.size() - 1, this.random));
            }
            else
            {
                enchantmentData.remove(this.random.nextInt(enchantmentData.size()));
            }
        }

        return enchantmentData;
    }

    public boolean enchantItem(EntityPlayer player, int enchantmentTier, ContainerReagentTable containerReagentTable)
    {
        ItemStack unenchantedStack = this.inventory.getStackInSlot(0);
        ItemStack lapisStack = this.inventory.getStackInSlot(1);
        ItemStack reagentStack = this.inventory.getStackInSlot(2);
        int i = enchantmentTier + 1;

        if((lapisStack.isEmpty() || lapisStack.getCount() < i) && !player.capabilities.isCreativeMode)
        {
            return false;
        }
        else if(this.enchantabilityLevels[enchantmentTier] > 0 && !unenchantedStack.isEmpty() && (player.experienceLevel >= i && player.experienceLevel >= this.enchantabilityLevels[enchantmentTier] || player.capabilities.isCreativeMode))
        {
            if(!this.world.isRemote)
            {
                List<EnchantmentData> enchantmentList = this.createEnchantmentList(enchantmentTier);

                if(!enchantmentList.isEmpty())
                {
                    ItemStack unenchantedStackCopy = unenchantedStack.copy();
                    boolean flag = unenchantedStack.getItem() == Items.BOOK;
                    player.onEnchant(unenchantedStack, i);

                    if(flag)
                    {
                        unenchantedStack = new ItemStack(Items.ENCHANTED_BOOK);
                        this.inventory.setStackInSlot(0, unenchantedStack);
                    }

                    IReagent reagent = null;

                    if(!reagentStack.isEmpty())
                    {
                        reagent = ReagenchantAPI.getInstance().getReagentRegistry().getReagent(reagentStack.getItem());
                    }

                    int maxReagentCost = 0;

                    for(EnchantmentData enchantmentData : enchantmentList)
                    {
                        if(!EnchantmentHelper.getEnchantments(unenchantedStack).keySet().contains(enchantmentData.enchantment))
                        {
                            if(reagent != null)
                            {
                                int reagentCost = reagent.getReagentCost(this.world, this.pos, player, unenchantedStack, reagentStack, enchantmentData, this.random);

                                if(flag)
                                {
                                    if(reagentCost <= reagentStack.getCount())
                                    {
                                        if(maxReagentCost < reagentCost)
                                        {
                                            maxReagentCost = reagentCost;
                                        }

                                        ItemEnchantedBook.addEnchantment(unenchantedStack, enchantmentData);
                                    }
                                }
                                else
                                {
                                    if(reagentCost <= reagentStack.getCount())
                                    {
                                        if(maxReagentCost < reagentCost)
                                        {
                                            maxReagentCost = reagentCost;
                                        }

                                        unenchantedStack.addEnchantment(enchantmentData.enchantment, enchantmentData.enchantmentLevel);
                                    }
                                }
                            }
                            else
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
                        }
                    }

                    if(!player.capabilities.isCreativeMode)
                    {
                        lapisStack.shrink(i);

                        if(lapisStack.isEmpty())
                        {
                            this.inventory.setStackInSlot(1, ItemStack.EMPTY);
                        }

                        if(reagent != null)
                        {
                            if(reagent.consumeReagent(this.world, this.pos, player, flag ? unenchantedStackCopy : unenchantedStack, reagentStack, enchantmentList, this.random))
                            {
                                reagentStack.shrink(maxReagentCost);

                                if(reagentStack.isEmpty())
                                {
                                    this.inventory.setStackInSlot(2, ItemStack.EMPTY);
                                }
                            }
                        }
                    }

                    player.addStat(StatList.ITEM_ENCHANTED);

                    if(player instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP) player, unenchantedStack, i);
                    }

                    this.reagentTable.markDirty();
                    this.xpSeed = player.getXPSeed();
                    this.onContentsChanged(containerReagentTable);
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

    public World getWorld()
    {
        return this.world;
    }

    public BlockPos getPos()
    {
        return this.pos;
    }

    public EntityPlayer getPlayer()
    {
        return this.player;
    }

    public TileEntityReagentTable getReagentTable()
    {
        return this.reagentTable;
    }

    public IItemHandler getInventory()
    {
        return this.inventory;
    }

    public Random getRandom()
    {
        return this.random;
    }

    public int getXpSeed()
    {
        return this.xpSeed;
    }

    public int[] getEnchantments()
    {
        return this.enchantments;
    }

    public int[] getEnchantmentLevels()
    {
        return this.enchantmentLevels;
    }

    public int[] getEnchantabilityLevels()
    {
        return this.enchantabilityLevels;
    }

    public int getLapisAmount()
    {
        ItemStack lapisStack = this.inventory.getStackInSlot(1);
        return lapisStack.isEmpty() ? 0 : lapisStack.getCount();
    }

    public int getReagentAmount()
    {
        ItemStack reagentStack = this.inventory.getStackInSlot(2);
        return reagentStack.isEmpty() ? 0 : reagentStack.getCount();
    }

    public void setXpSeed(int xpSeed)
    {
        this.xpSeed = xpSeed;
    }
}
