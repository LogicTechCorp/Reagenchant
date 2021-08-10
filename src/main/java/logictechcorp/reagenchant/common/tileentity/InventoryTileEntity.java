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

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Random;

public abstract class InventoryTileEntity extends TileEntity implements INamedContainerProvider {
    protected final ItemStackHandler itemStackHandler;
    protected final LazyOptional<ItemStackHandler> itemStackHandlerCapability;
    protected final Random random;
    protected final ITextComponent name;
    protected ITextComponent customName;

    public InventoryTileEntity(TileEntityType<?> tileEntityType, ITextComponent name, int inventorySize) {
        super(tileEntityType);
        this.itemStackHandler = new ItemStackHandler(inventorySize) {
            @Override
            protected void onContentsChanged(int slot) {
                InventoryTileEntity.this.onContentsChanged();
            }
        };
        this.itemStackHandlerCapability = LazyOptional.of(() -> this.itemStackHandler);
        this.random = new Random();
        this.name = name;
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.put("Inventory", this.itemStackHandler.serializeNBT());

        if(this.customName != null) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }

        return compound;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.itemStackHandler.deserializeNBT(compound.getCompound("Inventory"));

        if(compound.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
    }

    @Override
    public void onDataPacket(NetworkManager networkManager, SUpdateTileEntityPacket packet) {
        this.load(this.getBlockState(), packet.getTag());
    }

    protected void onContentsChanged() {
        BlockState state = this.getBlockState();
        this.level.sendBlockUpdated(this.worldPosition, state, state, Constants.BlockFlags.DEFAULT);
        this.setChanged();
    }

    public void dropContents(World world, BlockPos pos) {
        for(int i = 0; i < this.itemStackHandler.getSlots(); i++) {
            InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), this.itemStackHandler.getStackInSlot(i));
        }
    }

    @Override
    public abstract Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player);

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 0, this.save(new CompoundNBT()));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capibility, Direction direction) {
        if(capibility == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.itemStackHandlerCapability.cast();
        }

        return LazyOptional.empty();
    }

    public ItemStackHandler getItemStackHandler() {
        return this.itemStackHandler;
    }

    public Random getRandom() {
        return this.random;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.customName != null ? this.customName : this.name;
    }

    public void setCustomName(ITextComponent customName) {
        this.customName = customName;
    }
}
