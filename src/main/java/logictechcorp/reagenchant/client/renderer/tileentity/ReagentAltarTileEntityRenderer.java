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

package logictechcorp.reagenchant.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import logictechcorp.reagenchant.common.tileentity.ReagentAltarTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class ReagentAltarTileEntityRenderer extends TileEntityRenderer<ReagentAltarTileEntity> {
    public ReagentAltarTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(ReagentAltarTileEntity reagentAltar, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = reagentAltar.getItemStackHandler().getStackInSlot(0);

        if(!stack.isEmpty()) {
            World world = reagentAltar.getWorld();
            float movementValue = world.getGameTime() + partialTicks;

            matrixStack.push();
            matrixStack.translate(0.5F, MathHelper.sin(movementValue / 10.0F) * 0.1F + 1.025F, 0.5F);
            matrixStack.rotate(Vector3f.YP.rotation(movementValue / 20.0F));
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            RenderHelper.enableStandardItemLighting();
            Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED, combinedLight, combinedOverlay, matrixStack, buffer);
            RenderHelper.disableStandardItemLighting();
            matrixStack.pop();
        }
    }
}
