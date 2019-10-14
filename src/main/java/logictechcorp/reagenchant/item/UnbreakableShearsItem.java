package logictechcorp.reagenchant.item;

import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;

import java.util.function.Consumer;

public class UnbreakableShearsItem extends ShearsItem
{
    public UnbreakableShearsItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand)
    {
        return !UnbreakingHandler.isItemBroken(stack) && super.itemInteractionForEntity(stack, player, entity, hand);
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
    public float getDestroySpeed(ItemStack stack, BlockState state)
    {
        return UnbreakingHandler.getDestroySpeed(stack, super.getDestroySpeed(stack, state));
    }
}
