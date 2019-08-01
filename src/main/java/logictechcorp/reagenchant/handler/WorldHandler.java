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

import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.reagent.ReagentConfigManager;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class WorldHandler
{
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event)
    {
        World world = event.getWorld();

        if(!world.isRemote)
        {
            if(world.provider.getDimension() == DimensionType.OVERWORLD.getId())
            {
                ReagentConfigManager.readReagentConfigs();
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event)
    {
        World world = event.getWorld();

        if(!world.isRemote)
        {
            if(world.provider.getDimension() == DimensionType.OVERWORLD.getId())
            {
                ReagentConfigManager.writeReagentConfigs();
            }
        }
    }
}
