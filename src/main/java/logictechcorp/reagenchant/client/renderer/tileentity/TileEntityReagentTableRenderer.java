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

import logictechcorp.reagenchant.init.ReagenchantTextures;
import logictechcorp.reagenchant.tileentity.TileEntityReagentTable;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityReagentTableRenderer extends TileEntitySpecialRenderer<TileEntityReagentTable>
{
    private final ModelBook modelBook = new ModelBook();

    @Override
    public void render(TileEntityReagentTable reagentTable, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
        float ticks = (float) reagentTable.getTickCounter() + partialTicks;
        GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(ticks * 0.1F) * 0.01F, 0.0F);
        float bookRotation;

        for(bookRotation = reagentTable.getBookRotation() - reagentTable.getBookRotationPrev(); bookRotation >= (float) Math.PI; bookRotation -= ((float) Math.PI * 2F))
        {
        }

        while(bookRotation < -(float) Math.PI)
        {
            bookRotation += ((float) Math.PI * 2F);
        }

        float previousBookRotation = reagentTable.getBookRotationPrev() + bookRotation * partialTicks;
        GlStateManager.rotate(-previousBookRotation * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
        this.bindTexture(ReagenchantTextures.REAGENT_TABLE_BOOK);
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
        this.modelBook.render(null, ticks, pageOneFlip, pageTwoFlip, bookSpread, 0.0F, 0.0625F);
        GlStateManager.popMatrix();
    }
}
