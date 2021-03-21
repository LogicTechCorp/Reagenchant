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

package logictechcorp.reagenchant.common.loot;

import com.google.gson.JsonObject;
import logictechcorp.reagenchant.core.util.item.UnbreakableItemStackUtil;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import java.util.List;

public class UnbreakableItemStackLootModifier extends LootModifier {
    public UnbreakableItemStackLootModifier(ILootCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        ItemStack toolStack = context.get(LootParameters.TOOL);
        BlockState brokenBlockState = context.get(LootParameters.BLOCK_STATE);

        if(toolStack != null && brokenBlockState != null && UnbreakableItemStackUtil.isBroken(toolStack) && brokenBlockState.getRequiresTool()) {
            generatedLoot.clear();
        }

        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<UnbreakableItemStackLootModifier> {
        @Override
        public UnbreakableItemStackLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
            return new UnbreakableItemStackLootModifier(conditions);
        }

        @Override
        public JsonObject write(UnbreakableItemStackLootModifier instance) {
            return new JsonObject();
        }
    }
}
