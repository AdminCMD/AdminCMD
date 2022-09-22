/*
 * This file is part of AdminCMD
 * Copyright (C) 2020 AdminCMD Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.admincmd.commands.server;

import com.admincmd.Main;
import com.admincmd.commandapi.*;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager.MessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

@CommandHandler
public class ReloadCommand {

    @BaseCommand(command = "acreload", sender = Sender.ALL, aliases = "reload", helpArguments = {"", "<plugin>"})
    public CommandResult executeConsole(CommandSender sender, CommandArgs args) {
        if (args.isEmpty()) {
            Main.getInstance().getServer().reload();
            sender.sendMessage(MessageType.INFO.getPrefix() + Locales.SERVER_RELOAD_FULL.getString());
            return CommandResult.SUCCESS;
        }

        if (args.getLength() != 1) {
            return CommandResult.ERROR;
        }

        Plugin pl = Main.getInstance().getServer().getPluginManager().getPlugin(args.getString(0));
        if (pl == null) {
            sender.sendMessage(MessageType.ERROR.getPrefix() + Locales.SERVER_RELOAD_NOT_FOUND.getString());
            return CommandResult.SUCCESS;
        }
        Main.getInstance().getServer().getPluginManager().disablePlugin(pl);
        Main.getInstance().getServer().getPluginManager().enablePlugin(pl);
        sender.sendMessage(MessageType.INFO.getPrefix() + Locales.SERVER_RELOAD_SINGLE.getString());
        return CommandResult.SUCCESS;
    }
}
