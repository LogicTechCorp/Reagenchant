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
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.reagent.Reagent;
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
import net.minecraft.util.Util;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
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
    private final int[] enchantmentHints;
    private final int[] enchantmentLevels;
    private final int[] enchantabilityLevels;
    private final int[] reagentCosts;

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

                this.random.setSeed(this.xpSeed);

                for(int enchantmentIndex = 0; enchantmentIndex < 3; enchantmentIndex++)
                {
                    this.enchantmentHints[enchantmentIndex] = -1;
                    this.enchantmentLevels[enchantmentIndex] = -1;
                    this.enchantabilityLevels[enchantmentIndex] = EnchantmentHelper.calcItemStackEnchantability(this.random, enchantmentIndex, (int) power, unenchantedStack);

                    if(this.enchantabilityLevels[enchantmentIndex] < enchantmentIndex + 1)
                    {
                        this.enchantabilityLevels[enchantmentIndex] = 0;
                    }

                    this.enchantabilityLevels[enchantmentIndex] = ForgeEventFactory.onEnchantmentLevelSet(this.world, this.pos, enchantmentIndex, (int) power, unenchantedStack, this.enchantabilityLevels[enchantmentIndex]);
                    this.reagentCosts[enchantmentIndex] = 0;
                }

                for(int enchantmentIndex = 0; enchantmentIndex < 3; enchantmentIndex++)
                {
                    if(this.enchantabilityLevels[enchantmentIndex] > 0)
                    {
                        List<EnchantmentData> enchantments = this.compileEnchantmentList(enchantmentIndex);

                        if(!enchantments.isEmpty())
                        {
                            EnchantmentData randomEnchantmentData = enchantments.get(this.random.nextInt(enchantments.size()));
                            this.enchantmentHints[enchantmentIndex] = Enchantment.getEnchantmentID(randomEnchantmentData.enchantment);
                            this.enchantmentLevels[enchantmentIndex] = randomEnchantmentData.enchantmentLevel;
                            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(this.inventory.getStackInSlot(2).getItem());

                            if(!reagent.isEmpty())
                            {
                                for(EnchantmentData enchantmentData : enchantments)
                                {
                                    int reagentCost = reagent.getCost(enchantmentData.enchantment);

                                    if(reagentCost > this.reagentCosts[enchantmentIndex])
                                    {
                                        this.reagentCosts[enchantmentIndex] = reagentCost;
                                    }
                                }
                            }
                            else
                            {
                                this.reagentCosts[enchantmentIndex] = 0;
                            }
                        }
                    }
                }

                containerReagentTable.detectAndSendChanges();
            }
        }
        else
        {
            for(int enchantmentIndex = 0; enchantmentIndex < 3; enchantmentIndex++)
            {
                this.enchantmentHints[enchantmentIndex] = -1;
                this.enchantmentLevels[enchantmentIndex] = -1;
                this.enchantabilityLevels[enchantmentIndex] = 0;
                this.reagentCosts[enchantmentIndex] = 0;
            }
        }
    }

    private List<EnchantmentData> compileEnchantmentList(int enchantmentIndex)
    {
        ItemStack unenchantedStack = this.inventory.getStackInSlot(0);
        ItemStack reagentStack = this.inventory.getStackInSlot(2);
        int enchantabilityLevel = this.enchantabilityLevels[enchantmentIndex];

        this.random.setSeed((this.xpSeed + enchantmentIndex));
        List<EnchantmentData> reagentEnchantments = new ArrayList<>();
        List<EnchantmentData> defaultEnchantments = EnchantmentHelper.buildEnchantmentList(this.random, unenchantedStack, enchantabilityLevel, false);
        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());

        if(!reagent.isEmpty() && reagent.canApplyEnchantments(unenchantedStack))
        {
            reagentEnchantments = reagent.compileEnchantmentList(unenchantedStack, enchantmentIndex, enchantabilityLevel, this.random);
        }

        List<EnchantmentData> refinedEnchantments = new ArrayList<>();

        if(!reagentEnchantments.isEmpty())
        {
            EnchantmentData removedEnchantment = WeightedRandom.getRandomItem(this.random, reagentEnchantments);
            refinedEnchantments.add(reagentEnchantments.remove(reagentEnchantments.indexOf(removedEnchantment)));

            while(this.random.nextInt(50) <= enchantabilityLevel)
            {
                EnchantmentHelper.removeIncompatible(defaultEnchantments, Util.getLastElement(refinedEnchantments));

                if(defaultEnchantments.isEmpty())
                {
                    break;
                }

                if(!reagentEnchantments.isEmpty())
                {
                    removedEnchantment = WeightedRandom.getRandomItem(this.random, reagentEnchantments);
                    refinedEnchantments.add(reagentEnchantments.remove(reagentEnchantments.indexOf(removedEnchantment)));
                }
                else
                {
                    refinedEnchantments.add(WeightedRandom.getRandomItem(this.random, defaultEnchantments));
                }

                enchantabilityLevel /= 2;
            }
        }
        else
        {
            refinedEnchantments.addAll(defaultEnchantments);
        }

        if(unenchantedStack.getItem() == Items.BOOK && refinedEnchantments.size() > 1)
        {
            if(!reagentEnchantments.isEmpty())
            {
                refinedEnchantments.remove(RandomHelper.getNumberInRange(1, refinedEnchantments.size() - 1, this.random));
            }
            else
            {
                refinedEnchantments.remove(this.random.nextInt(refinedEnchantments.size()));
            }
        }

        return refinedEnchantments;
    }

    boolean enchantItem(EntityPlayer player, int enchantmentIndex, ContainerReagentTable containerReagentTable)
    {
        ItemStack unenchantedStack = this.inventory.getStackInSlot(0);
        ItemStack lapisStack = this.inventory.getStackInSlot(1);
        ItemStack reagentStack = this.inventory.getStackInSlot(2);
        int lapisCost = enchantmentIndex + 1;

        if((lapisStack.isEmpty() || lapisStack.getCount() < lapisCost || reagentStack.getCount() < this.reagentCosts[enchantmentIndex]) && !player.capabilities.isCreativeMode)
        {
            return false;
        }
        else if(this.enchantabilityLevels[enchantmentIndex] > 0 && !unenchantedStack.isEmpty() && (player.experienceLevel >= lapisCost && player.experienceLevel >= this.enchantabilityLevels[enchantmentIndex] || player.capabilities.isCreativeMode))
        {
            if(!this.world.isRemote)
            {
                List<EnchantmentData> enchantments = this.compileEnchantmentList(enchantmentIndex);
                Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());

                if(!enchantments.isEmpty())
                {
                    ItemStack enchantedStack = unenchantedStack;
                    boolean isBook = unenchantedStack.getItem() == Items.BOOK;
                    player.onEnchant(unenchantedStack, lapisCost);

                    if(isBook)
                    {
                        enchantedStack = new ItemStack(Items.ENCHANTED_BOOK);
                        this.inventory.setStackInSlot(0, enchantedStack);
                    }

                    for(EnchantmentData enchantmentData : enchantments)
                    {
                        if(isBook)
                        {
                            ItemEnchantedBook.addEnchantment(enchantedStack, enchantmentData);
                        }
                        else
                        {
                            enchantedStack.addEnchantment(enchantmentData.enchantment, enchantmentData.enchantmentLevel);
                        }
                    }

                    if(!player.capabilities.isCreativeMode)
                    {
                        lapisStack.shrink(lapisCost);

                        if(lapisStack.isEmpty())
                        {
                            this.inventory.setStackInSlot(1, ItemStack.EMPTY);
                        }

                        if(!reagent.isEmpty() && reagent.consumeReagent(unenchantedStack, enchantments))
                        {
                            reagentStack.shrink(this.reagentCosts[enchantmentIndex]);

                            if(reagentStack.isEmpty())
                            {
                                this.inventory.setStackInSlot(2, ItemStack.EMPTY);
                            }
                        }
                    }

                    player.addStat(StatList.ITEM_ENCHANTED);

                    if(player instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP) player, unenchantedStack, lapisCost);
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
