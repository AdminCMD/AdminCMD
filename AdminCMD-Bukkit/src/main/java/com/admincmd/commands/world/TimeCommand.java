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

import com.admincmd.commandapi.BaseCommand;
import com.admincmd.commandapi.CommandArgs;
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandResult;
import com.admincmd.commandapi.Sender;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Config;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.world.ACWorld;
import com.admincmd.world.WorldManager;
import org.bukkit.entity.Player;

@CommandHandler
public class TimeCommand {

    @BaseCommand(command = "time", sender = Sender.PLAYER, permission = "admincmd.world.time", helpArguments = {"day <-w world>", "night <-w world>", "<time> <-w world>", "pause <-w world>", "unpause <-w world>"})
    public CommandResult executeTime(Player sender, CommandArgs args) {
        ACPlayer se = PlayerManager.getPlayer(sender);
        if (args.getLength() < 1) {
            return CommandResult.ERROR;
        }

        ACWorld target = WorldManager.getWorld(sender.getWorld());
        if (args.hasFlag("w")) {
            if (!args.getFlag("w").isWorld()) {
                return CommandResult.NOT_A_WORLD;
            }
            if (!sender.hasPermission("admincmd.world.time.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }
            target = args.getFlag("w").getWorld();
        }

        if (args.getString(0).equalsIgnoreCase("day")) {
            WorldManager.setTime(target, 0);
            String msg;
            if (Config.BUNGEECORD.getBoolean()) {
                msg = Locales.WORLD_DAY_SET.getString().replaceAll("%world%", target.getServer() + ":" + target.getName());
            } else {
                msg = Locales.WORLD_DAY_SET.getString().replaceAll("%world%", target.getName());
            }
            return Messager.sendMessage(se, msg, Messager.MessageType.INFO);
        } else if (args.getString(0).equalsIgnoreCase("night")) {
            WorldManager.setTime(target, 13100);
            String msg;
            if (Config.BUNGEECORD.getBoolean()) {
                msg = Locales.WORLD_NIGHT_SET.getString().replaceAll("%world%", target.getServer() + ":" + target.getName());
            } else {
                msg = Locales.WORLD_NIGHT_SET.getString().replaceAll("%world%", target.getName());
            }
            return Messager.sendMessage(se, msg, Messager.MessageType.INFO);
        } else if (args.isInteger(0)) {
            long time = args.getInt(0);
            WorldManager.setTime(target, time);

            String msg;
            if (Config.BUNGEECORD.getBoolean()) {
                msg = Locales.WORLD_TIME_SET.getString().replaceAll("%world%", target.getServer() + ":" + target.getName());
            } else {
                msg = Locales.WORLD_TIME_SET.getString().replaceAll("%world%", target.getName());
            }
            msg = msg.replaceAll("%time%", time + "");
            return Messager.sendMessage(se, msg, Messager.MessageType.INFO);
        } else if (args.getString(0).equalsIgnoreCase("pause")) {
            WorldManager.pauseTime(target, true);
            String msg;
            if (Config.BUNGEECORD.getBoolean()) {
                msg = Locales.WORLD_TIME_PAUSED.getString().replaceAll("%world%", target.getServer() + ":" + target.getName());
            } else {
                msg = Locales.WORLD_TIME_PAUSED.getString().replaceAll("%world%", target.getName());
            }
            return Messager.sendMessage(se, msg, Messager.MessageType.INFO);
        } else if (args.getString(0).equalsIgnoreCase("unpause")) {
            WorldManager.pauseTime(target, false);

            String msg;
            if (Config.BUNGEECORD.getBoolean()) {
                msg = Locales.WORLD_TIME_UNPAUSED.getString().replaceAll("%world%", target.getServer() + ":" + target.getName());
            } else {
                msg = Locales.WORLD_TIME_UNPAUSED.getString().replaceAll("%world%", target.getName());
            }
            return Messager.sendMessage(se, msg, Messager.MessageType.INFO);
        }

        return CommandResult.ERROR;
    }
}
