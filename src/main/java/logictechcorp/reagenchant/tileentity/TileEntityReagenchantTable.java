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

package logictechcorp.reagenchant.tileentity;

import logictechcorp.libraryex.tileentity.TileEntityInventory;
import logictechcorp.reagenchant.Reagenchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;

import java.util.Random;

public class TileEntityReagenchantTable extends TileEntityInventory implements ITickable, IInteractionObject
{
    private int tickCounter;
    private float pageFlip;
    private float pageFlipPrev;
    private float flipT;
    private float flipA;
    private float bookSpread;
    private float bookSpreadPrev;
    private float bookRotation;
    private float bookRotationPrev;
    private float tRot;
    private static final Random rand = new Random();
    private String customName;

    public TileEntityReagenchantTable()
    {
        super(3);
    }

    @Override
    public boolean acceptsItemStack(ItemStack stack)
    {
        return false;
    }

    @Override
    public void update()
    {
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        EntityPlayer player = this.world.getClosestPlayer((double) ((float) this.pos.getX() + 0.5F), (double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 3.0D, false);

        if(player != null)
        {
            double posX = player.posX - (double) ((float) this.pos.getX() + 0.5F);
            double posZ = player.posZ - (double) ((float) this.pos.getZ() + 0.5F);
            this.tRot = (float) MathHelper.atan2(posZ, posX);
            this.bookSpread += 0.1F;

            if(this.bookSpread < 0.5F || rand.nextInt(40) == 0)
            {
                float f1 = this.flipT;

                while(true)
                {
                    this.flipT += (float) (rand.nextInt(4) - rand.nextInt(4));

                    if(f1 != this.flipT)
                    {
                        break;
                    }
                }
            }
        }
        else
        {
            this.tRot += 0.02F;
            this.bookSpread -= 0.1F;
        }

        while(this.bookRotation >= (float) Math.PI)
        {
            this.bookRotation -= ((float) Math.PI * 2F);
        }

        while(this.bookRotation < -(float) Math.PI)
        {
            this.bookRotation += ((float) Math.PI * 2F);
        }

        while(this.tRot >= (float) Math.PI)
        {
            this.tRot -= ((float) Math.PI * 2F);
        }

        while(this.tRot < -(float) Math.PI)
        {
            this.tRot += ((float) Math.PI * 2F);
        }

        float f2;

        for(f2 = this.tRot - this.bookRotation; f2 >= (float) Math.PI; f2 -= ((float) Math.PI * 2F))
        {
        }

        while(f2 < -(float) Math.PI)
        {
            f2 += ((float) Math.PI * 2F);
        }

        this.bookRotation += f2 * 0.4F;
        this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
        this.tickCounter++;
        this.pageFlipPrev = this.pageFlip;
        float f = (this.flipT - this.pageFlip) * 0.4F;
        f = MathHelper.clamp(f, -0.2F, 0.2F);
        this.flipA += (f - this.flipA) * 0.9F;
        this.pageFlip += this.flipA;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer player)
    {
        return new ContainerEnchantment(playerInventory, this.world, this.pos);
    }

    @Override
    public boolean hasCustomName()
    {
        return this.customName != null && !this.customName.isEmpty();
    }

    @Override
    public String getGuiID()
    {
        return Reagenchant.MOD_ID + ":reagenchant_table";
    }

    @Override
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container." + Reagenchant.MOD_ID + ":reagenchant_table";
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return (this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName()));
    }

    public void setCustomName(String customNameIn)
    {
        this.customName = customNameIn;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        if(this.hasCustomName())
        {
            compound.setString("CustomName", this.customName);
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        if(compound.hasKey("CustomName", 8))
        {
            this.customName = compound.getString("CustomName");
        }
    }
}
