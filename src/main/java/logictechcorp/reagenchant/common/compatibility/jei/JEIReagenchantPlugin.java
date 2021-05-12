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

import logictechcorp.reagenchant.client.gui.screen.inventory.ReagentEnchantingTableScreen;
import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.registry.ReagenchantBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIReagenchantPlugin implements IModPlugin {
    static final ResourceLocation ID = new ResourceLocation(Reagenchant.MOD_ID, "reagent_enchanting");
    static final JEIReagentRecipeManager REAGENT_RECIPE_MANAGER = new JEIReagentRecipeManager();

    public static JEIReagentRecipeManager getReagentRecipeManager() {
        return REAGENT_RECIPE_MANAGER;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(ReagenchantBlocks.REAGENT_ENCHANTING_TABLE.get()), ID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registry) {
        registry.addRecipeClickArea(ReagentEnchantingTableScreen.class, 3, 3, 58, 43, ID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new JEIReagentEnchantingCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registry) {
        registry.addRecipeManagerPlugin(REAGENT_RECIPE_MANAGER);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }
}
