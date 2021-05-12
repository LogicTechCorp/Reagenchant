package logictechcorp.reagenchant.common.network.item;

import logictechcorp.reagenchant.common.inventory.container.CustomAnvilContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageCUpdateItemNamePacket {
    private final String name;

    public MessageCUpdateItemNamePacket(String name) {
        this.name = name;
    }

    public static MessageCUpdateItemNamePacket deserialize(PacketBuffer buffer) {
        return new MessageCUpdateItemNamePacket(buffer.readString());
    }

    public void serialize(PacketBuffer buffer) {
        buffer.writeString(this.name);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide() == LogicalSide.SERVER) {

                ServerPlayerEntity player = context.getSender();

                if(player != null) {
                    Container container = player.openContainer;

                    if(container instanceof CustomAnvilContainer) {
                        ((CustomAnvilContainer) container).updateItemName(this.name);
                    }
                }

                context.setPacketHandled(true);
            }
        });
    }
}
