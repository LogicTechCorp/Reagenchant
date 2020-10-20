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

package logictechcorp.reagenchant.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.inventory.container.ReagentTableContainer;
import logictechcorp.reagenchant.reagent.Reagent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ReagentTableScreen extends ContainerScreen<ReagentTableContainer>
{
    private static final ResourceLocation REAGENT_TABLE_WITH_REAGENT_GUI = new ResourceLocation(Reagenchant.MOD_ID, "textures/gui/container/reagent_table_with_reagent.png");
    private static final ResourceLocation REAGENT_TABLE_WITHOUT_REAGENT_GUI = new ResourceLocation(Reagenchant.MOD_ID, "textures/gui/container/reagent_table_without_reagent.png");
    private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private static final BookModel BOOK_MODEL = new BookModel();

    private final Random random;
    private float flip;
    private float flipPrev;
    private float flipRandom;
    private float flipTurn;
    private float open;
    private float openPrev;
    private ItemStack last = ItemStack.EMPTY;

    public ReagentTableScreen(ReagentTableContainer container, PlayerInventory playerInventory, ITextComponent component)
    {
        super(container, playerInventory, component);
        this.random = new Random();
    }

    @Override
    public void tick()
    {
        super.tick();
        this.tickBook();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.font.drawString(this.title.getFormattedText(), 12.0F, 5.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float) (this.ySize - 96 + 2), 4210752);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        int width = (this.width - this.xSize) / 2;
        int height = (this.height - this.ySize) / 2;

        for(int enchantmentTier = 0; enchantmentTier < 3; enchantmentTier++)
        {
            double posX = mouseX - (double) (width + 62);
            double posY = mouseY - (double) (height + 14 + 19 * enchantmentTier);

            if(posX >= 0.0D && posY >= 0.0D && posX < 108.0D && posY < 19.0D && this.container.enchantItem(this.minecraft.player, enchantmentTier))
            {
                this.minecraft.playerController.sendEnchantPacket((this.container).windowId, enchantmentTier);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        ResourceLocation guiTexture;

        if(!this.container.getItemStackHandler().getStackInSlot(2).isEmpty())
        {
            guiTexture = REAGENT_TABLE_WITH_REAGENT_GUI;
        }
        else
        {
            guiTexture = REAGENT_TABLE_WITHOUT_REAGENT_GUI;
        }

        RenderHelper.setupGuiFlatDiffuseLighting();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(guiTexture);
        int width = (this.width - this.xSize) / 2;
        int height = (this.height - this.ySize) / 2;
        this.blit(width, height, 0, 0, this.xSize, this.ySize);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int guiScaleFactor = (int) this.minecraft.getMainWindow().getGuiScaleFactor();
        RenderSystem.viewport((this.width - 320) / 2 * guiScaleFactor, (this.height - 240) / 2 * guiScaleFactor, 320 * guiScaleFactor, 240 * guiScaleFactor);
        RenderSystem.translatef(-0.34F, 0.23F, 0.0F);
        RenderSystem.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
        RenderSystem.matrixMode(5888);
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.push();
        MatrixStack.Entry matrixstack$entry = matrixStack.getLast();
        matrixstack$entry.getMatrix().setIdentity();
        matrixstack$entry.getNormal().setIdentity();
        matrixStack.translate(0.0D, 3.3F, 1984.0D);
        matrixStack.scale(5.0F, 5.0F, 5.0F);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(20.0F));
        float openFlip = MathHelper.lerp(partialTicks, this.openPrev, this.open);
        matrixStack.translate(((1.0F - openFlip) * 0.2F), ((1.0F - openFlip) * 0.1F), ((1.0F - openFlip) * 0.25F));
        float f2 = -(1.0F - openFlip) * 90.0F - 90.0F;
        matrixStack.rotate(Vector3f.YP.rotationDegrees(f2));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(180.0F));
        float pageOneFlip = MathHelper.lerp(partialTicks, this.flipPrev, this.flip) + 0.25F;
        float pageTwoFlip = MathHelper.lerp(partialTicks, this.flipPrev, this.flip) + 0.75F;
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

        RenderSystem.enableRescaleNormal();
        BOOK_MODEL.func_228247_a_(0.0F, pageOneFlip, pageTwoFlip, openFlip);
        IRenderTypeBuffer.Impl renderBuffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        IVertexBuilder vertexBuilder = renderBuffer.getBuffer(BOOK_MODEL.getRenderType(BOOK_TEXTURE));
        BOOK_MODEL.render(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        renderBuffer.finish();
        matrixStack.pop();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.minecraft.getMainWindow().getFramebufferWidth(), this.minecraft.getMainWindow().getFramebufferHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderHelper.setupGui3DDiffuseLighting();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        EnchantmentNameParts.getInstance().reseedRandomGenerator(this.container.getXpSeed() + (this.container.getReagentAmount() == 0 ? 0 : 1));

        for(int i = 0; i < 3; i++)
        {
            int enchantabilityLevel = this.container.getEnchantabilityLevels()[i];
            int rectanglePosX = width + 62;
            int textPosX = rectanglePosX + 20;
            this.setBlitOffset(0);
            this.minecraft.getTextureManager().bindTexture(guiTexture);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            if(enchantabilityLevel == 0)
            {
                this.blit(rectanglePosX, height + 14 + 19 * i, 0, 185, 108, 19);
            }
            else
            {
                String enchantabilityLevelString = "" + enchantabilityLevel;
                int length = 86 - this.font.getStringWidth(enchantabilityLevelString);
                String randomName = EnchantmentNameParts.getInstance().generateNewRandomName(this.font, length);
                FontRenderer fontRenderer = this.minecraft.getFontResourceManager().getFontRenderer(Minecraft.standardGalacticFontRenderer);
                int color = 6839882;

                if(((this.container.getLapisAmount() < i + 1 || this.container.getReagentAmount() < this.container.getReagentCost()[i] || this.minecraft.player.experienceLevel < enchantabilityLevel) && !this.minecraft.player.abilities.isCreativeMode) || this.container.getEnchantments()[i] == -1)
                {
                    this.blit(rectanglePosX, height + 14 + 19 * i, 0, 185, 108, 19);
                    this.blit(rectanglePosX + 1, height + 15 + 19 * i, 16 * i, 239, 16, 16);
                    fontRenderer.drawSplitString(randomName, textPosX, height + 16 + 19 * i, length, (color & 16711422) >> 1);
                    color = 4226832;
                }
                else
                {
                    int cursorPosX = mouseX - (width + 62);
                    int cursorPosY = mouseY - (height + 14 + 19 * i);

                    if(cursorPosX >= 0 && cursorPosY >= 0 && cursorPosX < 108 && cursorPosY < 19)
                    {
                        this.blit(rectanglePosX, height + 14 + 19 * i, 0, 204, 108, 19);
                        color = 16777088;
                    }
                    else
                    {
                        this.blit(rectanglePosX, height + 14 + 19 * i, 0, 166, 108, 19);
                    }

                    this.blit(rectanglePosX + 1, height + 15 + 19 * i, 16 * i, 223, 16, 16);
                    fontRenderer.drawSplitString(randomName, textPosX, height + 16 + 19 * i, length, color);
                    color = 8453920;
                }

                fontRenderer = this.minecraft.fontRenderer;
                fontRenderer.drawStringWithShadow(enchantabilityLevelString, (float) (textPosX + 86 - fontRenderer.getStringWidth(enchantabilityLevelString)), (float) (height + 16 + 19 * i + 7), color);
            }
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        partialTicks = this.minecraft.getTickLength();
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        for(int i = 0; i < 3; i++)
        {
            Enchantment enchantment = Enchantment.getEnchantmentByID(this.container.getEnchantments()[i]);
            int enchantmentLevel = this.container.getEnchantmentLevels()[i];
            int enchantabilityLevel = this.container.getEnchantabilityLevels()[i];
            int enchantmentTier = i + 1;

            if(this.isPointInRegion(62, 14 + 19 * i, 108, 17, mouseX, mouseY) && enchantabilityLevel > 0)
            {
                List<String> list = new ArrayList<>();
                list.add("" + TextFormatting.WHITE + TextFormatting.ITALIC + I18n.format("container.enchant.clue", enchantment == null ? "" : enchantment.getDisplayName(enchantmentLevel).getFormattedText()));

                if(enchantment == null)
                {
                    Collections.addAll(list, "", TextFormatting.RED + I18n.format("forge.container.enchant.limitedEnchantability"));
                }
                else if(!this.minecraft.player.abilities.isCreativeMode)
                {
                    list.add("");

                    if(this.minecraft.player.experienceLevel < enchantabilityLevel)
                    {
                        list.add(TextFormatting.RED + I18n.format("container.enchant.level.requirement", enchantabilityLevel));
                    }
                    else
                    {
                        String lapisText;
                        TextFormatting lapisTextFormatting = this.container.getLapisAmount() >= enchantmentTier ? TextFormatting.GRAY : TextFormatting.RED;

                        if(enchantmentTier == 1)
                        {
                            lapisText = I18n.format("container.enchant.lapis.one");
                        }
                        else
                        {
                            lapisText = I18n.format("container.enchant.lapis.many", enchantmentTier);
                        }

                        list.add(lapisTextFormatting + "" + lapisText);

                        ItemStack reagentStack = this.container.getItemStackHandler().getStackInSlot(2);
                        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());

                        if(!reagent.isEmpty())
                        {
                            int reagentCost = this.container.getReagentCost()[i];

                            if(reagentCost > 0)
                            {
                                String reagentText;
                                TextFormatting reagentTextFormatting = this.container.getReagentAmount() >= reagentCost ? TextFormatting.GRAY : TextFormatting.RED;

                                if(reagentCost == 1)
                                {
                                    reagentText = I18n.format("container." + Reagenchant.MOD_ID + ".reagent_table.reagent.cost.one", I18n.format(reagentStack.getTranslationKey()));
                                }
                                else
                                {
                                    reagentText = I18n.format("container." + Reagenchant.MOD_ID + ".reagent_table.reagent.cost.many", reagentCost, I18n.format(reagentStack.getTranslationKey()));
                                }

                                list.add(reagentTextFormatting + "" + reagentText);
                            }
                        }

                        String enchantLevelText;

                        if(enchantmentTier == 1)
                        {
                            enchantLevelText = I18n.format("container.enchant.level.one");
                        }
                        else
                        {
                            enchantLevelText = I18n.format("container.enchant.level.many", enchantmentTier);
                        }

                        list.add(TextFormatting.GRAY + "" + enchantLevelText);
                    }
                }

                this.renderTooltip(list, mouseX, mouseY);
                break;
            }
        }
    }

    private void tickBook()
    {
        ItemStack stack = this.container.getSlot(0).getStack();

        if(!ItemStack.areItemStacksEqual(stack, this.last))
        {
            this.last = stack;

            do
            {
                this.flipRandom += (float) (this.random.nextInt(4) - this.random.nextInt(4));
            }
            while(!(this.flip > this.flipRandom + 1.0F) && !(this.flip < this.flipRandom - 1.0F));
        }

        this.flipPrev = this.flip;
        this.openPrev = this.open;
        boolean flag = false;

        for(int i = 0; i < 3; i++)
        {
            if(this.container.getEnchantabilityLevels()[i] != 0)
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
