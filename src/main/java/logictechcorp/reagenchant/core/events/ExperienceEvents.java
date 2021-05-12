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

package logictechcorp.reagenchant.core.events;

import logictechcorp.reagenchant.core.Reagenchant;
import logictechcorp.reagenchant.core.ReagenchantConfig;
import logictechcorp.reagenchant.core.util.ExperienceUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class ExperienceEvents {
    public static final String PLAYER_XP_KEPT_ON_DEATH = Reagenchant.MOD_ID + ":PlayerXpKeptOnDeath";

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        LivingEntity living = event.getEntityLiving();

        if(living instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) living;
            int playerXp = ExperienceUtil.getPlayerExperience(player);
            int droppedXp = (int) (playerXp * ReagenchantConfig.COMMON.percentOfXpDroppedOnDeath.get());
            int lostXp = (int) (droppedXp * ReagenchantConfig.COMMON.percentOfDroppedXpLost.get());

            event.setDroppedExperience(droppedXp - lostXp);

            CompoundNBT persistentCompound = player.getPersistentData();

            if(!persistentCompound.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
                persistentCompound.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
            }

            CompoundNBT persistentDataCompound = persistentCompound.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
            persistentDataCompound.putInt(PLAYER_XP_KEPT_ON_DEATH, playerXp - droppedXp);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();

        if(!event.isEndConquered()) {
            CompoundNBT persistentCompound = player.getPersistentData();

            if(persistentCompound.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
                CompoundNBT persistentDataCompound = persistentCompound.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
                player.giveExperiencePoints(persistentDataCompound.getInt(PLAYER_XP_KEPT_ON_DEATH));
                persistentDataCompound.remove(PLAYER_XP_KEPT_ON_DEATH);
            }
        }
    }
}
