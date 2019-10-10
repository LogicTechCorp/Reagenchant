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
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class ReagentTableContainer extends Container
{
    private final PlayerEntity player;
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
        this.player = playerInventory.player;
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
                return stack.isEnchantable();
            }

            @Override
            public int getSlotStackLimit()
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

            @Override
            public int getSlotStackLimit()
            {
                return 64;
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
                return Reagenchant.REAGENT_MANAGER.isReagentItem(stack.getItem());
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
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }

        this.xpSeed.set(this.player.getXPSeed());
        this.onContentsChanged();

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

    private void onContentsChanged()
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
        int i = enchantmentTier + 1;

        if((lapisStack.isEmpty() || lapisStack.getCount() < i) && !player.abilities.isCreativeMode)
        {
            return false;
        }
        else if(this.enchantabilityLevels[enchantmentTier] > 0 && !unenchantedStack.isEmpty() && (player.experienceLevel >= i && player.experienceLevel >= this.enchantabilityLevels[enchantmentTier] || player.abilities.isCreativeMode))
        {
            this.worldPosCallable.consume((world, pos) ->
            {
                List<EnchantmentData> enchantmentList = this.createEnchantmentList(enchantmentTier);

                if(!enchantmentList.isEmpty())
                {
                    ItemStack unenchantedStackTemp = unenchantedStack;
                    boolean flag = unenchantedStack.getItem() == Items.BOOK;
                    player.onEnchant(unenchantedStack, i);

                    if(flag)
                    {
                        unenchantedStackTemp = new ItemStack(Items.ENCHANTED_BOOK);
                        this.itemStackHandler.setStackInSlot(0, unenchantedStack);
                    }

                    Reagent reagent = null;

                    if(!reagentStack.isEmpty())
                    {
                        reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());
                    }

                    int maxReagentCost = 0;

                    for(EnchantmentData enchantmentData : enchantmentList)
                    {
                        if(!EnchantmentHelper.getEnchantments(unenchantedStack).keySet().contains(enchantmentData.enchantment))
                        {
                            if(reagent != null)
                            {
                                int reagentCost = reagent.getCost(world, pos, player, unenchantedStack, reagentStack, enchantmentData, this.random);

                                if(flag)
                                {
                                    if(reagentCost <= reagentStack.getCount())
                                    {
                                        if(maxReagentCost < reagentCost)
                                        {
                                            maxReagentCost = reagentCost;
                                        }

                                        EnchantedBookItem.addEnchantment(unenchantedStack, enchantmentData);
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
                                    EnchantedBookItem.addEnchantment(unenchantedStack, enchantmentData);
                                }
                                else
                                {
                                    unenchantedStack.addEnchantment(enchantmentData.enchantment, enchantmentData.enchantmentLevel);
                                }
                            }
                        }
                    }

                    if(!player.abilities.isCreativeMode)
                    {
                        lapisStack.shrink(i);

                        if(lapisStack.isEmpty())
                        {
                            this.itemStackHandler.setStackInSlot(1, ItemStack.EMPTY);
                        }

                        if(reagent != null)
                        {
                            if(reagent.consumeReagent(world, pos, player, flag ? unenchantedStackTemp : unenchantedStack, reagentStack, enchantmentList, this.random))
                            {
                                reagentStack.shrink(maxReagentCost);

                                if(reagentStack.isEmpty())
                                {
                                    this.itemStackHandler.setStackInSlot(2, ItemStack.EMPTY);
                                }
                            }
                        }
                    }

                    player.addStat(Stats.ENCHANT_ITEM);

                    if(player instanceof ServerPlayerEntity)
                    {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity) player, unenchantedStack, i);
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

        this.random.setSeed((this.xpSeed.get() + enchantmentTier));
        List<EnchantmentData> enchantmentData = EnchantmentHelper.buildEnchantmentList(this.random, unenchantedStack, enchantabilityLevel, false);
        boolean usedReagentEnchantments = false;

        if(!reagentStack.isEmpty())
        {
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());

            Optional<List<EnchantmentData>> optionalEnchantmentData = this.worldPosCallable.apply((world, pos) ->
            {
                if(reagent.hasApplicableEnchantments(world, pos, this.player, unenchantedStack, reagentStack, this.random))
                {
                    return reagent.createEnchantmentList(world, pos, this.player, unenchantedStack, reagentStack, enchantmentTier, enchantabilityLevel, this.random);
                }
                return null;
            });

            if(optionalEnchantmentData.isPresent())
            {
                enchantmentData = optionalEnchantmentData.get();
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
            else if(Reagenchant.REAGENT_MANAGER.isReagentItem(slotStack.getItem()))
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

    public PlayerEntity getPlayer()
    {
        return this.player;
    }

    public ItemStackHandler getItemStackHandler()
    {
        return this.itemStackHandler;
    }

    public IWorldPosCallable getWorldPosCallable()
    {
        return this.worldPosCallable;
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
        return lapisStack.isEmpty() ? 0 : lapisStack.getCount();
    }

    public int getReagentAmount()
    {
        ItemStack reagentStack = this.itemStackHandler.getStackInSlot(2);
        return reagentStack.isEmpty() ? 0 : reagentStack.getCount();
    }

    private float getEnchantPower(World world, BlockPos pos)
    {
        return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
    }

    void setXpSeed(int xpSeed)
    {
        this.xpSeed.set(xpSeed);
    }
}
