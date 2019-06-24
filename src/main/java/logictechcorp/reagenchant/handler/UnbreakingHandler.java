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

package logictechcorp.reagenchant.handler;

import logictechcorp.reagenchant.Reagenchant;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class UnbreakingHandler
{
    private static final ResourceLocation BROKEN_PROPERTY_KEY = Reagenchant.getResource("broken");
    private static final IItemPropertyGetter BROKEN_PROPERTY = new IItemPropertyGetter()
    {
        @Override
        @SideOnly(Side.CLIENT)
        public float apply(ItemStack stack, World world, EntityLivingBase entity)
        {
            return UnbreakingHandler.isItemBroken(stack) ? 1.0F : 0.0F;
        }
    };

    @SubscribeEvent
    public static void onPlayerBreakSpeed(BreakSpeed event)
    {
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setNewSpeed(0.5F);
        }
    }

    @SubscribeEvent
    public static void onPlayerHarvestCheck(HarvestCheck event)
    {
        ItemStack stack = event.getEntityPlayer().getHeldItemMainhand();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanHarvest(false);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BreakEvent event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        Block block = event.getState().getBlock();
        ItemStack stack = event.getPlayer().getHeldItemMainhand();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            if(block instanceof IShearable)
            {
                IShearable shearable = (IShearable) block;

                if(shearable.isShearable(stack, world, pos))
                {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }

            event.setExpToDrop(0);
        }
    }

    @SubscribeEvent
    public static void onHarvestDrops(HarvestDropsEvent event)
    {
        EntityPlayer player = event.getHarvester();

        if(player != null)
        {
            ItemStack stack = event.getHarvester().getHeldItemMainhand();

            if(UnbreakingHandler.isItemBroken(stack))
            {
                event.getDrops().clear();
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event)
    {
        ItemStack stack = event.getItemStack();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(RightClickItem event)
    {
        ItemStack stack = event.getItemStack();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(EntityInteractSpecific event)
    {
        ItemStack stack = event.getItemStack();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(EntityInteract event)
    {
        ItemStack stack = event.getItemStack();

        if(UnbreakingHandler.isItemBroken(stack))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        Entity attacked = event.getEntity();
        DamageSource source = event.getSource();
        Entity attacker = source.getTrueSource();

        if(attacker instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) attacker;
            ItemStack stack = player.getHeldItemMainhand();

            if(UnbreakingHandler.isItemBroken(stack))
            {
                event.setAmount(1.0F);
            }
        }
        if(attacked instanceof EntityPlayer)
        {
            UnbreakingHandler.damagePlayer((EntityPlayer) attacked, source, event.getAmount());
            event.setAmount(0.0F);
        }
    }

    public static void addBrokenPropertyToItems()
    {
        for(Item item : ForgeRegistries.ITEMS)
        {
            item.addPropertyOverride(BROKEN_PROPERTY_KEY, BROKEN_PROPERTY);
        }
    }

    private static void damagePlayer(EntityPlayer player, DamageSource source, float amount)
    {
        NonNullList<ItemStack> armorStacks = player.inventory.armorInventory;
        NonNullList<ItemStack> brokenArmorStacks = NonNullList.withSize(4, ItemStack.EMPTY);

        for(int i = 0; i < armorStacks.size(); i++)
        {
            if(UnbreakingHandler.isItemBroken(armorStacks.get(i)))
            {
                brokenArmorStacks.set(i, armorStacks.set(i, ItemStack.EMPTY));
            }
        }

        float damage = ISpecialArmor.ArmorProperties.applyArmor(player, armorStacks, source, amount);

        if(!source.isDamageAbsolute())
        {
            if(player.isPotionActive(MobEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD)
            {
                int potionAmplifier = (player.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                damage = damage * (25.0F - potionAmplifier) / 25.0F;
            }

            if(damage <= 0.0F)
            {
                damage = 0.0F;
            }
            else
            {
                int enchantmentModifierDamage = EnchantmentHelper.getEnchantmentModifierDamage(armorStacks, source);

                if(enchantmentModifierDamage > 0)
                {
                    damage = CombatRules.getDamageAfterMagicAbsorb(damage, enchantmentModifierDamage);
                }
            }
        }

        float totalDamage = damage;
        damage = Math.max(damage - player.getAbsorptionAmount(), 0.0F);
        player.setAbsorptionAmount(player.getAbsorptionAmount() - (totalDamage - damage));
        damage = ForgeHooks.onLivingDamage(player, source, damage);

        if(damage != 0.0F)
        {
            float playerHealth = player.getHealth();
            player.addExhaustion(source.getHungerDamage());
            player.setHealth(playerHealth - damage);
            player.getCombatTracker().trackDamage(source, playerHealth, damage);

            if(damage < 3.4028235E37F)
            {
                player.addStat(StatList.DAMAGE_TAKEN, Math.round(damage * 10.0F));
            }
        }

        for(int i = 0; i < armorStacks.size(); i++)
        {
            ItemStack armorStack = armorStacks.get(i);

            if(!armorStack.isEmpty() && !UnbreakingHandler.isItemBroken(armorStack) && armorStack.getMaxDamage() == armorStack.getItemDamage())
            {
                armorStacks.set(i, ItemStack.EMPTY);
            }
        }

        for(int i = 0; i < brokenArmorStacks.size(); i++)
        {
            ItemStack armorStack = brokenArmorStacks.set(i, ItemStack.EMPTY);

            if(!armorStack.isEmpty())
            {
                armorStacks.set(i, armorStack);
            }
        }
    }

    private static boolean isItemBroken(ItemStack stack)
    {
        int usesRemaining = (stack.getMaxDamage() - stack.getItemDamage());
        boolean canBeBroken = !stack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0;
        boolean isBroken = canBeBroken && usesRemaining <= 1;

        if(usesRemaining == 0 && canBeBroken)
        {
            stack.setItemDamage(stack.getMaxDamage() - 1);
        }

        if(isBroken && EnchantmentHelper.hasBindingCurse(stack))
        {
            NBTTagList enchantmentList = stack.getTagCompound().getTagList("ench", 10);

            for(int i = 0; i < enchantmentList.tagCount(); i++)
            {
                NBTTagCompound compound = enchantmentList.getCompoundTagAt(i);

                if(compound.getShort("id") == Enchantment.getEnchantmentID(Enchantments.BINDING_CURSE))
                {
                    enchantmentList.removeTag(i);
                    break;
                }
            }
        }

        return isBroken;
    }
}
