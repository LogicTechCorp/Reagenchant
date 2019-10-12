package logictechcorp.reagenchant.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Map;

public class UnbreakableHoeItem extends UnbreakableTieredItem
{
    private static final Map<Block, BlockState> HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));
    private final float speed;

    public UnbreakableHoeItem(IItemTier itemTier, float attackSpeedIn, Properties properties)
    {
        super(itemTier, properties);
        this.speed = attackSpeedIn;
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        if(UnbreakingHandler.isItemBroken(context.getItem()))
        {
            return ActionResultType.PASS;
        }

        World world = context.getWorld();
        BlockPos pos = context.getPos();
        int hook = ForgeEventFactory.onHoeUse(context);

        if(hook != 0)
        {
            return hook > 0 ? ActionResultType.SUCCESS : ActionResultType.FAIL;
        }
        if(context.getFace() != Direction.DOWN && world.isAirBlock(pos.up()))
        {
            BlockState state = HOE_LOOKUP.get(world.getBlockState(pos).getBlock());

            if(state != null)
            {
                PlayerEntity player = context.getPlayer();
                world.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

                if(!world.isRemote)
                {
                    world.setBlockState(pos, state, 11);

                    if(player != null)
                    {
                        context.getItem().damageItem(1, player, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
                    }
                }

                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.PASS;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        stack.damageItem(1, attacker, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlotType, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlotType);

        if(equipmentSlotType == EquipmentSlotType.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", 0.0D, AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.getSpeed(stack), AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    public float getSpeed(ItemStack stack)
    {
        return UnbreakingHandler.isItemBroken(stack) ? 0 : this.speed;
    }
}
