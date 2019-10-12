package logictechcorp.reagenchant.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
import net.minecraftforge.common.ToolType;

import java.util.Map;
import java.util.Set;

public class UnbreakableShovelItem extends UnbreakableToolItem
{
    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
    protected static final Map<Block, BlockState> BLOCK_FLATTENING_MAP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.getDefaultState()));

    public UnbreakableShovelItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties)
    {
        super(attackDamage, attackSpeed, tier, EFFECTIVE_ON, properties.addToolType(ToolType.SHOVEL, tier.getHarvestLevel()));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getPos();

        if(!UnbreakingHandler.isItemBroken(context.getItem()) && context.getFace() != Direction.DOWN && world.getBlockState(pos.up()).isAir(world, pos.up()))
        {
            BlockState state = BLOCK_FLATTENING_MAP.get(world.getBlockState(pos).getBlock());

            if(state != null)
            {
                PlayerEntity player = context.getPlayer();
                world.playSound(player, pos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);

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
    public boolean canHarvestBlock(ItemStack stack, BlockState state)
    {
        if(UnbreakingHandler.isItemBroken(stack))
        {
            return false;
        }

        Block block = state.getBlock();
        return block == Blocks.SNOW || block == Blocks.SNOW_BLOCK;
    }
}
