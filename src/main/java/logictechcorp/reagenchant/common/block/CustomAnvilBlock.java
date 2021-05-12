package logictechcorp.reagenchant.common.block;

import logictechcorp.reagenchant.common.tileentity.CustomAnvilTileEntity;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class CustomAnvilBlock extends AnvilBlock {
    public CustomAnvilBlock(Properties properties) {
        super(properties);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CustomAnvilTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if(stack.hasDisplayName()) {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof CustomAnvilTileEntity) {
                ((CustomAnvilTileEntity) tileEntity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if(!(newState.getBlock() instanceof CustomAnvilBlock)) {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof CustomAnvilTileEntity) {
                ((CustomAnvilTileEntity) tileEntity).dropContents(world, pos);
            }

            world.removeTileEntity(pos);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof CustomAnvilTileEntity) {
                CustomAnvilTileEntity anvil = (CustomAnvilTileEntity) tileEntity;

                if(player instanceof ServerPlayerEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, anvil, pos);
                }
            }

            player.addStat(Stats.INTERACT_WITH_ANVIL);
        }

        return ActionResultType.func_233537_a_(world.isRemote);

    }
}
