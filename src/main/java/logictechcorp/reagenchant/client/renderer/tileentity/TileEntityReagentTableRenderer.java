/*
 * Reagenchant
 * Copyright (c) 2019 by LogicTechCorp
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

import com.mojang.blaze3d.platform.GlStateManager;
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.tileentity.ReagentTableTileEntity;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value = Dist.CLIENT)
public class TileEntityReagentTableRenderer extends TileEntityRenderer<ReagentTableTileEntity>
{
    private static final ResourceLocation REAGENT_TABLE_BOOK = new ResourceLocation(Reagenchant.MOD_ID, "textures/entity/reagent_table_book.png");

    private final BookModel modelBook = new BookModel();

    @Override
    public void render(ReagentTableTileEntity reagentTable, double x, double y, double z, float partialTicks, int destroyStage)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        float ticks = (float) reagentTable.getTickCounter() + partialTicks;
        GlStateManager.translatef(0.0F, 0.1F + MathHelper.sin(ticks * 0.1F) * 0.01F, 0.0F);
        float bookRotation;

        for(bookRotation = reagentTable.getBookRotation() - reagentTable.getBookRotationPrev(); bookRotation >= (float) Math.PI; bookRotation -= ((float) Math.PI * 2F))
        {
        }

        while(bookRotation < -(float) Math.PI)
        {
            bookRotation += ((float) Math.PI * 2F);
        }

        float previousBookRotation = reagentTable.getBookRotationPrev() + bookRotation * partialTicks;
        GlStateManager.rotatef(-previousBookRotation * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTexture(REAGENT_TABLE_BOOK);
        float pageOneFlip = reagentTable.getPageFlipPrev() + (reagentTable.getPageFlip() - reagentTable.getPageFlipPrev()) * partialTicks + 0.25F;
        float pageTwoFlip = reagentTable.getPageFlipPrev() + (reagentTable.getPageFlip() - reagentTable.getPageFlipPrev()) * partialTicks + 0.75F;
        pageOneFlip = (pageOneFlip - (float) MathHelper.fastFloor((double) pageOneFlip)) * 1.6F - 0.3F;
        pageTwoFlip = (pageTwoFlip - (float) MathHelper.fastFloor((double) pageTwoFlip)) * 1.6F - 0.3F;

        if(pageOneFlip < 0.0F)
        {
            pageOneFlip = 0.0F;
        }

        if(pageTwoFlip < 0.0F)
        {
            pageTwoFlip = 0.0F;
        }

        if(pageOneFlip > 1.0F)
        {
            pageOneFlip = 1.0F;
        }

        if(pageTwoFlip > 1.0F)
        {
            pageTwoFlip = 1.0F;
        }

        float bookSpread = reagentTable.getBookSpreadPrev() + (reagentTable.getBookSpread() - reagentTable.getBookSpreadPrev()) * partialTicks;
        GlStateManager.enableCull();
        this.modelBook.render(ticks, pageOneFlip, pageTwoFlip, bookSpread, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }
}
