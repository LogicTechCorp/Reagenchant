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

package logictechcorp.reagenchant.core.util;

import net.minecraft.entity.player.PlayerEntity;

public class ExperienceUtil {
    public static int getPlayerExperience(PlayerEntity player) {
        return getExperienceForLevel(player.experienceLevel) + (int) (player.experience * player.xpBarCap());
    }

    public static int getExperienceForLevel(int experienceLevel) {
        if(experienceLevel <= 16) {
            return (6 * experienceLevel) + (experienceLevel * experienceLevel);
        }
        else if(experienceLevel <= 31) {
            return (int) (2.5F * experienceLevel * experienceLevel) - (int) (40.5 * experienceLevel) + 360;
        }
        else {
            return (int) (4.5F * experienceLevel * experienceLevel) - (int) (162.5 * experienceLevel) + 2220;
        }
    }
}
