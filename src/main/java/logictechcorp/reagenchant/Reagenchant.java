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

package logictechcorp.reagenchant;

import logictechcorp.libraryex.api.IModData;
import logictechcorp.libraryex.api.IProxy;
import logictechcorp.reagenchant.api.ReagenchantAPI;
import logictechcorp.reagenchant.api.internal.iface.IReagenchantAPI;
import logictechcorp.reagenchant.api.internal.iface.IReagentManager;
import logictechcorp.reagenchant.api.internal.iface.IReagentRegistry;
import logictechcorp.reagenchant.init.ReagenchantReagents;
import logictechcorp.reagenchant.proxy.ClientProxy;
import logictechcorp.reagenchant.proxy.ServerProxy;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reagenchant.MOD_ID)
public class Reagenchant implements IModData, IReagenchantAPI
{
    public static final String MOD_ID = "reagenchant";

    public static Reagenchant instance;
    public static IProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger("Reagenchant");

    public Reagenchant()
    {
        Reagenchant.instance = this;
        Reagenchant.proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        proxy.setupSidedListeners();
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        ReagenchantAPI.setInstance(this);
        ReagenchantReagents.initReagents();
    }

    @Override
    public String getModId()
    {
        return MOD_ID;
    }

    @Override
    public ItemGroup getItemGroup()
    {
        return ItemGroup.DECORATIONS;
    }

    @Override
    public boolean writeRecipesToJson()
    {
        return false;
    }

    @Override
    public boolean isStub()
    {
        return false;
    }

    @Override
    public IReagentRegistry getReagentRegistry()
    {
        return ReagentRegistry.INSTANCE;
    }

    @Override
    public IReagentManager getReagentManager()
    {
        return ReagentManager.INSTANCE;
    }

    public static ResourceLocation getResource(String name)
    {
        return new ResourceLocation(Reagenchant.MOD_ID + ":" + name);
    }
}
