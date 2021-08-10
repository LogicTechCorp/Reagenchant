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
        this(id, playerInventory, IWorldPosCallable.NULL, new ItemStackHandler(4));
    }

    public CustomAnvilContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPosCallable, ItemStackHandler itemStackHandler) {
        super(ReagenchantContainers.CUSTOM_ANVIL_CONTAINER.get(), id);
        this.player = playerInventory.player;
        this.worldPosCallable = worldPosCallable;
        this.repairCost = IntReferenceHolder.standalone();
        this.useIronInsteadOfXp = IntReferenceHolder.standalone();
        this.useIronInsteadOfXp.set(1);

        this.addSlot(new SlotItemHandler(itemStackHandler, 0, 27, 47) {
            @Override
            public void setChanged() {
                CustomAnvilContainer.this.updateOutput();
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, 1, 76, 47) {
            @Override
            public void setChanged() {
                CustomAnvilContainer.this.updateOutput();
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, 2, 6, 6) {
            @Override
            public void setChanged() {
                CustomAnvilContainer.this.updateOutput();
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem().is(Tags.Items.INGOTS_IRON);
            }
        });
        this.addSlot(new SlotItemHandler(itemStackHandler, 3, 134, 47) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(PlayerEntity player) {
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

        this.addDataSlot(this.repairCost);
        this.addDataSlot(this.useIronInsteadOfXp);
    }

    public void updateOutput() {
        ItemStack inputStack = this.getSlot(0).getItem();
        this.repairCost.set(1);
        this.useIronInsteadOfXp.set(1);
        int additionalRepairCost = 0;
        int baseRepairCost = 0;
        int incrementalRepairCost = 0;

        if(inputStack.isEmpty()) {
            this.getSlot(3).set(ItemStack.EMPTY);
            this.repairCost.set(0);
        }
        else {
            ItemStack outputStack = inputStack.copy();
            ItemStack repairMaterialStack = this.getSlot(1).getItem();
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(outputStack);
            baseRepairCost = baseRepairCost + inputStack.getBaseRepairCost() + (repairMaterialStack.isEmpty() ? 0 : repairMaterialStack.getBaseRepairCost());
            this.repairMaterialCost = 0;
            boolean isEnchantedBook = false;

            if(!repairMaterialStack.isEmpty()) {
                AnvilUpdateEvent anvilUpdateEvent = new AnvilUpdateEvent(inputStack, repairMaterialStack, this.customItemName, baseRepairCost, this.player);

                if(MinecraftForge.EVENT_BUS.post(anvilUpdateEvent)) {
                    this.getSlot(3).set(ItemStack.EMPTY);
                    return;
                }

                ItemStack anvilUpdateEventStack = anvilUpdateEvent.getOutput();

                if(anvilUpdateEventStack.isEmpty()) {
                    isEnchantedBook = repairMaterialStack.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantments(repairMaterialStack).isEmpty();

                    if(outputStack.isDamageableItem() && outputStack.getItem().isValidRepairItem(inputStack, repairMaterialStack)) {
                        int minimumOutputStackDamage = Math.min(outputStack.getDamageValue(), outputStack.getMaxDamage() / 4);

                        if(minimumOutputStackDamage <= 0) {
                            this.getSlot(3).set(ItemStack.EMPTY);
                            this.repairCost.set(0);
                            return;
                        }

                        int repairMaterialCost;

                        for(repairMaterialCost = 0; minimumOutputStackDamage > 0 && repairMaterialCost < repairMaterialStack.getCount(); repairMaterialCost++) {
                            int outputStackDamage = outputStack.getDamageValue() - minimumOutputStackDamage;
                            minimumOutputStackDamage = Math.min(outputStackDamage, outputStack.getMaxDamage() / 4);
                            outputStack.setDamageValue(outputStackDamage);
                            additionalRepairCost++;
                        }

                        this.repairMaterialCost = repairMaterialCost;
                    }
                    else {
                        if(!isEnchantedBook && (outputStack.getItem() != repairMaterialStack.getItem() || !outputStack.isDamageableItem())) {
                            this.getSlot(3).set(ItemStack.EMPTY);
                            this.repairCost.set(0);
                            return;
                        }

                        if(outputStack.isDamageableItem() && !isEnchantedBook) {
                            int inputStackDamageRemaining = inputStack.getMaxDamage() - inputStack.getDamageValue();
                            int repairStackDamageRemaining = repairMaterialStack.getMaxDamage() - repairMaterialStack.getDamageValue();
                            int partialOutputStackDamage = repairStackDamageRemaining + outputStack.getMaxDamage() * 12 / 100;
                            int outputStackDamage = inputStackDamageRemaining + partialOutputStackDamage;
                            int outputStackDamageRemaining = outputStack.getMaxDamage() - outputStackDamage;

                            if(outputStackDamageRemaining < 0) {
                                outputStackDamageRemaining = 0;
                            }

                            if(outputStackDamageRemaining < outputStack.getDamageValue()) {
                                outputStack.setDamageValue(outputStackDamageRemaining);
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

                                boolean canApplyEnchantment = repairEnchantment.canEnchant(inputStack);

                                if(this.player.abilities.instabuild || inputStack.getItem() == Items.ENCHANTED_BOOK) {
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
                            this.getSlot(3).set(ItemStack.EMPTY);
                            this.repairCost.set(0);
                            return;
                        }
                    }
                }
                else {
                    this.getSlot(3).set(anvilUpdateEventStack);
                    this.repairCost.set(anvilUpdateEvent.getCost());
                    this.repairMaterialCost = anvilUpdateEvent.getMaterialCost();
                    return;
                }
            }

            if(StringUtils.isBlank(this.customItemName)) {
                if(inputStack.hasCustomHoverName()) {
                    incrementalRepairCost = 1;
                    additionalRepairCost += incrementalRepairCost;
                    outputStack.resetHoverName();
                }
            }
            else if(!this.customItemName.equals(inputStack.getHoverName().getString())) {
                incrementalRepairCost = 1;
                additionalRepairCost += incrementalRepairCost;
                outputStack.setHoverName(new StringTextComponent(this.customItemName));
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

            if(this.repairCost.get() >= 40 && !this.player.abilities.instabuild) {
                outputStack = ItemStack.EMPTY;
            }

            if(!outputStack.isEmpty()) {
                int outputStackMaxRepairCost = outputStack.getBaseRepairCost();

                if(!repairMaterialStack.isEmpty() && outputStackMaxRepairCost < repairMaterialStack.getBaseRepairCost()) {
                    outputStackMaxRepairCost = repairMaterialStack.getBaseRepairCost();
                }

                if(incrementalRepairCost != additionalRepairCost || incrementalRepairCost == 0) {
                    outputStackMaxRepairCost = this.getNewMaxRepairCost(outputStackMaxRepairCost);
                }

                outputStack.setRepairCost(outputStackMaxRepairCost);
                EnchantmentHelper.setEnchantments(enchantments, outputStack);
            }

            this.getSlot(3).set(outputStack);
            this.broadcastChanges();
        }
    }

    public void updateItemName(String customItemName) {
        this.customItemName = customItemName;
        ItemStack outputStack = this.getSlot(3).getItem();

        if(!outputStack.isEmpty()) {
            if(StringUtils.isBlank(customItemName)) {
                outputStack.resetHoverName();
            }
            else {
                outputStack.setHoverName(new StringTextComponent(this.customItemName));
            }
        }

        this.updateOutput();
    }

    public ItemStack onRepair(PlayerEntity player, ItemStack stack) {
        if(!player.abilities.instabuild) {
            if(this.useIronInsteadOfXp()) {
                ItemStack ironStack = this.getSlot(2).getItem();
                ironStack.setCount(ironStack.getCount() - this.repairCost.get());
            }
            else {
                player.giveExperienceLevels(-this.repairCost.get());
            }
        }

        float breakChance = ForgeHooks.onAnvilRepair(player, stack, this.getSlot(0).getItem(), this.getSlot(1).getItem());
        this.getSlot(0).set(ItemStack.EMPTY);

        if(this.repairMaterialCost > 0) {
            ItemStack repairMaterialStack = this.getSlot(1).getItem();

            if(!repairMaterialStack.isEmpty() && repairMaterialStack.getCount() > this.repairMaterialCost) {
                repairMaterialStack.shrink(this.repairMaterialCost);
                this.getSlot(1).set(repairMaterialStack);
            }
            else {
                this.getSlot(1).set(ItemStack.EMPTY);
            }
        }
        else {
            this.getSlot(1).set(ItemStack.EMPTY);
        }

        this.repairCost.set(0);
        this.worldPosCallable.execute((world, pos) -> {
            BlockState oldState = world.getBlockState(pos);

            if(!player.abilities.instabuild && oldState.is(BlockTags.ANVIL) && player.getRandom().nextFloat() < breakChance) {
                BlockState newState = AnvilBlock.damage(oldState);

                if(newState == null) {
                    world.removeBlock(pos, false);
                    world.levelEvent(1029, pos, 0);
                }
                else {
                    world.setBlock(pos, newState, 2);
                    world.levelEvent(1030, pos, 0);
                }
            }
            else {
                world.levelEvent(1030, pos, 0);
            }

        });

        return stack;
    }

    public boolean canRepair(PlayerEntity player) {
        if(player.abilities.instabuild) {
            return true;
        }
        else {
            if(this.repairCost.get() > 0) {
                if(this.useIronInsteadOfXp()) {
                    return this.getSlot(2).getItem().getCount() >= this.repairCost.get();
                }
                else {
                    return player.experienceLevel >= this.repairCost.get();
                }
            }
        }

        return false;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return this.worldPosCallable.evaluate((world, pos) -> world.getBlockState(pos).is(BlockTags.ANVIL) && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D, true);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if(slot != null && slot.hasItem()) {
            ItemStack oldStack = slot.getItem();
            newStack = oldStack.copy();

            if(index == 3) {
                if(!this.moveItemStackTo(oldStack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(oldStack, newStack);
            }
            else if(index >= 4 && index < 40) {
                if(!this.moveItemStackTo(oldStack, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if(!this.moveItemStackTo(oldStack, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if(oldStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            }
            else {
                slot.setChanged();
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
