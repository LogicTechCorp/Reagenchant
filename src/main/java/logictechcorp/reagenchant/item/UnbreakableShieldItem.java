package logictechcorp.reagenchant.item;

import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class UnbreakableShieldItem extends ShieldItem
{
    public UnbreakableShieldItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getHeldItem(hand);

        if(UnbreakingHandler.isItemBroken(stack))
        {
            return new ActionResult<>(ActionResultType.FAIL, stack);
        }
        else
        {
            player.setActiveHand(hand);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }
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
    public UseAction getUseAction(ItemStack stack)
    {
        return UnbreakingHandler.isItemBroken(stack) ? UseAction.NONE : UseAction.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return UnbreakingHandler.isItemBroken(stack) ? 0 : 72000;
    }
}
