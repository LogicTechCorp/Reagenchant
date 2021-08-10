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

package logictechcorp.reagenchant.common.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import logictechcorp.reagenchant.common.network.item.reagent.MessageSUpdateReagentsPacket;
import logictechcorp.reagenchant.common.reagent.Reagent;
import logictechcorp.reagenchant.common.reagent.ReagentEnchantData;
import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.fml.network.PacketDistributor;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReagentCommand {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands
                .literal("reagent")
                .then(registerCreation())
                .then(registerDefaultAddition())
                .then(registerCustomAddition())
                .then(registerRemoval())
                .then(registerDeletion());
    }

    private static ArgumentBuilder<CommandSource, ?> registerCreation() {
        return Commands
                .literal("create")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("item", ItemArgument.item()).executes(ReagentCommand::createReagent));
    }

    private static ArgumentBuilder<CommandSource, ?> registerDefaultAddition() {
        return Commands
                .literal("addEnchantment")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("item", ItemArgument.item())
                        .then(Commands.argument("enchantment", EnchantmentArgument.enchantment())
                                .executes(ReagentCommand::addDefaultToReagent)));
    }

    private static ArgumentBuilder<CommandSource, ?> registerCustomAddition() {
        return Commands
                .literal("addEnchantment")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("item", ItemArgument.item())
                        .then(Commands.argument("enchantment", EnchantmentArgument.enchantment())
                                .then(Commands.argument("minimum enchantment level", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("maximum enchantment level", IntegerArgumentType.integer(1))
                                                .then(Commands.argument("probability", FloatArgumentType.floatArg(0.0F))
                                                        .then(Commands.argument("reagent cost", IntegerArgumentType.integer(0)).executes(ReagentCommand::addCustomToReagent)))))));
    }

    private static ArgumentBuilder<CommandSource, ?> registerRemoval() {
        return Commands
                .literal("removeEnchantment")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("item", ItemArgument.item())
                        .then(Commands.argument("enchantment", EnchantmentArgument.enchantment())
                                .executes(ReagentCommand::removeFromReagent)));
    }

    private static ArgumentBuilder<CommandSource, ?> registerDeletion() {
        return Commands
                .literal("delete")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("item", ItemArgument.item()).executes(ReagentCommand::deleteReagent));
    }

    private static int createReagent(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();
        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);

        if(reagent.isEmpty()) {
            source.sendSuccess(new TranslationTextComponent("command.reagenchant.reagent.create.success", item.getRegistryName()), true);
        }
        else {
            source.sendFailure(new TranslationTextComponent("command.reagenchant.reagent.create.override", item.getRegistryName()));
        }

        reagent = Reagenchant.REAGENT_MANAGER.createReagent(item);
        Reagenchant.REAGENT_MANAGER.registerReagent(reagent);
        saveReagentFile(server, reagent);
        sendClientSyncPacket(source);
        return CommandCompletion.SUCCESS;
    }

    private static int addDefaultToReagent(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();

        if(item != Items.AIR) {
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);
            Enchantment enchantment = EnchantmentArgument.getEnchantment(context, "enchantment");

            if(reagent.isEmpty()) {
                source.sendFailure(new TranslationTextComponent("command.reagenchant.reagent.add.error", item.getRegistryName()));
                return CommandCompletion.FAILURE;
            }

            reagent.addEnchantment(new ReagentEnchantData(enchantment, enchantment.getMinLevel(), enchantment.getMaxLevel(), 0.5F, 1));
            source.sendSuccess(new TranslationTextComponent("command.reagenchant.reagent.add.success", enchantment.getRegistryName(), item.getRegistryName()), true);
            saveReagentFile(server, reagent);
            sendClientSyncPacket(source);
            return CommandCompletion.SUCCESS;
        }

        return CommandCompletion.FAILURE;
    }

    private static int addCustomToReagent(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();

        if(item != Items.AIR) {
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);
            Enchantment enchantment = EnchantmentArgument.getEnchantment(context, "enchantment");
            int minimumLevel = IntegerArgumentType.getInteger(context, "minimum enchantment level");
            int maximumLevel = IntegerArgumentType.getInteger(context, "maximum enchantment level");
            float enchantmentProbability = FloatArgumentType.getFloat(context, "probability");
            int reagentCost = IntegerArgumentType.getInteger(context, "reagent cost");

            if(reagent.isEmpty()) {
                source.sendFailure(new TranslationTextComponent("command.reagenchant.reagent.add.error", item.getRegistryName()));
                return CommandCompletion.FAILURE;
            }

            reagent.addEnchantment(new ReagentEnchantData(enchantment, minimumLevel, maximumLevel, enchantmentProbability, reagentCost));
            source.sendSuccess(new TranslationTextComponent("command.reagenchant.reagent.add.success", enchantment.getRegistryName(), item.getRegistryName()), true);
            saveReagentFile(server, reagent);
            sendClientSyncPacket(source);
            return CommandCompletion.SUCCESS;
        }

        return CommandCompletion.FAILURE;
    }

    private static int removeFromReagent(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();

        if(item != Items.AIR) {
            Enchantment enchantment = EnchantmentArgument.getEnchantment(context, "enchantment");
            Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);

            if(reagent.isEmpty()) {
                source.sendFailure(new TranslationTextComponent("command.reagenchant.reagent.remove.error", item.getRegistryName()));
                return CommandCompletion.FAILURE;
            }

            reagent.removeEnchantment(enchantment);
            source.sendSuccess(new TranslationTextComponent("command.reagenchant.reagent.remove.success", enchantment.getRegistryName(), item.getRegistryName()), true);
            saveReagentFile(server, reagent);
            sendClientSyncPacket(source);
            return CommandCompletion.SUCCESS;
        }

        return CommandCompletion.FAILURE;
    }

    private static int deleteReagent(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Item item = ItemArgument.getItem(context, "item").getItem();
        Reagent reagent = Reagenchant.REAGENT_MANAGER.getReagent(item);

        if(reagent.isEmpty()) {
            source.sendFailure(new TranslationTextComponent("command.reagenchant.reagent.delete.error", item.getRegistryName()));
            return CommandCompletion.FAILURE;
        }

        Reagenchant.REAGENT_MANAGER.unregisterReagent(reagent);
        source.sendSuccess(new TranslationTextComponent("command.reagenchant.reagent.delete.success", item.getRegistryName()), true);
        deleteReagentFile(server, reagent);
        sendClientSyncPacket(source);
        return CommandCompletion.SUCCESS;
    }

    static void saveReagentFile(Path datapackDirectoryPath, String reagentPackName, Reagent reagent) {
        Path reagentPackPath = datapackDirectoryPath.resolve(reagentPackName);
        Path reagentPackReagentsPath = reagentPackPath.resolve(Paths.get("data", Reagenchant.MOD_ID, "reagents"));

        if(!Files.exists(reagentPackReagentsPath)) {
            try {
                Files.createDirectories(reagentPackReagentsPath);
            }
            catch(IOException e) {
                e.printStackTrace();
                return;
            }
        }

        Path reagentPackMCMetaPath = reagentPackPath.resolve("pack.mcmeta");

        if(!Files.exists(reagentPackMCMetaPath)) {
            try {
                Files.createFile(reagentPackMCMetaPath);
                if(Files.exists(reagentPackMCMetaPath)) {
                    JsonObject jsonObject = new JsonObject();
                    JsonObject packObject = new JsonObject();
                    packObject.addProperty("description", "Custom reagent pack.");
                    packObject.addProperty("pack_format", 4);
                    jsonObject.add("pack", packObject);

                    try(FileWriter fileWriter = new FileWriter(reagentPackMCMetaPath.toFile())) {
                        fileWriter.write(GSON.toJson(jsonObject));
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        Path reagentPackReagentFilePath = reagentPackReagentsPath.resolve(reagent.getItem().getRegistryName().getPath().replace(":", "/") + ".json");

        if(!Files.exists(reagentPackReagentFilePath)) {
            try {
                Files.createFile(reagentPackReagentFilePath);
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        if(Files.exists(reagentPackReagentFilePath)) {
            JsonObject reagentObject = new JsonObject();
            reagentObject.addProperty("item", reagent.getItem().getRegistryName().toString());

            JsonArray enchantmentArray = new JsonArray();

            for(Enchantment enchantment : reagent.getEnchantments()) {
                JsonObject enchantmentObject = new JsonObject();
                ReagentEnchantData enchantmentData = reagent.getReagentEnchantData(enchantment);
                enchantmentObject.addProperty("enchantment", enchantment.getRegistryName().toString());
                enchantmentObject.addProperty("minimumEnchantmentLevel", enchantmentData.getMinimumEnchantmentLevel());
                enchantmentObject.addProperty("maximumEnchantmentLevel", enchantmentData.getMaximumEnchantmentLevel());
                enchantmentObject.addProperty("probability", enchantmentData.getEnchantmentProbability());
                enchantmentObject.addProperty("reagentCost", enchantmentData.getReagentCost());
                enchantmentArray.add(enchantmentObject);
            }

            reagentObject.add("enchantments", enchantmentArray);

            try(FileWriter fileWriter = new FileWriter(reagentPackReagentFilePath.toFile())) {
                fileWriter.write(GSON.toJson(reagentObject));
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveReagentFile(MinecraftServer server, Reagent reagent) {
        Path datapackDirectoryPath = server.getWorldPath(FolderName.DATAPACK_DIR).toAbsolutePath().normalize();
        saveReagentFile(datapackDirectoryPath, "custom_reagent_pack", reagent);
    }

    static void deleteReagentFile(Path datapackDirectoryPath, String reagentPackName, Reagent reagent) {
        Path customReagentDirectory = datapackDirectoryPath.resolve(Paths.get(reagentPackName, "data", Reagenchant.MOD_ID, "reagents"));
        Path customReagentFile = customReagentDirectory.resolve(reagent.getItem().getRegistryName().getPath().replace(":", "/") + ".json");

        try {
            Files.deleteIfExists(customReagentFile);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReagentFile(MinecraftServer server, Reagent reagent) {
        Path datapackDirectoryPath = server.getWorldPath(FolderName.DATAPACK_DIR).toAbsolutePath().normalize();
        deleteReagentFile(datapackDirectoryPath, "custom_reagent_pack", reagent);
    }

    private static void sendClientSyncPacket(CommandSource source) {
        try {
            ServerPlayerEntity player = source.getPlayerOrException();
            Reagenchant.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageSUpdateReagentsPacket(Reagenchant.REAGENT_MANAGER.getReagents().values()));
        }
        catch(CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

}
