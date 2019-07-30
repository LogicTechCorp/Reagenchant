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

package logictechcorp.reagenchant.api;

import logictechcorp.reagenchant.api.internal.IReagenchantAPI;
import logictechcorp.reagenchant.api.internal.ReagenchantAPIStub;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public final class ReagenchantAPI
{
    private static IReagenchantAPI instance = ReagenchantAPIStub.INSTANCE;

    private ReagenchantAPI()
    {
    }

    public static IReagenchantAPI getInstance()
    {
        return instance;
    }

    public static void setInstance(IReagenchantAPI instance)
    {
        ModContainer mod = Loader.instance().activeModContainer();

        if(mod != null && mod.getModId().equals("reagenchant"))
        {
            ReagenchantAPI.instance = instance;
        }
    }
}
