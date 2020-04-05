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

import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Utils {

    public static String replaceColors(String string) {
        return string.replaceAll("&((?i)[0-9a-fk-or])", "§$1");
    }

    public static String removeColors(String string) {
        return string.replaceAll("§((?i)[0-9a-fk-or])", "");
    }
    
    public static String replacePlayerPlaceholders(OfflinePlayer player) {
        String result = Config.MESSAGE_FORMAT.getString();
               
        result = result.replace("%prefix", Vault.getPrefix(player));
        result = result.replace("%suffix", Vault.getSuffix(player));
        result = result.replace("%name", player.getName());        
        result = replaceColors(result);
        return result;
    }
    
    public static Block getBlockLooking(Player player, int range) {
        Block b = player.getTargetBlock((Set<Material>) null, range);
        return b;

    }

    public static Location getLocationLooking(Player player, int range) {
        Block b = getBlockLooking(player, range);
        return b.getLocation();
    }

}
