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

import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.init.ReagenchantBlocks;
import logictechcorp.reagenchant.init.ReagenchantTextures;
import logictechcorp.reagenchant.reagent.Reagent;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import java.util.Map;

class ReagentTableCategory implements IRecipeCategory<ReagentWrapper>
{
    static final String ID = Reagenchant.MOD_ID + ":reagent_table";
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private final IDrawable background;
    private final IDrawable icon;
    private final String title;

    ReagentTableCategory(IGuiHelper guiHelper)
    {
        this.background = guiHelper.createDrawable(ReagenchantTextures.REAGENT_TABLE_RECIPE_GUI, 0, 0, 79, 25);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ReagenchantBlocks.REAGENT_TABLE));
        this.title = I18n.format("container." + Reagenchant.MOD_ID + ":jei.reagent_table.title");

    }

    @Override
    public String getUid()
    {
        return ID;
    }

    @Override
    public String getTitle()
    {
        return this.title;
    }

    @Override
    public String getModName()
    {
        return Reagenchant.NAME;
    }

    @Override
    public IDrawable getBackground()
    {
        return this.background;
    }

    @Override
    public IDrawable getIcon()
    {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ReagentWrapper reagentWrapper, IIngredients ingredients)
    {
        Reagent reagent = reagentWrapper.getReagent();

        recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).addTooltipCallback((slotIndex, input, ingredient, tooltip) ->
        {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(ingredient);

            for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet())
            {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();

                tooltip.add("");
                tooltip.add(enchantment.getTranslatedName(level));
                tooltip.add(I18n.format("container." + Reagenchant.MOD_ID + ":jei.reagent_table.enchantment_probability.base", reagent.getReagentEnchantData(enchantment).getEnchantmentProbability() * 100.0D));
                tooltip.add(I18n.format("container." + Reagenchant.MOD_ID + ":jei.reagent_table.reagent_cost", reagent.getReagentEnchantData(enchantment).getReagentCost()));
            }
        });
        recipeLayout.getItemStacks().init(INPUT_SLOT, true, 0, 4);
        recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 58, 4);
        recipeLayout.getItemStacks().set(INPUT_SLOT, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        recipeLayout.getItemStacks().set(OUTPUT_SLOT, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

}
