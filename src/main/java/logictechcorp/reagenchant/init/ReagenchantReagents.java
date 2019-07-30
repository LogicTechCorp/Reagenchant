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

import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.api.reagent.IReagent;
import logictechcorp.reagenchant.reagent.Reagent;
import logictechcorp.reagenchant.reagent.ReagentEnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class ReagenchantReagents
{
    private static final IReagent GUNPOWDER = new Reagent(Items.GUNPOWDER);
    private static final IReagent IRON_INGOT = new Reagent(Items.IRON_INGOT);
    private static final IReagent GOLD_INGOT = new Reagent(Items.GOLD_INGOT);
    private static final IReagent DIAMOND = new Reagent(Items.DIAMOND);
    private static final IReagent OBSIDIAN = new Reagent(Item.getItemFromBlock(Blocks.OBSIDIAN));
    private static final IReagent REDSTONE = new Reagent(Items.REDSTONE);
    private static final IReagent PACKED_ICE = new Reagent(Item.getItemFromBlock(Blocks.PACKED_ICE));
    private static final IReagent PRISMARINE_SHARD = new Reagent(Items.PRISMARINE_SHARD);
    private static final IReagent PRISMARINE_CRYSTALS = new Reagent(Items.PRISMARINE_CRYSTALS);
    private static final IReagent GLOWSTONE_DUST = new Reagent(Items.GLOWSTONE_DUST);
    private static final IReagent BLAZE_POWDER = new Reagent(Items.BLAZE_POWDER);
    private static final IReagent NETHER_STAR = new Reagent(Items.NETHER_STAR);

    public static void initReagents()
    {
        GUNPOWDER.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.SMITE, 0.5D, 1));
        GUNPOWDER.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.FEATHER_FALLING, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(GUNPOWDER);

        IRON_INGOT.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.PROJECTILE_PROTECTION, 0.5D, 1));
        IRON_INGOT.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.PUNCH, 0.5D, 1));
        IRON_INGOT.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.KNOCKBACK, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(IRON_INGOT);

        GOLD_INGOT.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.PROTECTION, 0.5D, 1));
        GOLD_INGOT.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.SILK_TOUCH, 0.5D, 1));
        GOLD_INGOT.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.SWEEPING, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(GOLD_INGOT);

        DIAMOND.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.BLAST_PROTECTION, 0.5D, 1));
        DIAMOND.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.FORTUNE, 0.5D, 1));
        DIAMOND.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.LOOTING, 0.5D, 1));
        DIAMOND.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.INFINITY, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(DIAMOND);

        OBSIDIAN.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.UNBREAKING, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(OBSIDIAN);

        REDSTONE.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.THORNS, 0.5D, 1));
        REDSTONE.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.EFFICIENCY, 0.5D, 1));
        REDSTONE.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.SHARPNESS, 0.5D, 1));
        REDSTONE.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.POWER, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(REDSTONE);

        PACKED_ICE.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.FROST_WALKER, 1.0D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(PACKED_ICE);

        PRISMARINE_SHARD.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.LURE, 0.5D, 1));
        PRISMARINE_SHARD.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.AQUA_AFFINITY, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(PRISMARINE_SHARD);

        PRISMARINE_CRYSTALS.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.LUCK_OF_THE_SEA, 0.5D, 1));
        PRISMARINE_CRYSTALS.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.RESPIRATION, 0.5D, 1));
        PRISMARINE_CRYSTALS.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.DEPTH_STRIDER, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(PRISMARINE_CRYSTALS);

        GLOWSTONE_DUST.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.BANE_OF_ARTHROPODS, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(GLOWSTONE_DUST);

        BLAZE_POWDER.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.FIRE_PROTECTION, 0.5D, 1));
        BLAZE_POWDER.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.FLAME, 0.5D, 1));
        BLAZE_POWDER.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.FIRE_ASPECT, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(BLAZE_POWDER);

        NETHER_STAR.addReagentEnchantmentData(new ReagentEnchantmentData(Enchantments.MENDING, 0.5D, 1));
        ReagenchantAPI.getInstance().getReagentRegistry().registerReagent(NETHER_STAR);
    }
}
