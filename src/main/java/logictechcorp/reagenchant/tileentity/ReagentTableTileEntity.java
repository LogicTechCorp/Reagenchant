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
import logictechcorp.reagenchant.init.ReagenchantTileEntityTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ReagentTableTileEntity extends TileEntityInventory implements ITickableTileEntity, INameable
{
    private PlayerEntity user;
    private ITextComponent customName;
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

    public ReagentTableTileEntity()
    {
        super(ReagenchantTileEntityTypes.REAGENT_TABLE_TILE_ENTITY, 3);
    }

    @Override
    public boolean acceptsItemStack(ItemStack stack)
    {
        return true;
    }

    @Override
    public void tick()
    {
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;

        PlayerEntity playerToFace;

        if(this.user != null)
        {
            playerToFace = this.user;
        }
        else
        {
            playerToFace = this.world.getClosestPlayer((double) ((float) this.pos.getX() + 0.5F), (double) ((float) this.pos.getY() + 0.5F), (double) ((float) this.pos.getZ() + 0.5F), 3.0D, null);
        }

        if(playerToFace != null)
        {
            double posX = playerToFace.posX - (double) ((float) this.pos.getX() + 0.5F);
            double posZ = playerToFace.posZ - (double) ((float) this.pos.getZ() + 0.5F);
            this.offsetRotation = (float) MathHelper.atan2(posZ, posX);
            this.bookSpread += 0.1F;

            if(this.bookSpread < 0.5F || this.random.nextInt(40) == 0)
            {
                float randomFlip = this.flipRandom;

                while(true)
                {
                    this.flipRandom += (float) (this.random.nextInt(4) - this.random.nextInt(4));

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

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);
        if(this.hasCustomName())
        {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        return compound;
    }

    @Override
    public void read(CompoundNBT compound)
    {
        super.read(compound);
        if(compound.contains("CustomName", 8))
        {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
    }

    @Override
    public ITextComponent getCustomName()
    {
        return this.customName;
    }

    @Override
    public ITextComponent getName()
    {
        return (this.customName != null ? this.customName : new TranslationTextComponent("container.enchant"));
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

    public void setCustomName(ITextComponent customName)
    {
        this.customName = customName;
    }
}
