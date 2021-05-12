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
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class JEIReagentRecipeManager implements IRecipeManagerPlugin {
    private final Map<ResourceLocation, JEIReagentRecipe> reagentRecipes = new HashMap<>();

    public void registerReagentRecipe(Reagent reagent) {
        if(!reagent.getEnchantments().isEmpty()) {
            this.reagentRecipes.put(reagent.getItem().getRegistryName(), new JEIReagentRecipe(reagent));
        }
    }

    public void unregisterReagentRecipe(Reagent reagent) {
        this.reagentRecipes.remove(reagent.getItem().getRegistryName());
    }

    public void clearReagentRecipes() {
        this.reagentRecipes.clear();
    }

    @Override
    public <V> List<ResourceLocation> getRecipeCategoryUids(IFocus<V> focus) {
        return Collections.singletonList(JEIReagenchantPlugin.ID);
    }

    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
        return this.getRecipes(recipeCategory);
    }

    @Override
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {
        if(recipeCategory.getUid().equals(JEIReagenchantPlugin.ID)) {
            return (List<T>) new ArrayList<>(this.reagentRecipes.values());
        }

        return Collections.emptyList();
    }
}
