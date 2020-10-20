/*
 * Reagenchant
 * Copyright (c) 2019-2020 by LogicTechCorp
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
import logictechcorp.libraryex.resource.BuiltinDataPack;
import logictechcorp.reagenchant.block.ReagenchantBlocks;
import logictechcorp.reagenchant.client.gui.screen.ReagentTableScreen;
import logictechcorp.reagenchant.client.renderer.tileentity.ReagentTableTileEntityRenderer;
import logictechcorp.reagenchant.command.ReagenchantCommand;
import logictechcorp.reagenchant.inventory.container.ReagenchantContainers;
import logictechcorp.reagenchant.item.ReagenchantItems;
import logictechcorp.reagenchant.network.item.reagent.MessageSUpdateReagentsPacket;
import logictechcorp.reagenchant.reagent.ReagentManager;
import logictechcorp.reagenchant.tileentity.ReagenchantTileEntityTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.FolderPackFinder;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

@Mod(Reagenchant.MOD_ID)
public class Reagenchant
{
    public static final String MOD_ID = "reagenchant";
    public static final String NETWORK_VERSION = "reagenchant_network_1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "network"), () -> NETWORK_VERSION, NETWORK_VERSION::equals, NETWORK_VERSION::equals);
    public static final ReagentManager REAGENT_MANAGER = new ReagentManager();

    public static final Logger LOGGER = LogManager.getLogger("Reagenchant");

    public Reagenchant()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () ->
        {
            modEventBus.addListener(this::onClientSetup);
        });

        ReagenchantBlocks.BLOCK_OVERRIDES.register(modEventBus);
        ReagenchantItems.ITEM_OVERRIDES.register(modEventBus);
        ReagenchantTileEntityTypes.TILE_ENTITY_TYPE_OVERRIDES.register(modEventBus);
        ReagenchantContainers.CONTAINERS.register(modEventBus);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        forgeEventBus.addListener(this::onServerAboutToStart);
        forgeEventBus.addListener(this::onServerStarting);
        forgeEventBus.addListener(this::onPlayerLoggedIn);
        forgeEventBus.addListener(this::onServerStopping);

        this.registerMessages();
        ReagenchantConfig.registerConfigs();
    }

    @OnlyIn(Dist.CLIENT)
    private void onClientSetup(FMLClientSetupEvent event)
    {
        ClientRegistry.bindTileEntityRenderer(ReagenchantTileEntityTypes.REAGENT_TABLE_TILE_ENTITY.get(), ReagentTableTileEntityRenderer::new);
        ScreenManager.registerFactory(ReagenchantContainers.REAGENT_TABLE_CONTAINER.get(), ReagentTableScreen::new);
    }

    private void onServerAboutToStart(FMLServerAboutToStartEvent event)
    {
        MinecraftServer server = event.getServer();
        ResourcePackList<ResourcePackInfo> resourcePacks = server.getResourcePacks();
        ModFile modFile = ModList.get().getModFileById(MOD_ID).getFile();

        if(ReagenchantConfig.COMMON.reagentPackUseGlobalReagentPacks.get())
        {
            Path globalBiomePacksPath = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(Paths.get("reagenchant", "reagent_packs")), "reagenchant reagent packs");
            resourcePacks.addPackFinder(new FolderPackFinder(globalBiomePacksPath.toFile()));
        }

        if(ReagenchantConfig.COMMON.reagentPackUseReagenchantReagentPack.get())
        {
            resourcePacks.addPackFinder(new BuiltinDataPack(modFile, "reagent_pack"));
        }
        server.getResourceManager().addReloadListener(REAGENT_MANAGER);
    }

    private void onServerStarting(FMLServerStartingEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
        ReagenchantCommand.register(dispatcher);
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity player = event.getPlayer();
        World world = player.getEntityWorld();

        if(!world.isRemote())
        {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new MessageSUpdateReagentsPacket(REAGENT_MANAGER.getReagents().values()));
        }
    }

    private void onServerStopping(FMLServerStoppingEvent event)
    {
        REAGENT_MANAGER.cleanup();
    }

    private void registerMessages()
    {
        CHANNEL.messageBuilder(MessageSUpdateReagentsPacket.class, 0)
                .encoder(MessageSUpdateReagentsPacket::serialize)
                .decoder(MessageSUpdateReagentsPacket::deserialize)
                .consumer(MessageSUpdateReagentsPacket::handle)
                .add();
    }
}
