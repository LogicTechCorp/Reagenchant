/*
 * Reagenchant
 * Copyright (c) 2019-2021 by LogicTechCorp
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

package logictechcorp.reagenchant.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class ReagenchantConfig {

    public static final Common COMMON;
    private static final ForgeConfigSpec COMMON_SPEC;

    static {
        Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonSpecPair.getLeft();
        COMMON_SPEC = commonSpecPair.getRight();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ReagenchantConfig.COMMON_SPEC);
    }

    public static class Common {
        public final ForgeConfigSpec.ConfigValue<Boolean> unbreakableItems;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Reagenchant common configuration")
                    .push("common");
            builder.push("tweaks");

            this.unbreakableItems = builder
                    .comment("If items with Unbreakable should not break; Default: true")
                    .translation(createTranslation("common.tweaks.unbreakable_items"))
                    .define("unbreakableItems", true);
            builder.pop();
            builder.pop();
        }
    }

    private static String createTranslation(String path) {
        return "config." + Reagenchant.MOD_ID + "." + path;
    }
}
