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

import logictechcorp.libraryex.utility.RandomHelper;
import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.api.reagent.IReagent;
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
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;
import java.util.Random;

public class ReagentTableManager
{
    private final World world;
    private final BlockPos pos;
    private final TileEntityReagentTable reagentTable;
    private final ItemStackHandler inventory;
    private final Random random;
    private int xpSeed;
    private int[] enchantmentHints;
    private int[] enchantmentLevels;
    private int[] enchantabilityLevels;
    private int[] reagentCosts;

    public ReagentTableManager(World world, BlockPos pos, TileEntityReagentTable reagentTable)
    {
        this.world = world;
        this.pos = pos;
        this.reagentTable = reagentTable;
        this.inventory = reagentTable.getInventory();
        this.random = reagentTable.getRandom();
        this.xpSeed = reagentTable.getUser().getXPSeed();
        this.enchantmentHints = new int[]{-1, -1, -1};
        this.enchantmentLevels = new int[]{-1, -1, -1};
        this.enchantabilityLevels = new int[3];
        this.reagentCosts = new int[3];
    }

    void onContentsChanged(ContainerReagentTable containerReagentTable)
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

                for(int enchantmentTier = 0; enchantmentTier < 3; enchantmentTier++)
                {
                    this.enchantmentHints[enchantmentTier] = -1;
                    this.enchantmentLevels[enchantmentTier] = -1;
                    this.enchantabilityLevels[enchantmentTier] = EnchantmentHelper.calcItemStackEnchantability(this.random, enchantmentTier, (int) power, unenchantedStack);

                    if(this.enchantabilityLevels[enchantmentTier] < enchantmentTier + 1)
                    {
                        this.enchantabilityLevels[enchantmentTier] = 0;
                    }

                    this.enchantabilityLevels[enchantmentTier] = ForgeEventFactory.onEnchantmentLevelSet(this.world, this.pos, enchantmentTier, (int) power, unenchantedStack, this.enchantabilityLevels[enchantmentTier]);
                    this.reagentCosts[enchantmentTier] = 0;
                }

                for(int enchantmentTier = 0; enchantmentTier < 3; enchantmentTier++)
                {
                    if(this.enchantabilityLevels[enchantmentTier] > 0)
                    {
                        List<EnchantmentData> enchantments = this.createEnchantmentList(enchantmentTier);

                        if(enchantments != null && !enchantments.isEmpty())
                        {
                            EnchantmentData enchantmentData = enchantments.get(this.random.nextInt(enchantments.size()));
                            this.enchantmentHints[enchantmentTier] = Enchantment.getEnchantmentID(enchantmentData.enchantment);
                            this.enchantmentLevels[enchantmentTier] = enchantmentData.enchantmentLevel;

                            ItemStack reagentStack = this.inventory.getStackInSlot(2);
                            int reagentCost = 0;

                            if(!reagentStack.isEmpty())
                            {
                                EntityPlayer player = this.reagentTable.getUser();
                                IReagent reagent = ReagenchantAPI.getInstance().getReagentRegistry().getReagent(reagentStack.getItem());

                                for(Enchantment enchantment : reagent.getReagentEnchantmentData())
                                {
                                    if(enchantment.canApply(unenchantedStack))
                                    {
                                        reagentCost += reagent.getReagentCost(this.world, this.pos, player, unenchantedStack, reagentStack, new EnchantmentData(enchantment, reagent.getEnchantmentLevel(enchantment, enchantmentTier, this.enchantabilityLevels[enchantmentTier], this.random)), this.random);
                                    }
                                }
                            }

                            this.reagentCosts[enchantmentTier] = reagentCost;
                        }
                    }
                }

