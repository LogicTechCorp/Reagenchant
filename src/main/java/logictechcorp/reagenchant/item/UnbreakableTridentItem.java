package logictechcorp.reagenchant.item;

import com.google.common.collect.Multimap;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class UnbreakableTridentItem extends TridentItem
{
    public UnbreakableTridentItem(Properties properties)
    {
        super(properties);
        this.addPropertyOverride(UnbreakingHandler.BROKEN_PROPERTY_KEY, UnbreakingHandler.BROKEN_PROPERTY);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        return UnbreakingHandler.getActionResult(player.getHeldItem(hand), () -> super.onItemRightClick(world, player, hand));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
    {
        return UnbreakingHandler.damageItem(stack, entity, super.damageItem(stack, amount, entity, onBroken));
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return UnbreakingHandler.getUseDuration(stack, super.getUseDuration(stack));
    }

    @Override
    public UseAction getUseAction(ItemStack stack)
    {
        return UnbreakingHandler.getUseAction(stack, super.getUseAction(stack));
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);

        if(equipmentSlot == EquipmentSlotType.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", UnbreakingHandler.getAttackDamage(stack, 8.0D), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", UnbreakingHandler.getAttackSpeed(stack, -2.9D), AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }
}
