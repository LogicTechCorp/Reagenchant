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
import logictechcorp.reagenchant.api.reagent.ReagentConfigurable;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;

public class ReagenchantReagents
{
    private static final IReagent FEATHER = new ReagentConfigurable(Reagenchant.getResource("feather"), Items.FEATHER);
    private static final IReagent IRON_INGOT = new ReagentConfigurable(Reagenchant.getResource("iron_ingot"), Items.IRON_INGOT);
    private static final IReagent REDSTONE = new ReagentConfigurable(Reagenchant.getResource("redstone"), Items.REDSTONE);
    private static final IReagent DIAMOND = new ReagentConfigurable(Reagenchant.getResource("diamond"), Items.DIAMOND);
    private static final IReagent PRISMARINE_CRYSTALS = new ReagentConfigurable(Reagenchant.getResource("prismarine_crystals"), Items.PRISMARINE_CRYSTALS);
    private static final IReagent GLOWSTONE_DUST = new ReagentConfigurable(Reagenchant.getResource("glowstone_dust"), Items.GLOWSTONE_DUST);
    private static final IReagent BLAZE_POWDER = new ReagentConfigurable(Reagenchant.getResource("blaze_powder"), Items.BLAZE_POWDER);
    private static final IReagent SHULKER_SHELL = new ReagentConfigurable(Reagenchant.getResource("shulker_shell"), Items.SHULKER_SHELL);

    public static void initReagents()
    {
        FEATHER.addEnchantment(Enchantments.FEATHER_FALLING, 0.5D, 1);
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(FEATHER);

        IRON_INGOT.addEnchantment(Enchantments.KNOCKBACK, 0.5D, 1);
        IRON_INGOT.addEnchantment(Enchantments.PROJECTILE_PROTECTION, 0.5D, 1);
        IRON_INGOT.addEnchantment(Enchantments.PUNCH, 0.5D, 1);
        IRON_INGOT.addEnchantment(Enchantments.SWEEPING, 0.5D, 1);
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(IRON_INGOT);

        REDSTONE.addEnchantment(Enchantments.POWER, 0.5D, 1);
        REDSTONE.addEnchantment(Enchantments.SHARPNESS, 0.5D, 1);
        REDSTONE.addEnchantment(Enchantments.EFFICIENCY, 0.5D, 1);
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(REDSTONE);

        DIAMOND.addEnchantment(Enchantments.FORTUNE, 0.5D, 1);
        DIAMOND.addEnchantment(Enchantments.LOOTING, 0.5D, 1);
        DIAMOND.addEnchantment(Enchantments.INFINITY, 0.5D, 1);
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(DIAMOND);

        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.AQUA_AFFINITY, 0.5D, 1);
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.DEPTH_STRIDER, 0.5D, 1);
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.LUCK_OF_THE_SEA, 0.5D, 1);
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.LURE, 0.5D, 1);
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.RESPIRATION, 0.5D, 1);
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(PRISMARINE_CRYSTALS);

        GLOWSTONE_DUST.addEnchantment(Enchantments.BANE_OF_ARTHROPODS, 0.5D, 1);
        GLOWSTONE_DUST.addEnchantment(Enchantments.SMITE, 0.5D, 1);
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(GLOWSTONE_DUST);

        BLAZE_POWDER.addEnchantment(Enchantments.FIRE_ASPECT, 0.5D, 1);
        BLAZE_POWDER.addEnchantment(Enchantments.FIRE_PROTECTION, 0.5D, 1);
        BLAZE_POWDER.addEnchantment(Enchantments.FLAME, 0.5D, 1);
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(BLAZE_POWDER);

        SHULKER_SHELL.addEnchantment(Enchantments.BLAST_PROTECTION, 0.5D, 1);
        SHULKER_SHELL.addEnchantment(Enchantments.PROTECTION, 0.5D, 1);
        SHULKER_SHELL.addEnchantment(Enchantments.UNBREAKING, 0.5D, 1);
        SHULKER_SHELL.addEnchantment(Enchantments.SILK_TOUCH, 0.5D, 1);
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(SHULKER_SHELL);

    }
}
