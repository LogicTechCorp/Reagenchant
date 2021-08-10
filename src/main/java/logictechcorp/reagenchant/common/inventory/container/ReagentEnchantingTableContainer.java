/*
 * Reagenchant
 * Copyright (c) 2019-2021 by LogicTechCorp
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

package logictechcorp.reagenchant.common.inventory.container;

import logictechcorp.reagenchant.common.reagent.Reagent;
import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.registry.ReagenchantContainers;
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

public class ReagentEnchantingTableContainer extends Container {
    private final IWorldPosCallable worldPosCallable;
    private final Random random;
    private final IntReferenceHolder xpSeed;
    private final int[] enchantments;
    private final int[] enchantmentLevels;
    private final int[] enchantabilityLevels;
    private final int[] reagentCosts;

    public ReagentEnchantingTableContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.NULL, new ItemStackHandler(3));
    }

    public ReagentEnchantingTableContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable, ItemStackHandler itemStackHandler) {
        super(ReagenchantContainers.REAGENT_ENCHANTING_TABLE_CONTAINER.get(), id);
        this.worldPosCallable = worldPosCallable;
        this.random = new Random();
        this.xpSeed = IntReferenceHolder.standalone();
        this.enchantments = new int[]{ -1, -1, -1 };
        this.enchantmentLevels = new int[]{ -1, -1, -1 };
        this.enchantabilityLevels = new int[3];
        this.reagentCosts = new int[3];

        this.addSlot(new SlotItemHandler(itemStackHandler, 0, 6, 47) {
            @Override
            public void setChanged() {
                ReagentEnchantingTableContainer.this.onContentsChanged();
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.isEnchantable() || stack.getItem() == Items.BOOK;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public int getMaxStackSize(ItemStack stack) {
                return 1;
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, 1, 24, 47) {
            @Override
            public void setChanged() {
                ReagentEnchantingTableContainer.this.onContentsChanged();
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem().is(Tags.Items.GEMS_LAPIS);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, 2, 42, 47) {
            @Override
            public void setChanged() {
                ReagentEnchantingTableContainer.this.onContentsChanged();
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return Reagenchant.REAGENT_MANAGER.isReagent(stack.getItem());
            }
        });

        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for(int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 142));
        }

        this.xpSeed.set(playerInventory.player.getEnchantmentSeed());
        this.addDataSlot(this.xpSeed);
        this.addDataSlot(IntReferenceHolder.shared(this.enchantments, 0));
        this.addDataSlot(IntReferenceHolder.shared(this.enchantments, 1));
        this.addDataSlot(IntReferenceHolder.shared(this.enchantments, 2));
        this.addDataSlot(IntReferenceHolder.shared(this.enchantmentLevels, 0));
        this.addDataSlot(IntReferenceHolder.shared(this.enchantmentLevels, 1));
        this.addDataSlot(IntReferenceHolder.shared(this.enchantmentLevels, 2));
        this.addDataSlot(IntReferenceHolder.shared(this.enchantabilityLevels, 0));
        this.addDataSlot(IntReferenceHolder.shared(this.enchantabilityLevels, 1));
        this.addDataSlot(IntReferenceHolder.shared(this.enchantabilityLevels, 2));
        this.addDataSlot(IntReferenceHolder.shared(this.reagentCosts, 0));
        this.addDataSlot(IntReferenceHolder.shared(this.reagentCosts, 1));
        this.addDataSlot(IntReferenceHolder.shared(this.reagentCosts, 2));
    }

    public void onContentsChanged() {
        ItemStack unenchantedStack = this.getSlot(0).getItem();

        if(unenchantedStack.isEnchantable() && this.getSlot(1).hasItem()) {
            this.worldPosCallable.execute((world, pos) ->
            {
                float power = 0;

                for(int z = -1; z <= 1; z++) {
                    for(int x = -1; x <= 1; x++) {
                        BlockPos adjustedPos = pos.offset(x, 0, z);
                        BlockPos adjustedPosUp = adjustedPos.above();

                        if((z != 0 || x != 0) && !world.getBlockState(adjustedPos).isSolidRender(world, adjustedPos) && !world.getBlockState(adjustedPosUp).isSolidRender(world, adjustedPosUp)) {
                            power += this.getEnchantPower(world, pos.offset(x * 2, 0, z * 2));
                            power += this.getEnchantPower(world, pos.offset(x * 2, 1, z * 2));

                            if(x != 0 && z != 0) {
                                power += this.getEnchantPower(world, pos.offset(x * 2, 0, z));
                                power += this.getEnchantPower(world, pos.offset(x * 2, 1, z));
                                power += this.getEnchantPower(world, pos.offset(x, 0, z * 2));
                                power += this.getEnchantPower(world, pos.offset(x, 1, z * 2));
                            }
                        }
                    }
                }

                this.random.setSeed(this.xpSeed.get());

                for(int i = 0; i < 3; i++) {
                    this.enchantments[i] = -1;
                    this.enchantmentLevels[i] = -1;
                    this.enchantabilityLevels[i] = EnchantmentHelper.getEnchantmentCost(this.random, i, (int) power, unenchantedStack);

                    if(this.enchantabilityLevels[i] < i + 1) {
                        this.enchantabilityLevels[i] = 0;
                    }

                    this.enchantabilityLevels[i] = ForgeEventFactory.onEnchantmentLevelSet(world, pos, i, (int) power, unenchantedStack, this.enchantabilityLevels[i]);
                }

                for(int i = 0; i < 3; i++) {
                    if(this.enchantabilityLevels[i] > 0) {
                        List<EnchantmentData> enchantments = this.compileEnchantmentList(i);

                        if(!enchantments.isEmpty()) {
                            EnchantmentData randomEnchantmentData = enchantments.get(this.random.nextInt(enchantments.size()));
                            this.enchantments[i] = Registry.ENCHANTMENT.getId(randomEnchantmentData.enchantment);
                            this.enchantmentLevels[i] = randomEnchantmentData.level;
                            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(this.getSlot(2).getItem().getItem());

                            if(!reagent.isEmpty()) {
                                for(EnchantmentData enchantmentData : enchantments) {
                                    int reagentCost = reagent.getCost(enchantmentData.enchantment);

                                    if(reagentCost > this.reagentCosts[i]) {
                                        this.reagentCosts[i] = reagentCost;
                                    }
                                }
                            }
                            else {
                                this.reagentCosts[i] = 0;
                            }
                        }
                    }
                }

                this.broadcastChanges();
            });
        }
        else {
            for(int i = 0; i < 3; i++) {
                this.enchantments[i] = -1;
                this.enchantmentLevels[i] = -1;
                this.enchantabilityLevels[i] = 0;
                this.reagentCosts[i] = 0;
            }
        }
    }

    @Override
    public boolean clickMenuButton(PlayerEntity player, int enchantmentTier) {
        ItemStack unenchantedStack = this.getSlot(0).getItem();
        ItemStack lapisStack = this.getSlot(1).getItem();
        ItemStack reagentStack = this.getSlot(2).getItem();
        int lapisCost = enchantmentTier + 1;

        if((lapisStack.isEmpty() || lapisStack.getCount() < lapisCost || reagentStack.getCount() < this.reagentCosts[enchantmentTier]) && !player.abilities.instabuild) {
            return false;
        }
        else if(this.enchantabilityLevels[enchantmentTier] > 0 && !unenchantedStack.isEmpty() && (player.experienceLevel >= lapisCost && player.experienceLevel >= this.enchantabilityLevels[enchantmentTier] || player.abilities.instabuild)) {
            this.worldPosCallable.execute((world, pos) ->
            {
                List<EnchantmentData> enchantments = this.compileEnchantmentList(enchantmentTier);
                Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());

                if(!enchantments.isEmpty()) {
                    ItemStack enchantedStack = unenchantedStack;
                    boolean isBook = unenchantedStack.getItem() == Items.BOOK;
                    player.onEnchantmentPerformed(unenchantedStack, lapisCost);

                    if(isBook) {
                        enchantedStack = new ItemStack(Items.ENCHANTED_BOOK);
                        this.getSlot(0).set(enchantedStack);
                    }

                    for(EnchantmentData enchantmentData : enchantments) {
                        if(isBook) {
                            EnchantedBookItem.addEnchantment(enchantedStack, enchantmentData);
                        }
                        else {
                            enchantedStack.enchant(enchantmentData.enchantment, enchantmentData.level);
                        }
                    }

                    if(!player.abilities.instabuild) {
                        lapisStack.shrink(lapisCost);

                        if(lapisStack.isEmpty()) {
                            this.getSlot(1).set(ItemStack.EMPTY);
                        }

                        if(!reagent.isEmpty() && reagent.consumeReagent(unenchantedStack, enchantments)) {
                            reagentStack.shrink(this.reagentCosts[enchantmentTier]);

                            if(reagentStack.isEmpty()) {
                                this.getSlot(2).set(ItemStack.EMPTY);
                            }
                        }
                    }

                    player.awardStat(Stats.ENCHANT_ITEM);

                    if(player instanceof ServerPlayerEntity) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayerEntity) player, enchantedStack, lapisCost);
                    }

                    this.xpSeed.set(player.getEnchantmentSeed());
                    this.onContentsChanged();
                    world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
                }
            });
            return true;
        }
        else {
            return false;
        }
    }

    private List<EnchantmentData> compileEnchantmentList(int enchantmentTier) {
        ItemStack unenchantedStack = this.getSlot(0).getItem();
        ItemStack reagentStack = this.getSlot(2).getItem();
        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());
        int enchantabilityLevel = this.enchantabilityLevels[enchantmentTier];

        this.random.setSeed((this.xpSeed.get() + enchantmentTier));
        List<EnchantmentData> reagentEnchantments = new ArrayList<>();
        List<EnchantmentData> defaultEnchantments = EnchantmentHelper.selectEnchantment(this.random, unenchantedStack, enchantabilityLevel, false);

        if(!reagent.isEmpty() && reagent.canApplyEnchantments(unenchantedStack)) {
            final int finalEnchantabilityLevel = enchantabilityLevel;
            reagentEnchantments = this.worldPosCallable.evaluate((world, pos) -> reagent.compileEnchantmentList(unenchantedStack, enchantmentTier, finalEnchantabilityLevel, this.random), reagentEnchantments);
        }

        List<EnchantmentData> refinedEnchantments = new ArrayList<>();

        if(!reagentEnchantments.isEmpty()) {
            EnchantmentData removedEnchantment = WeightedRandom.getRandomItem(this.random, reagentEnchantments);
            refinedEnchantments.add(reagentEnchantments.remove(reagentEnchantments.indexOf(removedEnchantment)));

            while(this.random.nextInt(50) <= enchantabilityLevel) {
                EnchantmentHelper.filterCompatibleEnchantments(defaultEnchantments, Util.lastOf(refinedEnchantments));

                if(defaultEnchantments.isEmpty()) {
                    break;
                }

                if(!reagentEnchantments.isEmpty()) {
                    removedEnchantment = WeightedRandom.getRandomItem(this.random, reagentEnchantments);
                    refinedEnchantments.add(reagentEnchantments.remove(reagentEnchantments.indexOf(removedEnchantment)));
                }
                else {
                    refinedEnchantments.add(WeightedRandom.getRandomItem(this.random, defaultEnchantments));
                }

                enchantabilityLevel /= 2;
            }
        }
        else {
            refinedEnchantments.addAll(defaultEnchantments);
        }

        if(unenchantedStack.getItem() == Items.BOOK && refinedEnchantments.size() > 1) {
            if(!reagentEnchantments.isEmpty()) {
                refinedEnchantments.remove(this.random.ints(1, 1, refinedEnchantments.size()).findFirst().orElse(1));
            }
            else {
                refinedEnchantments.remove(this.random.nextInt(refinedEnchantments.size()));
            }
        }

        return refinedEnchantments;
    }

    @Override
    public void removed(PlayerEntity player) {
        super.removed(player);

        this.worldPosCallable.execute((world, pos) ->
        {
            if(this.getSlot(0).getItem().isEnchanted()) {
                if(!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).hasDisconnected()) {
                    player.drop(this.getSlot(0).getItem(), false);
                }
                else {
                    player.inventory.placeItemBackInInventory(world, this.getSlot(0).getItem());
                }

                this.getSlot(0).set(ItemStack.EMPTY);
            }
        });
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(this.worldPosCallable, player, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if(index == 0) {
                if(!this.moveItemStackTo(slotStack, 3, 38, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if(index == 1) {
                if(!this.moveItemStackTo(slotStack, 3, 38, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if(index == 2) {
                if(!this.moveItemStackTo(slotStack, 3, 38, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if(slotStack.getItem() == Items.LAPIS_LAZULI) {
                if(!this.moveItemStackTo(slotStack, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            }
            else if(Reagenchant.REAGENT_MANAGER.isReagent(slotStack.getItem())) {
                if(!this.moveItemStackTo(slotStack, 2, 3, true)) {
                    if(this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(slotStack)) {
                        return ItemStack.EMPTY;
                    }

                    if(!slotStack.isEmpty()) {
                        this.slots.get(0).set(slotStack.split(1));
                    }

                    return ItemStack.EMPTY;
                }
            }
            else {
                if(this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(slotStack)) {
                    return ItemStack.EMPTY;
                }

                if(!slotStack.isEmpty()) {
                    this.slots.get(0).set(slotStack.split(1));
                }
            }

            if(slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
            }

            if(slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return stack;
    }

    public int getXpSeed() {
        return this.xpSeed.get();
    }

    public int[] getEnchantments() {
        return this.enchantments;
    }

    public int[] getEnchantmentLevels() {
        return this.enchantmentLevels;
    }

    public int[] getEnchantabilityLevels() {
        return this.enchantabilityLevels;
    }

    public int[] getReagentCosts() {
        return this.reagentCosts;
    }

    public int getLapisAmount() {
        ItemStack lapisStack = this.getSlot(1).getItem();
        return lapisStack.getCount();
    }

    public int getReagentAmount() {
        ItemStack reagentStack = this.getSlot(2).getItem();
        return reagentStack.getCount();
    }

    private float getEnchantPower(World world, BlockPos pos) {
        return world.getBlockState(pos).getEnchantPowerBonus(world, pos);
    }
}
