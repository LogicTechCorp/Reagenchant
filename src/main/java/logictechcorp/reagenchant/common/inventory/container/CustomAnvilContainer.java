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

import logictechcorp.reagenchant.core.registry.ReagenchantContainers;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class CustomAnvilContainer extends Container {
    private final PlayerEntity player;
    private final IWorldPosCallable worldPosCallable;
    private final IntReferenceHolder repairCost;
    private final IntReferenceHolder useIronInsteadOfXp;
    private int repairMaterialCost;
    private String customItemName;

    public CustomAnvilContainer(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, IWorldPosCallable.DUMMY, new ItemStackHandler(4));
    }

    public CustomAnvilContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable, ItemStackHandler itemStackHandler) {
        super(ReagenchantContainers.CUSTOM_ANVIL_CONTAINER.get(), id);
        this.player = playerInventory.player;
        this.worldPosCallable = worldPosCallable;
        this.repairCost = IntReferenceHolder.single();
        this.useIronInsteadOfXp = IntReferenceHolder.single();
        this.useIronInsteadOfXp.set(1);

        this.addSlot(new SlotItemHandler(itemStackHandler, 0, 27, 47) {
            @Override
            public void onSlotChanged() {
                CustomAnvilContainer.this.updateOutput();
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, 1, 76, 47) {
            @Override
            public void onSlotChanged() {
                CustomAnvilContainer.this.updateOutput();
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, 2, 6, 6) {
            @Override
            public void onSlotChanged() {
                CustomAnvilContainer.this.updateOutput();
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem().isIn(Tags.Items.INGOTS_IRON);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, 3, 134, 47) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeStack(PlayerEntity player) {
                return CustomAnvilContainer.this.canRepair(player);
            }

            @Override
            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                return CustomAnvilContainer.this.onRepair(player, stack);
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

        this.trackInt(this.repairCost);
        this.trackInt(this.useIronInsteadOfXp);
    }

    public void updateOutput() {
        ItemStack inputStack = this.getSlot(0).getStack();
        this.repairCost.set(1);
        this.useIronInsteadOfXp.set(1);
        int additionalRepairCost = 0;
        int baseRepairCost = 0;
        int incrementalRepairCost = 0;

        if(inputStack.isEmpty()) {
            this.getSlot(3).putStack(ItemStack.EMPTY);
            this.repairCost.set(0);
        }
        else {
            ItemStack outputStack = inputStack.copy();
            ItemStack repairMaterialStack = this.getSlot(1).getStack();
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(outputStack);
            baseRepairCost = baseRepairCost + inputStack.getRepairCost() + (repairMaterialStack.isEmpty() ? 0 : repairMaterialStack.getRepairCost());
            this.repairMaterialCost = 0;
            boolean isEnchantedBook = false;

            if(!repairMaterialStack.isEmpty()) {
                AnvilUpdateEvent anvilUpdateEvent = new AnvilUpdateEvent(inputStack, repairMaterialStack, this.customItemName, baseRepairCost, this.player);

                if(MinecraftForge.EVENT_BUS.post(anvilUpdateEvent)) {
                    this.getSlot(3).putStack(ItemStack.EMPTY);
                    return;
                }

                ItemStack anvilUpdateEventStack = anvilUpdateEvent.getOutput();

                if(anvilUpdateEventStack.isEmpty()) {
                    isEnchantedBook = repairMaterialStack.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(repairMaterialStack).isEmpty();

                    if(outputStack.isDamageable() && outputStack.getItem().getIsRepairable(inputStack, repairMaterialStack)) {
                        int minimumOutputStackDamage = Math.min(outputStack.getDamage(), outputStack.getMaxDamage() / 4);

                        if(minimumOutputStackDamage <= 0) {
                            this.getSlot(3).putStack(ItemStack.EMPTY);
                            this.repairCost.set(0);
                            return;
                        }

                        int repairMaterialCost;

                        for(repairMaterialCost = 0; minimumOutputStackDamage > 0 && repairMaterialCost < repairMaterialStack.getCount(); repairMaterialCost++) {
                            int outputStackDamage = outputStack.getDamage() - minimumOutputStackDamage;
                            minimumOutputStackDamage = Math.min(outputStackDamage, outputStack.getMaxDamage() / 4);
                            outputStack.setDamage(outputStackDamage);
                            additionalRepairCost++;
                        }

                        this.repairMaterialCost = repairMaterialCost;
                    }
                    else {
                        if(!isEnchantedBook && (outputStack.getItem() != repairMaterialStack.getItem() || !outputStack.isDamageable())) {
                            this.getSlot(3).putStack(ItemStack.EMPTY);
                            this.repairCost.set(0);
                            return;
                        }

                        if(outputStack.isDamageable() && !isEnchantedBook) {
                            int inputStackDamageRemaining = inputStack.getMaxDamage() - inputStack.getDamage();
                            int repairStackDamageRemaining = repairMaterialStack.getMaxDamage() - repairMaterialStack.getDamage();
                            int partialOutputStackDamage = repairStackDamageRemaining + outputStack.getMaxDamage() * 12 / 100;
                            int outputStackDamage = inputStackDamageRemaining + partialOutputStackDamage;
                            int outputStackDamageRemaining = outputStack.getMaxDamage() - outputStackDamage;

                            if(outputStackDamageRemaining < 0) {
                                outputStackDamageRemaining = 0;
                            }

                            if(outputStackDamageRemaining < outputStack.getDamage()) {
                                outputStack.setDamage(outputStackDamageRemaining);
                                additionalRepairCost += 2;
                            }
                        }

                        Map<Enchantment, Integer> repairEnchantments = EnchantmentHelper.getEnchantments(repairMaterialStack);
                        boolean appliedEnchantment = false;
                        boolean cantApplyEnchantment = false;

                        for(Enchantment repairEnchantment : repairEnchantments.keySet()) {
                            if(repairEnchantment != null) {
                                int enchantmentLevel = enchantments.getOrDefault(repairEnchantment, 0);
                                int maxEnchantmentLevel = repairEnchantments.get(repairEnchantment);
                                maxEnchantmentLevel = enchantmentLevel == maxEnchantmentLevel ? maxEnchantmentLevel + 1 : Math.max(maxEnchantmentLevel, enchantmentLevel);

                                boolean canApplyEnchantment = repairEnchantment.canApply(inputStack);

                                if(this.player.abilities.isCreativeMode || inputStack.getItem() == Items.ENCHANTED_BOOK) {
                                    canApplyEnchantment = true;
                                }

                                for(Enchantment enchantment : enchantments.keySet()) {
                                    if(enchantment != repairEnchantment && !repairEnchantment.isCompatibleWith(enchantment)) {
                                        canApplyEnchantment = false;
                                        additionalRepairCost++;
                                    }
                                }

                                if(!canApplyEnchantment) {
                                    cantApplyEnchantment = true;
                                }
                                else {
                                    appliedEnchantment = true;

                                    if(maxEnchantmentLevel > repairEnchantment.getMaxLevel()) {
                                        maxEnchantmentLevel = repairEnchantment.getMaxLevel();
                                    }

                                    enchantments.put(repairEnchantment, maxEnchantmentLevel);
                                    this.useIronInsteadOfXp.set(0);

                                    int enchantmentRarity = 0;

                                    switch(repairEnchantment.getRarity()) {
                                        case COMMON:
                                            enchantmentRarity = 1;
                                            break;
                                        case UNCOMMON:
                                            enchantmentRarity = 2;
                                            break;
                                        case RARE:
                                            enchantmentRarity = 4;
                                            break;
                                        case VERY_RARE:
                                            enchantmentRarity = 8;
                                    }

                                    if(isEnchantedBook) {
                                        enchantmentRarity = Math.max(1, enchantmentRarity / 2);
                                    }

                                    additionalRepairCost += enchantmentRarity * maxEnchantmentLevel;

                                    if(inputStack.getCount() > 1) {
                                        additionalRepairCost = 40;
                                    }
                                }
                            }
                        }

                        if(cantApplyEnchantment && !appliedEnchantment) {
                            this.getSlot(3).putStack(ItemStack.EMPTY);
                            this.repairCost.set(0);
                            return;
                        }
                    }
                }
                else {
                    this.getSlot(3).putStack(anvilUpdateEventStack);
                    this.repairCost.set(anvilUpdateEvent.getCost());
                    this.repairMaterialCost = anvilUpdateEvent.getMaterialCost();
                    return;
                }
            }

            if(StringUtils.isBlank(this.customItemName)) {
                if(inputStack.hasDisplayName()) {
                    incrementalRepairCost = 1;
                    additionalRepairCost += incrementalRepairCost;
                    outputStack.clearCustomName();
                }
            }
            else if(!this.customItemName.equals(inputStack.getDisplayName().getString())) {
                incrementalRepairCost = 1;
                additionalRepairCost += incrementalRepairCost;
                outputStack.setDisplayName(new StringTextComponent(this.customItemName));
            }

            if(isEnchantedBook && !outputStack.isBookEnchantable(repairMaterialStack)) {
                outputStack = ItemStack.EMPTY;
            }

            this.repairCost.set(baseRepairCost + additionalRepairCost);

            if(additionalRepairCost <= 0) {
                outputStack = ItemStack.EMPTY;
            }

            if(incrementalRepairCost == additionalRepairCost && incrementalRepairCost > 0 && this.repairCost.get() >= 40) {
                this.repairCost.set(39);
            }

            if(this.repairCost.get() >= 40 && !this.player.abilities.isCreativeMode) {
                outputStack = ItemStack.EMPTY;
            }

            if(!outputStack.isEmpty()) {
                int outputStackMaxRepairCost = outputStack.getRepairCost();

                if(!repairMaterialStack.isEmpty() && outputStackMaxRepairCost < repairMaterialStack.getRepairCost()) {
                    outputStackMaxRepairCost = repairMaterialStack.getRepairCost();
                }

                if(incrementalRepairCost != additionalRepairCost || incrementalRepairCost == 0) {
                    outputStackMaxRepairCost = this.getNewMaxRepairCost(outputStackMaxRepairCost);
                }

                outputStack.setRepairCost(outputStackMaxRepairCost);
                EnchantmentHelper.setEnchantments(enchantments, outputStack);
            }

            this.getSlot(3).putStack(outputStack);
            this.detectAndSendChanges();
        }
    }

    public void updateItemName(String customItemName) {
        this.customItemName = customItemName;
        ItemStack outputStack = this.getSlot(3).getStack();

        if(!outputStack.isEmpty()) {
            if(StringUtils.isBlank(customItemName)) {
                outputStack.clearCustomName();
            }
            else {
                outputStack.setDisplayName(new StringTextComponent(this.customItemName));
            }
        }

        this.updateOutput();
    }

    public ItemStack onRepair(PlayerEntity player, ItemStack stack) {
        if(!player.abilities.isCreativeMode) {
            if(this.useIronInsteadOfXp()) {
                ItemStack ironStack = this.getSlot(2).getStack();
                ironStack.setCount(ironStack.getCount() - this.repairCost.get());
            }
            else {
                player.addExperienceLevel(-this.repairCost.get());
            }
        }

        float breakChance = ForgeHooks.onAnvilRepair(player, stack, this.getSlot(0).getStack(), this.getSlot(1).getStack());
        this.getSlot(0).putStack(ItemStack.EMPTY);

        if(this.repairMaterialCost > 0) {
            ItemStack repairMaterialStack = this.getSlot(1).getStack();

            if(!repairMaterialStack.isEmpty() && repairMaterialStack.getCount() > this.repairMaterialCost) {
                repairMaterialStack.shrink(this.repairMaterialCost);
                this.getSlot(1).putStack(repairMaterialStack);
            }
            else {
                this.getSlot(1).putStack(ItemStack.EMPTY);
            }
        }
        else {
            this.getSlot(1).putStack(ItemStack.EMPTY);
        }

        this.repairCost.set(0);
        this.worldPosCallable.consume((world, pos) -> {
            BlockState oldState = world.getBlockState(pos);

            if(!player.abilities.isCreativeMode && oldState.isIn(BlockTags.ANVIL) && player.getRNG().nextFloat() < breakChance) {
                BlockState newState = AnvilBlock.damage(oldState);

                if(newState == null) {
                    world.removeBlock(pos, false);
                    world.playEvent(1029, pos, 0);
                }
                else {
                    world.setBlockState(pos, newState, 2);
                    world.playEvent(1030, pos, 0);
                }
            }
            else {
                world.playEvent(1030, pos, 0);
            }

        });

        return stack;
    }

    public boolean canRepair(PlayerEntity player) {
        if(player.abilities.isCreativeMode) {
            return true;
        }
        else {
            if(this.repairCost.get() > 0) {
                if(this.useIronInsteadOfXp()) {
                    return this.getSlot(2).getStack().getCount() >= this.repairCost.get();
                }
                else {
                    return player.experienceLevel >= this.repairCost.get();
                }
            }
        }

        return false;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        return this.worldPosCallable.applyOrElse((world, pos) -> world.getBlockState(pos).isIn(BlockTags.ANVIL) && player.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(slot != null && slot.getHasStack()) {
            ItemStack oldStack = slot.getStack();
            newStack = oldStack.copy();

            if(index == 3) {
                if(!this.mergeItemStack(oldStack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(oldStack, newStack);
            }
            else if(index >= 4 && index < 40) {
                if(!this.mergeItemStack(oldStack, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.mergeItemStack(oldStack, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if(oldStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }

            if(oldStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, oldStack);
        }

        return newStack;
    }

    public int getNewMaxRepairCost(int oldMaxRepairCost) {
        return oldMaxRepairCost * 2 + 1;
    }

    public int getRepairCost() {
        return this.repairCost.get();
    }

    public boolean useIronInsteadOfXp() {
        return this.useIronInsteadOfXp.get() == 1;
    }
}
