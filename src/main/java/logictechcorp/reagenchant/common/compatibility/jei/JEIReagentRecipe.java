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

package logictechcorp.reagenchant.common.compatibility.jei;

import logictechcorp.reagenchant.common.reagent.Reagent;
import logictechcorp.reagenchant.common.reagent.ReagentEnchantData;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class JEIReagentRecipe {
    private final Reagent reagent;

    JEIReagentRecipe(Reagent reagent) {
        this.reagent = reagent;
    }

    public Reagent getReagent() {
        return this.reagent;
    }

    public void setIngredients(IIngredients ingredients) {
        Set<Enchantment> enchantments = this.reagent.getEnchantments();

        if(!enchantments.isEmpty()) {
            List<List<ItemStack>> slots = new ArrayList<>();
            List<ItemStack> enchantedBookStacks = new ArrayList<>();

            for(Enchantment enchantment : enchantments) {
                ReagentEnchantData reagentEnchantData = this.reagent.getReagentEnchantData(enchantment);

                for(int level = reagentEnchantData.getMinimumEnchantmentLevel(); level <= reagentEnchantData.getMaximumEnchantmentLevel(); level++) {
                    ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                    EnchantedBookItem.addEnchantment(enchantedBook, new EnchantmentData(enchantment, level));
                    enchantedBookStacks.add(enchantedBook);
                }
            }

            slots.add(enchantedBookStacks);
            ingredients.setInput(VanillaTypes.ITEM, new ItemStack(this.reagent.getItem()));
            ingredients.setOutputLists(VanillaTypes.ITEM, slots);
        }
    }
}