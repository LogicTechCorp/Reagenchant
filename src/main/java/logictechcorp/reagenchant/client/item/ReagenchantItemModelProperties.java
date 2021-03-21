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

package logictechcorp.reagenchant.client.item;

import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.util.item.UnbreakableItemStackUtil;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ReagenchantItemModelProperties {
    private static final ResourceLocation BROKEN_PROPERTY_KEY = new ResourceLocation(Reagenchant.MOD_ID, "broken");
    private static final IItemPropertyGetter BROKEN_PROPERTY = (stack, world, entity) -> UnbreakableItemStackUtil.isBroken(stack) ? 1.0F : 0.0F;

    public static void register() {
        registerBrokenItemProperty();
    }

    private static void registerBrokenItemProperty() {
        for(Item item : ForgeRegistries.ITEMS) {
            if(item.isDamageable()) {
                ItemModelsProperties.registerProperty(item, BROKEN_PROPERTY_KEY, BROKEN_PROPERTY);
            }
        }
    }
}
