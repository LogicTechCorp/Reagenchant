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

package logictechcorp.reagenchant.client.gui;

import logictechcorp.reagenchant.init.ReagenchantTextures;
import logictechcorp.reagenchant.inventory.ContainerReagentTable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.glu.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class GuiReagentTable extends GuiContainer
{
    private static final ModelBook MODEL_BOOK = new ModelBook();
    private final ContainerReagentTable container;
    private final Random random;
    private float flip;
    private float flipPrev;
    private float flipRandom;
    private float flipTurn;
    private float open;
    private float openPrev;
    private ItemStack last = ItemStack.EMPTY;

    public GuiReagentTable(ContainerReagentTable container)
    {
        super(container);
        this.container = (ContainerReagentTable) this.inventorySlots;
        this.random = new Random();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        ITextComponent textComponent = this.container.getReagentTableManager().getReagentTable().getDisplayName();

        if(textComponent != null)
        {
            this.fontRenderer.drawString(textComponent.getFormattedText(), 12, 5, 4210752);
        }
        else
        {
            this.fontRenderer.drawString(I18n.format("container.enchant"), 12, 5, 4210752);
        }

        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        this.tickBook();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int width = (this.width - this.xSize) / 2;
        int height = (this.height - this.ySize) / 2;

        for(int i = 0; i < 3; i++)
        {
            int cursorX = mouseX - (width + 62);
            int cursorY = mouseY - (height + 14 + 19 * i);

            if(cursorX >= 0 && cursorY >= 0 && cursorX < 108 && cursorY < 19 && this.container.enchantItem(this.mc.player, i))
            {
                this.mc.playerController.sendEnchantPacket(this.container.windowId, i);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ReagenchantTextures.REAGENT_ENCHANTMENT_TABLE_GUI);
        int width = (this.width - this.xSize) / 2;
        int height = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(width, height, 0, 0, this.xSize, this.ySize);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        GlStateManager.viewport((scaledResolution.getScaledWidth() - 320) / 2 * scaledResolution.getScaleFactor(), (scaledResolution.getScaledHeight() - 240) / 2 * scaledResolution.getScaleFactor(), 320 * scaledResolution.getScaleFactor(), 240 * scaledResolution.getScaleFactor());
        GlStateManager.translate(-0.34F, 0.23F, 0.0F);
        Project.gluPerspective(90.0F, 1.3333334F, 9.0F, 80.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.translate(0.0F, 3.3F, -16.0F);
        GlStateManager.scale(1.0F, 1.0F, 1.0F);
        GlStateManager.scale(5.0F, 5.0F, 5.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(ReagenchantTextures.REAGENT_ENCHANTMENT_TABLE_BOOK);
        GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
        float f2 = this.openPrev + (this.open - this.openPrev) * partialTicks;
        GlStateManager.translate((1.0F - f2) * 0.2F, (1.0F - f2) * 0.1F, (1.0F - f2) * 0.25F);
        GlStateManager.rotate(-(1.0F - f2) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        float f3 = this.flipPrev + (this.flip - this.flipPrev) * partialTicks + 0.25F;
        float f4 = this.flipPrev + (this.flip - this.flipPrev) * partialTicks + 0.75F;
        f3 = (f3 - (float) MathHelper.fastFloor((double) f3)) * 1.6F - 0.3F;
        f4 = (f4 - (float) MathHelper.fastFloor((double) f4)) * 1.6F - 0.3F;

        if(f3 < 0.0F)
        {
            f3 = 0.0F;
        }

        if(f4 < 0.0F)
        {
            f4 = 0.0F;
        }

        if(f3 > 1.0F)
        {
            f3 = 1.0F;
        }

        if(f4 > 1.0F)
        {
            f4 = 1.0F;
        }

        GlStateManager.enableRescaleNormal();
        MODEL_BOOK.render(null, 0.0F, f3, f4, f2, 0.0F, 0.0625F);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.matrixMode(5889);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        EnchantmentNameParts.getInstance().reseedRandomGenerator((long) this.container.getReagentTableManager().getXpSeed());
        int lapisAmount = this.container.getLapisAmount();

        for(int i = 0; i < 3; i++)
        {
            int rectanglePosX = width + 62;
            int textPosX = rectanglePosX + 20;
            this.zLevel = 0.0F;
            this.mc.getTextureManager().bindTexture(ReagenchantTextures.REAGENT_ENCHANTMENT_TABLE_GUI);
            int enchantmentLevel = this.container.getReagentTableManager().getEnchantmentLevels()[i];
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if(enchantmentLevel == 0)
            {
                this.drawTexturedModalRect(rectanglePosX, height + 14 + 19 * i, 0, 185, 108, 19);
            }
            else
            {
                String enchantLevelString = "" + enchantmentLevel;
                int textWrapWidth = 86 - this.fontRenderer.getStringWidth(enchantLevelString);
                String randomEnchantmentName = EnchantmentNameParts.getInstance().generateNewRandomName(this.fontRenderer, textWrapWidth);
                FontRenderer fontrenderer = this.mc.standardGalacticFontRenderer;
                int color = 6839882;

                if(((lapisAmount < i + 1 || this.mc.player.experienceLevel < enchantmentLevel) && !this.mc.player.capabilities.isCreativeMode) || this.container.getReagentTableManager().getEnchantments()[i] == -1)
                {
                    this.drawTexturedModalRect(rectanglePosX, height + 14 + 19 * i, 0, 185, 108, 19);
                    this.drawTexturedModalRect(rectanglePosX + 1, height + 15 + 19 * i, 16 * i, 239, 16, 16);
                    fontrenderer.drawSplitString(randomEnchantmentName, textPosX, height + 16 + 19 * i, textWrapWidth, (color & 16711422) >> 1);
                    color = 4226832;
                }
                else
                {
                    int cursorPosX = mouseX - (width + 62);
                    int cursorPosY = mouseY - (height + 14 + 19 * i);

                    if(cursorPosX >= 0 && cursorPosY >= 0 && cursorPosX < 108 && cursorPosY < 19)
                    {
                        this.drawTexturedModalRect(rectanglePosX, height + 14 + 19 * i, 0, 204, 108, 19);
                        color = 16777088;
                    }
                    else
                    {
                        this.drawTexturedModalRect(rectanglePosX, height + 14 + 19 * i, 0, 166, 108, 19);
                    }

                    this.drawTexturedModalRect(rectanglePosX + 1, height + 15 + 19 * i, 16 * i, 223, 16, 16);
                    fontrenderer.drawSplitString(randomEnchantmentName, textPosX, height + 16 + 19 * i, textWrapWidth, color);
                    color = 8453920;
                }

                fontrenderer = this.mc.fontRenderer;
                fontrenderer.drawStringWithShadow(enchantLevelString, (float) (textPosX + 86 - fontrenderer.getStringWidth(enchantLevelString)), (float) (height + 16 + 19 * i + 7), color);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        partialTicks = this.mc.getTickLength();
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        for(int i = 0; i < 3; i++)
        {
            int enchantmentLevel = this.container.getReagentTableManager().getExperienceLevels()[i];
            Enchantment enchantment = Enchantment.getEnchantmentByID(this.container.getReagentTableManager().getEnchantments()[i]);
            int worldClue = this.container.getReagentTableManager().getEnchantmentLevels()[i];
            int enchantmentTier = i + 1;

            if(this.isPointInRegion(62, 14 + 19 * i, 108, 17, mouseX, mouseY) && enchantmentLevel > 0)
            {
                List<String> list = new ArrayList<>();
                list.add("" + TextFormatting.WHITE + TextFormatting.ITALIC + I18n.format("container.enchant.clue", enchantment == null ? "" : enchantment.getTranslatedName(worldClue)));

                if(enchantment == null)
                {
                    Collections.addAll(list, "", TextFormatting.RED + I18n.format("forge.container.enchant.limitedEnchantability"));
                }
                else if(!this.mc.player.capabilities.isCreativeMode)
                {
                    list.add("");

                    if(this.mc.player.experienceLevel < enchantmentLevel)
                    {
                        list.add(TextFormatting.RED + I18n.format("container.enchant.level.requirement", this.container.getReagentTableManager().getExperienceLevels()[i]));
                    }
                    else
                    {
                        String text;

                        if(enchantmentTier == 1)
                        {
                            text = I18n.format("container.enchant.lapis.one");
                        }
                        else
                        {
                            text = I18n.format("container.enchant.lapis.many", enchantmentTier);
                        }

                        TextFormatting textformatting = this.container.getLapisAmount() >= enchantmentTier ? TextFormatting.GRAY : TextFormatting.RED;
                        list.add(textformatting + "" + text);

                        if(enchantmentTier == 1)
                        {
                            text = I18n.format("container.enchant.level.one");
                        }
                        else
                        {
                            text = I18n.format("container.enchant.level.many", enchantmentTier);
                        }

                        list.add(TextFormatting.GRAY + "" + text);
                    }
                }

                this.drawHoveringText(list, mouseX, mouseY);
                break;
            }
        }
    }

    private void tickBook()
    {
        ItemStack stack = this.inventorySlots.getSlot(0).getStack();

        if(!ItemStack.areItemStacksEqual(stack, this.last))
        {
            this.last = stack;

            while(true)
            {
                this.flipRandom += (float) (this.random.nextInt(4) - this.random.nextInt(4));

                if(this.flip > this.flipRandom + 1.0F || this.flip < this.flipRandom - 1.0F)
                {
                    break;
                }
            }
        }

        this.flipPrev = this.flip;
        this.openPrev = this.open;
        boolean flag = false;

        for(int i = 0; i < 3; i++)
        {
            if(this.container.getReagentTableManager().getEnchantmentLevels()[i] != 0)
            {
                flag = true;
            }
        }

        if(flag)
        {
            this.open += 0.2F;
        }
        else
        {
            this.open -= 0.2F;
        }

        this.open = MathHelper.clamp(this.open, 0.0F, 1.0F);
        float flip = (this.flipRandom - this.flip) * 0.4F;
        flip = MathHelper.clamp(flip, -0.2F, 0.2F);
        this.flipTurn += (flip - this.flipTurn) * 0.9F;
        this.flip += this.flipTurn;
    }
}
