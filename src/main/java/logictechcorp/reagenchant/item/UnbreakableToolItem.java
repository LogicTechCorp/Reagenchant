package logictechcorp.reagenchant.item;

import com.google.common.collect.Multimap;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraftforge.common.ToolType;

import java.util.Set;
import java.util.function.Consumer;

public class UnbreakableToolItem extends ToolItem
{
    public UnbreakableToolItem(float attackDamage, float attackSpeed, IItemTier tier, Set<Block> effectiveBlocks, Item.Properties properties)
    {
        super(attackDamage, attackSpeed, tier, effectiveBlocks, properties);
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

        if(equipmentSlotType == EquipmentSlotType.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this.getAttackDamage(stack), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", this.getAttackSpeed(stack), AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public int getHarvestLevel(ItemStack stack, ToolType toolType, PlayerEntity player, BlockState state)
    {
        return UnbreakingHandler.isItemBroken(stack) ? -1 : super.getHarvestLevel(stack, toolType, player, state);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state)
    {
        return UnbreakingHandler.isItemBroken(stack) ? 1.0F : super.getDestroySpeed(stack, state);
    }

    public float getAttackDamage(ItemStack stack)
    {
        return UnbreakingHandler.isItemBroken(stack) ? 0 : this.attackDamage;
    }

    public float getAttackSpeed(ItemStack stack)
    {
        return UnbreakingHandler.isItemBroken(stack) ? 0 : this.attackSpeed;
    }
}
