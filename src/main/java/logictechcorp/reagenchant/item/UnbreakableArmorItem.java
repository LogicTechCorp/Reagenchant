package logictechcorp.reagenchant.item;

import com.google.common.collect.Multimap;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public class UnbreakableArmorItem extends ArmorItem
{
    private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};

    public UnbreakableArmorItem(IArmorMaterial armorMaterial, EquipmentSlotType equipmentSlotType, Properties properties)
    {
        super(armorMaterial, equipmentSlotType, properties);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
    {
        if(UnbreakingHandler.isItemBroken(stack))
        {
            return 0;
        }

        if(UnbreakingHandler.canItemBeBroken(stack))
        {
            UnbreakingHandler.breakItem(entity, stack, EquipmentSlotType.MAINHAND);
            return 0;
        }

        return super.damageItem(stack, amount, entity, onBroken);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlotType, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlotType);

        if(equipmentSlotType == this.slot)
        {
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlotType.getIndex()], "Armor modifier", this.getDamageReduceAmount(stack), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlotType.getIndex()], "Armor toughness", this.getToughness(stack), AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    public int getDamageReduceAmount(ItemStack stack)
    {
        return UnbreakingHandler.isItemBroken(stack) ? 0 : this.damageReduceAmount;
    }

    public float getToughness(ItemStack stack)
    {
        return UnbreakingHandler.isItemBroken(stack) ? 0 : this.toughness;
    }
}
