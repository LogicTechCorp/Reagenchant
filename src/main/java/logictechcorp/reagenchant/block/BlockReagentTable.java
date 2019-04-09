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

import logictechcorp.libraryex.block.BlockTileEntity;
import logictechcorp.libraryex.block.builder.BlockBuilder;
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.handler.GuiHandler;
import logictechcorp.reagenchant.tileentity.TileEntityReagentTable;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockReagentTable extends BlockTileEntity<TileEntityReagentTable>
{
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);

    public BlockReagentTable()
    {
        super(Blocks.ENCHANTING_TABLE.getRegistryName(), TileEntityReagentTable.class, new BlockBuilder(Material.ROCK, MapColor.RED).hardness(5.0F).resistance(2000.0F).creativeTab(Reagenchant.instance.getCreativeTab()));
        this.setLightOpacity(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        super.randomDisplayTick(state, world, pos, rand);

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
                        BlockPos blockpos = pos.add(x, y, z);

                        if(net.minecraftforge.common.ForgeHooks.getEnchantPower(world, blockpos) > 0)
                        {
                            if(!world.isAirBlock(pos.add(x / 2, 0, z / 2)))
                            {
                                break;
                            }

                            world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, (double) pos.getX() + 0.5D, (double) pos.getY() + 2.0D, (double) pos.getZ() + 0.5D, (double) ((float) x + rand.nextFloat()) - 0.5D, (double) ((float) y - rand.nextFloat() - 1.0F), (double) ((float) z + rand.nextFloat()) - 0.5D);
                        }
                    }
                }
            }
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if(world.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof TileEntityReagentTable)
            {
                player.openGui(Reagenchant.instance, GuiHandler.REAGENT_TABLE_ID, world, pos.getX(), pos.getY(), pos.getZ());
            }

            return true;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        if(stack.hasDisplayName())
        {
            TileEntity tileentity = world.getTileEntity(pos);

            if(tileentity instanceof TileEntityReagentTable)
            {
                ((TileEntityReagentTable) tileentity).setCustomName(stack.getDisplayName());
            }
        }
    }
}
