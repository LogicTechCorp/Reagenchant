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

package logictechcorp.reagenchant;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Paths;

public class ReagenchantConfig
{
    private static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON;

    static
    {
        Pair<CommonConfig, ForgeConfigSpec> netherSpecPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = netherSpecPair.getRight();
        COMMON = netherSpecPair.getLeft();
    }

    static void registerConfigs()
    {
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve("reagenchant"), "reagenchant config");
        FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(Paths.get("reagenchant", "reagent_packs")), "reagenchant reagent packs");

        ModLoadingContext loadingContext = ModLoadingContext.get();
        loadingContext.registerConfig(ModConfig.Type.COMMON, COMMON_SPEC, "reagenchant/common-config.toml");
    }

    public static class CommonConfig
    {
        //Reagent pack settings
        public final ForgeConfigSpec.BooleanValue reagentPackUseGlobalReagentPacks;
        public final ForgeConfigSpec.BooleanValue reagentPackUseReagenchantReagentPack;

        CommonConfig(ForgeConfigSpec.Builder builder)
        {
            //Start common config
            builder.comment("Common configuration settings")
                    .push("common");

            //Start reagent pack config
            builder.comment("Reagent pack configuration settings")
                    .push("reagent_packs");
            this.reagentPackUseGlobalReagentPacks = builder
                    .comment("Use global reagent packs.")
                    .define("useGlobalReagentPacks", true);
            this.reagentPackUseReagenchantReagentPack = builder
                    .comment("Use the Reagenchant reagent pack.")
                    .define("useReagenchantReagentPack", true);
            builder.pop();

            //End common config
            builder.pop();
        }
    }
}
