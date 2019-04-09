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

import logictechcorp.libraryex.item.builder.ItemBuilder;
import logictechcorp.libraryex.utility.InjectionHelper;
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.block.BlockReagentTable;
import logictechcorp.reagenchant.item.ItemBlockReagentTable;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(Reagenchant.MOD_ID)
public class ReagenchantBlocks
{
    @GameRegistry.ObjectHolder("minecraft:enchanting_table")
    public static final BlockReagentTable REAGENT_ENCHANTMENT_TABLE = InjectionHelper.nullValue();

    private static final ItemBuilder DEFAULT_ITEM_BLOCK_BUILDER = new ItemBuilder();

    @Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
    public static class EventHandler
    {
        @SubscribeEvent
        public static void onRegisterBlocks(RegistryEvent.Register<Block> event)
        {
            event.getRegistry().registerAll(
                    new BlockReagentTable()
            );
        }

        @SubscribeEvent
        public static void onRegisterItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().registerAll(
                    new ItemBlockReagentTable()
            );
        }
    }

    public static ItemBuilder getDefaultItemBlockBuilder()
    {
        return DEFAULT_ITEM_BLOCK_BUILDER;
    }
}
