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
package com.admincmd.bungeecord.maintenance;

import com.admincmd.bungeecord.utils.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MaintenanceCommand extends Command {

    public MaintenanceCommand() {
        super("maintenance", "admincmd.maintenance.toggle");
    }

    @Override
    public void execute(CommandSender cs, String[] strings) {
        if (strings.length != 1) {
            cs.sendMessage(new TextComponent("Wrong usage, Use /maintenance [on|off|check]"));
            return;
        }
        if (strings[0].equalsIgnoreCase("on")) {
            Config.MAINTENANCE.set(true);
            TextComponent message = new TextComponent("Maintenance-mode is on.");
            message.setColor(ChatColor.GREEN);
            cs.sendMessage(message);
            if (!Config.KICK.getBoolean()) {
                return;
            }
            for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                if (p.hasPermission("admincmd.maintenance.bypass")) {
                    continue;
                }
                TextComponent kickmessage = new TextComponent(Config.KICKMESSAGE.getString().replaceAll("%perm%", "admincmd.maintenance.bypass"));
                kickmessage.setColor(ChatColor.RED);
                p.disconnect(kickmessage);
            }
        } else if (strings[0].equalsIgnoreCase("off")) {
            Config.MAINTENANCE.set(false);
            TextComponent message = new TextComponent("Maintenance-mode is off.");
            message.setColor(ChatColor.GREEN);
            cs.sendMessage(message);
        } else if (strings[0].equalsIgnoreCase("check")) {
            String word = Config.MAINTENANCE.getBoolean() ? "on" : "off";

            TextComponent message = new TextComponent("Maintenace is currently " + word);
            message.setColor(ChatColor.GREEN);
            cs.sendMessage(message);
        } else {
            cs.sendMessage(new TextComponent("Wrong usage, Use /maintenance [on|off|check]"));
        }
    }

}
