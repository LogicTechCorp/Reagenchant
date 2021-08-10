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

import logictechcorp.reagenchant.core.util.item.UnbreakableItemStackUtil;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbEntityMixin {
    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.setDamageValue(I)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onOnCollideWithPlayer(PlayerEntity entity, CallbackInfo callback, Map.Entry<EquipmentSlotType, ItemStack> entry) {
        ItemStack stack = entry.getValue();

        if(UnbreakableItemStackUtil.isBroken(stack)) {
            UnbreakableItemStackUtil.fixItem(stack);
        }
    }
}
