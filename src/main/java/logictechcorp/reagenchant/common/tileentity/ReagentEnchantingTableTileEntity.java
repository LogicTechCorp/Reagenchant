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

package logictechcorp.reagenchant.common.tileentity;

import logictechcorp.reagenchant.common.inventory.container.ReagentEnchantingTableContainer;
import logictechcorp.reagenchant.core.registry.ReagenchantTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Random;

public class ReagentEnchantingTableTileEntity extends InventoryTileEntity implements ITickableTileEntity {
    public int ticks;
    public float nextPageFlipAmount;
    public float pageFlipAmount;
    public float randomPageFlipAmount;
    public float adjustedFlipAmount;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    public float nextPageAngle;
    public float pageAngle;
    public float angle;

    protected Random random = new Random();

    public ReagentEnchantingTableTileEntity() {
        super(ReagenchantTileEntityTypes.REAGENT_ENCHANTING_TABLE_TILE_ENTITY.get(), new TranslationTextComponent("container.enchant"), 3);
    }

    @Override
    public void tick() {
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        this.pageAngle = this.nextPageAngle;
        PlayerEntity player = this.world.getClosestPlayer((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D, 3.0D, false);

        if(player != null) {
            double posX = player.getPosX() - ((double) this.pos.getX() + 0.5D);
            double posZ = player.getPosZ() - ((double) this.pos.getZ() + 0.5D);
            this.angle = (float) MathHelper.atan2(posZ, posX);
            this.nextPageTurningSpeed += 0.1F;

            if(this.nextPageTurningSpeed < 0.5F || this.random.nextInt(40) == 0) {
                float temp = this.randomPageFlipAmount;

                do {
                    this.randomPageFlipAmount += (float) (this.random.nextInt(4) - this.random.nextInt(4));

                }
                while(temp == this.randomPageFlipAmount);
            }
        }
        else {
            this.angle += 0.02F;
            this.nextPageTurningSpeed -= 0.1F;
        }

        while(this.nextPageAngle >= (float) Math.PI) {
            this.nextPageAngle -= ((float) Math.PI * 2F);
        }

        while(this.nextPageAngle < -(float) Math.PI) {
            this.nextPageAngle += ((float) Math.PI * 2F);
        }

        while(this.angle >= (float) Math.PI) {
            this.angle -= ((float) Math.PI * 2F);
        }

        while(this.angle < -(float) Math.PI) {
            this.angle += ((float) Math.PI * 2F);
        }

        float angle = this.angle - this.nextPageAngle;

        while(angle >= (float) Math.PI) {
            angle -= ((float) Math.PI * 2F);
        }

        while(angle < -(float) Math.PI) {
            angle += ((float) Math.PI * 2F);
        }

        this.nextPageAngle += angle * 0.4F;
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0F, 1.0F);
        this.ticks++;
        this.pageFlipAmount = this.nextPageFlipAmount;
        float flipAmount = (this.randomPageFlipAmount - this.nextPageFlipAmount) * 0.4F;
        flipAmount = MathHelper.clamp(flipAmount, -0.2F, 0.2F);
        this.adjustedFlipAmount += (flipAmount - this.adjustedFlipAmount) * 0.9F;
        this.nextPageFlipAmount += this.adjustedFlipAmount;
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ReagentEnchantingTableContainer(windowId, playerInventory, IWorldPosCallable.of(this.world, this.pos), this.itemStackHandler);
    }
}
