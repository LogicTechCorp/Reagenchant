/*
 * Reagenchant
 * Copyright (c) 2019-2020 by LogicTechCorp
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

package logictechcorp.reagenchant.inventory.container;

import logictechcorp.libraryex.utility.RandomHelper;
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.reagent.Reagent;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReagentTableContainer extends Container
{
    private final ItemStackHandler itemStackHandler;
    private final IWorldPosCallable worldPosCallable;
    private final Random random;
    private IntReferenceHolder xpSeed = IntReferenceHolder.single();
    private int[] enchantments;
    private int[] enchantmentLevels;
    private int[] enchantabilityLevels;

    public ReagentTableContainer(int id, PlayerInventory playerInventory)
    {
        this(id, playerInventory, new ItemStackHandler(3), IWorldPosCallable.DUMMY);
    }

    public ReagentTableContainer(int id, PlayerInventory playerInventory, ItemStackHandler itemStackHandler, IWorldPosCallable worldPosCallable)
    {
        super(ReagenchantContainers.REAGENT_TABLE_CONTAINER.get(), id);
        this.itemStackHandler = itemStackHandler;
        this.worldPosCallable = worldPosCallable;
        this.random = new Random();
        this.enchantments = new int[]{-1, -1, -1};
        this.enchantmentLevels = new int[]{-1, -1, -1};
        this.enchantabilityLevels = new int[3];

        this.addSlot(new SlotItemHandler(this.itemStackHandler, 0, 6, 47)
        {
            @Override
            public void onSlotChanged()
            {
                ReagentTableContainer.this.onContentsChanged();
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.isEnchantable() || stack.getItem() == Items.BOOK;
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }

            @Override
            public int getItemStackLimit(ItemStack stack)
            {
                return 1;
            }
        });
        this.addSlot(new SlotItemHandler(this.itemStackHandler, 1, 24, 47)
        {
            @Override
            public void onSlotChanged()
            {
                ReagentTableContainer.this.onContentsChanged();
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack.getItem().isIn(Tags.Items.GEMS_LAPIS);
            }
        });
        this.addSlot(new SlotItemHandler(this.itemStackHandler, 2, 42, 47)
        {
            @Override
            public void onSlotChanged()
            {
                ReagentTableContainer.this.onContentsChanged();
            }

            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return Reagenchant.REAGENT_MANAGER.isReagent(stack.getItem());
            }
        });

        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }

        this.xpSeed.set(playerInventory.player.getXPSeed());
        this.trackInt(IntReferenceHolder.create(this.enchantmentLevels, 0));
        this.trackInt(IntReferenceHolder.create(this.enchantmentLevels, 1));
        this.trackInt(IntReferenceHolder.create(this.enchantmentLevels, 2));
        this.trackInt(this.xpSeed);
        this.trackInt(IntReferenceHolder.create(this.enchantments, 0));
        this.trackInt(IntReferenceHolder.create(this.enchantments, 1));
        this.trackInt(IntReferenceHolder.create(this.enchantments, 2));
        this.trackInt(IntReferenceHolder.create(this.enchantabilityLevels, 0));
        this.trackInt(IntReferenceHolder.create(this.enchantabilityLevels, 1));
        this.trackInt(IntReferenceHolder.create(this.enchantabilityLevels, 2));
    }

    public void onContentsChanged()
    {
        ItemStack unenchantedStack = this.itemStackHandler.getStackInSlot(0);

        if(unenchantedStack.isEnchantable() && !this.itemStackHandler.getStackInSlot(1).isEmpty())
        {
            this.worldPosCallable.consume((world, pos) ->
            {
                float power = 0;

                for(int z = -1; z <= 1; z++)
                {
                    for(int x = -1; x <= 1; x++)
                    {
                        BlockPos adjustedPos = pos.add(x, 0, z);
                        BlockPos adjustedPosUp = adjustedPos.up();

                        if((z != 0 || x != 0) && !world.getBlockState(adjustedPos).isOpaqueCube(world, adjustedPos) && !world.getBlockState(adjustedPosUp).isOpaqueCube(world, adjustedPosUp))
                        {
                            power += this.getEnchantPower(world, pos.add(x * 2, 0, z * 2));
                            power += this.getEnchantPower(world, pos.add(x * 2, 1, z * 2));

                            if(x != 0 && z != 0)
                            {
                                power += this.getEnchantPower(world, pos.add(x * 2, 0, z));
                                power += this.getEnchantPower(world, pos.add(x * 2, 1, z));
                                power += this.getEnchantPower(world, pos.add(x, 0, z * 2));
                                power += this.getEnchantPower(world, pos.add(x, 1, z * 2));
                            }
                        }
                    }
                }

                this.random.setSeed(this.xpSeed.get());

                for(int i = 0; i < 3; i++)
                {
                    this.enchantments[i] = -1;
                    this.enchantmentLevels[i] = -1;
                    this.enchantabilityLevels[i] = EnchantmentHelper.calcItemStackEnchantability(this.random, i, (int) power, unenchantedStack);

                    if(this.enchantabilityLevels[i] < i + 1)
                    {
                        this.enchantabilityLevels[i] = 0;
                    }

                    this.enchantabilityLevels[i] = ForgeEventFactory.onEnchantmentLevelSet(world, pos, i, (int) power, unenchantedStack, this.enchantabilityLevels[i]);
                }

                for(int i = 0; i < 3; i++)
                {
                    if(this.enchantabilityLevels[i] > 0)
                    {
                        List<EnchantmentData> enchantmentList = this.createEnchantmentList(i);

                        if(enchantmentList != null && !enchantmentList.isEmpty())
                        {
                            EnchantmentData enchantmentData = enchantmentList.get(this.random.nextInt(enchantmentList.size()));
                            this.enchantments[i] = Registry.ENCHANTMENT.getId(enchantmentData.enchantment);
                            this.enchantmentLevels[i] = enchantmentData.enchantmentLevel;
                        }
                    }
                }

                this.detectAndSendChanges();
            });
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

    @Override
    public boolean enchantItem(PlayerEntity player, int enchantmentTier)
    {
        ItemStack unenchantedStack = this.itemStackHandler.getStackInSlot(0);
        ItemStack lapisStack = this.itemStackHandler.getStackInSlot(1);
        ItemStack reagentStack = this.itemStackHandler.getStackInSlot(2);
        int lapisCost = enchantmentTier + 1;

        if((lapisStack.isEmpty() || lapisStack.getCount() < lapisCost) && !player.abilities.isCreativeMode)
        {
            return false;
        }
        else if(this.enchantabilityLevels[enchantmentTier] > 0 && !unenchantedStack.isEmpty() && (player.experienceLevel >= lapisCost && player.experienceLevel >= this.enchantabilityLevels[enchantmentTier] || player.abilities.isCreativeMode))
        {
            this.worldPosCallable.consume((world, pos) ->
            {
                List<EnchantmentData> enchantmentList = this.createEnchantmentList(enchantmentTier);

                if(!enchantmentList.isEmpty())
                {
                    ItemStack enchantedStack = unenchantedStack;
                    boolean flag = unenchantedStack.getItem() == Items.BOOK;
                    player.onEnchant(unenchantedStack, lapisCost);

                    if(flag)
                    {
                        enchantedStack = new ItemStack(Items.ENCHANTED_BOOK);
                        this.itemStackHandler.setStackInSlot(0, enchantedStack);
                    }

                    Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());
                    int maxReagentCost = 0;

                    for(EnchantmentData enchantmentData : enchantmentList)
                    {
                        if(!EnchantmentHelper.getEnchantments(enchantedStack).containsKey(enchantmentData.enchantment))
                        {
                            if(!reagent.isEmpty())
                            {
                                int reagentCost = reagent.getCost(enchantedStack, reagentStack, enchantmentData, this.random);

                                if(flag)
                                {
                                    if(reagentCost <= reagentStack.getCount())
                                    {
                                        if(maxReagentCost < reagentCost)
                                        {
                                            maxReagentCost = reagentCost;
                                        }

                                        EnchantedBookItem.addEnchantment(enchantedStack, enchantmentData);
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

                                        enchantedStack.addEnchantment(enchantmentData.enchantment, enchantmentData.enchantmentLevel);
                                    }
                                }
                            }
                            else
                            {
                                if(flag)
                                {
                                    EnchantedBookItem.addEnchantment(enchantedStack, enchantmentData);
                                }
                                else
                                {
                                    enchantedStack.addEnchantment(enchantmentData.enchantment, enchantmentData.enchantmentLevel);
                                }
                            }
                        }
                    }

                    if(!player.abilities.isCreativeMode)
                    {
                        lapisStack.shrink(lapisCost);

                        if(lapisStack.isEmpty())
                        {
                            this.itemStackHandler.setStackInSlot(1, ItemStack.EMPTY);
                        }

                        if(!reagent.isEmpty() && reagent.consumeReagent(unenchantedStack, enchantedStack, reagentStack, enchantmentList, this.random))
                        {
                            reagentStack.shrink(maxReagentCost);

                            if(reagentStack.isEmpty())
                            {
                                this.itemStackHandler.setStackInSlot(2, ItemStack.EMPTY);
                            }
                        }
                    }

                    player.addStat(Stats.ENCHANT_ITEM);

                    if(player instanceof ServerPlayerEntity)
                    {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity) player, enchantedStack, lapisCost);
                    }

                    this.xpSeed.set(player.getXPSeed());
                    this.onContentsChanged();
                    world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                }
            });
            return true;
        }
        else
        {
            return false;
        }
    }

    private List<EnchantmentData> createEnchantmentList(int enchantmentTier)
    {
        ItemStack unenchantedStack = this.itemStackHandler.getStackInSlot(0);
        ItemStack reagentStack = this.itemStackHandler.getStackInSlot(2);
        int enchantabilityLevel = this.enchantabilityLevels[enchantmentTier];
        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());

        this.random.setSeed((this.xpSeed.get() + enchantmentTier));
        List<EnchantmentData> reagentEnchantmentData = new ArrayList<>();
        List<EnchantmentData> defaultEnchantmentData = EnchantmentHelper.buildEnchantmentList(this.random, unenchantedStack, enchantabilityLevel, false);

        if(!reagent.isEmpty() && reagent.hasApplicableEnchantments(unenchantedStack, reagentStack, this.random))
        {
            final int finalEnchantabilityLevel = enchantabilityLevel;
            reagentEnchantmentData = this.worldPosCallable.applyOrElse((world, pos) -> reagent.createEnchantmentList(unenchantedStack, reagentStack, enchantmentTier, finalEnchantabilityLevel, this.random), reagentEnchantmentData);
        }

        List<EnchantmentData> aggregateEnchantmentData = new ArrayList<>(reagentEnchantmentData);
        aggregateEnchantmentData.addAll(defaultEnchantmentData);

        List<EnchantmentData> refinedEnchantmentData = new ArrayList<>();

        if(!reagentEnchantmentData.isEmpty())
        {
            refinedEnchantmentData.add(WeightedRandom.getRandomItem(this.random, reagentEnchantmentData));

            while(this.random.nextInt(50) <= enchantabilityLevel)
            {
                EnchantmentHelper.removeIncompatible(aggregateEnchantmentData, Util.getLast(refinedEnchantmentData));

                if(aggregateEnchantmentData.isEmpty())
                {
                    break;
                }

                refinedEnchantmentData.add(WeightedRandom.getRandomItem(this.random, aggregateEnchantmentData));
                enchantabilityLevel /= 2;
            }
        }
        else
        {
            refinedEnchantmentData.addAll(aggregateEnchantmentData);
        }

        if(unenchantedStack.getItem() == Items.BOOK && refinedEnchantmentData.size() > 1)
        {
            if(!reagentEnchantmentData.isEmpty())
            {
                refinedEnchantmentData.remove(RandomHelper.getNumberInRange(1, refinedEnchantmentData.size() - 1, this.random));
            }
            else
            {
                refinedEnchantmentData.remove(this.random.nextInt(refinedEnchantmentData.size()));
            }
        }

        return refinedEnchantmentData;
    }

    @Override
    public void onContainerClosed(PlayerEntity player)
    {
        super.onContainerClosed(player);

        this.worldPosCallable.consume((world, pos) ->
        {
            if(this.itemStackHandler.getStackInSlot(0).isEnchanted())
            {
                if(!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).hasDisconnected())
                {
                    player.dropItem(this.itemStackHandler.extractItem(0, 64, false), false);
                }
                else
                {
                    player.inventory.placeItemBackInInventory(world, this.itemStackHandler.extractItem(0, 64, false));
                }
            }
        });
    }

    @Override
    public boolean canInteractWith(PlayerEntity player)
    {
        return isWithinUsableDistance(this.worldPosCallable, player, Blocks.ENCHANTING_TABLE);
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
            else if(Reagenchant.REAGENT_MANAGER.isReagent(slotStack.getItem()))
            {
                if(!this.mergeItemStack(slotStack, 2, 3, true))
                {
                    if(this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(slotStack))
                    {
                        return ItemStack.EMPTY;
                    }

                    if(!slotStack.isEmpty())
                    {
                        this.inventorySlots.get(0).putStack(slotStack.split(1));
                    }

                    return ItemStack.EMPTY;
                }
            }
            else
            {
                if(this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(slotStack))
                {
                    return ItemStack.EMPTY;
                }

                if(!slotStack.isEmpty())
                {
                    this.inventorySlots.get(0).putStack(slotStack.split(1));
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

    public ItemStackHandler getItemStackHandler()
    {
        return this.itemStackHandler;
    }

    public Random getRandom()
    {
        return this.random;
    }

    public int getXpSeed()
    {
        return this.xpSeed.get();
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
        ItemStack lapisStack = this.itemStackHandler.getStackInSlot(1);
        return lapisStack.getCount();
    }

    public int getReagentAmount()
    {
        ItemStack reagentStack = this.itemStackHandler.getStackInSlot(2);
        return reagentStack.getCount();
    }

    private float getEnchantPower(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
    }
}
