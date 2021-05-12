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

package logictechcorp.reagenchant.core.registry.helper;

import com.minecraftabnormals.abnormals_core.core.util.registry.AbstractSubRegistryHelper;
import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ContainerSubRegistryHelper extends AbstractSubRegistryHelper<ContainerType<?>> {
    public ContainerSubRegistryHelper(RegistryHelper parent, DeferredRegister<ContainerType<?>> deferredRegister) {
        super(parent, deferredRegister);
    }

    public ContainerSubRegistryHelper(RegistryHelper parent) {
        super(parent, DeferredRegister.create(ForgeRegistries.CONTAINERS, parent.getModId()));
    }

    public <C extends Container, F extends ContainerType.IFactory<C>> RegistryObject<ContainerType<C>> createContainer(String name, Supplier<? extends F> container) {
        return this.deferredRegister.register(name, () -> new ContainerType<>(container.get()));
    }
}
