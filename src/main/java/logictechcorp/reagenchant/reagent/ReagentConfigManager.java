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
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.api.internal.IReagentRegistry;
import logictechcorp.reagenchant.api.reagent.IReagent;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public final class ReagentConfigManager
{
    private ReagentConfigManager()
    {
    }

    public static void readReagentConfigs()
    {
        Reagenchant.LOGGER.info("Reading Reagent configs from disk.");
        Path path = new File(LibraryEx.CONFIG_DIRECTORY, Reagenchant.MOD_ID + "/reagents").toPath();

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
                        IReagentRegistry reagentRegistry = ReagenchantAPI.getInstance().getReagentRegistry();
                        IReagent reagent;

                        if(reagentRegistry.hasReagent(item))
                        {
                            reagent = reagentRegistry.getReagent(item);
                        }
                        else
                        {
                            reagent = new Reagent(item);
                        }

                        reagent.readFromConfig(config);
                        reagentRegistry.registerReagent(reagent);
                    }

                    config.save();
                    config.close();
                }
                else if(!configFile.isDirectory())
                {
                    Reagenchant.LOGGER.info("Skipping file located at, {}, as it is not a json file.", configFile.getPath());
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void writeReagentConfigs()
    {
        Reagenchant.LOGGER.info("Writing Reagent configs to disk.");
        IReagentRegistry reagentRegistry = ReagenchantAPI.getInstance().getReagentRegistry();

        for(IReagent reagent : reagentRegistry.getReagents().values())
        {
            File configFile = new File(LibraryEx.CONFIG_DIRECTORY, Reagenchant.MOD_ID + "/reagents/" + reagent.getItem().getRegistryName().toString().replace(":", "/") + ".json");
            FileConfig config = FileConfig.builder(configFile, JsonFormat.fancyInstance()).preserveInsertionOrder().build();

            if(!configFile.exists())
            {
                try
                {
                    Files.createDirectories(configFile.getParentFile().toPath());
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                config.load();
            }

            reagent.writeToConfig(config);
            config.save();
            config.close();
        }
    }
}
