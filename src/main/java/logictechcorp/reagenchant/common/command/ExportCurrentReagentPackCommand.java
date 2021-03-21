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

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import logictechcorp.reagenchant.common.reagent.Reagent;
import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.FolderName;

import java.nio.file.Path;

public class ExportCurrentReagentPackCommand {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("exportCurrentReagentPack")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(ExportCurrentReagentPackCommand::exportCurrentReagentPack);
    }

    private static int exportCurrentReagentPack(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        Path datapackDirectoryPath = server.func_240776_a_(FolderName.DATAPACKS).toAbsolutePath().normalize();

        for(Reagent reagent : Reagenchant.REAGENT_MANAGER.getReagents().values()) {
            ReagentCommand.saveReagentFile(datapackDirectoryPath, "current_reagent_pack", reagent);
        }

        Reagenchant.LOGGER.info("Exported current reagent pack to: {}", datapackDirectoryPath);
        return CommandCompletion.SUCCESS;
    }
}
