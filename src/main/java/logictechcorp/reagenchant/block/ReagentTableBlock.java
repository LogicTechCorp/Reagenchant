/*
 * Reagenchant
 * Copyright (c) 2019 by LogicTechCorp
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

package logictechcorp.reagenchant.block;

import logictechcorp.libraryex.block.TileEntityBlock;
import logictechcorp.reagenchant.inventory.ReagentTableContainer;
import logictechcorp.reagenchant.inventory.ReagentTableManager;
import logictechcorp.reagenchant.tileentity.ReagentTableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class ReagentTableBlock extends TileEntityBlock<ReagentTableTileEntity>
{
    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public ReagentTableBlock()
    {
        super(Block.Properties.create(Material.ROCK, MaterialColor.RED).hardnessAndResistance(5.0F, 2000.0F), ReagentTableTileEntity.class);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand)
    {
        super.animateTick(state, world, pos, rand);

        for(int x = -2; x <= 2; x++)
        {
            for(int z = -2; z <= 2; z++)
            {
                if(x > -2 && x < 2 && z == -1)
                {
                    z = 2;
                }

                if(rand.nextInt(16) == 0)
                {
                    for(int y = 0; y <= 1; y++)
                    {
                        BlockPos adjustedPos = pos.add(x, y, z);
                        if(world.getBlockState(adjustedPos).getEnchantPowerBonus(world, pos) > 0)
                        {
                            if(!world.isAirBlock(pos.add(x / 2, 0, z / 2)))
                            {
                                break;
                            }

                            world.addParticle(ParticleTypes.ENCHANT, (double) pos.getX() + 0.5D, (double) pos.getY() + 2.0D, (double) pos.getZ() + 0.5D, (double) ((float) x + rand.nextFloat()) - 0.5D, (double) ((float) y - rand.nextFloat() - 1.0F), (double) ((float) z + rand.nextFloat()) - 0.5D);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
        if(stack.hasDisplayName())
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof ReagentTableTileEntity)
            {
                ((ReagentTableTileEntity) tileEntity).setCustomName(stack.getDisplayName());
            }
        }

    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
    {
        if(world.isRemote)
        {
            return true;
        }
        else
        {
            player.openContainer(state.getContainer(world, pos));
            return true;
        }
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type)
    {
        return false;
    }

    @Override
    public boolean func_220074_n(BlockState p_220074_1_)
    {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentTableTileEntity)
        {
            ReagentTableTileEntity reagentTable = (ReagentTableTileEntity) tileEntity;
            return new SimpleNamedContainerProvider((id, inventory, player) -> new ReagentTableContainer(new ReagentTableManager(world, pos, player, reagentTable), id), reagentTable.getDisplayName());
        }
        else
        {
            return null;
        }
    }
}
