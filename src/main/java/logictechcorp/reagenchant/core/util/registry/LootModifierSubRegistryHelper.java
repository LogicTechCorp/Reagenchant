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

package logictechcorp.reagenchant.core.util.registry;

import com.minecraftabnormals.abnormals_core.core.util.registry.AbstractSubRegistryHelper;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class LootModifierSubRegistryHelper extends AbstractSubRegistryHelper<GlobalLootModifierSerializer<?>> {
    public LootModifierSubRegistryHelper(RegistryHelper parent, DeferredRegister<GlobalLootModifierSerializer<?>> deferredRegister) {
        super(parent, deferredRegister);
    }

    public LootModifierSubRegistryHelper(RegistryHelper parent) {
        super(parent, DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, parent.getModId()));
    }

    public RegistryObject<GlobalLootModifierSerializer<?>> createLootModifier(String name, Supplier<GlobalLootModifierSerializer<?>> container) {
        return this.deferredRegister.register(name, container);
    }
}