                containerReagentTable.detectAndSendChanges();
            }
        }
        else
        {
            for(int enchantmentTier = 0; enchantmentTier < 3; enchantmentTier++)
            {
                this.enchantmentHints[enchantmentTier] = -1;
                this.enchantmentLevels[enchantmentTier] = -1;
                this.enchantabilityLevels[enchantmentTier] = 0;
                this.reagentCosts[enchantmentTier] = 0;
            }
        }
    }

    private List<EnchantmentData> createEnchantmentList(int enchantmentTier)
    {
        ItemStack unenchantedStack = this.inventory.getStackInSlot(0);
        ItemStack reagentStack = this.inventory.getStackInSlot(2);
        int enchantabilityLevel = this.enchantabilityLevels[enchantmentTier];

        this.random.setSeed(this.xpSeed + enchantmentTier);
        List<EnchantmentData> enchantments = EnchantmentHelper.buildEnchantmentList(this.random, unenchantedStack, enchantabilityLevel, false);
        boolean usedReagentEnchantments = false;

        if(!reagentStack.isEmpty())
        {
            IReagent reagent = ReagenchantAPI.getInstance().getReagentRegistry().getReagent(reagentStack.getItem());

            if(reagent.hasApplicableEnchantments(this.world, this.pos, this.reagentTable.getUser(), unenchantedStack, reagentStack, this.random))
            {
                enchantments = reagent.createEnchantmentList(this.world, this.pos, this.reagentTable.getUser(), unenchantedStack, reagentStack, enchantmentTier, enchantabilityLevel, this.random);
                usedReagentEnchantments = true;
            }
        }

        if(unenchantedStack.getItem() == Items.BOOK && enchantments.size() > 1)
        {
            if(usedReagentEnchantments)
            {
                enchantments.remove(RandomHelper.getNumberInRange(1, enchantments.size() - 1, this.random));
            }
            else
            {
                enchantments.remove(this.random.nextInt(enchantments.size()));
            }
        }

        return enchantments;
    }

    boolean enchantItem(EntityPlayer player, int enchantmentTier, ContainerReagentTable containerReagentTable)
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
                List<EnchantmentData> enchantments = this.createEnchantmentList(enchantmentTier);

                if(!enchantments.isEmpty())
                {
                    ItemStack unenchantedStackCopy = unenchantedStack.copy();
                    boolean itemIsBook = unenchantedStack.getItem() == Items.BOOK;

                    if(itemIsBook)
                    {
                        unenchantedStack = new ItemStack(Items.ENCHANTED_BOOK);
                        this.inventory.setStackInSlot(0, unenchantedStack);
                    }

                    IReagent reagent = null;

                    if(!reagentStack.isEmpty())
                    {
                        reagent = ReagenchantAPI.getInstance().getReagentRegistry().getReagent(reagentStack.getItem());
                    }

                    int reagentCost = 0;

                    for(EnchantmentData enchantmentData : enchantments)
                    {
                        if(!EnchantmentHelper.getEnchantments(unenchantedStack).keySet().contains(enchantmentData.enchantment))
                        {
                            if(reagent != null)
                            {
                                reagentCost = this.reagentCosts[enchantmentTier];

                                if(itemIsBook)
                                {
                                    if(reagentCost > reagentStack.getCount())
                                    {
                                        return false;
                                    }
                                    else
                                    {
                                        ItemEnchantedBook.addEnchantment(unenchantedStack, enchantmentData);
                                    }
                                }
                                else
                                {
                                    if(reagentCost > reagentStack.getCount())
                                    {
                                        return false;
                                    }
                                    else
                                    {
                                        unenchantedStack.addEnchantment(enchantmentData.enchantment, enchantmentData.enchantmentLevel);
                                    }
                                }
                            }
                            else
                            {
                                if(itemIsBook)
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

                    player.onEnchant(unenchantedStack, i);

                    if(!player.capabilities.isCreativeMode)
                    {
                        lapisStack.shrink(i);

                        if(lapisStack.isEmpty())
                        {
                            this.inventory.setStackInSlot(1, ItemStack.EMPTY);
                        }

                        if(reagent != null)
                        {
                            if(reagent.consumeReagent(this.world, this.pos, player, itemIsBook ? unenchantedStackCopy : unenchantedStack, reagentStack, enchantments, this.random))
                            {
                                reagentStack.shrink(reagentCost);

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

    public TileEntityReagentTable getReagentTable()
    {
        return this.reagentTable;
    }

    public ItemStackHandler getInventory()
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

    public int[] getEnchantmentHints()
    {
        return this.enchantmentHints;
    }

    public int[] getEnchantmentLevels()
    {
        return this.enchantmentLevels;
    }

    public int[] getEnchantabilityLevels()
    {
        return this.enchantabilityLevels;
    }

    public int[] getReagentCosts()
    {
        return this.reagentCosts;
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

    void setXpSeed(int xpSeed)
    {
        this.xpSeed = xpSeed;
    }
}
