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
        public final ForgeConfigSpec.ConfigValue<Boolean> enableQuarkCompatibility;
        public final ForgeConfigSpec.ConfigValue<Boolean> unbreakableItems;
        public final ForgeConfigSpec.ConfigValue<Double> percentOfXpDroppedOnDeath;
        public final ForgeConfigSpec.ConfigValue<Double> percentOfDroppedXpLost;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Reagenchant common configuration")
                    .push("common");
            builder.push("compatibility");
            builder.push("quark");
            this.enableQuarkCompatibility = builder
                    .comment("If Quark compatibility should be enabled; Default: true")
                    .translation(createTranslation("common.compatibility.quark.enable_quark_compatibility"))
                    .define("enableQuarkCompatibility", true);
            builder.pop();
            builder.pop();
            builder.push("tweaks");
            builder.push("items");
            this.unbreakableItems = builder
                    .comment("If items with the Unbreakable enchantment should not break; Default: true")
                    .translation(createTranslation("common.tweaks.items.unbreakable_items"))
                    .define("unbreakableItems", true);
            builder.pop();
            builder.push("player");
            this.percentOfXpDroppedOnDeath = builder
                    .comment("The percent of experience which should drop on death; Default: 0.5")
                    .translation(createTranslation("common.tweaks.player.percent_of_xp_dropped_on_death"))
                    .defineInRange("percentOfXpDroppedOnDeath", 0.5, 0.0, 1.0, Double.class);
            this.percentOfDroppedXpLost = builder
                    .comment("The percent of experience dropped which should be lost; Default: 0.25")
                    .translation(createTranslation("common.tweaks.player.percent_of_dropped_xp_lost"))
                    .defineInRange("percentOfDroppedXpLost", 0.25, 0.0, 1.0, Double.class);
            builder.pop();
            builder.pop();
            builder.pop();
        }
    }

    private static String createTranslation(String path) {
        return "config." + Reagenchant.MOD_ID + "." + path;
    }
}
