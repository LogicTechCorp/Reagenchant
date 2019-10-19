package logictechcorp.reagenchant.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import logictechcorp.libraryex.command.CommandAction;
import logictechcorp.libraryex.command.argument.CommandActionArgument;
import logictechcorp.reagenchant.Reagenchant;
import logictechcorp.reagenchant.reagent.Reagent;
import logictechcorp.reagenchant.reagent.ReagentEnchantmentData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.loading.FileUtils;
import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReagentCommand
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands
                .literal("reagent")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("action", CommandActionArgument.create())
                        .then(Commands.argument("item", ItemArgument.item())
                                .executes(ReagentCommand::createReagent)
                                .then(Commands.argument("enchantment", EnchantmentArgument.enchantment())
                                        .executes(ReagentCommand::defaultReagent)
                                        .then(Commands.argument("minimum level", IntegerArgumentType.integer(1))
                                                .then(Commands.argument("maximum level", IntegerArgumentType.integer(1))
                                                        .then(Commands.argument("probability", DoubleArgumentType.doubleArg(0.0D))
                                                                .then(Commands.argument("cost", IntegerArgumentType.integer(0))
                                                                        .executes(ReagentCommand::customReagent)))))))));
    }

    private static int createReagent(CommandContext<CommandSource> context)
    {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();

        CommandAction commandAction = CommandActionArgument.getCommandAction(context, "action");
        Item item = ItemArgument.getItem(context, "item").getItem();

        if(item != Items.AIR)
        {
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);
            String itemName = item.getRegistryName().toString();

            if(commandAction == CommandAction.CREATE)
            {
                if(reagent == null)
                {
                    reagent = new Reagent(item);
                    source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.create.success", itemName), true);
                }
                else
                {
                    source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.create.override", itemName));
                }

                saveReagent(server, reagent);
            }
            else if(commandAction == CommandAction.ADD)
            {
                source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.add.error", "null", itemName));
            }
            else if(commandAction == CommandAction.REMOVE)
            {
                source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.remove.error", "null", itemName));
            }
        }

        return commandAction.ordinal();
    }

    private static int customReagent(CommandContext<CommandSource> context)
    {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();

        CommandAction commandAction = CommandActionArgument.getCommandAction(context, "action");
        Item item = ItemArgument.getItem(context, "item").getItem();
        Enchantment enchantment = EnchantmentArgument.getEnchantment(context, "enchantment");
        int minimumLevel = IntegerArgumentType.getInteger(context, "minimum level");
        int maximumLevel = IntegerArgumentType.getInteger(context, "maximum level");
        double probability = DoubleArgumentType.getDouble(context, "probability");
        int cost = IntegerArgumentType.getInteger(context, "cost");

        if(item != Items.AIR)
        {
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);
            String itemName = item.getRegistryName().toString();
            String enchantmentName = enchantment.getRegistryName().toString();

            if(commandAction == CommandAction.CREATE)
            {
                if(reagent == null)
                {
                    reagent = new Reagent(item);
                    source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.create.success", itemName), true);
                }
                else
                {
                    source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.create.override", itemName));
                }

                saveReagent(server, reagent);
            }
            else if(commandAction == CommandAction.ADD)
            {
                if(reagent == null)
                {
                    source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.add.error", enchantmentName, itemName));
                }
                else
                {
                    reagent.addEnchantment(new ReagentEnchantmentData(enchantment, minimumLevel, maximumLevel, probability, cost));

                    saveReagent(server, reagent);
                    source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.add.success", enchantmentName, itemName), true);
                }
            }
            else if(commandAction == CommandAction.REMOVE)
            {
                if(reagent == null)
                {
                    source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.remove.error", enchantmentName, itemName));
                }
                else
                {
                    reagent.removeEnchantment(enchantment);

                    saveReagent(server, reagent);
                    source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.remove.success", enchantmentName, itemName), true);
                }
            }
        }

        return commandAction.ordinal();
    }

    private static int defaultReagent(CommandContext<CommandSource> context)
    {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();

        CommandAction commandAction = CommandActionArgument.getCommandAction(context, "action");
        Item item = ItemArgument.getItem(context, "item").getItem();
        Enchantment enchantment = EnchantmentArgument.getEnchantment(context, "enchantment");

        if(item != Items.AIR)
        {
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);
            String itemName = item.getRegistryName().toString();
            String enchantmentName = enchantment.getRegistryName().toString();

            if(commandAction == CommandAction.CREATE)
            {
                if(reagent == null)
                {
                    reagent = new Reagent(item);
                    source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.create.success", itemName), true);
                }
                else
                {
                    source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.create.override", itemName));
                }

                saveReagent(server, reagent);
            }
            else if(commandAction == CommandAction.ADD)
            {
                if(reagent == null)
                {
                    source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.add.error", enchantmentName, itemName));
                }
                else
                {
                    reagent.addEnchantment(new ReagentEnchantmentData(enchantment, 0.5F, 1));

                    saveReagent(server, reagent);
                    source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.add.success", enchantmentName, itemName), true);
                }
            }
            else if(commandAction == CommandAction.REMOVE)
            {
                if(reagent == null)
                {
                    source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.remove.error", enchantmentName, itemName));
                }
                else
                {
                    reagent.removeEnchantment(enchantment);

                    saveReagent(server, reagent);
                    source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.remove.success", enchantmentName, itemName), true);
                }
            }
        }

        return commandAction.ordinal();
    }

    private static void saveReagent(MinecraftServer server, Reagent reagent)
    {
        File datapackDirectory = server.getActiveAnvilConverter().getFile(server.getFolderName(), "datapacks");
        File customReagentDatapackFile = new File(datapackDirectory, "custom_reagent_pack/pack.mcmeta");
        File customReagentDirectory = new File(datapackDirectory, "custom_reagent_pack/data/" + Reagenchant.MOD_ID + "/reagents");
        File customReagentFile = new File(customReagentDirectory, reagent.getItem().getRegistryName().toString().replace(":", "/") + ".json");

        if(!customReagentDatapackFile.exists())
        {
            FileUtils.getOrCreateDirectory(customReagentDatapackFile.getParentFile().toPath(), customReagentDatapackFile.getParentFile().toString());

            JsonObject jsonObject = new JsonObject();
            JsonObject packObject = new JsonObject();
            packObject.addProperty("pack_format", 1);
            packObject.addProperty("description", "Custom reagent data pack");
            jsonObject.add("pack", packObject);

            try
            {
                FileWriter fileWriter = new FileWriter(customReagentDatapackFile);
                fileWriter.write(GSON.toJson(jsonObject));
                IOUtils.closeQuietly(fileWriter);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        if(!customReagentFile.exists())
        {
            FileUtils.getOrCreateDirectory(customReagentFile.getParentFile().toPath(), customReagentFile.getParentFile().toString());
        }

        JsonObject reagentObject = new JsonObject();
        reagentObject.addProperty("item", reagent.getItem().getRegistryName().toString());

        JsonArray enchantmentArray = new JsonArray();

        for(Enchantment enchantment : reagent.getEnchantments())
        {
            JsonObject enchantmentObject = new JsonObject();
            ReagentEnchantmentData enchantmentData = reagent.getReagentEnchantmentData(enchantment);
            enchantmentObject.addProperty("enchantment", enchantment.getRegistryName().toString());
            enchantmentObject.addProperty("minimumEnchantmentLevel", enchantmentData.getMinimumEnchantmentLevel());
            enchantmentObject.addProperty("maximumEnchantmentLevel", enchantmentData.getMaximumEnchantmentLevel());
            enchantmentObject.addProperty("probability", enchantmentData.getEnchantmentProbability());
            enchantmentObject.addProperty("reagentCost", enchantmentData.getReagentCost());
            enchantmentArray.add(enchantmentObject);
        }

        reagentObject.add("enchantments", enchantmentArray);

        try
        {
            FileWriter fileWriter = new FileWriter(customReagentFile);
            fileWriter.write(GSON.toJson(reagentObject));
            IOUtils.closeQuietly(fileWriter);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
