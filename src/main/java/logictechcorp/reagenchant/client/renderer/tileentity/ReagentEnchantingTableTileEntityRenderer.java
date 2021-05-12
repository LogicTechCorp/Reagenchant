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
import com.mojang.blaze3d.vertex.IVertexBuilder;
import logictechcorp.reagenchant.common.tileentity.ReagentEnchantingTableTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class ReagentEnchantingTableTileEntityRenderer extends TileEntityRenderer<ReagentEnchantingTableTileEntity> {
    public static final RenderMaterial BOOK_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/enchanting_table_book"));

    private final BookModel bookModel = new BookModel();

    public ReagentEnchantingTableTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(ReagentEnchantingTableTileEntity reagentEnchantingTable, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int combinedLight, int combinedOverlay) {
        matrixStack.push();
        matrixStack.translate(0.5D, 0.75D, 0.5D);
        float ticks = (float) reagentEnchantingTable.ticks + partialTicks;
        matrixStack.translate(0.0D, (0.1F + MathHelper.sin(ticks * 0.1F) * 0.01F), 0.0D);

        float angle;
        for(angle = reagentEnchantingTable.nextPageAngle - reagentEnchantingTable.pageAngle; angle >= (float) Math.PI; angle -= ((float) Math.PI * 2F)) {
        }

        while(angle < -(float) Math.PI) {
            angle += ((float) Math.PI * 2F);
        }

        float adjustedPageAngle = reagentEnchantingTable.pageAngle + angle * partialTicks;
        matrixStack.rotate(Vector3f.YP.rotation(-adjustedPageAngle));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(80.0F));
        float f3 = MathHelper.lerp(partialTicks, reagentEnchantingTable.pageFlipAmount, reagentEnchantingTable.nextPageFlipAmount);
        float rightPageFlipAmount = MathHelper.frac(f3 + 0.25F) * 1.6F - 0.3F;
        float leftPageFlipAmount = MathHelper.frac(f3 + 0.75F) * 1.6F - 0.3F;
        float bookOpenAmount = MathHelper.lerp(partialTicks, reagentEnchantingTable.pageTurningSpeed, reagentEnchantingTable.nextPageTurningSpeed);
        this.bookModel.setBookState(ticks, MathHelper.clamp(rightPageFlipAmount, 0.0F, 1.0F), MathHelper.clamp(leftPageFlipAmount, 0.0F, 1.0F), bookOpenAmount);
        IVertexBuilder ivertexbuilder = BOOK_TEXTURE.getBuffer(renderTypeBuffer, RenderType::getEntitySolid);
        this.bookModel.renderAll(matrixStack, ivertexbuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
    }
}
