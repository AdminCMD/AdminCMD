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
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.Collection;

public class MaintenanceEvents implements Listener {

    @EventHandler(priority = 5)
    public void onPing(final ProxyPingEvent e) throws IOException {
        if (Config.MAINTENANCE.getBoolean()) {
            ServerPing conn = e.getResponse();
            conn.setVersion(new Protocol(Config.VERSION.getString(), -1));
            MotD motd = new MotD(Config.MOTD_LINE_1.getString(), Config.MOTD_LINE_2.getString());
            conn.setDescriptionComponent(motd.getMotd());
            Favicon ico = Config.ICON.getFavicon();
            conn.setFavicon(ico);
            e.setResponse(conn);
        }
    }

    @EventHandler
    public void onLogin(final LoginEvent event) {
        if (!Config.MAINTENANCE.getBoolean()) {
            return;
        }
        boolean hasPerm = false;

        for (String s : ProxyServer.getInstance().getConfigurationAdapter().getGroups(event.getConnection().getName())) {
            Collection<?> perms = ProxyServer.getInstance().getConfigurationAdapter().getList("permissions." + s, null);
            if (perms.contains("admincmd.maintenance.bypass") || perms.contains("*")) {
                hasPerm = true;
                break;
            }
        }

        if (!hasPerm) {
            TextComponent kickmessage = new TextComponent(Config.KICKMESSAGE.getString().replaceAll("%perm%", "admincmd.maintenance.bypass"));
            kickmessage.setColor(ChatColor.RED);
            event.setReason(kickmessage);
            event.setCancelled(true);
        }
    }
}
