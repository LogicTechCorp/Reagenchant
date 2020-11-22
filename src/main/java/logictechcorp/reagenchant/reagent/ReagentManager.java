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
import logictechcorp.libraryex.LibraryEx;
import logictechcorp.libraryex.utility.FileHelper;
import logictechcorp.libraryex.utility.WorldHelper;
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.ReagenchantConfig;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class ReagentManager
{
    private final Logger logger;
    private final Map<ResourceLocation, Reagent> defaultReagents;
    private final Map<ResourceLocation, Reagent> currentReagents;

    public ReagentManager(String modName)
    {
        this.logger = LogManager.getLogger(modName);
        this.defaultReagents = new HashMap<>();
        this.currentReagents = new HashMap<>();
    }

    public void setup()
    {
        this.defaultReagents.putAll(this.currentReagents);
        this.currentReagents.clear();

        if(ReagenchantConfig.reagent.general.useGlobalReagentConfigs)
        {
            Path globalReagentConfigDirectoryPath = LibraryEx.CONFIG_DIRECTORY.toPath().resolve(Reagenchant.MOD_ID).resolve("reagents");
            this.createReagentConfigs(globalReagentConfigDirectoryPath);
        }
    }

    public void registerReagent(Reagent reagent)
    {
        if(reagent != null)
        {
            Item item = reagent.getItem();

            if(item != Items.AIR)
            {
                this.currentReagents.put(item.getRegistryName(), reagent);
            }
        }
    }

    public void unregisterReagent(Item item)
    {
        this.currentReagents.remove(item.getRegistryName());
    }

    public void onWorldLoad(WorldEvent.Load event)
    {
        World world = event.getWorld();

        if(!world.isRemote)
        {
            if(world.provider.getDimension() == DimensionType.OVERWORLD.getId())
            {
                if(ReagenchantConfig.reagent.general.useGlobalReagentConfigs)
                {
                    Path globalReagentConfigDirectoryPath = LibraryEx.CONFIG_DIRECTORY.toPath().resolve(Reagenchant.MOD_ID).resolve("reagents");
                    this.readReagentConfigs(globalReagentConfigDirectoryPath);
                }

                if(ReagenchantConfig.reagent.general.usePerWorldReagentConfigs)
                {
                    Path perWorldReagentConfigDirectoryPath = Paths.get(WorldHelper.getSaveDirectory(event.getWorld()), "config", Reagenchant.MOD_ID, "reagents");
                    this.createReagentConfigs(perWorldReagentConfigDirectoryPath);
                    this.readReagentConfigs(perWorldReagentConfigDirectoryPath);
                }
            }
        }
    }

    public void onWorldUnload(WorldEvent.Unload event)
    {
        World world = event.getWorld();

        if(!world.isRemote)
        {
            if(world.provider.getDimension() == DimensionType.OVERWORLD.getId())
            {
                this.currentReagents.clear();
            }
        }
    }

    public void syncClientReagents(Collection<Reagent> reagents)
    {
        this.currentReagents.clear();

        for(Reagent reagent : reagents)
        {
            this.currentReagents.put(reagent.getItem().getRegistryName(), reagent);
        }
    }

    public void readReagentConfigs(Path reagentConfigDirectoryPath)
    {
        if(Files.isReadable(reagentConfigDirectoryPath))
        {

            this.logger.info("Reading reagent configs.");

            try
            {
                Files.createDirectories(reagentConfigDirectoryPath);
                Iterator<Path> pathIter = Files.walk(reagentConfigDirectoryPath).iterator();

                while(pathIter.hasNext())
                {
                    File configFile = pathIter.next().toFile();

                    if(FileHelper.getFileExtension(configFile).equals("json"))
                    {
                        FileConfig config = FileConfig.builder(configFile, JsonFormat.fancyInstance()).preserveInsertionOrder().build();
                        config.load();

                        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(config.get("item")));

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
                        this.logger.info("Skipping file located at, {}, since it is not a json file.", configFile.getPath());
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            this.logger.warn("Unable to read reagent configs.");
        }
    }

    public void createReagentConfigs(Path reagentConfigDirectoryPath)
    {
        this.logger.info("Creating reagent configs.");

        try
        {
            for(Reagent reagent : this.defaultReagents.values())
            {
                ResourceLocation itemRegistryName = reagent.getItem().getRegistryName();
                File configFile = new File(reagentConfigDirectoryPath.toFile(), itemRegistryName.toString().replace(":", "/") + ".json");

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
        return this.currentReagents.containsKey(item.getRegistryName());
    }

    public Reagent getReagent(Item item)
    {
        return this.currentReagents.getOrDefault(item.getRegistryName(), Reagent.EMPTY);
    }

    public Map<ResourceLocation, Reagent> getDefaultReagents()
    {
        return Collections.unmodifiableMap(this.defaultReagents);
    }

    public Map<ResourceLocation, Reagent> getCurrentReagents()
    {
        return Collections.unmodifiableMap(this.currentReagents);
    }
}
