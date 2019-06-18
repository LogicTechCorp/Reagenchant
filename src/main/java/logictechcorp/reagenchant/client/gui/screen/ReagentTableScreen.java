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

package logictechcorp.reagenchant.client.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.api.reagent.IReagent;
import logictechcorp.reagenchant.init.ReagenchantTextures;
import logictechcorp.reagenchant.inventory.ReagentTableContainer;
import logictechcorp.reagenchant.inventory.ReagentTableManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ReagentTableScreen extends ContainerScreen
{
    private static final BookModel MODEL_BOOK = new BookModel();
    private final ReagentTableManager reagentTableManager;
    private final Random random;
    private float flip;
    private float flipPrev;
    private float flipRandom;
    private float flipTurn;
    private float open;
    private float openPrev;
    private ItemStack last = ItemStack.EMPTY;

    public ReagentTableScreen(ReagentTableContainer container)
    {
        super(container, container.getReagentTableManager().getUser().inventory, container.getReagentTableManager().getReagentTable().getName());
        this.reagentTableManager = container.getReagentTableManager();
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

        for(int k = 0; k < 3; ++k)
        {
            double posX = mouseX - (double) (width + 60);
            double posY = mouseY - (double) (height + 14 + 19 * k);

            if(posX >= 0.0D && posY >= 0.0D && posX < 108.0D && posY < 19.0D && this.container.enchantItem(this.minecraft.player, k))
            {
                this.minecraft.playerController.sendEnchantPacket((this.container).windowId, k);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
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

        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(guiTexture);
        int width = (this.width - this.xSize) / 2;
        int height = (this.height - this.ySize) / 2;
        this.blit(width, height, 0, 0, this.xSize, this.ySize);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        int guiScaleFactor = (int) this.minecraft.mainWindow.getGuiScaleFactor();
        GlStateManager.viewport((this.width - 320) / 2 * guiScaleFactor, (this.height - 240) / 2 * guiScaleFactor, 320 * guiScaleFactor, 240 * guiScaleFactor);
        GlStateManager.translatef(-0.34F, 0.23F, 0.0F);
        GlStateManager.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.translatef(0.0F, 3.3F, -16.0F);
        GlStateManager.scalef(1.0F, 1.0F, 1.0F);
        GlStateManager.scalef(5.0F, 5.0F, 5.0F);
        GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(ReagenchantTextures.REAGENT_TABLE_BOOK);
        GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
        float openFlip = MathHelper.lerp(partialTicks, this.openPrev, this.open);
        GlStateManager.translatef((1.0F - openFlip) * 0.2F, (1.0F - openFlip) * 0.1F, (1.0F - openFlip) * 0.25F);
        GlStateManager.rotatef(-(1.0F - openFlip) * 90.0F - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
        float pageOneFlip = MathHelper.lerp(partialTicks, this.flipPrev, this.flip) + 0.25F;
        float pageTwoFlip = MathHelper.lerp(partialTicks, this.flipPrev, this.flip) + 0.75F;
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

        GlStateManager.enableRescaleNormal();
        MODEL_BOOK.func_217103_a(0.0F, pageOneFlip, pageTwoFlip, openFlip, 0.0F, 0.0625F);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.matrixMode(5889);
        GlStateManager.viewport(0, 0, this.minecraft.mainWindow.getFramebufferWidth(), this.minecraft.mainWindow.getFramebufferHeight());
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        EnchantmentNameParts.getInstance().reseedRandomGenerator((long) this.reagentTableManager.getXpSeed());

        for(int i = 0; i < 3; ++i)
        {
            int rectanglePosX = width + 60;
            int textPosX = rectanglePosX + 20;
            this.blitOffset = 0;
            this.minecraft.getTextureManager().bindTexture(guiTexture);
            int enchantabilityLevel = this.reagentTableManager.getEnchantabilityLevels()[i];
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            if(enchantabilityLevel == 0)
            {
                this.blit(rectanglePosX, height + 14 + 19 * i, 0, 185, 108, 19);
            }
            else
            {
                String enchantLevelString = "" + enchantabilityLevel;
                int i2 = 86 - this.font.getStringWidth(enchantLevelString);
                String s1 = EnchantmentNameParts.getInstance().generateNewRandomName(this.font, i2);
                FontRenderer fontRenderer = this.minecraft.getFontResourceManager().getFontRenderer(Minecraft.standardGalacticFontRenderer);
                int color = 6839882;

                if(((this.reagentTableManager.getLapisAmount() < i + 1 || this.minecraft.player.experienceLevel < enchantabilityLevel) && !this.minecraft.player.abilities.isCreativeMode) || this.reagentTableManager.getEnchantments()[i] == -1)
                {
                    this.blit(rectanglePosX, height + 14 + 19 * i, 0, 185, 108, 19);
                    this.blit(rectanglePosX + 1, height + 15 + 19 * i, 16 * i, 239, 16, 16);
                    fontRenderer.drawSplitString(s1, textPosX, height + 16 + 19 * i, i2, (color & 16711422) >> 1);
                    color = 4226832;
                }
                else
                {
                    int cursorPosX = mouseX - (width + 60);
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
                    fontRenderer.drawSplitString(s1, textPosX, height + 16 + 19 * i, i2, color);
                    color = 8453920;
                }

                fontRenderer = this.minecraft.fontRenderer;
                fontRenderer.drawStringWithShadow(enchantLevelString, (float) (textPosX + 86 - fontRenderer.getStringWidth(enchantLevelString)), (float) (height + 16 + 19 * i + 7), color);
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
            Enchantment enchantment = Enchantment.getEnchantmentByID(this.reagentTableManager.getEnchantments()[i]);
            int enchantmentLevel = this.reagentTableManager.getEnchantmentLevels()[i];
            int enchantabilityLevel = this.reagentTableManager.getEnchantabilityLevels()[i];
            int enchantmentTier = i + 1;

            if(this.isPointInRegion(62, 14 + 19 * i, 108, 17, mouseX, mouseY) && enchantabilityLevel > 0)
            {
                List<String> list = new ArrayList<>();
                list.add("" + TextFormatting.WHITE + TextFormatting.ITALIC + I18n.format("gui.reagenchant:reagent_table.enchantment.clue", enchantment == null ? "" : enchantment.getDisplayName(enchantmentLevel).getFormattedText()));

                if(enchantment == null)
                {
                    Collections.addAll(list, "", TextFormatting.RED + I18n.format("gui.reagenchant:reagent_table.enchantment.limited"));
                }
                else if(!this.minecraft.player.abilities.isCreativeMode)
                {
                    list.add("");

                    if(this.minecraft.player.experienceLevel < enchantabilityLevel)
                    {
                        list.add(TextFormatting.RED + I18n.format("gui.reagenchant:reagent_table.experience.requirement", enchantabilityLevel));
                    }
                    else
                    {
                        TextFormatting lapisTextFormatting = this.reagentTableManager.getLapisAmount() >= enchantmentTier ? TextFormatting.GRAY : TextFormatting.RED;
                        list.add(lapisTextFormatting + "" + I18n.format("gui.reagenchant:reagent_table.lapis.cost", enchantmentTier));

                        ItemStack reagentStack = this.reagentTableManager.getInventory().getStackInSlot(2);

                        if(!reagentStack.isEmpty())
                        {
                            IReagent reagent = ReagenchantAPI.getInstance().getReagentRegistry().getReagent(reagentStack.getItem());

                            if(reagent.getAssociatedEnchantments().contains(enchantment))
                            {
                                ItemStack unenchantedStack = this.reagentTableManager.getInventory().getStackInSlot(0);
                                World world = this.reagentTableManager.getWorld();
                                BlockPos pos = this.reagentTableManager.getPos();
                                PlayerEntity player = this.reagentTableManager.getUser();
                                Random random = this.reagentTableManager.getRandom();

                                int reagentCost = reagent.getReagentCost(world, pos, player, unenchantedStack, reagentStack, new EnchantmentData(enchantment, enchantmentLevel), random);
                                TextFormatting reagentTextFormatting = this.reagentTableManager.getReagentAmount() >= reagentCost ? TextFormatting.GRAY : TextFormatting.RED;
                                list.add(reagentTextFormatting + "" + I18n.format("gui.reagenchant:reagent_table.reagent.cost", reagentCost));
                            }
                        }

                        list.add(TextFormatting.GRAY + "" + I18n.format("gui.reagenchant:reagent_table.experience.cost", enchantmentTier));
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
