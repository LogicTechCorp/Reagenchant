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

package logictechcorp.reagenchant.reagent;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.json.JsonFormat;
import logictechcorp.libraryex.utility.FileHelper;
import logictechcorp.libraryex.utility.WorldHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ReagentManager
{
    private final String modId;
    private final Logger logger;
    private final Map<ResourceLocation, Reagent> defaultReagents;
    private final Map<ResourceLocation, Reagent> worldSpecificReagents;

    public ReagentManager(String modId, String modName)
    {
        this.modId = modId;
        this.logger = LogManager.getLogger(modName);
        this.defaultReagents = new HashMap<>();
        this.worldSpecificReagents = new HashMap<>();
    }

    public void setup()
    {
        this.worldSpecificReagents.forEach(this.defaultReagents::put);
    }

    public void registerReagent(Reagent reagent)
    {
        if(reagent != null)
        {
            Item item = reagent.getItem();
            ResourceLocation itemRegistryName = item.getRegistryName();

            if(!this.worldSpecificReagents.containsKey(itemRegistryName) && item != Items.AIR)
            {
                this.worldSpecificReagents.put(item.getRegistryName(), reagent);
            }
        }
    }

    public void unregisterReagent(Item item)
    {
        this.worldSpecificReagents.remove(item.getRegistryName());
    }

    public void cleanup()
    {
        this.worldSpecificReagents.clear();
    }

    public void readReagentConfigs(WorldEvent.Load event)
    {
        this.logger.info("Reading Reagent configs.");
        Path path = new File(WorldHelper.getSaveDirectory(event.getWorld()), "/config/" + this.modId + "/reagents/").toPath();

        try
        {
            Files.createDirectories(path);
            Iterator<Path> pathIter = Files.walk(path).iterator();

            while(pathIter.hasNext())
            {
                File configFile = pathIter.next().toFile();

                if(FileHelper.getFileExtension(configFile).equals("json"))
                {
                    FileConfig config = FileConfig.builder(configFile, JsonFormat.fancyInstance()).preserveInsertionOrder().build();
                    config.load();

                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(config.getOrElse("item", "missing:no")));

                    if(item != null && item != Items.AIR)
                    {
                        Reagent reagent;

                        if(this.hasReagent(item))
                        {
                            reagent = this.getReagent(item);
                        }
                        else
                        {
                            reagent = new Reagent(item);
                        }

                        reagent.readFromConfig(config);
                        this.registerReagent(reagent);
                    }

                    config.save();
                    config.close();
                }
                else if(!configFile.isDirectory())
                {
                    this.logger.info("Skipping file located at, {}, as it is not a json file.", configFile.getPath());
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void createReagentConfigs(WorldEvent.Load event)
    {
        this.logger.info("Creating Reagent configs.");

        try
        {
            for(Reagent reagent : this.getDefaultReagents().values())
            {
                File configFile = new File(WorldHelper.getSaveDirectory(event.getWorld()), "config/" + this.modId + "/reagents/" + reagent.getItem().getRegistryName().toString().replace(":", "/") + ".json");

                if(!configFile.exists())
                {
                    Files.createDirectories(configFile.getParentFile().toPath());
                    FileConfig config = FileConfig.builder(configFile, JsonFormat.fancyInstance()).preserveInsertionOrder().build();
                    reagent.writeToConfig(config);
                    config.save();
                    config.close();
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public boolean hasReagent(Item item)
    {
        return this.worldSpecificReagents.containsKey(item.getRegistryName());
    }

    public Reagent getReagent(Item item)
    {
        return this.worldSpecificReagents.get(item.getRegistryName());
    }

    public Map<ResourceLocation, Reagent> getDefaultReagents()
    {
        return Collections.unmodifiableMap(this.defaultReagents);
    }

    public Map<ResourceLocation, Reagent> getWorldSpecificReagents()
    {
        return Collections.unmodifiableMap(this.worldSpecificReagents);
    }
}
