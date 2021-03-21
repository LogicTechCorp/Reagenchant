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

package logictechcorp.reagenchant.core.other;

import com.minecraftabnormals.abnormals_core.core.util.DataUtil;
import logictechcorp.reagenchant.core.util.item.UnbreakableItemStackUtil;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ReagenchantOverrides {
    public static void register() {
        registerAlternativeDispenserBehaviors();
    }

    private static void registerAlternativeDispenserBehaviors() {
        DataUtil.registerAlternativeDispenseBehavior(Items.FLINT_AND_STEEL, (blockSource, stack) -> UnbreakableItemStackUtil.isBroken(stack), new OptionalDispenseBehavior() {
            @Override
            public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
                return stack;
            }
        });
    }
}
