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
import logictechcorp.reagenchant.common.inventory.container.CustomAnvilContainer;
import logictechcorp.reagenchant.common.network.item.MessageCUpdateItemNamePacket;
import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CustomAnvilScreen extends ContainerScreen<CustomAnvilContainer> implements IContainerListener {
    private static final ResourceLocation CUSTOM_ANVIL_GUI = new ResourceLocation(Reagenchant.MOD_ID, "textures/gui/container/custom_anvil.png");
    private static final ITextComponent EXPENSIVE_TEXT_COMPONENT = new TranslationTextComponent("container.repair.expensive");
    private TextFieldWidget customNameField;

    public CustomAnvilScreen(CustomAnvilContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.titleLabelX = 60;
    }

    @Override
    public void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        int posX = (this.width - this.imageWidth) / 2;
        int posZ = (this.height - this.imageHeight) / 2;
        this.customNameField = new TextFieldWidget(this.font, posX + 62, posZ + 24, 103, 12, new TranslationTextComponent("container.repair"));
        this.customNameField.setCanLoseFocus(false);
        this.customNameField.setTextColor(-1);
        this.customNameField.setTextColorUneditable(-1);
        this.customNameField.setBordered(false);
        this.customNameField.setMaxLength(35);
        this.customNameField.setResponder(this::renameItem);
        this.children.add(this.customNameField);
        this.setInitialFocus(this.customNameField);
        this.menu.addSlotListener(this);
    }

    @Override
    public void tick() {
        super.tick();
        this.customNameField.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderSystem.disableBlend();
        this.customNameField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String customName = this.customNameField.getValue();
        this.init(minecraft, width, height);
        this.customNameField.setValue(customName);
    }

    @Override
    public void removed() {
        super.removed();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        this.menu.removeSlotListener(this);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 256) {
            this.minecraft.player.closeContainer();
        }

        return this.customNameField.keyPressed(keyCode, scanCode, modifiers) || this.customNameField.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void renameItem(String customName) {
        if(!customName.isEmpty()) {
            String newName = customName;
            Slot slot = this.menu.getSlot(0);

            if(slot.hasItem() && !slot.getItem().hasCustomHoverName() && customName.equals(slot.getItem().getHoverName().getString())) {
                newName = "";
            }

            this.menu.updateItemName(newName);
            Reagenchant.CHANNEL.sendToServer(new MessageCUpdateItemNamePacket(newName));
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        super.renderLabels(matrixStack, mouseX, mouseY);
        int repairCost = this.menu.getRepairCost();

        if(repairCost > 0) {
            int textColor = 8453920;
            ITextComponent anvilInfoTextComponent;

            if(repairCost >= 40 && !this.minecraft.player.abilities.instabuild) {
                anvilInfoTextComponent = EXPENSIVE_TEXT_COMPONENT;
                textColor = 16736352;
            }
            else if(!this.menu.getSlot(3).hasItem()) {
                anvilInfoTextComponent = null;
            }
            else {
                if(this.menu.useIronInsteadOfXp()) {
                    anvilInfoTextComponent = new TranslationTextComponent("container.reagenchant.anvil.iron_repair_cost", repairCost);
                }
                else {
                    anvilInfoTextComponent = new TranslationTextComponent("container.repair.cost", repairCost);
                }

                if(!this.menu.getSlot(3).mayPickup(this.inventory.player)) {
                    textColor = 16736352;
                }
            }

            if(anvilInfoTextComponent != null) {
                int posX = this.imageWidth - 8 - this.font.width(anvilInfoTextComponent) - 2;
                fill(matrixStack, posX - 2, 67, this.imageWidth - 8, 79, 1325400064);
                this.font.drawShadow(matrixStack, anvilInfoTextComponent, (float) posX, 69.0F, textColor);
            }
        }

    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(CUSTOM_ANVIL_GUI);
        int posX = (this.width - this.imageWidth) / 2;
        int posY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, posX, posY, 0, 0, this.imageWidth, this.imageHeight);
        this.blit(matrixStack, posX + 59, posY + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);

        if((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem() || this.menu.getSlot(2).hasItem()) && !this.menu.getSlot(3).hasItem()) {
            this.blit(matrixStack, posX + 99, posY + 45, this.imageWidth, 0, 28, 21);
        }
    }

    @Override
    public void refreshContainer(Container container, NonNullList<ItemStack> stacks) {
        this.slotChanged(container, 0, container.getSlot(0).getItem());
    }

    @Override
    public void setContainerData(Container container, int id, int value) {
    }

    @Override
    public void slotChanged(Container container, int slotId, ItemStack stack) {
        if(slotId == 0) {
            this.customNameField.setValue(stack.isEmpty() ? "" : stack.getHoverName().getString());
            this.customNameField.setEditable(!stack.isEmpty());
            this.setFocused(this.customNameField);
        }
    }
}
