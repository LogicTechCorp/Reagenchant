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

package logictechcorp.reagenchant.item;

import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ReagenchantItems
{
    public static final DeferredRegister<Item> ITEM_OVERRIDES = new DeferredRegister<>(ForgeRegistries.ITEMS, "minecraft");

    static
    {
        ITEM_OVERRIDES.register("enchanting_table", () -> new BlockItem(Blocks.ENCHANTING_TABLE, new Item.Properties().group(ItemGroup.DECORATIONS)));
        ITEM_OVERRIDES.register("bow", () -> new UnbreakableBowItem(new Item.Properties().maxDamage(384).group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("turtle_helmet", () -> new UnbreakableArmorItem(ArmorMaterial.TURTLE, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("iron_shovel", () -> new UnbreakableShovelItem(ItemTier.IRON, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("iron_pickaxe", () -> new UnbreakablePickaxeItem(ItemTier.IRON, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("iron_axe", () -> new UnbreakableAxeItem(ItemTier.IRON, 6.0F, -3.1F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("iron_sword", () -> new UnbreakableSwordItem(ItemTier.IRON, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("flint_and_steel", () -> new UnbreakableFlintAndSteelItem(new Item.Properties().maxDamage(64).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("wooden_sword", () -> new UnbreakableSwordItem(ItemTier.WOOD, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("wooden_shovel", () -> new UnbreakableShovelItem(ItemTier.WOOD, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("wooden_pickaxe", () -> new UnbreakablePickaxeItem(ItemTier.WOOD, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("wooden_axe", () -> new UnbreakableAxeItem(ItemTier.WOOD, 6.0F, -3.2F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("stone_sword", () -> new UnbreakableSwordItem(ItemTier.STONE, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("stone_shovel", () -> new UnbreakableShovelItem(ItemTier.STONE, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("stone_pickaxe", () -> new UnbreakablePickaxeItem(ItemTier.STONE, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("stone_axe", () -> new UnbreakableAxeItem(ItemTier.STONE, 7.0F, -3.2F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("diamond_sword", () -> new UnbreakableSwordItem(ItemTier.DIAMOND, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("diamond_shovel", () -> new UnbreakableShovelItem(ItemTier.DIAMOND, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("diamond_pickaxe", () -> new UnbreakablePickaxeItem(ItemTier.DIAMOND, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("diamond_axe", () -> new UnbreakableAxeItem(ItemTier.DIAMOND, 5.0F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("golden_sword", () -> new UnbreakableSwordItem(ItemTier.GOLD, 3, -2.4F, (new Item.Properties()).group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("golden_shovel", () -> new UnbreakableShovelItem(ItemTier.GOLD, 1.5F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("golden_pickaxe", () -> new UnbreakablePickaxeItem(ItemTier.GOLD, 1, -2.8F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("golden_axe", () -> new UnbreakableAxeItem(ItemTier.GOLD, 6.0F, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("wooden_hoe", () -> new UnbreakableHoeItem(ItemTier.WOOD, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("stone_hoe", () -> new UnbreakableHoeItem(ItemTier.STONE, -2.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("iron_hoe", () -> new UnbreakableHoeItem(ItemTier.IRON, -1.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("diamond_hoe", () -> new UnbreakableHoeItem(ItemTier.DIAMOND, 0.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("golden_hoe", () -> new UnbreakableHoeItem(ItemTier.GOLD, -3.0F, (new Item.Properties()).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("leather_helmet", () -> new UnbreakableDyeableArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("leather_chestplate", () -> new UnbreakableDyeableArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("leather_leggings", () -> new UnbreakableDyeableArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.LEGS, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("leather_boots", () -> new UnbreakableDyeableArmorItem(ArmorMaterial.LEATHER, EquipmentSlotType.FEET, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("chainmail_helmet", () -> new UnbreakableArmorItem(ArmorMaterial.CHAIN, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("chainmail_chestplate", () -> new UnbreakableArmorItem(ArmorMaterial.CHAIN, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("chainmail_leggings", () -> new UnbreakableArmorItem(ArmorMaterial.CHAIN, EquipmentSlotType.LEGS, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("chainmail_boots", () -> new UnbreakableArmorItem(ArmorMaterial.CHAIN, EquipmentSlotType.FEET, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("iron_helmet", () -> new UnbreakableArmorItem(ArmorMaterial.IRON, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("iron_chestplate", () -> new UnbreakableArmorItem(ArmorMaterial.IRON, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("iron_leggings", () -> new UnbreakableArmorItem(ArmorMaterial.IRON, EquipmentSlotType.LEGS, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("iron_boots", () -> new UnbreakableArmorItem(ArmorMaterial.IRON, EquipmentSlotType.FEET, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("diamond_helmet", () -> new UnbreakableArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("diamond_chestplate", () -> new UnbreakableArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("diamond_leggings", () -> new UnbreakableArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.LEGS, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("diamond_boots", () -> new UnbreakableArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.FEET, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("golden_helmet", () -> new UnbreakableArmorItem(ArmorMaterial.GOLD, EquipmentSlotType.HEAD, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("golden_chestplate", () -> new UnbreakableArmorItem(ArmorMaterial.GOLD, EquipmentSlotType.CHEST, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("golden_leggings", () -> new UnbreakableArmorItem(ArmorMaterial.GOLD, EquipmentSlotType.LEGS, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("golden_boots", () -> new UnbreakableArmorItem(ArmorMaterial.GOLD, EquipmentSlotType.FEET, new Item.Properties().group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("fishing_rod", () -> new UnbreakableFishingRodItem((new Item.Properties()).maxDamage(64).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("shears", () -> new UnbreakableShearsItem((new Item.Properties()).maxDamage(238).group(ItemGroup.TOOLS)));
        ITEM_OVERRIDES.register("carrot_on_a_stick", () -> new UnbreakableCarrotOnAStickItem((new Item.Properties()).maxDamage(25).group(ItemGroup.TRANSPORTATION)));
        ITEM_OVERRIDES.register("shield", () -> new UnbreakableShieldItem(new Item.Properties().maxDamage(336).group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("trident", () -> new UnbreakableTridentItem((new Item.Properties()).maxDamage(250).group(ItemGroup.COMBAT)));
        ITEM_OVERRIDES.register("crossbow", () -> new UnbreakableCrossbowItem(new Item.Properties().maxStackSize(1).group(ItemGroup.COMBAT).maxDamage(326)));
    }
}
