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

import logictechcorp.reagenchant.common.inventory.container.CustomAnvilContainer;
import logictechcorp.reagenchant.common.inventory.container.ReagentCandleContainer;
import logictechcorp.reagenchant.common.inventory.container.ReagentEnchantingTableContainer;
import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.registry.helper.ContainerSubRegistryHelper;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class ReagenchantContainers {
    public static final ContainerSubRegistryHelper REGISTRY_HELPER = Reagenchant.REGISTRY_HELPER.getContainerSubHelper();

    public static final RegistryObject<ContainerType<ReagentEnchantingTableContainer>> REAGENT_ENCHANTING_TABLE_CONTAINER = REGISTRY_HELPER.createContainer("reagent_enchanting_table_container", () -> ReagentEnchantingTableContainer::new);
    public static final RegistryObject<ContainerType<ReagentCandleContainer>> REAGENT_CANDLE_CONTAINER = REGISTRY_HELPER.createContainer("reagent_candle_container", () -> ReagentCandleContainer::new);
    public static final RegistryObject<ContainerType<CustomAnvilContainer>> CUSTOM_ANVIL_CONTAINER = REGISTRY_HELPER.createContainer("custom_anvil_container", () -> CustomAnvilContainer::new);
}

