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

package logictechcorp.reagenchant.api.reagent;

import com.electronwill.nightconfig.core.Config;
import logictechcorp.libraryex.config.ModJsonConfigFormat;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The base class for a Reagent that can be configured from json.
 */
public class ReagentConfigurable extends Reagent implements IReagentConfigurable
{
    public ReagentConfigurable(ResourceLocation name, Item associatedItem)
    {
        super(name, associatedItem);
    }

    @Override
    public void readFromConfig(Config config)
    {
        this.enchantments.clear();
        List<Config> enchantmentConfigs = config.getOrElse("enchantments", new ArrayList<>());

        for(Config enchantmentConfig : enchantmentConfigs)
        {
            Enchantment enchantment = Enchantment.getEnchantmentByLocation(enchantmentConfig.getOrElse("enchantment", ""));

            if(enchantment != null)
            {
                double probability = enchantmentConfig.getOrElse("probability", 0.5D);
                int reagentCost = enchantmentConfig.getOrElse("reagentCost", 1);

                if(probability <= 0.0D)
                {
                    probability = 0.5D;
                }
                if(reagentCost < 0)
                {
                    reagentCost = 1;
                }

                this.enchantments.put(enchantment.getRegistryName(), new Tuple<>(probability, reagentCost));
            }
        }
    }

    @Override
    public void writeToConfig(Config config)
    {
        List<Config> enchantmentConfigs = new ArrayList<>();
        config.set("name", this.name.toString());
        config.set("associatedItem", this.associatedItem.getRegistryName().toString());

        for(Map.Entry<ResourceLocation, Tuple<Double, Integer>> entry : this.enchantments.entrySet())
        {
            Tuple<Double, Integer> data = entry.getValue();

            Config enchantmentConfig = ModJsonConfigFormat.newConfig();
            enchantmentConfig.add("enchantment", entry.getKey().toString());
            enchantmentConfig.add("probability", data.getFirst());
            enchantmentConfig.add("reagentCost", data.getSecond());
            enchantmentConfigs.add(enchantmentConfig);
        }

        config.set("enchantments", enchantmentConfigs);
    }

    @Override
    public String getRelativeSaveFile()
    {
        return "config/reagenchant/reagents/" + this.name.toString().replace(":", "/") + ".json";
    }
}
