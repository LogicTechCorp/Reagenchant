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

package logictechcorp.reagenchant.core.util.item;

import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class UnbreakableItemStackUtil {
    public static final String BROKEN_ITEM_KEY = Reagenchant.MOD_ID + ":BrokenItem";
    public static final String DISABLED_ENCHANTMENTS_KEY = Reagenchant.MOD_ID + ":DisabledEnchantments";

    public static void breakItem(@Nullable ServerPlayerEntity player, ItemStack stack) {
        if(hasNoUses(stack) && !hasBrokenTag(stack)) {
            CompoundNBT stackCompound = stack.getTag();
            ListNBT enchantments = stack.getEnchantmentTags();
            ListNBT disabledEnchantments = new ListNBT();

            for(int tagIndex = 0; tagIndex < enchantments.size(); tagIndex++) {
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantments.getCompound(tagIndex).getString("id")));

                if(enchantment == Enchantments.BINDING_CURSE) {
                    enchantments.remove(tagIndex);
                }
                else if(enchantment != Enchantments.UNBREAKING && enchantment != Enchantments.MENDING) {
                    disabledEnchantments.add(enchantments.remove(tagIndex));
                }
            }

            stackCompound.putBoolean(BROKEN_ITEM_KEY, true);
            stackCompound.put(DISABLED_ENCHANTMENTS_KEY, disabledEnchantments);

            if(player != null) {
                ItemStack heldStack = player.getItemInHand(player.getUsedItemHand());

                if(ItemStack.matches(stack, heldStack)) {
                    player.broadcastBreakEvent(player.getUsedItemHand());
                }
                else {
                    PlayerInventory playerInventory = player.inventory;

                    for(int i = 0; i < playerInventory.armor.size(); i++) {
                        ItemStack armorStack = playerInventory.armor.get(i);

                        if(ItemStack.matches(stack, armorStack)) {
                            player.broadcastBreakEvent(EquipmentSlotType.byTypeAndIndex(EquipmentSlotType.Group.ARMOR, i));
                        }
                    }
                }
            }
        }
    }

    public static void fixItem(ItemStack stack) {
        if(hasBrokenTag(stack)) {
            CompoundNBT stackCompound = stack.getTag();
            ListNBT enchantments = stack.getEnchantmentTags();
            ListNBT disabledEnchantments = stackCompound.getList(DISABLED_ENCHANTMENTS_KEY, 10);

            for(int tagIndex = 0; tagIndex < disabledEnchantments.size(); tagIndex++) {
                enchantments.add(disabledEnchantments.remove(tagIndex));
            }

            stackCompound.remove(DISABLED_ENCHANTMENTS_KEY);
            stackCompound.putBoolean(BROKEN_ITEM_KEY, false);
        }
    }

    public static boolean hasUnbreakable(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0;
    }

    public static boolean hasNoUses(ItemStack stack) {
        return hasUnbreakable(stack) && stack.getMaxDamage() - stack.getDamageValue() <= 0;
    }

    public static boolean hasBrokenTag(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(BROKEN_ITEM_KEY);
    }

    public static boolean isBroken(ItemStack stack) {
        return hasNoUses(stack) && hasBrokenTag(stack);
    }
}
