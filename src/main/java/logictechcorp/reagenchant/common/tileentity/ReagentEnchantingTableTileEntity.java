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

package logictechcorp.reagenchant.common.tileentity;

import logictechcorp.reagenchant.core.registry.ReagenchantTileEntityTypes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

public class ReagentEnchantingTableTileEntity extends TileEntity implements ITickableTileEntity, INameable {
    public int ticks;
    public float nextPageFlipAmount;
    public float pageFlipAmount;
    public float randomPageFlipAmount;
    public float adjustedFlipAmount;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float nextPageAngle;
    public float pageAngle;
    public float angle;

    protected LazyOptional<ItemStackHandler> itemStackHandler;
    protected Random random;

    private ITextComponent customName;

    public ReagentEnchantingTableTileEntity() {
        super(ReagenchantTileEntityTypes.REAGENT_ENCHANTING_TABLE_TILE_ENTITY.get());
        this.itemStackHandler = LazyOptional.of(() -> new ItemStackHandler(3));
        this.random = new Random();
    }

    @Override
    public void tick() {
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        this.pageAngle = this.nextPageAngle;
        PlayerEntity player = this.world.getClosestPlayer((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D, 3.0D, false);

        if(player != null) {
            double posX = player.getPosX() - ((double) this.pos.getX() + 0.5D);
            double posZ = player.getPosZ() - ((double) this.pos.getZ() + 0.5D);
            this.angle = (float) MathHelper.atan2(posZ, posX);
            this.nextPageTurningSpeed += 0.1F;

            if(this.nextPageTurningSpeed < 0.5F || this.random.nextInt(40) == 0) {
                float temp = this.randomPageFlipAmount;

                do {
                    this.randomPageFlipAmount += (float) (this.random.nextInt(4) - this.random.nextInt(4));

                }
                while(temp == this.randomPageFlipAmount);
            }
        }
        else {
            this.angle += 0.02F;
            this.nextPageTurningSpeed -= 0.1F;
        }

        while(this.nextPageAngle >= (float) Math.PI) {
            this.nextPageAngle -= ((float) Math.PI * 2F);
        }

        while(this.nextPageAngle < -(float) Math.PI) {
            this.nextPageAngle += ((float) Math.PI * 2F);
        }

        while(this.angle >= (float) Math.PI) {
            this.angle -= ((float) Math.PI * 2F);
        }

        while(this.angle < -(float) Math.PI) {
            this.angle += ((float) Math.PI * 2F);
        }

        float angle = this.angle - this.nextPageAngle;

        while(angle >= (float) Math.PI) {
            angle -= ((float) Math.PI * 2F);
        }

        while(angle < -(float) Math.PI) {
            angle += ((float) Math.PI * 2F);
        }

        this.nextPageAngle += angle * 0.4F;
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        this.ticks++;
        this.pageFlipAmount = this.nextPageFlipAmount;
        float flipAmount = (this.randomPageFlipAmount - this.nextPageFlipAmount) * 0.4F;
        flipAmount = MathHelper.clamp(flipAmount, -0.2F, 0.2F);
        this.adjustedFlipAmount += (flipAmount - this.adjustedFlipAmount) * 0.9F;
        this.nextPageFlipAmount += this.adjustedFlipAmount;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);

        if(this.hasCustomName()) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }

        this.itemStackHandler.ifPresent(handler -> compound.put("Items", handler.serializeNBT()));
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);

        if(compound.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.getComponentFromJson(compound.getString("CustomName"));
        }

        this.itemStackHandler.ifPresent(handler -> handler.deserializeNBT(compound.getCompound("Items")));
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SUpdateTileEntityPacket packet) {
        this.read(this.world.getBlockState(packet.getPos()), packet.getNbtCompound());
    }

    public void dropContents(World world, BlockPos pos) {
        this.itemStackHandler.ifPresent(handler -> {
            for(int i = 0; i < handler.getSlots(); i++) {
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
            }
        });
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 0, this.write(new CompoundNBT()));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capibility, Direction direction) {
        if(capibility == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (LazyOptional<T>) this.itemStackHandler;
        }

        return LazyOptional.empty();
    }

    @Override
    public ITextComponent getName() {
        return (this.customName != null ? this.customName : new TranslationTextComponent("container.enchant"));
    }

    @Override
    public ITextComponent getCustomName() {
        return this.customName;
    }

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler.orElse(new ItemStackHandler(3));
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }
}
