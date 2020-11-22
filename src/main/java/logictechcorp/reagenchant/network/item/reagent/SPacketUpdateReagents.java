package logictechcorp.reagenchant.network.item.reagent;

import io.netty.buffer.ByteBuf;
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.reagent.Reagent;
import logictechcorp.reagenchant.reagent.ReagentEnchantData;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class SPacketUpdateReagents implements IMessage, IMessageHandler<SPacketUpdateReagents, IMessage>
{
    private Collection<Reagent> reagents;

    public SPacketUpdateReagents()
    {
        this(new ArrayList<>());
    }

    public SPacketUpdateReagents(Collection<Reagent> reagents)
    {
        this.reagents = reagents;
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        List<Reagent> reagents = new ArrayList<>();
        int reagentAmount = buffer.readInt();

        for(int reagentIndex = 0; reagentIndex < reagentAmount; reagentIndex++)
        {
            Item item = ByteBufUtils.readRegistryEntry(buffer, ForgeRegistries.ITEMS);
            Reagent reagent = new Reagent(item);
            int enchantmentAmount = buffer.readInt();

            for(int enchantmentIndex = 0; enchantmentIndex < enchantmentAmount; enchantmentIndex++)
            {
                Enchantment enchantment = ByteBufUtils.readRegistryEntry(buffer, ForgeRegistries.ENCHANTMENTS);
                int minimumEnchantmentLevel = buffer.readInt();
                int maximumEnchantmentLevel = buffer.readInt();
                double enchantmentProbability = buffer.readDouble();
                int reagentCost = buffer.readInt();

                ReagentEnchantData reagentEnchantData = new ReagentEnchantData(enchantment, minimumEnchantmentLevel, maximumEnchantmentLevel, enchantmentProbability, reagentCost);
                reagent.addEnchantment(reagentEnchantData);
            }

            reagents.add(reagent);
        }

        this.reagents = reagents;
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.reagents.size());

        for(Reagent reagent : this.reagents)
        {
            ByteBufUtils.writeRegistryEntry(buffer, reagent.getItem());

            Set<Enchantment> enchantments = reagent.getEnchantments();
            buffer.writeInt(enchantments.size());

            for(Enchantment enchantment : enchantments)
            {
                ReagentEnchantData reagentEnchantData = reagent.getReagentEnchantData(enchantment);
                ByteBufUtils.writeRegistryEntry(buffer, enchantment);
                buffer.writeInt(reagentEnchantData.getMinimumEnchantmentLevel());
                buffer.writeInt(reagentEnchantData.getMaximumEnchantmentLevel());
                buffer.writeDouble(reagentEnchantData.getEnchantmentProbability());
                buffer.writeInt(reagentEnchantData.getReagentCost());
            }
        }
    }

    @Override
    public IMessage onMessage(SPacketUpdateReagents packet, MessageContext context)
    {
        Reagenchant.REAGENT_MANAGER.syncClientReagents(packet.reagents);
        return null;
    }
}