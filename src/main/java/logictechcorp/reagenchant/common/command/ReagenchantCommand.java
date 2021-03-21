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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import logictechcorp.reagenchant.core.Reagenchant;
import net.minecraft.command.CommandSource;

public class ReagenchantCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder
                .<CommandSource>literal(Reagenchant.MOD_ID)
                .then(ReagentCommand.register())
                .then(ExportCurrentReagentPackCommand.register())
        );
    }
}
