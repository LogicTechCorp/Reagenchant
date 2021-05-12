package vazkii.quark.api;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.List;

public interface IModifiableEnchantmentInfluencer extends IEnchantmentInfluencer {
    List<Enchantment> getModifiedEnchantments(IBlockReader world, BlockPos pos, BlockState state, ItemStack stack, List<Enchantment> influencedEnchants);

    default float[] getModifiedColorComponents(IBlockReader world, BlockPos pos, BlockState state, float[] colorComponents) {
        return colorComponents;
    }
}
