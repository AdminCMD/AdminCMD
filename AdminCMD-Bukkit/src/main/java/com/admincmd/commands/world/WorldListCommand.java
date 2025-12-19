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
package com.admincmd.commands.world;

import com.admincmd.commandapi.*;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.world.WorldManager;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandHandler
public class WorldListCommand {

    @BaseCommand(command = "worlds", sender = Sender.ALL, permission = "admincmd.world.list", helpArguments = {""}, aliases = "wlist", async = true)
    public CommandResult executeConsole(CommandSender sender, CommandArgs args) {
        if (!args.isEmpty()) {
            return CommandResult.ERROR;
        }
        List<String> names = WorldManager.getWorldNames();
        String ret = String.join(", ", names);
        String msg = Locales.WORLD_WORLD_LOADED.getString();
        msg = msg.replaceAll("%worlds%", ret);
        sender.sendMessage(Messager.MessageType.INFO.getPrefix() + msg);
        return CommandResult.SUCCESS;
    }

}
