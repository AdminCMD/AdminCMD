/*
 * This file is part of AdminCMD
 * Copyright (C) 2015 AdminCMD Team
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
package com.admincmd.virtualchest;

import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class ChestCloseListener implements Listener {
    
    @EventHandler
    public void onChestClose(InventoryCloseEvent e) {
        if (!e.getView().getTitle().contains("Virtual Chest")) {
            return;
        }
        
        String[] split = e.getView().getTitle().split(": ");
        String owningPlayerName = split[1];
        ACPlayer owner = PlayerManager.getPlayer(owningPlayerName);
        ACChest chest = ChestManager.getChest(owner);
        chest.update(e.getInventory());
    }
    
}
