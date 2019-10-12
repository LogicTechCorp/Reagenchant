package logictechcorp.reagenchant.item;

import com.google.common.collect.Multimap;
import logictechcorp.reagenchant.handler.UnbreakingHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UnbreakableSwordItem extends UnbreakableTieredItem
{
    private final float attackDamage;
    private final float attackSpeed;

    public UnbreakableSwordItem(IItemTier itemTier, int attackDamage, float attackSpeed, Item.Properties properties)
    {
        super(itemTier, properties);
        this.attackSpeed = attackSpeed;
        this.attackDamage = attackDamage + itemTier.getAttackDamage();
    }

    @Override
    public boolean canPlayerBreakBlockWhileHolding(BlockState state, World world, BlockPos pos, PlayerEntity player)
    {
        return !player.isCreative();
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state)
    {
        Block block = state.getBlock();

        if(block == Blocks.COBWEB)
        {
            return 15.0F;
        }
        else
        {
            Material material = state.getMaterial();
            return material != Material.PLANTS && material != Material.TALL_PLANTS && material != Material.CORAL && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker)
    {
        stack.damageItem(1, attacker, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity livingEntity)
    {
        if(state.getBlockHardness(world, pos) != 0.0F)
        {
            stack.damageItem(2, livingEntity, (entity) -> entity.sendBreakAnimation(EquipmentSlotType.MAINHAND));
        }

        return true;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, BlockState state)
    {
        return !UnbreakingHandler.isItemBroken(stack) && state.getBlock() == Blocks.COBWEB;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlotType, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlotType);

        if(equipmentSlotType == EquipmentSlotType.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.getAttackDamage(stack), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", this.getAttackSpeed(stack), AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    public float getAttackDamage(ItemStack stack)
    {
        return UnbreakingHandler.isItemBroken(stack) ? 0 : this.attackDamage;
    }

    public float getAttackSpeed(ItemStack stack)
    {
        return UnbreakingHandler.isItemBroken(stack) ? 0 : this.attackSpeed;
    }
}
