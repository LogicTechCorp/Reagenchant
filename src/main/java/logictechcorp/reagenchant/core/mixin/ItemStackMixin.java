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

package logictechcorp.reagenchant.core.mixin;

import logictechcorp.reagenchant.core.ReagenchantConfig;
import logictechcorp.reagenchant.core.util.item.UnbreakableItemStackUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "attemptDamageItem", at = @At("TAIL"), cancellable = true)
    public void onAttemptDamageItem(int amount, Random rand, ServerPlayerEntity damager, CallbackInfoReturnable<Boolean> callback) {
        if(ReagenchantConfig.COMMON.unbreakableItems.get()) {
            ItemStack stack = ((ItemStack) (Object) this);

            if(stack.getDamage() >= stack.getMaxDamage()) {
                UnbreakableItemStackUtil.breakItem(damager, stack);
                stack.setDamage(stack.getMaxDamage());
                callback.setReturnValue(false);
            }
        }
    }
}
