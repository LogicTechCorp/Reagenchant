/*
 * Reagenchant
 * Copyright (c) 2019-2020 by LogicTechCorp
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
import logictechcorp.reagenchant.tileentity.ReagentTableTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT)
public class ReagentTableTileEntityRenderer extends TileEntityRenderer<ReagentTableTileEntity>
{
    private static final Material BOOK_TEXTURE = new Material(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/enchanting_table_book"));

    private final BookModel bookModel = new BookModel();

    public ReagentTableTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher)
    {
        super(rendererDispatcher);
    }

    @Override
    public void render(ReagentTableTileEntity reagentTable, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int combinedLight, int combinedOverlay)
    {
        matrixStack.push();
        matrixStack.translate(0.5D, 0.75D, 0.5D);
        float ticks = (float) reagentTable.getTickCounter() + partialTicks;
        matrixStack.translate(0.0D, (0.1F + MathHelper.sin(ticks * 0.1F) * 0.01F), 0.0D);

        float bookRotation;
        for(bookRotation = reagentTable.getBookRotation() - reagentTable.getBookRotationPrev(); bookRotation >= (float) Math.PI; bookRotation -= ((float) Math.PI * 2F))
        {
        }

        while(bookRotation < -(float) Math.PI)
        {
            bookRotation += ((float) Math.PI * 2F);
        }

        float previousBookRotation = reagentTable.getBookRotationPrev() + bookRotation * partialTicks;
        matrixStack.rotate(Vector3f.YP.rotation(-previousBookRotation));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(80.0F));
        float pageFlip = MathHelper.lerp(partialTicks, reagentTable.getPageFlipPrev(), reagentTable.getPageFlip());
        float pageFlipOne = MathHelper.frac(pageFlip + 0.25F) * 1.6F - 0.3F;
        float pageFlipTwo = MathHelper.frac(pageFlip + 0.75F) * 1.6F - 0.3F;
        float bookSpread = MathHelper.lerp(partialTicks, reagentTable.getBookSpreadPrev(), reagentTable.getBookSpread());
        this.bookModel.func_228247_a_(ticks, MathHelper.clamp(pageFlipOne, 0.0F, 1.0F), MathHelper.clamp(pageFlipTwo, 0.0F, 1.0F), bookSpread);
        IVertexBuilder vertexBuilder = BOOK_TEXTURE.getBuffer(renderBuffer, RenderType::getEntitySolid);
        this.bookModel.func_228249_b_(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.pop();
    }
}
