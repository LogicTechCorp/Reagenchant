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

import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.block.ReagentTableBlock;
import logictechcorp.reagenchant.item.ReagentTableItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ReagenchantBlocks
{
    private static final Item.Properties DEFAULT_ITEM_BLOCK_BUILDER = new Item.Properties().group(Reagenchant.instance.getItemGroup());

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new ReagentTableBlock().setRegistryName("minecraft:enchanting_table"));
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ReagentTableItem().setRegistryName("minecraft:enchanting_table"));
    }

    public static Item.Properties getDefaultItemBlockBuilder()
    {
        return DEFAULT_ITEM_BLOCK_BUILDER;
    }
}
