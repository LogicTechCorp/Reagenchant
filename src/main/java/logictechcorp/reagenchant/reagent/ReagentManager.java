/*
 * Reagenchant
 * Copyright (c) 2019-2020 by LogicTechCorp
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import logictechcorp.reagenchant.Reagenchant;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReagentManager extends ReloadListener<Map<ResourceLocation, JsonObject>>
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String folderName;
    private final Map<ResourceLocation, Reagent> reagents;

    public ReagentManager()
    {
        this("reagents");
    }

    public ReagentManager(String folderName)
    {
        this.folderName = folderName;
        this.reagents = new HashMap<>();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> locations, IResourceManager resourceManager, IProfiler profiler)
    {
        locations.forEach((resourceLocation, object) ->
        {
            try
            {
                if(!resourceLocation.getPath().startsWith(this.folderName))
                {
                    resourceLocation = new ResourceLocation(resourceLocation.getNamespace(), this.folderName + "/" + resourceLocation.getPath() + ".json");
                }

                IResource resource = resourceManager.getResource(resourceLocation);
                InputStream inputStream = resource.getInputStream();
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, JSONUtils.fromJson(GSON, reader, JsonObject.class));
                IOUtils.closeQuietly(reader);
                IOUtils.closeQuietly(resource);

                if(dynamic.getValue() == null)
                {
                    Reagenchant.LOGGER.error("Couldn't load {} reagent config from {} data pack.", resource.getLocation(), resource.getPackName());
                }
                else
                {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(dynamic.get("item").asString("")));

                    if(item != null && item != Items.AIR)
                    {
                        List<ReagentEnchantmentData> enchantments = dynamic.get("enchantments").asList(this::deserializeReagentEnchantmentData);

                        Reagent reagent = this.createReagent(item);

                        for(ReagentEnchantmentData enchantmentData : enchantments)
                        {
                            if(!enchantmentData.isEmpty())
                            {
                                reagent.addEnchantment(enchantmentData);
                            }
                        }

                        this.reagents.put(item.getRegistryName(), reagent);
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected Map<ResourceLocation, JsonObject> prepare(IResourceManager resourceManager, IProfiler profiler)
    {
        Map<ResourceLocation, JsonObject> map = new HashMap<>();

        for(ResourceLocation resource : resourceManager.getAllResourceLocations(this.folderName, (fileName) -> fileName.endsWith(".json")))
        {
            String path = resource.getPath();
            ResourceLocation truncatedResource;

            if(!path.startsWith(this.folderName))
            {
                truncatedResource = resource;
            }
            else
            {
                truncatedResource = new ResourceLocation(resource.getNamespace(), path.substring(this.folderName.length() + 1, path.lastIndexOf(".")));
            }

            try(Reader reader = new BufferedReader(new InputStreamReader(resourceManager.getResource(resource).getInputStream(), StandardCharsets.UTF_8)))
            {
                JsonObject jsonObject = JSONUtils.fromJson(GSON, reader, JsonObject.class);

                if(jsonObject != null)
                {
                    if(map.put(truncatedResource, jsonObject) != null)
                    {
                        Reagenchant.LOGGER.error("Duplicate data file: {}", truncatedResource);
                    }
                }
                else
                {
                    Reagenchant.LOGGER.error("Invalid data file: {}", truncatedResource);
                }
            }
            catch(IOException e)
            {
                Reagenchant.LOGGER.error("Unreadable data file: {}", truncatedResource);
            }
        }

        return map;
    }

    public Reagent createReagent(Item item)
    {
        return new Reagent(item);
    }

    public void registerReagent(Reagent reagent)
    {
        if(reagent.getItem() != Items.AIR)
        {
            this.reagents.put(reagent.getItem().getRegistryName(), reagent);
        }
    }

    public void unregisterReagent(Reagent reagent)
    {
        if(reagent.getItem() != Items.AIR)
        {
            this.reagents.remove(reagent.getItem().getRegistryName());
        }
    }

    protected <T> ReagentEnchantmentData deserializeReagentEnchantmentData(Dynamic<T> dynamic)
    {
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(dynamic.get("enchantment").asString("null")));

        if(enchantment != null)
        {
            double probability = dynamic.get("probability").asDouble(0.5D);
            if(probability <= 0.0D)
            {
                probability = 0.5D;
            }

            int minimumEnchantmentLevel = dynamic.get("minimumEnchantmentLevel").asInt(enchantment.getMinLevel());
            int maximumEnchantmentLevel = dynamic.get("maximumEnchantmentLevel").asInt(enchantment.getMaxLevel());

            if(minimumEnchantmentLevel < 1)
            {
                minimumEnchantmentLevel = 1;
            }
            if(maximumEnchantmentLevel > 100)
            {
                maximumEnchantmentLevel = 100;
            }

            int reagentCost = dynamic.get("reagentCost").asInt(1);

            if(reagentCost < 0)
            {
                reagentCost = 1;
            }

            return new ReagentEnchantmentData(enchantment, minimumEnchantmentLevel, maximumEnchantmentLevel, probability, reagentCost);
        }

        return ReagentEnchantmentData.EMPTY;
    }

    public void cleanup()
    {
        this.reagents.clear();
    }

    public boolean isReagent(Item item)
    {
        return this.reagents.containsKey(item.getRegistryName());
    }

    public Reagent getReagent(Item item)
    {
        return this.reagents.getOrDefault(item.getRegistryName(), Reagent.EMPTY);
    }

    public Map<ResourceLocation, Reagent> getReagents()
    {
        return Collections.unmodifiableMap(this.reagents);
    }
}
