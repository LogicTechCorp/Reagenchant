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

import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.init.ReagenchantTextures;
import logictechcorp.reagenchant.inventory.ContainerReagentTable;
import logictechcorp.reagenchant.inventory.ReagentTableManager;
import logictechcorp.reagenchant.reagent.Reagent;
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
import net.minecraft.util.ResourceLocation;
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
    private final ReagentTableManager reagentTableManager;
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
        this.reagentTableManager = container.getReagentTableManager();
        this.random = new Random();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        this.tickBook();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        ITextComponent textComponent = this.reagentTableManager.getReagentTable().getDisplayName();

        if(textComponent != null)
        {
            this.fontRenderer.drawString(textComponent.getFormattedText(), 12, 5, 4210752);
        }
        else
        {
            this.fontRenderer.drawString(I18n.format("gui.reagenchant:reagent_table.title"), 12, 5, 4210752);
        }

        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int width = (this.width - this.xSize) / 2;
        int height = (this.height - this.ySize) / 2;

        for(int enchantmentIndex = 0; enchantmentIndex < 3; enchantmentIndex++)
        {
            int cursorX = mouseX - (width + 62);
            int cursorY = mouseY - (height + 14 + 19 * enchantmentIndex);

            if(cursorX >= 0 && cursorY >= 0 && cursorX < 108 && cursorY < 19 && this.container.enchantItem(this.mc.player, enchantmentIndex))
            {
                this.mc.playerController.sendEnchantPacket(this.container.windowId, enchantmentIndex);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        ResourceLocation guiTexture;

        if(!this.reagentTableManager.getInventory().getStackInSlot(2).isEmpty())
        {
            guiTexture = ReagenchantTextures.REAGENT_TABLE_WITH_REAGENT_GUI;
        }
        else
        {
            guiTexture = ReagenchantTextures.REAGENT_TABLE_WITHOUT_REAGENT_GUI;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(guiTexture);
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
        this.mc.getTextureManager().bindTexture(ReagenchantTextures.REAGENT_TABLE_BOOK);
        GlStateManager.rotate(20.0F, 1.0F, 0.0F, 0.0F);
        float openFlip = this.openPrev + (this.open - this.openPrev) * partialTicks;
        GlStateManager.translate((1.0F - openFlip) * 0.2F, (1.0F - openFlip) * 0.1F, (1.0F - openFlip) * 0.25F);
        GlStateManager.rotate(-(1.0F - openFlip) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        float pageOneFlip = this.flipPrev + (this.flip - this.flipPrev) * partialTicks + 0.25F;
        float pageTwoFlip = this.flipPrev + (this.flip - this.flipPrev) * partialTicks + 0.75F;
        pageOneFlip = (pageOneFlip - (float) MathHelper.fastFloor(pageOneFlip)) * 1.6F - 0.3F;
        pageTwoFlip = (pageTwoFlip - (float) MathHelper.fastFloor(pageTwoFlip)) * 1.6F - 0.3F;

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

        GlStateManager.enableRescaleNormal();
        MODEL_BOOK.render(null, 0.0F, pageOneFlip, pageTwoFlip, openFlip, 0.0F, 0.0625F);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.matrixMode(5889);
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        EnchantmentNameParts.getInstance().reseedRandomGenerator(this.reagentTableManager.getXpSeed());

        for(int enchantmentIndex = 0; enchantmentIndex < 3; enchantmentIndex++)
        {
            int enchantabilityLevel = this.reagentTableManager.getEnchantabilityLevels()[enchantmentIndex];
            int rectanglePosX = width + 62;
            int textPosX = rectanglePosX + 20;
            this.zLevel = 0.0F;
            this.mc.getTextureManager().bindTexture(guiTexture);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if(enchantabilityLevel == 0)
            {
                this.drawTexturedModalRect(rectanglePosX, height + 14 + 19 * enchantmentIndex, 0, 185, 108, 19);
            }
            else
            {
                String enchantLevelString = "" + enchantabilityLevel;
                int textWrapWidth = 86 - this.fontRenderer.getStringWidth(enchantLevelString);
                String randomEnchantmentName = EnchantmentNameParts.getInstance().generateNewRandomName(this.fontRenderer, textWrapWidth);
                FontRenderer fontRenderer = this.mc.standardGalacticFontRenderer;
                int color = 6839882;

                if(((this.reagentTableManager.getLapisAmount() < enchantmentIndex + 1 || this.reagentTableManager.getReagentAmount() < this.reagentTableManager.getReagentCosts()[enchantmentIndex] || this.mc.player.experienceLevel < enchantabilityLevel) && !this.mc.player.capabilities.isCreativeMode) || this.reagentTableManager.getEnchantmentHints()[enchantmentIndex] == -1)
                {
                    this.drawTexturedModalRect(rectanglePosX, height + 14 + 19 * enchantmentIndex, 0, 185, 108, 19);
                    this.drawTexturedModalRect(rectanglePosX + 1, height + 15 + 19 * enchantmentIndex, 16 * enchantmentIndex, 239, 16, 16);
                    fontRenderer.drawSplitString(randomEnchantmentName, textPosX, height + 16 + 19 * enchantmentIndex, textWrapWidth, (color & 16711422) >> 1);
                    color = 4226832;
                }
                else
                {
                    int cursorPosX = mouseX - (width + 62);
                    int cursorPosY = mouseY - (height + 14 + 19 * enchantmentIndex);

                    if(cursorPosX >= 0 && cursorPosY >= 0 && cursorPosX < 108 && cursorPosY < 19)
                    {
                        this.drawTexturedModalRect(rectanglePosX, height + 14 + 19 * enchantmentIndex, 0, 204, 108, 19);
                        color = 16777088;
                    }
                    else
                    {
                        this.drawTexturedModalRect(rectanglePosX, height + 14 + 19 * enchantmentIndex, 0, 166, 108, 19);
                    }

                    this.drawTexturedModalRect(rectanglePosX + 1, height + 15 + 19 * enchantmentIndex, 16 * enchantmentIndex, 223, 16, 16);
                    fontRenderer.drawSplitString(randomEnchantmentName, textPosX, height + 16 + 19 * enchantmentIndex, textWrapWidth, color);
                    color = 8453920;
                }

                fontRenderer = this.mc.fontRenderer;
                fontRenderer.drawStringWithShadow(enchantLevelString, (float) (textPosX + 86 - fontRenderer.getStringWidth(enchantLevelString)), (float) (height + 16 + 19 * enchantmentIndex + 7), color);
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

        for(int enchantmentIndex = 0; enchantmentIndex < 3; enchantmentIndex++)
        {
            Enchantment enchantmentHint = Enchantment.getEnchantmentByID(this.reagentTableManager.getEnchantmentHints()[enchantmentIndex]);
            int enchantmentLevel = this.reagentTableManager.getEnchantmentLevels()[enchantmentIndex];
            int enchantabilityLevel = this.reagentTableManager.getEnchantabilityLevels()[enchantmentIndex];

            if(this.isPointInRegion(62, 14 + 19 * enchantmentIndex, 108, 17, mouseX, mouseY) && enchantabilityLevel > 0)
            {
                List<String> list = new ArrayList<>();
                list.add("" + TextFormatting.WHITE + TextFormatting.ITALIC + I18n.format("container.enchant.clue", enchantmentHint == null ? "" : enchantmentHint.getTranslatedName(enchantmentLevel)));

                if(enchantmentHint == null)
                {
                    Collections.addAll(list, "", TextFormatting.RED + I18n.format("forge.container.enchant.limitedEnchantability"));
                }
                else if(!this.mc.player.capabilities.isCreativeMode)
                {
                    list.add("");

                    if(this.mc.player.experienceLevel < enchantabilityLevel)
                    {
                        list.add(TextFormatting.RED + I18n.format("container.enchant.level.requirement", enchantabilityLevel));
                    }
                    else
                    {
                        int enchantmentTierCost = enchantmentIndex + 1;

                        String lapisText;
                        TextFormatting lapisTextFormatting = this.reagentTableManager.getLapisAmount() >= enchantmentTierCost ? TextFormatting.GRAY : TextFormatting.RED;

                        if(enchantmentTierCost == 1)
                        {
                            lapisText = I18n.format("container.enchant.lapis.one");
                        }
                        else
                        {
                            lapisText = I18n.format("container.enchant.lapis.many", enchantmentTierCost);
                        }

                        list.add(lapisTextFormatting + "" + lapisText);

                        ItemStack reagentStack = this.reagentTableManager.getInventory().getStackInSlot(2);
                        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());

                        if(!reagent.isEmpty())
                        {
                            int reagentCost = this.reagentTableManager.getReagentCosts()[enchantmentIndex];

                            if(reagentCost > 0)
                            {
                                String reagentText;
                                TextFormatting reagentTextFormatting = this.reagentTableManager.getReagentAmount() >= reagentCost ? TextFormatting.GRAY : TextFormatting.RED;

                                if(reagentCost == 1)
                                {
                                    reagentText = I18n.format("container." + Reagenchant.MOD_ID + ".reagent_table.reagent.cost.one", I18n.format(reagentStack.getTranslationKey() + ".name"));
                                }
                                else
                                {
                                    reagentText = I18n.format("container." + Reagenchant.MOD_ID + ".reagent_table.reagent.cost.many", reagentCost, I18n.format(reagentStack.getTranslationKey() + ".name"));
                                }

                                list.add(reagentTextFormatting + "" + reagentText);
                            }
                        }

                        String enchantLevelText;

                        if(enchantmentTierCost == 1)
                        {
                            enchantLevelText = I18n.format("container.enchant.level.one");
                        }
                        else
                        {
                            enchantLevelText = I18n.format("container.enchant.level.many", enchantmentTierCost);
                        }

                        list.add(TextFormatting.GRAY + "" + enchantLevelText);
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
            if(this.reagentTableManager.getEnchantabilityLevels()[i] != 0)
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