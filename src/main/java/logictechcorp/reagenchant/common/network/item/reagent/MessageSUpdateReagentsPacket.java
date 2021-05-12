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

package logictechcorp.reagenchant.common.network.item.reagent;

import logictechcorp.reagenchant.common.reagent.Reagent;
import logictechcorp.reagenchant.common.reagent.ReagentEnchantData;
import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class MessageSUpdateReagentsPacket {
    private final Collection<Reagent> reagents;

    public MessageSUpdateReagentsPacket(Collection<Reagent> reagents) {
        this.reagents = reagents;
    }

    public static MessageSUpdateReagentsPacket deserialize(PacketBuffer buffer) {
        List<Reagent> reagents = new ArrayList<>();
        int reagentAmount = buffer.readInt();

        for(int reagentIndex = 0; reagentIndex < reagentAmount; reagentIndex++) {
            ResourceLocation itemRegistryName = buffer.readResourceLocation();
            Item item = ForgeRegistries.ITEMS.getValue(itemRegistryName);
            Reagent reagent = new Reagent(item);
            int enchantmentAmount = buffer.readInt();

            for(int enchantmentIndex = 0; enchantmentIndex < enchantmentAmount; enchantmentIndex++) {
                ResourceLocation enchantmentRegistryName = buffer.readResourceLocation();
                Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchantmentRegistryName);
                int minimumEnchantmentLevel = buffer.readInt();
                int maximumEnchantmentLevel = buffer.readInt();
                float enchantmentProbability = buffer.readFloat();
                int reagentCost = buffer.readInt();

                ReagentEnchantData reagentEnchantData = new ReagentEnchantData(enchantment, minimumEnchantmentLevel, maximumEnchantmentLevel, enchantmentProbability, reagentCost);
                reagent.addEnchantment(reagentEnchantData);
            }

            reagents.add(reagent);
        }

        return new MessageSUpdateReagentsPacket(reagents);
    }

    public void serialize(PacketBuffer buffer) {
        buffer.writeInt(this.reagents.size());

        for(Reagent reagent : this.reagents) {
            buffer.writeResourceLocation(reagent.getItem().getRegistryName());

            Set<Enchantment> enchantments = reagent.getEnchantments();
            buffer.writeInt(enchantments.size());

            for(Enchantment enchantment : enchantments) {
                ReagentEnchantData reagentEnchantData = reagent.getReagentEnchantData(enchantment);
                buffer.writeResourceLocation(enchantment.getRegistryName());
                buffer.writeInt(reagentEnchantData.getMinimumEnchantmentLevel());
                buffer.writeInt(reagentEnchantData.getMaximumEnchantmentLevel());
                buffer.writeFloat(reagentEnchantData.getEnchantmentProbability());
                buffer.writeInt(reagentEnchantData.getReagentCost());
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if(context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
                Reagenchant.REAGENT_MANAGER.syncClientReagents(this.reagents);
                context.setPacketHandled(true);
            }
        });
    }
}
