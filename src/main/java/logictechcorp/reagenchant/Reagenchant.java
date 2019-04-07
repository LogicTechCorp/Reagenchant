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

import logictechcorp.libraryex.IModData;
import logictechcorp.libraryex.proxy.IProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reagenchant.MOD_ID, name = Reagenchant.NAME, version = Reagenchant.VERSION, dependencies = Reagenchant.DEPENDENCIES)
public class Reagenchant implements IModData
{
    public static final String MOD_ID = "reagenchant";
    public static final String NAME = "Reagenchant";
    public static final String VERSION = "1.0.0";
    public static final String DEPENDENCIES = "required-after:libraryex@[1.0.9,);";

    @Mod.Instance(MOD_ID)
    public static Reagenchant instance;

    @SidedProxy(clientSide = "logictechcorp.reagenchant.proxy.ClientProxy", serverSide = "logictechcorp.reagenchant.proxy.ServerProxy")
    public static IProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger("Reagenchant");

    @Mod.EventHandler
    public void onFMLPreInitialization(FMLPreInitializationEvent event)
    {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @Mod.EventHandler
    public void onFMLPostInitialization(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }

    @Override
    public String getModId()
    {
        return MOD_ID;
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return CreativeTabs.DECORATIONS;
    }

    @Override
    public boolean writeRecipeJsons()
    {
        return false;
    }

    public static ResourceLocation getResource(String name)
    {
        return new ResourceLocation(Reagenchant.MOD_ID + ":" + name);
    }
}