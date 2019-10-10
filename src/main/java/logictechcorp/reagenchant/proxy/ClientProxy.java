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

package logictechcorp.reagenchant.proxy;

import logictechcorp.reagenchant.client.gui.screen.ReagentTableScreen;
import logictechcorp.reagenchant.client.renderer.tileentity.TileEntityReagentTableRenderer;
import logictechcorp.reagenchant.inventory.container.ReagenchantContainers;
import logictechcorp.reagenchant.tileentity.ReagentTableTileEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientProxy
{
    public ClientProxy()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {
        ClientRegistry.bindTileEntitySpecialRenderer(ReagentTableTileEntity.class, new TileEntityReagentTableRenderer());
        ScreenManager.registerFactory(ReagenchantContainers.REAGENT_TABLE_CONTAINER.get(), ReagentTableScreen::new);
    }
}
