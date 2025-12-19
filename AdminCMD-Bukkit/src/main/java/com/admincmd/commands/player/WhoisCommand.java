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
package com.admincmd.commands.player;

import com.admincmd.commandapi.*;
import com.admincmd.commandapi.CommandArgs.Flag;
import com.admincmd.home.HomeManager;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandHandler
public class WhoisCommand {

    @BaseCommand(command = "whois", sender = Sender.PLAYER, permission = "admincmd.player.whois", aliases = "pinfo", helpArguments = {"", "<-p player>"}, async = true)
    public CommandResult executePlayer(Player sender, CommandArgs args) {
        if (args.isEmpty()) {
            ACPlayer s = PlayerManager.getPlayer(sender);
            List<String> text = new ArrayList<>();
            text.add("&a---------- " + Utils.replacePlayerPlaceholders(sender) + " &a----------");
            text.add("&7Database ID: " + s.getID() + "");
            String flying = s.isFly() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
            String god = s.isGod() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
            String invisible = s.isInvisible() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
            String cmdWatcher = s.isCMDWatcher() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
            String spy = s.isSpy() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
            String freeze = s.isFreezed() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
            text.add("&7Flying: " + flying);
            text.add("&7God: " + god);
            text.add("&7Invisible: " + invisible);
            text.add("&7CommandWatcher: " + cmdWatcher);
            text.add("&7SpyMode: " + spy);
            text.add("&7Frozen: " + freeze);
            text.add("&a------------------------------");

            List<String> homes = HomeManager.getHomes(s);
            text.add("Homes (" + homes.size() + ")");
            text.addAll(homes);

            for (String st : text) {
                String edit = Utils.replaceColors(st);
                sender.sendMessage(edit);
            }
            return CommandResult.SUCCESS;
        } else {
            return executeConsole(sender, args);
        }
    }

    @BaseCommand(command = "whois", sender = Sender.CONSOLE, permission = "admincmd.player.whois", aliases = "pinfo", helpArguments = {"", "<-p player>"}, async = true)
    public CommandResult executeConsole(CommandSender sender, CommandArgs args) {
        if (args.isEmpty()) {
            return CommandResult.WRONG_SENDER;
        }

        if (!args.hasFlag("p")) {
            return CommandResult.ERROR;
        }

        Flag f = args.getFlag("p");
        if (!f.isRegisteredPlayer()) {
            return CommandResult.NOT_A_PLAYER;
        }

        if (!sender.hasPermission("admincmd.player.whois.other")) {
            return CommandResult.NO_PERMISSION_OTHER;
        }

        ACPlayer s = f.getPlayer();
        List<String> text = new ArrayList<>();
        text.add("&a---------- " + Utils.replacePlayerPlaceholders(s.getOfflinePlayer()) + " &a----------");
        text.add("&7Database ID: " + s.getID() + "");
        String flying = s.isFly() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
        String god = s.isGod() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
        String invisible = s.isInvisible() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
        String cmdWatcher = s.isCMDWatcher() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
        String spy = s.isSpy() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
        String freeze = s.isFreezed() ? ("&a" + Locales.COMMAND_MESSAGES_ENABLED.getString()) : ("&c" + Locales.COMMAND_MESSAGES_DISABLED.getString());
        text.add("&7Flying: " + flying);
        text.add("&7God: " + god);
        text.add("&7Invisible: " + invisible);
        text.add("&7CommandWatcher: " + cmdWatcher);
        text.add("&7SpyMode: " + spy);
        text.add("&7Frozen: " + freeze);
        text.add("&a------------------------------");

        List<String> homes = HomeManager.getHomes(s);
        text.add("Homes (" + homes.size() + ")");
        text.addAll(homes);

        for (String st : text) {
            String edit = Utils.replaceColors(st);
            sender.sendMessage(edit);
        }
        return CommandResult.SUCCESS;
    }

}
