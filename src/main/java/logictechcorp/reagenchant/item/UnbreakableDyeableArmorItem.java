package logictechcorp.reagenchant.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.IDyeableArmorItem;

public class UnbreakableDyeableArmorItem extends UnbreakableArmorItem implements IDyeableArmorItem
{
    public UnbreakableDyeableArmorItem(IArmorMaterial armorMaterial, EquipmentSlotType equipmentSlotType, Properties properties)
    {
        super(armorMaterial, equipmentSlotType, properties);
    }
}
