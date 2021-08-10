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
        return new MessageCUpdateItemNamePacket(buffer.readUtf());
    }

    public void serialize(PacketBuffer buffer) {
        buffer.writeUtf(this.name);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide() == LogicalSide.SERVER) {

                ServerPlayerEntity player = context.getSender();

                if(player != null) {
                    Container container = player.containerMenu;

                    if(container instanceof CustomAnvilContainer) {
                        ((CustomAnvilContainer) container).updateItemName(this.name);
                    }
                }

                context.setPacketHandled(true);
            }
        });
    }
}
