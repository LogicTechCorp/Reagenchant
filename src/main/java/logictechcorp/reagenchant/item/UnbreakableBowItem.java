package logictechcorp.reagenchant.item;

import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class UnbreakableBowItem extends BowItem
{
    public UnbreakableBowItem(Properties properties)
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
}
