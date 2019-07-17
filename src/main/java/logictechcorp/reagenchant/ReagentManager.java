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

package logictechcorp.reagenchant;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.json.JsonFormat;
import logictechcorp.libraryex.utility.FileHelper;
import logictechcorp.libraryex.utility.WorldHelper;
import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.api.internal.iface.IReagentManager;
import logictechcorp.reagenchant.api.internal.iface.IReagentRegistry;
import logictechcorp.reagenchant.api.reagent.IReagent;
import logictechcorp.reagenchant.api.reagent.IReagentConfigurable;
import logictechcorp.reagenchant.reagent.ReagentConfigurable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

final class ReagentManager implements IReagentManager
{
    static final IReagentManager INSTANCE = new ReagentManager();
    private final Marker marker = MarkerManager.getMarker("ReagentManager");

    private ReagentManager()
    {
    }

    @Override
    public void readReagentConfigs(WorldEvent.Load event)
    {
        World world = event.getWorld();

        if(world.provider.getDimension() == DimensionType.OVERWORLD.getId())
        {
            Reagenchant.LOGGER.info(this.marker, "Reading Reagent configs from disk.");
            Path path = new File(WorldHelper.getSaveDirectory(event.getWorld()), "/config/reagenchant/reagents").toPath();

            try
            {
                Files.createDirectories(path);
                Iterator<Path> pathIter = Files.walk(path).iterator();

                while(pathIter.hasNext())
                {
                    File configFile = pathIter.next().toFile();

                    if(FileHelper.getFileExtension(configFile).equals("json"))
                    {
                        FileConfig config = FileConfig.of(configFile, JsonFormat.fancyInstance());
                        config.load();

                        Item associatedItem = Item.getByNameOrId(config.getOrElse("associatedItem", "minecraft:air"));

                        if(associatedItem != null && associatedItem != Items.AIR)
                        {
                            IReagentRegistry reagentRegistry = ReagenchantAPI.getInstance().getReagentRegistry();
                            IReagent reagent;

                            if(reagentRegistry.isReagentItem(associatedItem))
                            {
                                reagent = reagentRegistry.getReagent(associatedItem);

                                if(!(reagent instanceof IReagentConfigurable))
                                {
                                    continue;
                                }

                                ((IReagentConfigurable) reagent).readFromConfig(config);
                            }
                            else
                            {
                                reagent = new ReagentConfigurable(new ResourceLocation(config.getOrElse("name", "missing:no")), associatedItem);
                                ((IReagentConfigurable) reagent).readFromConfig(config);
                                reagentRegistry.registerReagent(reagent);
                            }
                        }

                        config.close();
                    }
                    else if(!configFile.isDirectory())
                    {
                        Reagenchant.LOGGER.info(this.marker, "Skipping file located at, {}, as it is not a json file.", configFile.getPath());
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeReagentConfigs(WorldEvent.Unload event)
    {
        World world = event.getWorld();

        if(world.provider.getDimension() == DimensionType.OVERWORLD.getId())
        {
            Reagenchant.LOGGER.info(this.marker, "Writing Reagent configs to disk.");

            for(IReagent reagent : ReagenchantAPI.getInstance().getReagentRegistry().getReagents().values())
            {
                if(!(reagent instanceof IReagentConfigurable))
                {
                    continue;
                }

                IReagentConfigurable reagentConfigurable = (IReagentConfigurable) reagent;
                File configFile = new File(WorldHelper.getSaveDirectory(event.getWorld()), reagentConfigurable.getRelativeSaveFile());
                FileConfig fileConfig = FileConfig.of(configFile, JsonFormat.fancyInstance());

                if(!configFile.getParentFile().mkdirs() && configFile.exists() || configFile.exists())
                {
                    fileConfig.load();
                    reagentConfigurable.readFromConfig(fileConfig);
                }

                reagentConfigurable.writeToConfig(fileConfig);
                fileConfig.save();
                fileConfig.close();
            }
        }
    }
}
