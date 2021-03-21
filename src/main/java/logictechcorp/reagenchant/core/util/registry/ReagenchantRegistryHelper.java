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
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class ReagenchantRegistryHelper extends RegistryHelper {
    public ReagenchantRegistryHelper(String modId) {
        super(modId);
    }

    @Override
    protected void putDefaultSubHelpers() {
        super.putDefaultSubHelpers();
        this.putSubHelper(ForgeRegistries.CONTAINERS, new ContainerSubRegistryHelper(this));
        this.putSubHelper(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, new LootModifierSubRegistryHelper(this));
    }

    @Nonnull
    public <T extends AbstractSubRegistryHelper<ContainerType<?>>> T getContainerSubHelper() {
        return this.getSubHelper(ForgeRegistries.CONTAINERS);
    }

    @Nonnull
    public <T extends AbstractSubRegistryHelper<GlobalLootModifierSerializer<?>>> T getLootModifierSubHelper() {
        return this.getSubHelper(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS);
    }
}
