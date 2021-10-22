package logictechcorp.reagenchant.core.mixin;

import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.util.item.UnbreakableItemStackUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ClientItemStackMixin {
    @Redirect(method = "getTooltipLines", at = @At(value = "INVOKE", target = "net/minecraft/item/ItemStack.appendEnchantmentNames (Ljava/util/List;Lnet/minecraft/nbt/ListNBT;)V"))
    private void onGetTooltipLines(List<ITextComponent> tooltips, ListNBT enchantments) {
        ItemStack.appendEnchantmentNames(tooltips, enchantments);

        ListNBT disabledEnchantments = getDisabledEnchantmentTags(((ItemStack) (Object) this));
        int disabledEnchantmentCount = disabledEnchantments.size();

        for(int tagIndex = 0; tagIndex < disabledEnchantmentCount; tagIndex++) {
            CompoundNBT enchantmentCompound = disabledEnchantments.getCompound(tagIndex);
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(enchantmentCompound.getString("id")));

            if(enchantment != null) {
                tooltips.add(((IFormattableTextComponent) enchantment.getFullname(enchantmentCompound.getInt("lvl"))).withStyle(TextFormatting.GOLD));
            }
        }

        if(disabledEnchantmentCount > 0) {
            tooltips.add(new TranslationTextComponent("tooltip." + Reagenchant.MOD_ID + ".item.broken").withStyle(TextFormatting.GOLD));
        }
    }

    private static ListNBT getDisabledEnchantmentTags(ItemStack stack) {
        return stack.getTag() != null ? stack.getTag().getList(UnbreakableItemStackUtil.DISABLED_ENCHANTMENTS_KEY, 10) : new ListNBT();
    }
}
