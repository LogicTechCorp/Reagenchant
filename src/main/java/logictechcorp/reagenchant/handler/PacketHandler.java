package logictechcorp.reagenchant.handler;

import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.network.item.reagent.SPacketUpdateReagents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class PacketHandler
{
    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(Reagenchant.MOD_ID + ":network");

    public static void init()
    {
        CHANNEL.registerMessage(SPacketUpdateReagents.class, SPacketUpdateReagents.class, 0, Side.CLIENT);
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        World world = player.getEntityWorld();

        if(!world.isRemote)
        {
            CHANNEL.sendTo(new SPacketUpdateReagents(Reagenchant.REAGENT_MANAGER.getCurrentReagents().values()), (EntityPlayerMP) player);
        }
    }
}
