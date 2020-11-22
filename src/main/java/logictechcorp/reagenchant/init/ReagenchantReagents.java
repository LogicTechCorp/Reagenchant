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
import logictechcorp.reagenchant.reagent.Reagent;
import logictechcorp.reagenchant.reagent.ReagentEnchantData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class ReagenchantReagents
{
    private static final Reagent GUNPOWDER = new Reagent(Items.GUNPOWDER);
    private static final Reagent IRON_INGOT = new Reagent(Items.IRON_INGOT);
    private static final Reagent GOLD_INGOT = new Reagent(Items.GOLD_INGOT);
    private static final Reagent DIAMOND = new Reagent(Items.DIAMOND);
    private static final Reagent OBSIDIAN = new Reagent(Item.getItemFromBlock(Blocks.OBSIDIAN));
    private static final Reagent REDSTONE = new Reagent(Items.REDSTONE);
    private static final Reagent PACKED_ICE = new Reagent(Item.getItemFromBlock(Blocks.PACKED_ICE));
    private static final Reagent PRISMARINE_SHARD = new Reagent(Items.PRISMARINE_SHARD);
    private static final Reagent PRISMARINE_CRYSTALS = new Reagent(Items.PRISMARINE_CRYSTALS);
    private static final Reagent GLOWSTONE_DUST = new Reagent(Items.GLOWSTONE_DUST);
    private static final Reagent BLAZE_POWDER = new Reagent(Items.BLAZE_POWDER);
    private static final Reagent NETHER_STAR = new Reagent(Items.NETHER_STAR);

    public static void initReagents()
    {
        GUNPOWDER.addEnchantment(new ReagentEnchantData(Enchantments.SMITE, 0.5F, 1));
        GUNPOWDER.addEnchantment(new ReagentEnchantData(Enchantments.FEATHER_FALLING, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(GUNPOWDER);

        IRON_INGOT.addEnchantment(new ReagentEnchantData(Enchantments.PROJECTILE_PROTECTION, 0.5F, 1));
        IRON_INGOT.addEnchantment(new ReagentEnchantData(Enchantments.PUNCH, 0.5F, 1));
        IRON_INGOT.addEnchantment(new ReagentEnchantData(Enchantments.KNOCKBACK, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(IRON_INGOT);

        GOLD_INGOT.addEnchantment(new ReagentEnchantData(Enchantments.PROTECTION, 0.5F, 1));
        GOLD_INGOT.addEnchantment(new ReagentEnchantData(Enchantments.SILK_TOUCH, 0.5F, 1));
        GOLD_INGOT.addEnchantment(new ReagentEnchantData(Enchantments.SWEEPING, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(GOLD_INGOT);

        DIAMOND.addEnchantment(new ReagentEnchantData(Enchantments.BLAST_PROTECTION, 0.5F, 1));
        DIAMOND.addEnchantment(new ReagentEnchantData(Enchantments.FORTUNE, 0.5F, 1));
        DIAMOND.addEnchantment(new ReagentEnchantData(Enchantments.LOOTING, 0.5F, 1));
        DIAMOND.addEnchantment(new ReagentEnchantData(Enchantments.INFINITY, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(DIAMOND);

        OBSIDIAN.addEnchantment(new ReagentEnchantData(Enchantments.UNBREAKING, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(OBSIDIAN);

        REDSTONE.addEnchantment(new ReagentEnchantData(Enchantments.THORNS, 0.5F, 1));
        REDSTONE.addEnchantment(new ReagentEnchantData(Enchantments.EFFICIENCY, 0.5F, 1));
        REDSTONE.addEnchantment(new ReagentEnchantData(Enchantments.SHARPNESS, 0.5F, 1));
        REDSTONE.addEnchantment(new ReagentEnchantData(Enchantments.POWER, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(REDSTONE);

        PACKED_ICE.addEnchantment(new ReagentEnchantData(Enchantments.FROST_WALKER, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(PACKED_ICE);

        PRISMARINE_SHARD.addEnchantment(new ReagentEnchantData(Enchantments.LURE, 0.5F, 1));
        PRISMARINE_SHARD.addEnchantment(new ReagentEnchantData(Enchantments.AQUA_AFFINITY, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(PRISMARINE_SHARD);

        PRISMARINE_CRYSTALS.addEnchantment(new ReagentEnchantData(Enchantments.LUCK_OF_THE_SEA, 0.5F, 1));
        PRISMARINE_CRYSTALS.addEnchantment(new ReagentEnchantData(Enchantments.RESPIRATION, 0.5F, 1));
        PRISMARINE_CRYSTALS.addEnchantment(new ReagentEnchantData(Enchantments.DEPTH_STRIDER, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(PRISMARINE_CRYSTALS);

        GLOWSTONE_DUST.addEnchantment(new ReagentEnchantData(Enchantments.BANE_OF_ARTHROPODS, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(GLOWSTONE_DUST);

        BLAZE_POWDER.addEnchantment(new ReagentEnchantData(Enchantments.FIRE_PROTECTION, 0.5F, 1));
        BLAZE_POWDER.addEnchantment(new ReagentEnchantData(Enchantments.FLAME, 0.5F, 1));
        BLAZE_POWDER.addEnchantment(new ReagentEnchantData(Enchantments.FIRE_ASPECT, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(BLAZE_POWDER);

        NETHER_STAR.addEnchantment(new ReagentEnchantData(Enchantments.MENDING, 0.5F, 1));
        Reagenchant.REAGENT_MANAGER.registerReagent(NETHER_STAR);
    }
}
