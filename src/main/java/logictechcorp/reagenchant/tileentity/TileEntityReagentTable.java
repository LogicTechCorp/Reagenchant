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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Random;

public class TileEntityReagentTable extends TileEntityInventory implements ITickable
{
    private int tickCounter;
    private float pageFlip;
    private float pageFlipPrev;
    private float flipRandom;
    private float flipTurn;
    private float bookSpread;
    private float bookSpreadPrev;
    private float bookRotation;
    private float bookRotationPrev;
    private float offsetRotation;
    private String customName;
    private static final Random rand = new Random();

    public TileEntityReagentTable()
    {
        super(3);
    }

    @Override
    public boolean acceptsItemStack(ItemStack stack)
    {
        return true;
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
            this.offsetRotation = (float) MathHelper.atan2(posZ, posX);
            this.bookSpread += 0.1F;

            if(this.bookSpread < 0.5F || rand.nextInt(40) == 0)
            {
                float randomFlip = this.flipRandom;

                while(true)
                {
                    this.flipRandom += (float) (rand.nextInt(4) - rand.nextInt(4));

                    if(randomFlip != this.flipRandom)
                    {
                        break;
                    }
                }
            }
        }
        else
        {
            this.offsetRotation += 0.02F;
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

        while(this.offsetRotation >= (float) Math.PI)
        {
            this.offsetRotation -= ((float) Math.PI * 2F);
        }

        while(this.offsetRotation < -(float) Math.PI)
        {
            this.offsetRotation += ((float) Math.PI * 2F);
        }

        float rotation;

        for(rotation = this.offsetRotation - this.bookRotation; rotation >= (float) Math.PI; rotation -= ((float) Math.PI * 2F))
        {
        }

        while(rotation < -(float) Math.PI)
        {
            rotation += ((float) Math.PI * 2F);
        }

        this.bookRotation += rotation * 0.4F;
        this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);
        this.tickCounter++;
        this.pageFlipPrev = this.pageFlip;
        float flip = (this.flipRandom - this.pageFlip) * 0.4F;
        flip = MathHelper.clamp(flip, -0.2F, 0.2F);
        this.flipTurn += (flip - this.flipTurn) * 0.9F;
        this.pageFlip += this.flipTurn;
    }

    private boolean hasCustomName()
    {
        return this.customName != null && !this.customName.isEmpty();
    }

    private String getName()
    {
        return this.hasCustomName() ? this.customName : "container.enchant";
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return (this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName()));
    }

    public int getTickCounter()
    {
        return this.tickCounter;
    }

    public float getPageFlip()
    {
        return this.pageFlip;
    }

    public float getPageFlipPrev()
    {
        return this.pageFlipPrev;
    }

    public float getBookSpread()
    {
        return this.bookSpread;
    }

    public float getBookSpreadPrev()
    {
        return this.bookSpreadPrev;
    }

    public float getBookRotation()
    {
        return this.bookRotation;
    }

    public float getBookRotationPrev()
    {
        return this.bookRotationPrev;
    }

    public void setCustomName(String customName)
    {
        this.customName = customName;
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
