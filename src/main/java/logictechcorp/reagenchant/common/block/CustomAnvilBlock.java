/*
 * Reagenchant
 * Copyright (c) 2019-2021 by LogicTechCorp
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

import net.minecraft.block.AbstractBlock.Properties;

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
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if(stack.hasCustomHoverName()) {
            TileEntity tileEntity = world.getBlockEntity(pos);

            if(tileEntity instanceof CustomAnvilTileEntity) {
                ((CustomAnvilTileEntity) tileEntity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if(!(newState.getBlock() instanceof CustomAnvilBlock)) {
            TileEntity tileEntity = world.getBlockEntity(pos);

            if(tileEntity instanceof CustomAnvilTileEntity) {
                ((CustomAnvilTileEntity) tileEntity).dropContents(world, pos);
            }

            world.removeBlockEntity(pos);
        }
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if(!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);

            if(tileEntity instanceof CustomAnvilTileEntity) {
                CustomAnvilTileEntity anvil = (CustomAnvilTileEntity) tileEntity;

                if(player instanceof ServerPlayerEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, anvil, pos);
                }
            }

            player.awardStat(Stats.INTERACT_WITH_ANVIL);
        }

        return ActionResultType.sidedSuccess(world.isClientSide);
    }
}
