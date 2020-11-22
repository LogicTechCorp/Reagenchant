package logictechcorp.reagenchant;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config.LangKey("config." + Reagenchant.MOD_ID + ":title")
@Config(modid = Reagenchant.MOD_ID, name = Reagenchant.MOD_ID + "/" + Reagenchant.MOD_ID, category = "global")
public class ReagenchantConfig
{
    @Config.Name("reagent")
    @Config.LangKey("config." + Reagenchant.MOD_ID + ":reagent")
    public static Reagent reagent = new Reagent();

    public static class Reagent
    {
        @Config.Name("general")
        @Config.LangKey("config." + Reagenchant.MOD_ID + ":reagent.general")
        public General general = new General();

        public class General
        {
            @Config.LangKey("config." + Reagenchant.MOD_ID + ":reagent.general.useGlobalReagentConfigs")
            @Config.Comment({"Use global reagent configs"})
            public boolean useGlobalReagentConfigs = false;

            @Config.LangKey("config." + Reagenchant.MOD_ID + ":reagent.general.usePerWorldReagentConfigs")
            @Config.Comment({"Use per world reagent configs"})
            public boolean usePerWorldReagentConfigs = true;
        }
    }

    @Mod.EventBusSubscriber(modid = Reagenchant.MOD_ID)
    public static class ConfigSyncHandler
    {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if(event.getModID().equals(Reagenchant.MOD_ID))
            {
                ConfigManager.sync(Reagenchant.MOD_ID, Config.Type.INSTANCE);
                Reagenchant.LOGGER.info("Configuration has been saved.");
            }
        }
    }
}
