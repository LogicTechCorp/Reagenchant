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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.quark.api.event.ModuleStateChangedEvent;

@Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
public class QuarkEvents {
    public static boolean matrixEnchantingEnabled;

    @SubscribeEvent
    public static void onModuleStateChanged(ModuleStateChangedEvent event) {
        String moduleName = event.eventName;
        boolean enabled = event.enabled;

        if(moduleName.equals("matrix_enchanting")) {
            matrixEnchantingEnabled = enabled;
        }
    }
}
