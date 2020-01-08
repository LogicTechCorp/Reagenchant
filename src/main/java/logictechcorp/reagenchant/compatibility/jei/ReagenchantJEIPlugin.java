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
import logictechcorp.reagenchant.client.gui.GuiReagentTable;
import logictechcorp.reagenchant.init.ReagenchantBlocks;
import logictechcorp.reagenchant.reagent.Reagent;
import logictechcorp.reagenchant.reagent.ReagentManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class ReagenchantJEIPlugin implements IModPlugin
{
    @Override
    public void register(IModRegistry registry)
    {
        List<ReagentWrapper> reagentWrappers = new ArrayList<>();

        for(Reagent reagent : Reagenchant.REAGENT_MANAGER.getWorldSpecificReagents().values())
        {
            reagentWrappers.add(new ReagentWrapper(reagent));
        }

        registry.addRecipeCatalyst(new ItemStack(ReagenchantBlocks.REAGENT_TABLE), ReagentTableCategory.ID);
        registry.addRecipeClickArea(GuiReagentTable.class, 2, 2, 51, 39, ReagentTableCategory.ID);
        registry.addRecipes(reagentWrappers, ReagentTableCategory.ID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        registry.addRecipeCategories(new ReagentTableCategory(registry.getJeiHelpers().getGuiHelper()));
    }
}
