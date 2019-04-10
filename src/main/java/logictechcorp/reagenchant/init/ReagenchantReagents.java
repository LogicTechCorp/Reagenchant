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

package logictechcorp.reagenchant.init;

import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.api.reagent.IReagent;
import logictechcorp.reagenchant.reagent.Reagent;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;

public class ReagenchantReagents
{
    public static final IReagent FEATHER = new Reagent(Items.FEATHER, Reagenchant.getResource("feather"));
    public static final IReagent IRON_INGOT = new Reagent(Items.IRON_INGOT, Reagenchant.getResource("iron_ingot"));
    public static final IReagent REDSTONE = new Reagent(Items.REDSTONE, Reagenchant.getResource("redstone"));
    public static final IReagent DIAMOND = new Reagent(Items.DIAMOND, Reagenchant.getResource("diamond"));
    public static final IReagent PRISMARINE_CRYSTALS = new Reagent(Items.PRISMARINE_CRYSTALS, Reagenchant.getResource("prismarine_crystals"));
    public static final IReagent GLOWSTONE_DUST = new Reagent(Items.GLOWSTONE_DUST, Reagenchant.getResource("glowstone_dust"));
    public static final IReagent BLAZE_POWDER = new Reagent(Items.BLAZE_POWDER, Reagenchant.getResource("blaze_powder"));
    public static final IReagent SHULKER_SHELL = new Reagent(Items.SHULKER_SHELL, Reagenchant.getResource("shulker_shell"));

    public static void initReagents()
    {
        FEATHER.addEnchantment(Enchantments.FEATHER_FALLING, 0.5F, 1);
        ReagenchantAPI.getInstance().registerReagent(FEATHER);

        IRON_INGOT.addEnchantment(Enchantments.KNOCKBACK, 0.5F, 1);
        IRON_INGOT.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 0.5F, 1);
        IRON_INGOT.addEnchantment(Enchantments.PUNCH, 0.5F, 1);
        IRON_INGOT.addEnchantment(Enchantments.SWEEPING, 0.5F, 1);
        ReagenchantAPI.getInstance().registerReagent(IRON_INGOT);

        REDSTONE.addEnchantment(Enchantments.POWER, 0.5F, 1);
        REDSTONE.addEnchantment(Enchantments.SHARPNESS, 0.5F, 1);
        REDSTONE.addEnchantment(Enchantments.EFFICIENCY, 0.5F, 1);
        ReagenchantAPI.getInstance().registerReagent(REDSTONE);

        DIAMOND.addEnchantment(Enchantments.FORTUNE, 0.5F, 1);
        DIAMOND.addEnchantment(Enchantments.LOOTING, 0.5F, 1);
        DIAMOND.addEnchantment(Enchantments.INFINITY, 0.5F, 1);
        ReagenchantAPI.getInstance().registerReagent(DIAMOND);

        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.AQUA_AFFINITY, 0.5F, 1);
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.DEPTH_STRIDER, 0.5F, 1);
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.LUCK_OF_THE_SEA, 0.5F, 1);
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.LURE, 0.5F, 1);
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.RESPIRATION, 0.5F, 1);
        ReagenchantAPI.getInstance().registerReagent(PRISMARINE_CRYSTALS);

        GLOWSTONE_DUST.addEnchantment(Enchantments.BANE_OF_ARTHROPODS, 0.5F, 1);
        GLOWSTONE_DUST.addEnchantment(Enchantments.SMITE, 0.5F, 1);
        ReagenchantAPI.getInstance().registerReagent(GLOWSTONE_DUST);

        BLAZE_POWDER.addEnchantment(Enchantments.FIRE_ASPECT, 0.5F, 1);
        BLAZE_POWDER.addEnchantment(Enchantments.FIRE_PROTECTION, 0.5F, 1);
        BLAZE_POWDER.addEnchantment(Enchantments.FLAME, 0.5F, 1);
        ReagenchantAPI.getInstance().registerReagent(BLAZE_POWDER);

        SHULKER_SHELL.addEnchantment(Enchantments.BLAST_PROTECTION, 0.5F, 1);
        SHULKER_SHELL.addEnchantment(Enchantments.PROTECTION, 0.5F, 1);
        SHULKER_SHELL.addEnchantment(Enchantments.UNBREAKING, 0.5F, 1);
        SHULKER_SHELL.addEnchantment(Enchantments.SILK_TOUCH, 0.5F, 1);
        ReagenchantAPI.getInstance().registerReagent(SHULKER_SHELL);

    }
}
