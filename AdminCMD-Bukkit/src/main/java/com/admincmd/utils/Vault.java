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
package com.admincmd.utils;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Vault {

    private static Chat chat = null;

    public static String getPrefix(OfflinePlayer p) {
        if (chat == null) {
            return "";
        }
        World w;
        if (p.getPlayer() != null) {
            w = p.getPlayer().getWorld();
        } else {
            w = Bukkit.getWorlds().get(0);
        }
        return chat.getPlayerPrefix(w.getName(), p);
    }

    public static String getSuffix(OfflinePlayer p) {
        if (chat == null) {
            return "";
        }
        World w;
        if (p.getPlayer() != null) {
            w = p.getPlayer().getWorld();
        } else {
            w = Bukkit.getWorlds().get(0);
        }
        return chat.getPlayerSuffix(w.getName(), p) + ChatColor.RESET;
    }

    public static boolean setupChat() {
        try {
            RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
            if (chatProvider != null && chatProvider.getProvider() != null) {
                chat = chatProvider.getProvider();
                return chat != null && chat.isEnabled();
            }
            return false;
        } catch (Throwable e) {
            return false;
        }
    }
}
