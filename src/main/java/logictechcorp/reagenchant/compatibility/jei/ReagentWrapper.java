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

package logictechcorp.reagenchant.compatibility.jei;

import logictechcorp.reagenchant.api.reagent.IReagent;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

class ReagentWrapper implements IRecipeWrapper
{
    private final IReagent reagent;

    ReagentWrapper(IReagent reagent)
    {
        this.reagent = reagent;
    }

    @Override
    public void getIngredients(IIngredients ingredients)
    {
        List<List<ItemStack>> slots = new ArrayList<>();
        List<ItemStack> enchantments = new ArrayList<>();

        for(Enchantment enchantment : this.reagent.getAssociatedEnchantments())
        {
            for(int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++)
            {
                ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
                ItemEnchantedBook.addEnchantment(enchantedBook, new EnchantmentData(enchantment, level));
                enchantments.add(enchantedBook);
            }
        }

        slots.add(enchantments);
        ingredients.setInput(VanillaTypes.ITEM, new ItemStack(this.reagent.getItem()));
        ingredients.setOutputLists(VanillaTypes.ITEM, slots);
    }

    public IReagent getReagent()
    {
        return this.reagent;
    }
}
