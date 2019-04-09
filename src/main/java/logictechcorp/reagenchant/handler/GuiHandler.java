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

package logictechcorp.reagenchant.handler;

import logictechcorp.reagenchant.client.gui.GuiReagentTable;
import logictechcorp.reagenchant.inventory.ContainerReagentTable;
import logictechcorp.reagenchant.reagent.ReagentTableManager;
import logictechcorp.reagenchant.tileentity.TileEntityReagentTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;

public class GuiHandler implements IGuiHandler
{
    public static final int REAGENT_TABLE_ID = 0;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        return this.getGuiElement(id, player, world, x, y, z, Side.SERVER);
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        return this.getGuiElement(id, player, world, x, y, z, Side.CLIENT);
    }

    private Object getGuiElement(int id, EntityPlayer player, World world, int x, int y, int z, Side side)
    {
        BlockPos pos = new BlockPos(x, y, z);

        if(id == REAGENT_TABLE_ID)
        {
            TileEntity tileEntity = world.getTileEntity(pos);

            if(tileEntity instanceof TileEntityReagentTable)
            {
                ContainerReagentTable container = new ContainerReagentTable(new ReagentTableManager(world, pos, player, (TileEntityReagentTable) tileEntity));

                if(side.isClient())
                {
                    return new GuiReagentTable(container);
                }
                else
                {
                    return container;
                }
            }
        }

        return null;
    }
}
