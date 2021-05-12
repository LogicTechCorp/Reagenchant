package logictechcorp.reagenchant.common.tileentity;

import logictechcorp.reagenchant.common.inventory.container.CustomAnvilContainer;
import logictechcorp.reagenchant.core.registry.ReagenchantTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.TranslationTextComponent;

public class CustomAnvilTileEntity extends InventoryTileEntity {
    public CustomAnvilTileEntity() {
        super(ReagenchantTileEntityTypes.CUSTOM_ANVIL_TILE_ENTITY.get(), new TranslationTextComponent("container.repair"), 4);
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CustomAnvilContainer(windowId, playerInventory, IWorldPosCallable.of(this.world, this.pos), this.itemStackHandler);
    }
}
