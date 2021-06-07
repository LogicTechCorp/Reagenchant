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

package logictechcorp.reagenchant.core.registry;

import com.minecraftabnormals.abnormals_core.core.util.registry.BlockSubRegistryHelper;
import logictechcorp.reagenchant.common.block.CustomAnvilBlock;
import logictechcorp.reagenchant.common.block.ReagentAltarBlock;
import logictechcorp.reagenchant.common.block.ReagentEnchantingTableBlock;
import logictechcorp.reagenchant.common.compatibility.Compatibility;
import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.ReagenchantConfig;
import logictechcorp.reagenchant.core.events.QuarkEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class ReagenchantBlocks {
    public static final BlockSubRegistryHelper REGISTRY_HELPER = Reagenchant.REGISTRY_HELPER.getBlockSubHelper();
    public static final BlockSubRegistryHelper OVERRIDE_HELPER = Reagenchant.OVERRIDE_REGISTRY_HELPER.getBlockSubHelper();

    public static final RegistryObject<Block> REAGENT_ENCHANTING_TABLE;
    public static final RegistryObject<Block> CUSTOM_ANVIL;
    public static final RegistryObject<Block> CUSTOM_CHIPPED_ANVIL;
    public static final RegistryObject<Block> CUSTOM_DAMAGED_ANVIL;

    public static final RegistryObject<Block> REAGENT_ALTAR = REGISTRY_HELPER.createBlock("reagent_altar", ReagentAltarBlock::new, ItemGroup.DECORATIONS);

    static {
        if(!Compatibility.IS_QUARK_ODDITIES_LOADED || !QuarkEvents.matrixEnchantingEnabled || !ReagenchantConfig.COMMON.enableQuarkCompatibility.get()) {
            REAGENT_ENCHANTING_TABLE = OVERRIDE_HELPER.createBlock("enchanting_table", () -> new ReagentEnchantingTableBlock(AbstractBlock.Properties.from(Blocks.ENCHANTING_TABLE)), ItemGroup.DECORATIONS);
        }
        else {
            REAGENT_ENCHANTING_TABLE = RegistryObject.of(Blocks.ENCHANTING_TABLE.getRegistryName(), ForgeRegistries.BLOCKS);
        }

        if(!Compatibility.IS_APOTHEOSIS_LOADED) {
            CUSTOM_ANVIL = OVERRIDE_HELPER.createBlock("anvil", () -> new CustomAnvilBlock(AbstractBlock.Properties.from(Blocks.ANVIL)), ItemGroup.DECORATIONS);
            CUSTOM_CHIPPED_ANVIL = OVERRIDE_HELPER.createBlock("chipped_anvil", () -> new CustomAnvilBlock(AbstractBlock.Properties.from(Blocks.CHIPPED_ANVIL)), ItemGroup.DECORATIONS);
            CUSTOM_DAMAGED_ANVIL = OVERRIDE_HELPER.createBlock("damaged_anvil", () -> new CustomAnvilBlock(AbstractBlock.Properties.from(Blocks.DAMAGED_ANVIL)), ItemGroup.DECORATIONS);
        }
        else {
            CUSTOM_ANVIL = RegistryObject.of(Blocks.ANVIL.getRegistryName(), ForgeRegistries.BLOCKS);
            CUSTOM_CHIPPED_ANVIL = RegistryObject.of(Blocks.CHIPPED_ANVIL.getRegistryName(), ForgeRegistries.BLOCKS);
            CUSTOM_DAMAGED_ANVIL = RegistryObject.of(Blocks.DAMAGED_ANVIL.getRegistryName(), ForgeRegistries.BLOCKS);
        }
    }
}
