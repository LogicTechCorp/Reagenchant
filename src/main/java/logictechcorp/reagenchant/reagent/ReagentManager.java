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
import net.minecraft.client.resources.JsonReloadListener;
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
import java.util.*;

public class ReagentManager extends JsonReloadListener
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Map<ResourceLocation, Reagent> reagents;

    public ReagentManager(String folderName)
    {
        super(GSON, folderName);
        this.reagents = new HashMap<>();
    }

    public ReagentManager()
    {
        this("reagents");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonObject> locations, IResourceManager resourceManager, IProfiler profiler)
    {
        locations.forEach((resourceLocation, object) ->
        {
            try
            {
                IResource resource = resourceManager.getResource(this.getPreparedPath(resourceLocation));
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
                        List<ReagentEnchantData> enchantments = dynamic.get("enchantments").asList(ReagentEnchantData::deserialize);

                        Reagent reagent = this.createReagent(item);

                        for(ReagentEnchantData enchantmentData : enchantments)
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

    public void cleanup()
    {
        this.reagents.clear();
    }

    public void syncClientReagents(Collection<Reagent> reagents)
    {
        this.cleanup();

        for(Reagent reagent : reagents)
        {
            this.reagents.put(reagent.getItem().getRegistryName(), reagent);
        }
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
