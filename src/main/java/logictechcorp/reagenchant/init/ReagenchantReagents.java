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
 * along with this program.  If not, see <http:www.gnu.org/licenses/>.
 */

package logictechcorp.reagenchant.init;

import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.api.reagent.iface.IReagent;
import logictechcorp.reagenchant.api.reagent.impl.ReagentConfigurable;
import logictechcorp.reagenchant.api.reagent.impl.ReagentEnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class ReagenchantReagents
{
    private static final IReagent GUNPOWDER = new ReagentConfigurable(Reagenchant.getResource("gunpowder"), Items.GUNPOWDER);
    private static final IReagent IRON_INGOT = new ReagentConfigurable(Reagenchant.getResource("iron_ingot"), Items.IRON_INGOT);
    private static final IReagent GOLD_INGOT = new ReagentConfigurable(Reagenchant.getResource("gold_ingot"), Items.GOLD_INGOT);
    private static final IReagent DIAMOND = new ReagentConfigurable(Reagenchant.getResource("diamond"), Items.DIAMOND);
    private static final IReagent OBSIDIAN = new ReagentConfigurable(Reagenchant.getResource("obsidian"), Item.getItemFromBlock(Blocks.OBSIDIAN));
    private static final IReagent REDSTONE = new ReagentConfigurable(Reagenchant.getResource("redstone"), Items.REDSTONE);
    private static final IReagent PACKED_ICE = new ReagentConfigurable(Reagenchant.getResource("packed_ice"), Item.getItemFromBlock(Blocks.PACKED_ICE));
    private static final IReagent PRISMARINE_SHARD = new ReagentConfigurable(Reagenchant.getResource("prismarine_shard"), Items.PRISMARINE_SHARD);
    private static final IReagent PRISMARINE_CRYSTALS = new ReagentConfigurable(Reagenchant.getResource("prismarine_crystals"), Items.PRISMARINE_CRYSTALS);
    private static final IReagent GLOWSTONE_DUST = new ReagentConfigurable(Reagenchant.getResource("glowstone_dust"), Items.GLOWSTONE_DUST);
    private static final IReagent BLAZE_POWDER = new ReagentConfigurable(Reagenchant.getResource("blaze_powder"), Items.BLAZE_POWDER);
    private static final IReagent NETHER_STAR = new ReagentConfigurable(Reagenchant.getResource("nether_star"), Items.NETHER_STAR);

    public static void initReagents()
    {
        GUNPOWDER.addEnchantment(Enchantments.SMITE, new ReagentEnchantmentData(Enchantments.SMITE, 0.5D, 1));
        GUNPOWDER.addEnchantment(Enchantments.FEATHER_FALLING, new ReagentEnchantmentData(Enchantments.FEATHER_FALLING, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(GUNPOWDER);

        IRON_INGOT.addEnchantment(Enchantments.PROJECTILE_PROTECTION, new ReagentEnchantmentData(Enchantments.PROJECTILE_PROTECTION, 0.5D, 1));
        IRON_INGOT.addEnchantment(Enchantments.PUNCH, new ReagentEnchantmentData(Enchantments.PUNCH, 0.5D, 1));
        IRON_INGOT.addEnchantment(Enchantments.KNOCKBACK, new ReagentEnchantmentData(Enchantments.KNOCKBACK, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(IRON_INGOT);

        GOLD_INGOT.addEnchantment(Enchantments.PROTECTION, new ReagentEnchantmentData(Enchantments.PROTECTION, 0.5D, 1));
        GOLD_INGOT.addEnchantment(Enchantments.SILK_TOUCH, new ReagentEnchantmentData(Enchantments.SILK_TOUCH, 0.5D, 1));
        GOLD_INGOT.addEnchantment(Enchantments.SWEEPING, new ReagentEnchantmentData(Enchantments.SWEEPING, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(GOLD_INGOT);

        DIAMOND.addEnchantment(Enchantments.BLAST_PROTECTION, new ReagentEnchantmentData(Enchantments.BLAST_PROTECTION, 0.5D, 1));
        DIAMOND.addEnchantment(Enchantments.FORTUNE, new ReagentEnchantmentData(Enchantments.FORTUNE, 0.5D, 1));
        DIAMOND.addEnchantment(Enchantments.LOOTING, new ReagentEnchantmentData(Enchantments.LOOTING, 0.5D, 1));
        DIAMOND.addEnchantment(Enchantments.INFINITY, new ReagentEnchantmentData(Enchantments.INFINITY, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(DIAMOND);

        OBSIDIAN.addEnchantment(Enchantments.UNBREAKING, new ReagentEnchantmentData(Enchantments.UNBREAKING, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(OBSIDIAN);

        REDSTONE.addEnchantment(Enchantments.THORNS, new ReagentEnchantmentData(Enchantments.THORNS, 0.5D, 1));
        REDSTONE.addEnchantment(Enchantments.EFFICIENCY, new ReagentEnchantmentData(Enchantments.EFFICIENCY, 0.5D, 1));
        REDSTONE.addEnchantment(Enchantments.SHARPNESS, new ReagentEnchantmentData(Enchantments.SHARPNESS, 0.5D, 1));
        REDSTONE.addEnchantment(Enchantments.POWER, new ReagentEnchantmentData(Enchantments.POWER, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(REDSTONE);

        PACKED_ICE.addEnchantment(Enchantments.FROST_WALKER, new ReagentEnchantmentData(Enchantments.FROST_WALKER, 1.0D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(PACKED_ICE);

        PRISMARINE_SHARD.addEnchantment(Enchantments.LURE, new ReagentEnchantmentData(Enchantments.LURE, 0.5D, 1));
        PRISMARINE_SHARD.addEnchantment(Enchantments.AQUA_AFFINITY, new ReagentEnchantmentData(Enchantments.AQUA_AFFINITY, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(PRISMARINE_SHARD);

        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.LUCK_OF_THE_SEA, new ReagentEnchantmentData(Enchantments.LUCK_OF_THE_SEA, 0.5D, 1));
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.RESPIRATION, new ReagentEnchantmentData(Enchantments.RESPIRATION, 0.5D, 1));
        PRISMARINE_CRYSTALS.addEnchantment(Enchantments.DEPTH_STRIDER, new ReagentEnchantmentData(Enchantments.DEPTH_STRIDER, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(PRISMARINE_CRYSTALS);

        GLOWSTONE_DUST.addEnchantment(Enchantments.BANE_OF_ARTHROPODS, new ReagentEnchantmentData(Enchantments.BANE_OF_ARTHROPODS, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(GLOWSTONE_DUST);

        BLAZE_POWDER.addEnchantment(Enchantments.FIRE_PROTECTION, new ReagentEnchantmentData(Enchantments.FIRE_PROTECTION, 0.5D, 1));
        BLAZE_POWDER.addEnchantment(Enchantments.FLAME, new ReagentEnchantmentData(Enchantments.FLAME, 0.5D, 1));
        BLAZE_POWDER.addEnchantment(Enchantments.FIRE_ASPECT, new ReagentEnchantmentData(Enchantments.FIRE_ASPECT, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(BLAZE_POWDER);

        NETHER_STAR.addEnchantment(Enchantments.MENDING, new ReagentEnchantmentData(Enchantments.MENDING, 1.0D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(NETHER_STAR);
    }
}
