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

package logictechcorp.reagenchant;

import com.mojang.brigadier.CommandDispatcher;
import logictechcorp.libraryex.resource.OptionalResourcePack;
import logictechcorp.reagenchant.block.ReagenchantBlocks;
import logictechcorp.reagenchant.command.ReagentCommand;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import logictechcorp.reagenchant.inventory.container.ReagenchantContainers;
import logictechcorp.reagenchant.item.ReagenchantItems;
import logictechcorp.reagenchant.proxy.ClientProxy;
import logictechcorp.reagenchant.proxy.ServerProxy;
import logictechcorp.reagenchant.reagent.ReagentManager;
import logictechcorp.reagenchant.tileentity.ReagenchantTileEntities;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reagenchant.MOD_ID)
public class Reagenchant
{
    public static final String MOD_ID = "reagenchant";
    public static final ReagentManager REAGENT_MANAGER = new ReagentManager();

    public static final Logger LOGGER = LogManager.getLogger("Reagenchant");

    public Reagenchant()
    {
        DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::onCommonSetup);
        ReagenchantBlocks.BLOCK_OVERRIDES.register(modEventBus);
        ReagenchantItems.ITEM_OVERRIDES.register(modEventBus);
        ReagenchantTileEntities.TILE_ENTITIES.register(modEventBus);
        ReagenchantContainers.CONTAINERS.register(modEventBus);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(this::onServerAboutToStart);
        forgeEventBus.addListener(this::onServerStarting);
        forgeEventBus.addListener(this::onServerStopping);
        ReagenchantConfig.registerConfigs();
    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {
        UnbreakingHandler.setup();
    }

    private void onServerAboutToStart(FMLServerAboutToStartEvent event)
    {
        MinecraftServer server = event.getServer();
        ModFile modFile = ModList.get().getModFileById(MOD_ID).getFile();

        server.getResourcePacks().addPackFinder(new OptionalResourcePack(modFile, "reagent_pack", true));
        server.getResourceManager().addReloadListener(REAGENT_MANAGER);
    }

    private void onServerStarting(FMLServerStartingEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
        ReagentCommand.register(dispatcher);
    }

    private void onServerStopping(FMLServerStoppingEvent event)
    {
        REAGENT_MANAGER.cleanup();
    }
}
