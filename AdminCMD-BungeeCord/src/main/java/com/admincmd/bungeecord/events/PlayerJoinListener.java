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
package com.admincmd.bungeecord.events;

import com.admincmd.bungeecord.Main;
import com.admincmd.bungeecord.player.ACPlayer;
import com.admincmd.bungeecord.player.PlayerManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerJoinListener implements Listener {
    
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ACPlayer acp = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
        acp.setOnline(false);
    }
    
    @EventHandler
    public void onSwitchServer(ServerSwitchEvent event) {
        if (PlayerManager.getPlayer(event.getPlayer().getUniqueId()) == null) {
            PlayerManager.createPlayer(event.getPlayer());
        }
        
        ACPlayer acp = PlayerManager.getPlayer(event.getPlayer().getUniqueId());
        
        if (!acp.isOnline()) {
            acp.setOnline(true);
        }
        
        if (!acp.getName().equals(event.getPlayer().getName())) {
            Main.getInstance().getLogger().info("Player " + acp.getName() + " changed his name to " + event.getPlayer().getName() + " updating in database...");
            acp.setName(event.getPlayer().getName());
        }
        
        acp.setServer(event.getPlayer().getServer().getInfo().getName());
    }
    
}
