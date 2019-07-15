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

import logictechcorp.libraryex.utility.NBTHelper;
import logictechcorp.reagenchant.Reagenchant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
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
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
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
            return UnbreakingHandler.isItemConsideredBroken(stack) ? 1.0F : 0.0F;
        }
    };
    private static final String PLAYED_BREAKING_SOUND_KEY = Reagenchant.MOD_ID + ":PlayedBreakingSound";
    private static final String DISABLED_ENCHANTMENTS_KEY = Reagenchant.MOD_ID + ":DisabledEnchantments";

    @SubscribeEvent
    public static void onPlayerBreakSpeed(BreakSpeed event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.getHeldItemMainhand();

        if(UnbreakingHandler.isItemConsideredBroken(stack))
        {
            event.setNewSpeed(0.5F);
        }
    }

    @SubscribeEvent
    public static void onPlayerHarvestCheck(HarvestCheck event)
    {
        EntityPlayer player = event.getEntityPlayer();
        IBlockState state = event.getTargetBlock();
        ItemStack stack = player.getHeldItemMainhand();

        if(UnbreakingHandler.isItemConsideredBroken(stack) && !state.getMaterial().isToolNotRequired())
        {
            event.setCanHarvest(false);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BreakEvent event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = event.getState();
        EntityPlayer player = event.getPlayer();
        Block block = state.getBlock();
        ItemStack stack = player.getHeldItemMainhand();

        if(UnbreakingHandler.isItemConsideredBroken(stack))
        {
            if(block instanceof IShearable)
            {
                if(((IShearable) block).isShearable(stack, world, pos))
                {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState());
                }
            }

            if(!state.getMaterial().isToolNotRequired())
            {
                event.setExpToDrop(0);
            }
        }
    }

    @SubscribeEvent
    public static void onHarvestDrops(HarvestDropsEvent event)
    {
        IBlockState state = event.getState();
        EntityPlayer player = event.getHarvester();

        if(player != null)
        {
            ItemStack stack = event.getHarvester().getHeldItemMainhand();

            if(UnbreakingHandler.isItemConsideredBroken(stack))
            {
                UnbreakingHandler.breakItem(player, stack);

                if(!state.getMaterial().isToolNotRequired())
                {
                    event.getDrops().clear();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(RightClickBlock event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();

        if(UnbreakingHandler.isItemConsideredBroken(stack))
        {
            UnbreakingHandler.breakItem(player, stack);
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(RightClickItem event)
    {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if(UnbreakingHandler.isItemConsideredBroken(stack))
        {
            if(!(item instanceof ItemArmor) && !(item instanceof ISpecialArmor))
            {
                UnbreakingHandler.breakItem(player, stack);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(EntityInteract event)
    {
        EntityPlayer player = event.getEntityPlayer();
        Entity entity = event.getTarget();
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if(UnbreakingHandler.isItemConsideredBroken(stack))
        {
            UnbreakingHandler.breakItem(player, stack);

            if(entity instanceof EntityLivingBase && item.itemInteractionForEntity(ItemStack.EMPTY, event.getEntityPlayer(), (EntityLivingBase) entity, event.getHand()))
            {
                event.setCanceled(true);
            }
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

            if(UnbreakingHandler.isItemConsideredBroken(stack))
            {
                UnbreakingHandler.breakItem(player, stack);
                event.setAmount(1.0F);
            }
        }
        if(attacked instanceof EntityPlayer)
        {
            UnbreakingHandler.damagePlayer((EntityPlayer) attacked, source, event.getAmount());
            event.setAmount(0.0F);
        }
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event)
    {
        if(UnbreakingHandler.isItemConsideredBroken(event.getItemInput()))
        {
            UnbreakingHandler.fixItem(event.getItemResult());
        }
    }

    @SubscribeEvent
    public static void onMending(PlayerPickupXpEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();

        for(ItemStack stack : Enchantments.MENDING.getEntityEquipment(player))
        {
            if(UnbreakingHandler.isItemConsideredBroken(stack))
            {
                UnbreakingHandler.fixItem(stack);
            }
        }
    }

    public static void overrideBehavior()
    {
        for(Item item : ForgeRegistries.ITEMS)
        {
            item.addPropertyOverride(BROKEN_PROPERTY_KEY, BROKEN_PROPERTY);
        }

        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Items.FLINT_AND_STEEL, new Bootstrap.BehaviorDispenseOptional()
        {
            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                this.successful = false;

                if(!UnbreakingHandler.isItemConsideredBroken(stack))
                {
                    World world = source.getWorld();
                    BlockPos pos = source.getBlockPos().offset(source.getBlockState().getValue(BlockDispenser.FACING));
                    this.successful = true;

                    if(world.isAirBlock(pos))
                    {
                        world.setBlockState(pos, Blocks.FIRE.getDefaultState());

                        if(stack.attemptDamageItem(1, world.rand, null))
                        {
                            stack.setCount(0);
                        }
                    }
                    else if(world.getBlockState(pos).getBlock() == Blocks.TNT)
                    {
                        Blocks.TNT.onPlayerDestroy(world, pos, Blocks.TNT.getDefaultState().withProperty(BlockTNT.EXPLODE, true));
                        world.setBlockToAir(pos);
                    }
                }

                return stack;
            }
        });
    }

    private static void breakItem(EntityLivingBase livingEntity, ItemStack stack)
    {
        NBTTagCompound stackCompound = NBTHelper.ensureTagExists(stack);

        if(!stackCompound.getBoolean(PLAYED_BREAKING_SOUND_KEY))
        {
            livingEntity.renderBrokenItemStack(stack);
            stackCompound.setBoolean(PLAYED_BREAKING_SOUND_KEY, true);
        }

        NBTTagList enchantments = stack.getEnchantmentTagList();
        NBTTagList disabledEnchantments = new NBTTagList();

        for(int i = 0; i < enchantments.tagCount(); i++)
        {
            int enchantmentId = enchantments.getCompoundTagAt(i).getShort("id");

            if(enchantmentId == Enchantment.getEnchantmentID(Enchantments.BINDING_CURSE))
            {
                enchantments.removeTag(i);
            }
            else if(enchantmentId != Enchantment.getEnchantmentID(Enchantments.UNBREAKING) && enchantmentId != Enchantment.getEnchantmentID(Enchantments.MENDING))
            {
                disabledEnchantments.appendTag(enchantments.removeTag(i));
            }
        }

        NBTHelper.ensureTagExists(stack).setTag(DISABLED_ENCHANTMENTS_KEY, disabledEnchantments);
    }

    private static void fixItem(ItemStack stack)
    {
        NBTTagCompound stackCompound = NBTHelper.ensureTagExists(stack);

        if(stackCompound.getBoolean(PLAYED_BREAKING_SOUND_KEY))
        {
            stackCompound.setBoolean(PLAYED_BREAKING_SOUND_KEY, false);
        }

        NBTTagList enchantments = stack.getEnchantmentTagList();

        if(stackCompound.hasKey(DISABLED_ENCHANTMENTS_KEY))
        {
            NBTTagList disabledEnchantments = stackCompound.getTagList(DISABLED_ENCHANTMENTS_KEY, 10);

            for(int i = 0; i < disabledEnchantments.tagCount(); i++)
            {
                enchantments.appendTag(disabledEnchantments.removeTag(i));
            }
        }

        stackCompound.setTag("ench", enchantments);
    }

    private static void damagePlayer(EntityPlayer player, DamageSource source, float amount)
    {
        NonNullList<ItemStack> armorStacks = player.inventory.armorInventory;
        NonNullList<ItemStack> brokenArmorStacks = NonNullList.withSize(4, ItemStack.EMPTY);

        for(int i = 0; i < armorStacks.size(); i++)
        {
            if(UnbreakingHandler.isItemConsideredBroken(armorStacks.get(i)))
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

            if(!armorStack.isEmpty() && !UnbreakingHandler.isItemConsideredBroken(armorStack) && armorStack.getMaxDamage() == armorStack.getItemDamage())
            {
                UnbreakingHandler.breakItem(player, armorStack);
                armorStacks.set(i, ItemStack.EMPTY);
            }
        }

        for(int i = 0; i < brokenArmorStacks.size(); i++)
        {
            ItemStack armorStack = brokenArmorStacks.set(i, ItemStack.EMPTY);

            if(!armorStack.isEmpty())
            {
                UnbreakingHandler.breakItem(player, armorStack);
                armorStacks.set(i, armorStack);
            }
        }
    }

    private static boolean isItemConsideredBroken(ItemStack stack)
    {
        int usesRemaining = (stack.getMaxDamage() - stack.getItemDamage());
        boolean canBeBroken = !stack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0;
        boolean isConsideredBroken = canBeBroken && usesRemaining <= 1;

        if(usesRemaining == 0 && canBeBroken)
        {
            stack.setItemDamage(stack.getMaxDamage() - 1);
        }

        return isConsideredBroken;
    }
}
