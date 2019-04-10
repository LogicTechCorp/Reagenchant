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

import com.google.common.collect.ImmutableList;
import logictechcorp.reagenchant.api.reagent.IReagent;
import net.minecraft.item.Item;

import java.util.ArrayList;

public class ReagenchantAPIStub implements IReagenchantAPI
{
    static final IReagenchantAPI INSTANCE = new ReagenchantAPIStub();

    private ReagenchantAPIStub()
    {
    }

    @Override
    public boolean isStub()
    {
        return true;
    }

    @Override
    public void registerReagent(IReagent reagent)
    {

    }

    @Override
    public boolean isReagentItem(Item item)
    {
        return false;
    }

    @Override
    public IReagent getReagent(Item associatedItem)
    {
        return null;
    }

    @Override
    public ImmutableList<IReagent> getReagents()
    {
        return ImmutableList.copyOf(new ArrayList<>());
    }
}
