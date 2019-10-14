package logictechcorp.reagenchant.item;

import com.google.common.collect.Multimap;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraftforge.common.ToolType;

import java.util.function.Consumer;

public class UnbreakablePickaxeItem extends PickaxeItem
{
    public UnbreakablePickaxeItem(IItemTier itemTier, int attackDamage, float attackSpeed, Properties properties)
    {
        super(itemTier, attackDamage, attackSpeed, properties);
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
        Material material = state.getMaterial();
        float destroySpeed = material != Material.IRON && material != Material.ANVIL && material != Material.ROCK ? 1.0F : this.efficiency;
        return UnbreakingHandler.getDestroySpeed(stack, destroySpeed);
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
