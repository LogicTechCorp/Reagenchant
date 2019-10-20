package logictechcorp.reagenchant.item;

import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CarrotOnAStickItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class UnbreakableCarrotOnAStickItem extends CarrotOnAStickItem
{
    public UnbreakableCarrotOnAStickItem(Properties properties)
    {
        super(properties);
        this.addPropertyOverride(UnbreakingHandler.BROKEN_PROPERTY_KEY, UnbreakingHandler.BROKEN_PROPERTY);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerEntity, Hand hand)
    {
        return UnbreakingHandler.getActionResult(playerEntity.getHeldItem(hand), () -> super.onItemRightClick(world, playerEntity, hand));
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken)
    {
        return UnbreakingHandler.damageItem(stack, entity, super.damageItem(stack, amount, entity, onBroken));
    }
}
