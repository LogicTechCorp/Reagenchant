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

package logictechcorp.reagenchant.init;

import logictechcorp.libraryex.utility.InjectionHelper;
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.inventory.ReagentTableContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ReagenchantContainerTypes
{
    public static final ContainerType<ReagentTableContainer> REAGENT_TABLE_CONTAINER = InjectionHelper.nullValue();

    @SubscribeEvent
    public static void onRegisterContainerTypes(RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().registerAll(
                new ContainerType<>((ContainerType.IFactory<Container>) (id, inventory) -> new ReagentTableContainer(id)).setRegistryName(Reagenchant.getResource("reagent_table_container"))
        );
    }
}
