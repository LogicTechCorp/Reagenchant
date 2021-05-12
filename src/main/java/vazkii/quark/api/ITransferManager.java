/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [28/03/2016, 17:05:38 (GMT)]
 */
package vazkii.quark.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Supplier;

/**
 * Implement on a TileEntity or provide as a capability
 * to allow it to receive inventory transfering, and to have chest buttons
 * on the client.
 *
 * You should not check for TileEntities implementing this.
 * Instead, check if they provide this as a capability.
 */
public interface ITransferManager {

	boolean acceptsTransfer(PlayerEntity player);
	
	/**
	 * Override this if you want to add a custom IItemHandler for transfering.
	 */
	default IItemHandler getTransferItemHandler(Supplier<IItemHandler> defaultSupplier) {
		return defaultSupplier.get();
	}
	
}
