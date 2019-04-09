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

import logictechcorp.reagenchant.api.reagent.IReagent;
import logictechcorp.reagenchant.registry.ReagentRegistry;
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

import java.util.List;
import java.util.Random;

public class ReagentTableManager
{
    private final World world;
    private final BlockPos pos;
    private final EntityPlayer player;
    private final TileEntityReagentTable reagentTable;
    private final Random random;
    private int xpSeed;
    private int[] enchantments;
    private int[] enchantmentLevels;
    private int[] experienceLevels;

    public ReagentTableManager(World world, BlockPos pos, EntityPlayer player, TileEntityReagentTable reagentTable)
    {
        this.world = world;
        this.pos = pos;
        this.player = player;
        this.reagentTable = reagentTable;
        this.random = new Random();
        this.xpSeed = player.inventory.player.getXPSeed();
        this.enchantments = new int[]{-1, -1, -1};
        this.enchantmentLevels = new int[3];
        this.experienceLevels = new int[]{-1, -1, -1};
    }

    public void onContentsChanged()
    {
        ItemStack unenchantedStack = this.reagentTable.getInventory().getStackInSlot(0);
        ItemStack reagentStack = this.reagentTable.getInventory().getStackInSlot(2);

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
                    this.enchantmentLevels[i] = EnchantmentHelper.calcItemStackEnchantability(this.random, i, (int) power, unenchantedStack);
                    this.enchantments[i] = -1;
                    this.experienceLevels[i] = -1;

                    if(this.enchantmentLevels[i] < i + 1)
                    {
                        this.enchantmentLevels[i] = 0;
                    }
                    this.enchantmentLevels[i] = ForgeEventFactory.onEnchantmentLevelSet(this.world, this.pos, i, (int) power, unenchantedStack, this.enchantmentLevels[i]);
                }

                for(int i = 0; i < 3; i++)
                {
                    if(this.enchantmentLevels[i] > 0)
                    {
                        List<EnchantmentData> enchantmentList = this.getEnchantmentList(unenchantedStack, reagentStack, i, this.enchantmentLevels[i]);

                        if(enchantmentList != null && !enchantmentList.isEmpty())
                        {
                            EnchantmentData enchantmentData = enchantmentList.get(this.random.nextInt(enchantmentList.size()));
                            this.enchantments[i] = Enchantment.getEnchantmentID(enchantmentData.enchantment);
                            this.experienceLevels[i] = enchantmentData.enchantmentLevel;
                        }
                    }
                }
            }
        }
        else
        {
            for(int i = 0; i < 3; i++)
            {
                this.enchantmentLevels[i] = 0;
                this.enchantments[i] = -1;
                this.experienceLevels[i] = -1;
            }
        }

        this.reagentTable.markDirty();
    }

    public boolean enchantItem(EntityPlayer player, int enchantmentTier)
    {
        ItemStack unenchantedStack = this.reagentTable.getInventory().getStackInSlot(0);
        ItemStack lapisStack = this.reagentTable.getInventory().getStackInSlot(1);
        ItemStack reagentStack = this.reagentTable.getInventory().getStackInSlot(2);
        int i = enchantmentTier + 1;

        if((lapisStack.isEmpty() || lapisStack.getCount() < i) && !player.capabilities.isCreativeMode)
        {
            return false;
        }
        else if(this.enchantmentLevels[enchantmentTier] > 0 && !unenchantedStack.isEmpty() && (player.experienceLevel >= i && player.experienceLevel >= this.enchantmentLevels[enchantmentTier] || player.capabilities.isCreativeMode))
        {
            if(!this.world.isRemote)
            {
                List<EnchantmentData> enchantmentList = this.getEnchantmentList(unenchantedStack, reagentStack, enchantmentTier, this.enchantmentLevels[enchantmentTier]);

                if(!enchantmentList.isEmpty())
                {
                    player.onEnchant(unenchantedStack, i);
                    boolean flag = unenchantedStack.getItem() == Items.BOOK;

                    if(flag)
                    {
                        unenchantedStack = new ItemStack(Items.ENCHANTED_BOOK);
                        this.reagentTable.getInventory().setStackInSlot(0, unenchantedStack);
                    }

                    IReagent reagent = null;

                    if(!reagentStack.isEmpty())
                    {
                        reagent = ReagentRegistry.getReagent(reagentStack.getItem());
                    }

                    int maxReagentCost = 1;

                    for(EnchantmentData enchantmentData : enchantmentList)
                    {
                        if(reagent != null)
                        {
                            if(flag)
                            {
                                int reagentCost = reagent.getReagentCost(this.world, this.pos, player, unenchantedStack, reagentStack, enchantmentData, this.random);

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
                                int reagentCost = reagent.getReagentCost(this.world, this.pos, player, unenchantedStack, reagentStack, enchantmentData, this.random);

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

                    if(!player.capabilities.isCreativeMode)
                    {
                        lapisStack.shrink(i);

                        if(lapisStack.isEmpty())
                        {
                            this.reagentTable.getInventory().setStackInSlot(1, ItemStack.EMPTY);
                        }

                        if(reagent != null)
                        {
                            if(reagent.consumeReagent(this.world, this.pos, player, unenchantedStack, reagentStack, enchantmentList, this.random))
                            {
                                reagentStack.shrink(maxReagentCost);

                                if(reagentStack.isEmpty())
                                {
                                    this.reagentTable.getInventory().setStackInSlot(2, ItemStack.EMPTY);
                                }
                            }
                        }
                    }

                    player.addStat(StatList.ITEM_ENCHANTED);

                    if(player instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((EntityPlayerMP) player, unenchantedStack, i);
                    }

                    this.xpSeed = player.getXPSeed();
                    this.onContentsChanged();
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
        return this.reagentTable.getInventory();
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

    public int[] getExperienceLevels()
    {
        return this.experienceLevels;
    }

    private List<EnchantmentData> getEnchantmentList(ItemStack unenchantedStack, ItemStack reagentStack, int enchantmentTier, int enchantmentLevel)
    {
        this.random.setSeed((long) (this.xpSeed + enchantmentTier));
        List<EnchantmentData> enchantmentData = EnchantmentHelper.buildEnchantmentList(this.random, unenchantedStack, enchantmentLevel, false);

        if(!reagentStack.isEmpty())
        {
            IReagent reagent = ReagentRegistry.getReagent(reagentStack.getItem());

            if(reagent.hasApplicableEnchantments(this.world, this.pos, this.player, unenchantedStack, reagentStack, this.random))
            {
                enchantmentData = reagent.createEnchantmentList(this.world, this.pos, this.player, unenchantedStack, reagentStack, enchantmentTier, enchantmentLevel, false, this.random);
            }
        }

        if(unenchantedStack.getItem() == Items.BOOK && enchantmentData.size() > 1)
        {
            enchantmentData.remove(this.random.nextInt(enchantmentData.size()));
        }

        return enchantmentData;
    }

    public void setXpSeed(int xpSeed)
    {
        this.xpSeed = xpSeed;
    }
}
