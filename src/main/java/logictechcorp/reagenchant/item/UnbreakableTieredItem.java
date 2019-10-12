package logictechcorp.reagenchant.item;

import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraftforge.common.ToolType;

import java.util.function.Consumer;

public class UnbreakableTieredItem extends TieredItem
{
    public UnbreakableTieredItem(IItemTier itemTier, Properties properties)
    {
        super(itemTier, properties);
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
    public int getHarvestLevel(ItemStack stack, ToolType toolType, PlayerEntity player, BlockState state)
    {
        return UnbreakingHandler.isItemBroken(stack) ? -1 : super.getHarvestLevel(stack, toolType, player, state);
    }
}
