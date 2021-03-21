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

package logictechcorp.reagenchant.core.registry;

import logictechcorp.reagenchant.common.loot.UnbreakableItemStackLootModifier;
import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.util.registry.LootModifierSubRegistryHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class ReagenchantLootModifiers {
    public static final LootModifierSubRegistryHelper REGISTRY_HELPER = Reagenchant.REGISTRY_HELPER.getLootModifierSubHelper();

    public static final RegistryObject<GlobalLootModifierSerializer<?>> UNBREAKABLE_ITEMSTACK_LOOT_MODIFIER = REGISTRY_HELPER.createLootModifier("unbreakable_itemstack_loot_modifier", UnbreakableItemStackLootModifier.Serializer::new);

}
