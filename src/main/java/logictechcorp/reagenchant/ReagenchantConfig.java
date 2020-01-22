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

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReagenchantConfig
{
    private static final ForgeConfigSpec ENCHANTMENT_SPEC;
    public static final EnchantmentConfig ENCHANTMENT;

    static
    {
        Pair<EnchantmentConfig, ForgeConfigSpec> enchantmentSpecPair = new ForgeConfigSpec.Builder().configure(EnchantmentConfig::new);
        ENCHANTMENT_SPEC = enchantmentSpecPair.getRight();
        ENCHANTMENT = enchantmentSpecPair.getLeft();
    }

    static void registerConfigs()
    {
        try
        {
            Files.createDirectory(Paths.get(FMLPaths.CONFIGDIR.get().toAbsolutePath().toString(), "reagenchant"));
        }
        catch(IOException ignored)
        {
            Reagenchant.LOGGER.error("Failed to create reagenchant config directory.");
        }

        ModLoadingContext loadingContext = ModLoadingContext.get();
        loadingContext.registerConfig(ModConfig.Type.COMMON, ENCHANTMENT_SPEC, "reagenchant/enchantment-config.toml");

    }

    public static class EnchantmentConfig
    {
        //Unbreaking settings
        public final ForgeConfigSpec.BooleanValue unbreakingPreventsItemDestruction;

        EnchantmentConfig(ForgeConfigSpec.Builder builder)
        {
            //Start enchantment config
            builder.comment("Block configuration settings")
                    .push("block");

            //Unbreaking settings
            builder.comment("Unbreaking configuration settings")
                    .push("unbreaking");
            this.unbreakingPreventsItemDestruction = builder
                    .comment("Whether or not Unbreaking prevents item destruction.")
                    .define("preventsItemDestruction", true);
            builder.pop();

            //End enchantment config
            builder.pop();
        }
    }
}
