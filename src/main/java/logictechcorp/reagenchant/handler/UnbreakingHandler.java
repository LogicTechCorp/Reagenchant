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
import net.minecraft.block.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class UnbreakingHandler
{
    public static final ResourceLocation BROKEN_PROPERTY_KEY = new ResourceLocation(Reagenchant.MOD_ID, "broken");
    public static final IItemPropertyGetter BROKEN_PROPERTY = (stack, world, livingEntity) -> isItemBroken(stack) ? 1.0F : 0.0F;
    public static final String BROKEN_KEY = Reagenchant.MOD_ID + ":Broken";
    public static final String DISABLED_ENCHANTMENTS_KEY = Reagenchant.MOD_ID + ":DisabledEnchantments";

    public static void setup()
    {
        DispenserBlock.registerDispenseBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseBehavior()
        {
            @Override
            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
            {
                this.successful = false;

                if(canItemBeBroken(stack, 1))
                {
                    breakItem(null, stack, null);
                }

                if(!isItemBroken(stack))
                {
                    World world = source.getWorld();
                    BlockPos pos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));
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
                        Blocks.TNT.onPlayerDestroy(world, pos, Blocks.TNT.getDefaultState().with(TNTBlock.UNSTABLE, true));
                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                    }
                }

                return stack;
            }
        });

        MinecraftForge.EVENT_BUS.register(UnbreakingHandler.class);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        List<ITextComponent> tooltips = event.getToolTip();
        CompoundNBT compound = stack.getTag();

        if(compound != null && compound.contains(DISABLED_ENCHANTMENTS_KEY))
        {
            for(int tooltipIndex = 0; tooltipIndex < tooltips.size(); tooltipIndex++)
            {
                ITextComponent tooltip = tooltips.get(tooltipIndex);

                if(tooltip.toString().equals(I18n.format("item.modifiers.mainhand")))
                {
                    ListNBT disabledEnchantments = compound.getList(DISABLED_ENCHANTMENTS_KEY, 10);
                    int enchantmentCount = disabledEnchantments.size();

                    for(int tagIndex = 0; tagIndex < enchantmentCount; tagIndex++)
                    {
                        CompoundNBT enchantmentCompound = disabledEnchantments.getCompound(tagIndex);
                        Enchantment enchantment = Enchantment.getEnchantmentByID(enchantmentCompound.getShort("id"));
                        int index = ((tooltipIndex + tagIndex) - 1);

                        if(enchantment != null)
                        {
                            tooltips.add(index, enchantment.getDisplayName(enchantmentCompound.getShort("lvl")).applyTextStyle(TextFormatting.GOLD));
                        }

                        if((tagIndex + 1) == enchantmentCount)
                        {
                            tooltips.add((index + 1), new TranslationTextComponent("tooltip." + Reagenchant.MOD_ID + ".item.broken"));
                        }
                    }

                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event)
    {
        PlayerEntity player = event.getPlayer();
        ItemStack stack = player.getHeldItemMainhand();

        if(isItemBroken(stack))
        {
            event.setNewSpeed(0.5F);
        }
    }

    @SubscribeEvent
    public static void onPlayerHarvestCheck(PlayerEvent.HarvestCheck event)
    {
        PlayerEntity player = event.getPlayer();
        BlockState state = event.getTargetBlock();
        ItemStack stack = player.getHeldItemMainhand();

        if(isItemBroken(stack) && !state.getMaterial().isToolNotRequired())
        {
            event.setCanHarvest(false);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event)
    {
        IWorld world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        PlayerEntity player = event.getPlayer();
        Block block = state.getBlock();
        ItemStack stack = player.getHeldItemMainhand();

        if(isItemBroken(stack))
        {
            if(block instanceof IShearable)
            {
                if(((IShearable) block).isShearable(stack, world, pos))
                {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
                }
            }

            if(!state.getMaterial().isToolNotRequired())
            {
                event.setExpToDrop(0);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        PlayerEntity player = event.getPlayer();
        ItemStack stack = event.getItemStack();

        if(isItemBroken(stack))
        {
            breakItem(player, stack, EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.HAND, event.getHand().ordinal()));
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        PlayerEntity player = event.getPlayer();
        ItemStack stack = event.getItemStack();

        if(isItemBroken(stack))
        {
            if(!(stack.getItem() instanceof ArmorItem))
            {
                breakItem(player, stack, EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.HAND, event.getHand().ordinal()));
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        PlayerEntity player = event.getPlayer();
        Entity entity = event.getTarget();
        ItemStack stack = event.getItemStack();
        Hand hand = event.getHand();

        if(isItemBroken(stack))
        {
            breakItem(player, stack, EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.HAND, hand.ordinal()));

            if(entity instanceof LivingEntity && stack.getItem().itemInteractionForEntity(ItemStack.EMPTY, player, (LivingEntity) entity, hand))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingHurt(LivingHurtEvent event)
    {
        LivingEntity hurtEntity = event.getEntityLiving();

        if(hurtEntity instanceof PlayerEntity)
        {
            PlayerEntity player = (PlayerEntity) hurtEntity;

            for(EquipmentSlotType equipmentSlotType : EquipmentSlotType.values())
            {
                if(equipmentSlotType.getSlotType() == EquipmentSlotType.Group.ARMOR)
                {
                    ItemStack armorStack = player.inventory.armorInventory.get(equipmentSlotType.getIndex());

                    if(canItemBeBroken(armorStack, 1))
                    {
                        breakItem(player, armorStack, equipmentSlotType);
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAnvilRepair(AnvilRepairEvent event)
    {
        ItemStack inputStack = event.getItemInput();
        ItemStack outputStack = event.getItemResult();

        if(isItemBroken(inputStack) && (outputStack.getDamage() < inputStack.getDamage()))
        {
            fixItem(outputStack);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMending(PlayerXpEvent.PickupXp event)
    {
        PlayerEntity player = event.getPlayer();

        for(Hand hand : Hand.values())
        {
            ItemStack stack = player.getHeldItem(hand);

            if(isItemBroken(stack) && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0)
            {
                fixItem(stack);
            }
        }
    }

    public static void breakItem(LivingEntity livingEntity, ItemStack stack, EquipmentSlotType equipmentSlotType)
    {
        CompoundNBT stackCompound = stack.getOrCreateTag();

        if(!stackCompound.getBoolean(BROKEN_KEY))
        {
            ListNBT enchantments = stack.getEnchantmentTagList();
            ListNBT disabledEnchantments = new ListNBT();

            if(!stackCompound.contains(DISABLED_ENCHANTMENTS_KEY))
            {
                for(int tagIndex = 0; tagIndex < enchantments.size(); tagIndex++)
                {
                    ResourceLocation enchantmentName = new ResourceLocation(enchantments.getCompound(tagIndex).getString("id"));

                    if(enchantmentName.equals(Enchantments.BINDING_CURSE.getRegistryName()))
                    {
                        enchantments.remove(tagIndex);
                    }
                    else if(!enchantmentName.equals(Enchantments.UNBREAKING.getRegistryName()) && !enchantmentName.equals(Enchantments.MENDING.getRegistryName()))
                    {
                        disabledEnchantments.add(enchantments.remove(tagIndex));
                    }
                }

                stackCompound.put(DISABLED_ENCHANTMENTS_KEY, disabledEnchantments);
            }

            stackCompound.putBoolean(BROKEN_KEY, true);

            if(livingEntity != null && equipmentSlotType != null)
            {
                livingEntity.sendBreakAnimation(equipmentSlotType);
            }
        }
    }

    public static void fixItem(ItemStack stack)
    {
        CompoundNBT stackCompound = stack.getOrCreateTag();

        if(stackCompound.getBoolean(BROKEN_KEY))
        {
            ListNBT enchantments = stack.getEnchantmentTagList();

            if(stackCompound.contains(DISABLED_ENCHANTMENTS_KEY))
            {
                ListNBT disabledEnchantments = stackCompound.getList(DISABLED_ENCHANTMENTS_KEY, 10);

                for(int tagIndex = 0; tagIndex < disabledEnchantments.size(); tagIndex++)
                {
                    enchantments.add(disabledEnchantments.remove(tagIndex));
                }
            }

            stackCompound.remove(DISABLED_ENCHANTMENTS_KEY);
            stackCompound.put("Enchantments", enchantments);
            stackCompound.putBoolean(BROKEN_KEY, false);
        }
    }

    public static boolean isItemBroken(ItemStack stack)
    {
        return stack.getOrCreateTag().getBoolean(BROKEN_KEY);
    }

    public static boolean canItemBeBroken(ItemStack stack, int damageAmount)
    {
        if(isItemBroken(stack))
        {
            return false;
        }

        int usesRemaining = (stack.getMaxDamage() - stack.getDamage());
        boolean canBeBroken = !stack.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack) > 0;
        return (usesRemaining - damageAmount) <= 0 && canBeBroken;
    }
}
