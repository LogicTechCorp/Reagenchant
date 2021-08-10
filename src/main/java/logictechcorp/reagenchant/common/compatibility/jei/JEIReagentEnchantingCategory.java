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
import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.registry.ReagenchantBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Map;

import static logictechcorp.reagenchant.common.compatibility.jei.JEIReagenchantPlugin.ID;

class JEIReagentEnchantingCategory implements IRecipeCategory<JEIReagentRecipe> {
    public static final ResourceLocation REAGENT_ENCHANTMENT_TABLE_RECIPE_GUI = new ResourceLocation(Reagenchant.MOD_ID, "textures/gui/container/reagent_enchanting_recipe.png");
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private final IDrawable background;
    private final IDrawable icon;
    private final ITextComponent title;

    JEIReagentEnchantingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(REAGENT_ENCHANTMENT_TABLE_RECIPE_GUI, 0, 0, 79, 25);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ReagenchantBlocks.REAGENT_ENCHANTING_TABLE.get()));
        this.title = new TranslationTextComponent("container." + Reagenchant.MOD_ID + ".jei.reagent_enchanting.title");
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public Class<? extends JEIReagentRecipe> getRecipeClass() {
        return JEIReagentRecipe.class;
    }

    @Override
    public String getTitle() {
        return this.title.getString();
    }

    @Override
    public ITextComponent getTitleAsTextComponent() {
        return this.title;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(JEIReagentRecipe reagentRecipe, IIngredients ingredients) {
        reagentRecipe.setIngredients(ingredients);
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, JEIReagentRecipe reagentRecipe, IIngredients ingredients) {
        Reagent reagent = reagentRecipe.getReagent();

        recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).addTooltipCallback((slotIndex, input, ingredientStack, tooltip) -> {
            if(!input) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(ingredientStack);

                for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    Enchantment enchantment = entry.getKey();
                    int level = entry.getValue();

                    tooltip.add(StringTextComponent.EMPTY);
                    tooltip.add(enchantment.getFullname(level));
                    tooltip.add(new TranslationTextComponent("container." + Reagenchant.MOD_ID + ".jei.reagent_enchanting.enchantment_probability", reagent.getReagentEnchantData(enchantment).getEnchantmentProbability() * 100.0D));
                    tooltip.add(new TranslationTextComponent("container." + Reagenchant.MOD_ID + ".jei.reagent_enchanting.reagent_cost", reagent.getReagentEnchantData(enchantment).getReagentCost()));
                }
            }
        });
        recipeLayout.getItemStacks().init(INPUT_SLOT, true, 0, 4);
        recipeLayout.getItemStacks().init(OUTPUT_SLOT, false, 58, 4);
        recipeLayout.getItemStacks().set(INPUT_SLOT, ingredients.getInputs(VanillaTypes.ITEM).get(0));
        recipeLayout.getItemStacks().set(OUTPUT_SLOT, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
    }

}