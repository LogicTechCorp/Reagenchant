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

package logictechcorp.reagenchant.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import logictechcorp.reagenchant.common.inventory.container.ReagentEnchantingTableContainer;
import logictechcorp.reagenchant.common.reagent.Reagent;
import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnchantmentNameParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReagentEnchantingTableScreen extends ContainerScreen<ReagentEnchantingTableContainer> {
    private static final ResourceLocation REAGENT_ENCHANTING_TABLE_WITH_REAGENT_GUI = new ResourceLocation(Reagenchant.MOD_ID, "textures/gui/container/reagent_enchanting_table_with_reagent.png");
    private static final ResourceLocation REAGENT_ENCHANTING_TABLE_WITHOUT_REAGENT_GUI = new ResourceLocation(Reagenchant.MOD_ID, "textures/gui/container/reagent_enchanting_table_without_reagent.png");
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

    public ReagentEnchantingTableScreen(ReagentEnchantingTableContainer container, PlayerInventory playerInventory, ITextComponent component) {
        super(container, playerInventory, component);
        this.random = new Random();
    }

    @Override
    public void tick() {
        super.tick();
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        int width = (this.width - this.imageWidth) / 2;
        int height = (this.height - this.imageHeight) / 2;

        for(int enchantmentTier = 0; enchantmentTier < 3; enchantmentTier++) {
            double posX = mouseX - (double) (width + 62);
            double posY = mouseY - (double) (height + 14 + 19 * enchantmentTier);

            if(posX >= 0.0D && posY >= 0.0D && posX < 108.0D && posY < 19.0D && this.menu.clickMenuButton(this.minecraft.player, enchantmentTier)) {
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, enchantmentTier);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation guiTexture;

        if(this.menu.getSlot(2).hasItem()) {
            guiTexture = REAGENT_ENCHANTING_TABLE_WITH_REAGENT_GUI;
        }
        else {
            guiTexture = REAGENT_ENCHANTING_TABLE_WITHOUT_REAGENT_GUI;
        }

        RenderHelper.setupForFlatItems();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(guiTexture);
        int width = (this.width - this.imageWidth) / 2;
        int height = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, width, height, 0, 0, this.imageWidth, this.imageHeight);
        RenderSystem.matrixMode(5889);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        int guiScaleFactor = (int) this.minecraft.getWindow().getGuiScale();
        RenderSystem.viewport((this.width - 320) / 2 * guiScaleFactor, (this.height - 240) / 2 * guiScaleFactor, 320 * guiScaleFactor, 240 * guiScaleFactor);
        RenderSystem.translatef(-0.34F, 0.23F, 0.0F);
        RenderSystem.multMatrix(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
        RenderSystem.matrixMode(5888);
        matrixStack.pushPose();
        MatrixStack.Entry matrixStackLast = matrixStack.last();
        matrixStackLast.pose().setIdentity();
        matrixStackLast.normal().setIdentity();
        matrixStack.translate(0.0D, 3.3F, 1984.0D);
        matrixStack.scale(5.0F, 5.0F, 5.0F);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(20.0F));
        float openFlip = MathHelper.lerp(partialTicks, this.openPrev, this.open);
        matrixStack.translate(((1.0F - openFlip) * 0.2F), ((1.0F - openFlip) * 0.1F), ((1.0F - openFlip) * 0.25F));
        float f2 = -(1.0F - openFlip) * 90.0F - 90.0F;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(f2));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
        float pageOneFlip = MathHelper.lerp(partialTicks, this.flipPrev, this.flip) + 0.25F;
        float pageTwoFlip = MathHelper.lerp(partialTicks, this.flipPrev, this.flip) + 0.75F;
        pageOneFlip = (pageOneFlip - (float) MathHelper.fastFloor(pageOneFlip)) * 1.6F - 0.3F;
        pageTwoFlip = (pageTwoFlip - (float) MathHelper.fastFloor(pageTwoFlip)) * 1.6F - 0.3F;

        if(pageOneFlip < 0.0F) {
            pageOneFlip = 0.0F;
        }
        if(pageTwoFlip < 0.0F) {
            pageTwoFlip = 0.0F;
        }
        if(pageOneFlip > 1.0F) {
            pageOneFlip = 1.0F;
        }
        if(pageTwoFlip > 1.0F) {
            pageTwoFlip = 1.0F;
        }

        RenderSystem.enableRescaleNormal();
        BOOK_MODEL.setupAnim(0.0F, pageOneFlip, pageTwoFlip, openFlip);
        IRenderTypeBuffer.Impl renderTypeBuffer = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
        IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(BOOK_MODEL.renderType(BOOK_TEXTURE));
        BOOK_MODEL.renderToBuffer(matrixStack, vertexBuilder, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        renderTypeBuffer.endBatch();
        matrixStack.popPose();
        RenderSystem.matrixMode(5889);
        RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderHelper.setupFor3DItems();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        EnchantmentNameParts.getInstance().initSeed(this.menu.getXpSeed() + (this.menu.getReagentAmount() == 0 ? 0 : Item.getId(this.menu.getSlot(2).getItem().getItem())));

        for(int i = 0; i < 3; i++) {
            int enchantabilityLevel = this.menu.getEnchantabilityLevels()[i];
            int rectanglePosX = width + 62;
            int textPosX = rectanglePosX + 20;
            this.setBlitOffset(0);
            this.minecraft.getTextureManager().bind(guiTexture);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

            if(enchantabilityLevel == 0) {
                this.blit(matrixStack, rectanglePosX, height + 14 + 19 * i, 0, 185, 108, 19);
            }
            else {
                String enchantabilityLevelString = "" + enchantabilityLevel;
                int length = 86 - this.font.width(enchantabilityLevelString);
                ITextProperties randomName = EnchantmentNameParts.getInstance().getRandomName(this.font, length);
                int color = 6839882;

                if(((this.menu.getLapisAmount() < i + 1 || this.menu.getReagentAmount() < this.menu.getReagentCosts()[i] || this.minecraft.player.experienceLevel < enchantabilityLevel) && !this.minecraft.player.abilities.instabuild) || this.menu.getEnchantments()[i] == -1) {
                    this.blit(matrixStack, rectanglePosX, height + 14 + 19 * i, 0, 185, 108, 19);
                    this.blit(matrixStack, rectanglePosX + 1, height + 15 + 19 * i, 16 * i, 239, 16, 16);
                    this.font.drawWordWrap(randomName, textPosX, height + 16 + 19 * i, length, (color & 16711422) >> 1);
                    color = 4226832;
                }
                else {
                    int cursorPosX = mouseX - (width + 62);
                    int cursorPosY = mouseY - (height + 14 + 19 * i);

                    if(cursorPosX >= 0 && cursorPosY >= 0 && cursorPosX < 108 && cursorPosY < 19) {
                        this.blit(matrixStack, rectanglePosX, height + 14 + 19 * i, 0, 204, 108, 19);
                        color = 16777088;
                    }
                    else {
                        this.blit(matrixStack, rectanglePosX, height + 14 + 19 * i, 0, 166, 108, 19);
                    }

                    this.blit(matrixStack, rectanglePosX + 1, height + 15 + 19 * i, 16 * i, 223, 16, 16);
                    this.font.drawWordWrap(randomName, textPosX, height + 16 + 19 * i, length, color);
                    color = 8453920;
                }

                this.font.drawShadow(matrixStack, enchantabilityLevelString, (float) (textPosX + 86 - this.font.width(enchantabilityLevelString)), (float) (height + 16 + 19 * i + 7), color);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        partialTicks = this.minecraft.getDeltaFrameTime();
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        for(int i = 0; i < 3; i++) {
            Enchantment enchantment = Enchantment.byId(this.menu.getEnchantments()[i]);
            int enchantmentLevel = this.menu.getEnchantmentLevels()[i];
            int enchantabilityLevel = this.menu.getEnchantabilityLevels()[i];
            int enchantmentTier = i + 1;

            if(this.isHovering(62, 14 + 19 * i, 108, 17, mouseX, mouseY) && enchantabilityLevel > 0) {
                List<ITextComponent> list = new ArrayList<>();
                list.add((new TranslationTextComponent("container.enchant.clue", enchantment == null ? "" : enchantment.getFullname(enchantmentLevel))).withStyle(TextFormatting.WHITE));

                if(enchantment == null) {
                    list.add(new StringTextComponent(""));
                    list.add(new TranslationTextComponent("forge.container.enchant.limitedEnchantability").withStyle(TextFormatting.RED));
                }
                else if(!this.minecraft.player.abilities.instabuild) {
                    list.add(StringTextComponent.EMPTY);

                    if(this.minecraft.player.experienceLevel < enchantabilityLevel) {
                        list.add((new TranslationTextComponent("container.enchant.level.requirement", enchantabilityLevel)).withStyle(TextFormatting.RED));
                    }
                    else {
                        IFormattableTextComponent lapisTextFormatting;

                        if(enchantmentTier == 1) {
                            lapisTextFormatting = new TranslationTextComponent("container.enchant.lapis.one");
                        }
                        else {
                            lapisTextFormatting = new TranslationTextComponent("container.enchant.lapis.many", enchantmentTier);
                        }

                        list.add(lapisTextFormatting.withStyle(this.menu.getLapisAmount() >= enchantmentTier ? TextFormatting.GRAY : TextFormatting.RED));

                        ItemStack reagentStack = this.menu.getSlot(2).getItem();
                        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(reagentStack.getItem());

                        if(!reagent.isEmpty()) {
                            int reagentCost = this.menu.getReagentCosts()[i];

                            if(reagentCost > 0) {
                                IFormattableTextComponent reagentTextFormatting;

                                if(reagentCost == 1) {
                                    reagentTextFormatting = new TranslationTextComponent("container." + Reagenchant.MOD_ID + ".reagent_enchanting_table.reagent.cost.one", new TranslationTextComponent(reagentStack.getDescriptionId()));
                                }
                                else {
                                    reagentTextFormatting = new TranslationTextComponent("container." + Reagenchant.MOD_ID + ".reagent_enchanting_table.reagent.cost.many", reagentCost, new TranslationTextComponent(reagentStack.getDescriptionId()));
                                }

                                list.add(reagentTextFormatting.withStyle(this.menu.getReagentAmount() >= reagentCost ? TextFormatting.GRAY : TextFormatting.RED));
                            }
                        }

                        IFormattableTextComponent enchantmentTierTextFormatting;

                        if(enchantmentTier == 1) {
                            enchantmentTierTextFormatting = new TranslationTextComponent("container.enchant.level.one");
                        }
                        else {
                            enchantmentTierTextFormatting = new TranslationTextComponent("container.enchant.level.many", enchantmentTier);
                        }

                        list.add(enchantmentTierTextFormatting.withStyle(TextFormatting.GRAY));
                    }
                }

                this.renderComponentTooltip(matrixStack, list, mouseX, mouseY);
                break;
            }
        }
    }

    private void tickBook() {
        ItemStack stack = this.menu.getSlot(0).getItem();

        if(!ItemStack.matches(stack, this.last)) {
            this.last = stack;

            do {
                this.flipRandom += (float) (this.random.nextInt(4) - this.random.nextInt(4));
            }
            while(!(this.flip > this.flipRandom + 1.0F) && !(this.flip < this.flipRandom - 1.0F));
        }

        this.flipPrev = this.flip;
        this.openPrev = this.open;
        boolean flag = false;

        for(int i = 0; i < 3; i++) {
            if(this.menu.getEnchantabilityLevels()[i] != 0) {
                flag = true;
            }
        }

        if(flag) {
            this.open += 0.2F;
        }
        else {
            this.open -= 0.2F;
        }

        this.open = MathHelper.clamp(this.open, 0.0F, 1.0F);
        float flip = (this.flipRandom - this.flip) * 0.4F;
        flip = MathHelper.clamp(flip, -0.2F, 0.2F);
        this.flipTurn += (flip - this.flipTurn) * 0.9F;
        this.flip += this.flipTurn;
    }
}
