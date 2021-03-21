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

package logictechcorp.reagenchant.core;

import com.minecraftabnormals.abnormals_core.core.util.registry.RegistryHelper;
import com.mojang.brigadier.CommandDispatcher;
import logictechcorp.reagenchant.client.gui.screen.inventory.ReagentEnchantingTableScreen;
import logictechcorp.reagenchant.client.item.ReagenchantItemModelProperties;
import logictechcorp.reagenchant.client.renderer.tileentity.ReagentTableTileEntityRenderer;
import logictechcorp.reagenchant.common.command.ReagenchantCommand;
import logictechcorp.reagenchant.common.network.item.reagent.MessageSUpdateReagentsPacket;
import logictechcorp.reagenchant.common.reagent.ReagentManager;
import logictechcorp.reagenchant.core.other.ReagenchantOverrides;
import logictechcorp.reagenchant.core.registry.ReagenchantContainers;
import logictechcorp.reagenchant.core.registry.ReagenchantTileEntityTypes;
import logictechcorp.reagenchant.core.util.registry.ReagenchantRegistryHelper;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reagenchant.MOD_ID)
public class Reagenchant {
    public static final String MOD_ID = "reagenchant";
    public static final String NETWORK_VERSION = "reagenchant_network_1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "network"), () -> NETWORK_VERSION, NETWORK_VERSION::equals, NETWORK_VERSION::equals);
    public static final ReagenchantRegistryHelper REGISTRY_HELPER = new ReagenchantRegistryHelper(MOD_ID);
    public static final RegistryHelper OVERRIDE_REGISTRY_HELPER = new RegistryHelper("minecraft");
    public static final ReagentManager REAGENT_MANAGER = new ReagentManager();

    public static final Logger LOGGER = LogManager.getLogger("Reagenchant");

    public Reagenchant() {
        ReagenchantConfig.register();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        REGISTRY_HELPER.register(modEventBus);
        OVERRIDE_REGISTRY_HELPER.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::onClientSetup));

        MinecraftForge.EVENT_BUS.register(this);
        this.registerMessages();
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ReagenchantOverrides::register);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ReagenchantItemModelProperties.register();
            ClientRegistry.bindTileEntityRenderer(ReagenchantTileEntityTypes.REAGENT_ENCHANTING_TABLE_TILE_ENTITY.get(), ReagentTableTileEntityRenderer::new);
            ScreenManager.registerFactory(ReagenchantContainers.REAGENT_ENCHANTING_TABLE_CONTAINER.get(), ReagentEnchantingTableScreen::new);
        });
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        ReagenchantCommand.register(dispatcher);
    }

    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(REAGENT_MANAGER);
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.getEntityWorld();

        if(!world.isRemote()) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new MessageSUpdateReagentsPacket(REAGENT_MANAGER.getReagents().values()));
        }
    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        REAGENT_MANAGER.cleanup();
    }

    private void registerMessages() {
        CHANNEL.messageBuilder(MessageSUpdateReagentsPacket.class, 0)
                .encoder(MessageSUpdateReagentsPacket::serialize)
                .decoder(MessageSUpdateReagentsPacket::deserialize)
                .consumer(MessageSUpdateReagentsPacket::handle)
                .add();
    }
}
