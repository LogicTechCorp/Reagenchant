/*
 * Reagenchant
 * Copyright (c) 2019-2020 by LogicTechCorp
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

package logictechcorp.reagenchant.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import logictechcorp.libraryex.command.CommandCompletion;
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

    public static ArgumentBuilder<CommandSource, ?> register()
    {
        return Commands
                .literal("reagent")
                .then(registerCreation())
                .then(registerDefaultAddition())
                .then(registerCustomAddition())
                .then(registerRemoval())
                .then(registerDeletion());
    }

    private static ArgumentBuilder<CommandSource, ?> registerCreation()
    {
        return Commands
                .literal("create")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("item", ItemArgument.item()).executes(ReagentCommand::createReagent));
    }

    private static ArgumentBuilder<CommandSource, ?> registerDefaultAddition()
    {
        return Commands
                .literal("addEnchantment")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("item", ItemArgument.item())
                        .then(Commands.argument("enchantment", EnchantmentArgument.enchantment())
                                .executes(ReagentCommand::addDefaultToReagent)));
    }

    private static ArgumentBuilder<CommandSource, ?> registerCustomAddition()
    {
        return Commands
                .literal("addEnchantment")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("item", ItemArgument.item())
                        .then(Commands.argument("enchantment", EnchantmentArgument.enchantment())
                                .then(Commands.argument("minimum level", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("maximum level", IntegerArgumentType.integer(1))
                                                .then(Commands.argument("probability", DoubleArgumentType.doubleArg(0.0D))
                                                        .then(Commands.argument("cost", IntegerArgumentType.integer(0)).executes(ReagentCommand::addCustomToReagent)))))));
    }

    private static ArgumentBuilder<CommandSource, ?> registerRemoval()
    {
        return Commands
                .literal("removeEnchantment")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("item", ItemArgument.item())
                        .then(Commands.argument("enchantment", EnchantmentArgument.enchantment()).executes(ReagentCommand::removeFromReagent)));
    }

    private static ArgumentBuilder<CommandSource, ?> registerDeletion()
    {
        return Commands
                .literal("delete")
                .requires(source -> source.hasPermissionLevel(2))
                .then(Commands.argument("item", ItemArgument.item()).executes(ReagentCommand::deleteReagent));
    }

    private static int createReagent(CommandContext<CommandSource> context)
    {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();
        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);

        if(reagent.isEmpty() && item != Items.AIR)
        {
            reagent = Reagenchant.REAGENT_MANAGER.createReagent(item);
            Reagenchant.REAGENT_MANAGER.registerReagent(reagent);
            source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.create.success", item.getRegistryName()), true);
        }
        else
        {
            source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.create.override", item.getRegistryName()));
        }

        saveReagentFile(server, reagent);
        return CommandCompletion.SUCCESS;
    }

    private static int addDefaultToReagent(CommandContext<CommandSource> context)
    {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();

        if(item != Items.AIR)
        {
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);
            Enchantment enchantment = EnchantmentArgument.getEnchantment(context, "enchantment");

            if(reagent.isEmpty())
            {
                source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.add.error", item.getRegistryName()));
                return CommandCompletion.FAILURE;
            }

            reagent.addEnchantment(new ReagentEnchantmentData(enchantment, enchantment.getMinLevel(), enchantment.getMaxLevel(), 0.5F, 1));
            source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.add.success", enchantment.getRegistryName(), item.getRegistryName()), true);
            saveReagentFile(server, reagent);
            return CommandCompletion.SUCCESS;
        }

        return CommandCompletion.FAILURE;
    }

    private static int addCustomToReagent(CommandContext<CommandSource> context)
    {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();

        if(item != Items.AIR)
        {
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);
            Enchantment enchantment = EnchantmentArgument.getEnchantment(context, "enchantment");
            int minimumLevel = IntegerArgumentType.getInteger(context, "minimum level");
            int maximumLevel = IntegerArgumentType.getInteger(context, "maximum level");
            double probability = DoubleArgumentType.getDouble(context, "probability");
            int cost = IntegerArgumentType.getInteger(context, "cost");

            if(reagent.isEmpty())
            {
                source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.add.error", item.getRegistryName()));
                return CommandCompletion.FAILURE;
            }

            reagent.addEnchantment(new ReagentEnchantmentData(enchantment, minimumLevel, maximumLevel, probability, cost));
            source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.add.success", enchantment.getRegistryName(), item.getRegistryName()), true);
            saveReagentFile(server, reagent);
            return CommandCompletion.SUCCESS;
        }

        return CommandCompletion.FAILURE;
    }

    private static int removeFromReagent(CommandContext<CommandSource> context)
    {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();

        if(item != Items.AIR)
        {
            Enchantment enchantment = EnchantmentArgument.getEnchantment(context, "enchantment");
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);

            if(reagent.isEmpty())
            {
                source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.remove.error", item.getRegistryName()));
                return CommandCompletion.FAILURE;
            }

            reagent.removeEnchantment(enchantment);
            source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.remove.success", enchantment.getRegistryName(), item.getRegistryName()), true);
            saveReagentFile(server, reagent);
            return CommandCompletion.SUCCESS;
        }

        return CommandCompletion.FAILURE;
    }

    private static int deleteReagent(CommandContext<CommandSource> context)
    {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();
        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);

        if(reagent.isEmpty())
        {
            source.sendErrorMessage(new TranslationTextComponent("command.reagenchant.reagent.delete.error", item.getRegistryName()));
            return CommandCompletion.FAILURE;
        }

        Reagenchant.REAGENT_MANAGER.unregisterReagent(reagent);
        source.sendFeedback(new TranslationTextComponent("command.reagenchant.reagent.delete.success", item.getRegistryName()), true);
        deleteReagentFile(server, reagent);
        return CommandCompletion.SUCCESS;
    }

    private static void saveReagentFile(MinecraftServer server, Reagent reagent)
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
            packObject.addProperty("description", "Custom reagent data pack.");
            packObject.addProperty("pack_format", 4);
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

    private static void deleteReagentFile(MinecraftServer server, Reagent reagent)
    {
        File datapackDirectory = server.getActiveAnvilConverter().getFile(server.getFolderName(), "datapacks");
        File customReagentDirectory = new File(datapackDirectory, "/custom_reagent_pack/data/" + Reagenchant.MOD_ID + "/reagents/");
        File customReagentFile = new File(customReagentDirectory, reagent.getItem().getRegistryName().toString().replace(":", "/") + ".json");

        if(customReagentFile.exists())
        {
            customReagentFile.delete();
        }
    }
}
