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
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Config;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.world.ACWorld;
import com.admincmd.world.WorldManager;
import org.bukkit.entity.Player;

@CommandHandler
public class SunCommand {

    @BaseCommand(command = "sun", sender = Sender.PLAYER, permission = "admincmd.world.sun", helpArguments = {"", "<-w world>"})
    public CommandResult executeSun(Player sender, CommandArgs args) {
        ACPlayer se = PlayerManager.getPlayer(sender);
        if (args.isEmpty()) {
            ACWorld world = WorldManager.getWorld(sender.getWorld());
            WorldManager.setSun(world);
            String msg = Locales.WORLD_WEATHER_CLEAR.getString().replaceAll("%world%", sender.getWorld().getName());
            return Messager.sendMessage(se, msg, Messager.MessageType.INFO);
        }

        if (args.hasFlag("w")) {
            CommandArgs.Flag flag = args.getFlag("w");
            if (!flag.isWorld()) {
                return CommandResult.NOT_A_WORLD;
            }

            if (!sender.hasPermission("admincmd.world.sun.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }

            ACWorld world = flag.getWorld();
            WorldManager.setSun(world);

            String msg;
            if (Config.BUNGEECORD.getBoolean()) {
                msg = Locales.WORLD_WEATHER_CLEAR.getString().replaceAll("%world%", world.getServer() + ":" + world.getName());
            } else {
                msg = Locales.WORLD_WEATHER_CLEAR.getString().replaceAll("%world%", world.getName());
            }
            return Messager.sendMessage(se, msg, Messager.MessageType.INFO);
        }

        return CommandResult.ERROR;
    }

}
