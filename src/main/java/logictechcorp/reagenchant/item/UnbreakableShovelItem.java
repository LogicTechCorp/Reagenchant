package logictechcorp.reagenchant.item;

import com.google.common.collect.Multimap;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.common.ToolType;

import java.util.function.Consumer;

public class UnbreakableShovelItem extends ShovelItem
{
    public UnbreakableShovelItem(IItemTier itemTier, float attackDamage, float attackSpeed, Properties properties)
    {
        super(itemTier, attackDamage, attackSpeed, properties);
        this.addPropertyOverride(UnbreakingHandler.BROKEN_PROPERTY_KEY, UnbreakingHandler.BROKEN_PROPERTY);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        return UnbreakingHandler.getActionResultType(context.getItem(), () -> super.onItemUse(context));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
    {
        return UnbreakingHandler.damageItem(stack, entity, super.damageItem(stack, amount, entity, onBroken));
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state)
    {
        return UnbreakingHandler.canHarvestBlock(stack, super.canHarvestBlock(state));
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType toolType, PlayerEntity player, BlockState state)
    {
        return UnbreakingHandler.getHarvestLevel(stack, super.getHarvestLevel(stack, toolType, player, state));
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state)
    {
        return UnbreakingHandler.getDestroySpeed(stack, super.getDestroySpeed(stack, state));
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlotType, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlotType);

        if(equipmentSlotType == EquipmentSlotType.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", UnbreakingHandler.getAttackDamage(stack, this.attackDamage), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", UnbreakingHandler.getAttackSpeed(stack, this.attackSpeed), AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }
}
