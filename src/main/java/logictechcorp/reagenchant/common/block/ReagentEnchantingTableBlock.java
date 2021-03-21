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

import logictechcorp.reagenchant.common.inventory.container.ReagentEnchantingTableContainer;
import logictechcorp.reagenchant.common.tileentity.ReagentEnchantingTableTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ReagentEnchantingTableBlock extends EnchantingTableBlock {
    public ReagentEnchantingTableBlock(AbstractBlock.Properties properties) {
        super(properties);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentEnchantingTableTileEntity) {
            ((ReagentEnchantingTableTileEntity) tileEntity).dropContents(world, pos);
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if(stack.hasDisplayName()) {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof ReagentEnchantingTableTileEntity) {
                ((ReagentEnchantingTableTileEntity) tileEntity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReagentEnchantingTableTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if(!world.isRemote) {
            if(world.getTileEntity(pos) instanceof EnchantingTableTileEntity) {
                world.setTileEntity(pos, this.createTileEntity(state, world));
            }

            player.openContainer(state.getContainer(world, pos));
            Container openContainer = player.openContainer;

            if(openContainer instanceof ReagentEnchantingTableContainer) {
                ((ReagentEnchantingTableContainer) openContainer).onContentsChanged();
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentEnchantingTableTileEntity) {
            ReagentEnchantingTableTileEntity reagentTable = (ReagentEnchantingTableTileEntity) tileEntity;
            return new SimpleNamedContainerProvider((id, playerInventory, player) -> new ReagentEnchantingTableContainer(id, playerInventory, reagentTable.getItemStackHandler(), IWorldPosCallable.of(world, pos)), reagentTable.getDisplayName());
        }
        else {
            return null;
        }
    }
}
