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
import logictechcorp.reagenchant.common.tileentity.ReagentAltarTileEntity;
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
import net.minecraftforge.items.ItemStackHandler;
import vazkii.quark.api.IModifiableEnchantmentInfluencer;

import java.util.List;

public class ReagentAltarBlock extends Block implements IModifiableEnchantmentInfluencer {
    private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 11.0D, 13.0D);

    private final DyeColor influenceColor;

    public ReagentAltarBlock() {
        this(DyeColor.WHITE);
    }

    public ReagentAltarBlock(DyeColor influenceColor) {
        super(AbstractBlock.Properties.of(Material.DECORATION, influenceColor.getMaterialColor()).strength(0.2F).lightLevel((state) -> 14).sound(SoundType.WOOL));
        this.influenceColor = influenceColor;
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if(stack.hasCustomHoverName()) {
            TileEntity tileEntity = world.getBlockEntity(pos);

            if(tileEntity instanceof ReagentAltarTileEntity) {
                ((ReagentAltarTileEntity) tileEntity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tileEntity = world.getBlockEntity(pos);

        if(tileEntity instanceof ReagentAltarTileEntity) {
            ((ReagentAltarTileEntity) tileEntity).dropContents(world, pos);
        }

        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if(!world.isClientSide) {
            TileEntity tileEntity = world.getBlockEntity(pos);

            if(tileEntity instanceof ReagentAltarTileEntity) {
                ReagentAltarTileEntity reagentAltar = (ReagentAltarTileEntity) tileEntity;

                if(player instanceof ServerPlayerEntity) {
                    ItemStackHandler itemStackHandler = reagentAltar.getItemStackHandler();
                    ItemStack heldStack = player.getItemInHand(hand);

                    if(heldStack.isEmpty()) {
                        player.inventory.placeItemBackInInventory(world, itemStackHandler.extractItem(0, itemStackHandler.getStackInSlot(0).getCount(), false));
                    }
                    else if(Reagenchant.REAGENT_MANAGER.isReagent(heldStack.getItem())) {
                        ItemStack leftoverStack = itemStackHandler.insertItem(0, heldStack, false);
                        player.setItemInHand(hand, leftoverStack);
                    }
                }
            }
        }

        return ActionResultType.sidedSuccess(world.isClientSide);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new ReagentAltarTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);

        if(tileEntity instanceof ReagentAltarTileEntity) {
            ReagentAltarTileEntity reagentAltar = (ReagentAltarTileEntity) tileEntity;
            ItemStack reagentStack = reagentAltar.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                return super.getLightValue(state, world, pos);
            }
        }

        return 0;
    }

    @Override
    public DyeColor getEnchantmentInfluenceColor(IBlockReader world, BlockPos pos, BlockState state) {
        TileEntity tileEntity = world.getBlockEntity(pos);

        if(tileEntity instanceof ReagentAltarTileEntity) {
            ReagentAltarTileEntity reagentAltar = (ReagentAltarTileEntity) tileEntity;
            ItemStack reagentStack = reagentAltar.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                return this.influenceColor;
            }
        }

        return null;
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);

        if(tileEntity instanceof ReagentAltarTileEntity) {
            ReagentAltarTileEntity reagentAltar = (ReagentAltarTileEntity) tileEntity;
            ItemStack reagentStack = reagentAltar.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                return 1.0F;
            }
        }

        return 0.0F;
    }

    @Override
    public List<Enchantment> getModifiedEnchantments(IBlockReader world, BlockPos pos, BlockState state, ItemStack stack, List<Enchantment> influencedEnchants) {
        TileEntity tileEntity = world.getBlockEntity(pos);

        if(tileEntity instanceof ReagentAltarTileEntity) {
            ReagentAltarTileEntity reagentAltar = (ReagentAltarTileEntity) tileEntity;
            ItemStack reagentStack = reagentAltar.getItemStackHandler().getStackInSlot(0);

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
        TileEntity tileEntity = world.getBlockEntity(pos);

        if(tileEntity instanceof ReagentAltarTileEntity) {
            ReagentAltarTileEntity reagentAltar = (ReagentAltarTileEntity) tileEntity;
            ItemStack reagentStack = reagentAltar.getItemStackHandler().getStackInSlot(0);

            if(!reagentStack.isEmpty()) {
                return ItemStackColorUtil.getAverageColorComponents(reagentStack, reagentAltar.getRandom());
            }
        }

        return colorComponents;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
