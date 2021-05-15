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
        this.titleX = 60;
    }

    @Override
    public void init() {
        super.init();
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        int posX = (this.width - this.xSize) / 2;
        int posZ = (this.height - this.ySize) / 2;
        this.customNameField = new TextFieldWidget(this.font, posX + 62, posZ + 24, 103, 12, new TranslationTextComponent("container.repair"));
        this.customNameField.setCanLoseFocus(false);
        this.customNameField.setTextColor(-1);
        this.customNameField.setDisabledTextColour(-1);
        this.customNameField.setEnableBackgroundDrawing(false);
        this.customNameField.setMaxStringLength(35);
        this.customNameField.setResponder(this::renameItem);
        this.children.add(this.customNameField);
        this.setFocusedDefault(this.customNameField);
        this.container.addListener(this);
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
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String customName = this.customNameField.getText();
        this.init(minecraft, width, height);
        this.customNameField.setText(customName);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);
        this.container.removeListener(this);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 256) {
            this.minecraft.player.closeScreen();
        }

        return this.customNameField.keyPressed(keyCode, scanCode, modifiers) || this.customNameField.canWrite() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void renameItem(String customName) {
        if(!customName.isEmpty()) {
            String newName = customName;
            Slot slot = this.container.getSlot(0);

            if(slot.getHasStack() && !slot.getStack().hasDisplayName() && customName.equals(slot.getStack().getDisplayName().getString())) {
                newName = "";
            }

            this.container.updateItemName(newName);
            Reagenchant.CHANNEL.sendToServer(new MessageCUpdateItemNamePacket(newName));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.disableBlend();
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        int repairCost = this.container.getRepairCost();

        if(repairCost > 0) {
            int textColor = 8453920;
            ITextComponent anvilInfoTextComponent;

            if(repairCost >= 40 && !this.minecraft.player.abilities.isCreativeMode) {
                anvilInfoTextComponent = EXPENSIVE_TEXT_COMPONENT;
                textColor = 16736352;
            }
            else if(!this.container.getSlot(3).getHasStack()) {
                anvilInfoTextComponent = null;
            }
            else {
                if(this.container.useIronInsteadOfXp()) {
                    anvilInfoTextComponent = new TranslationTextComponent("container.reagenchant.anvil.iron_repair_cost", repairCost);
                }
                else {
                    anvilInfoTextComponent = new TranslationTextComponent("container.repair.cost", repairCost);
                }

                if(!this.container.getSlot(3).canTakeStack(this.playerInventory.player)) {
                    textColor = 16736352;
                }
            }

            if(anvilInfoTextComponent != null) {
                int posX = this.xSize - 8 - this.font.getStringPropertyWidth(anvilInfoTextComponent) - 2;
                fill(matrixStack, posX - 2, 67, this.xSize - 8, 79, 1325400064);
                this.font.drawTextWithShadow(matrixStack, anvilInfoTextComponent, (float) posX, 69.0F, textColor);
            }
        }

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CUSTOM_ANVIL_GUI);
        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;
        this.blit(matrixStack, posX, posY, 0, 0, this.xSize, this.ySize);
        this.blit(matrixStack, posX + 59, posY + 20, 0, this.ySize + (this.container.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

        if((this.container.getSlot(0).getHasStack() || this.container.getSlot(1).getHasStack() || this.container.getSlot(2).getHasStack()) && !this.container.getSlot(3).getHasStack()) {
            this.blit(matrixStack, posX + 99, posY + 45, this.xSize, 0, 28, 21);
        }
    }

    @Override
    public void sendAllContents(Container container, NonNullList<ItemStack> stacks) {
        this.sendSlotContents(container, 0, container.getSlot(0).getStack());
    }

    @Override
    public void sendWindowProperty(Container container, int id, int value) {
    }

    @Override
    public void sendSlotContents(Container container, int slotId, ItemStack stack) {
        if(slotId == 0) {
            this.customNameField.setText(stack.isEmpty() ? "" : stack.getDisplayName().getString());
            this.customNameField.setEnabled(!stack.isEmpty());
            this.setListener(this.customNameField);
        }
    }
}
