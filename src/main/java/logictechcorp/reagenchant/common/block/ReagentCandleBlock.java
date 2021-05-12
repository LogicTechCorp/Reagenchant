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

import logictechcorp.reagenchant.client.util.item.ItemStackColorUtil;
import logictechcorp.reagenchant.common.reagent.Reagent;
import logictechcorp.reagenchant.common.tileentity.ReagentCandleTileEntity;
import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.api.IModifiableEnchantmentInfluencer;

import java.util.List;
import java.util.Random;

public class ReagentCandleBlock extends Block implements IModifiableEnchantmentInfluencer {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(6F, 0F, 6F, 10F, 8F, 10F);

    private final DyeColor influenceColor;

    public ReagentCandleBlock() {
        this(DyeColor.WHITE);
    }

    public ReagentCandleBlock(DyeColor influenceColor) {
        super(AbstractBlock.Properties.create(Material.MISCELLANEOUS, influenceColor.getMapColor()).hardnessAndResistance(0.2F).setLightLevel((state) -> 14).sound(SoundType.CLOTH));
        this.influenceColor = influenceColor;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        double posX = (double) pos.getX() + 0.5D;
        double posY = (double) pos.getY() + 0.7D;
        double posZ = (double) pos.getZ() + 0.5D;

        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentCandleTileEntity) {
            ReagentCandleTileEntity reagentCandle = (ReagentCandleTileEntity) tileEntity;
            ItemStack reagentStack = reagentCandle.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                float[] colorComponents = ItemStackColorUtil.getAverageColorComponents(reagentStack, random);
                world.addParticle(new RedstoneParticleData(colorComponents[0], colorComponents[1], colorComponents[2], 1.0F), posX, posY, posZ, 0.0D, 0.0D, 0.0D);
                world.addParticle(ParticleTypes.FLAME, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if(stack.hasDisplayName()) {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof ReagentCandleTileEntity) {
                ((ReagentCandleTileEntity) tileEntity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentCandleTileEntity) {
            ((ReagentCandleTileEntity) tileEntity).dropContents(world, pos);
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if(!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof ReagentCandleTileEntity) {
                ReagentCandleTileEntity reagentCandle = (ReagentCandleTileEntity) tileEntity;

                if(player instanceof ServerPlayerEntity) {
                    NetworkHooks.openGui((ServerPlayerEntity) player, reagentCandle, pos);
                }
            }
        }

        return ActionResultType.func_233537_a_(world.isRemote);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReagentCandleTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentCandleTileEntity) {
            ReagentCandleTileEntity reagentCandle = (ReagentCandleTileEntity) tileEntity;
            ItemStack reagentStack = reagentCandle.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                return super.getLightValue(state, world, pos);
            }
        }

        return 0;
    }

    @Override
    public DyeColor getEnchantmentInfluenceColor(IBlockReader world, BlockPos pos, BlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentCandleTileEntity) {
            ReagentCandleTileEntity reagentCandle = (ReagentCandleTileEntity) tileEntity;
            ItemStack reagentStack = reagentCandle.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                return this.influenceColor;
            }
        }

        return null;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentCandleTileEntity) {
            ReagentCandleTileEntity reagentCandle = (ReagentCandleTileEntity) tileEntity;
            ItemStack reagentStack = reagentCandle.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                return 1.0F;
            }
        }

        return 0.0F;
    }

    @Override
    public List<Enchantment> getModifiedEnchantments(IBlockReader world, BlockPos pos, BlockState state, ItemStack stack, List<Enchantment> influencedEnchants) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentCandleTileEntity) {
            ReagentCandleTileEntity reagentCandle = (ReagentCandleTileEntity) tileEntity;
            ItemStack reagentStack = reagentCandle.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());
                return reagent.getApplicableEnchantments(stack);
            }
        }

        influencedEnchants.clear();
        return influencedEnchants;
    }

    @Override
    public float[] getModifiedColorComponents(IBlockReader world, BlockPos pos, BlockState state, float[] colorComponents) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if(tileEntity instanceof ReagentCandleTileEntity) {
            ReagentCandleTileEntity reagentCandle = (ReagentCandleTileEntity) tileEntity;
            ItemStack reagentStack = reagentCandle.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                return ItemStackColorUtil.getAverageColorComponents(reagentStack, reagentCandle.getRandom());
            }
        }

        return colorComponents;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
