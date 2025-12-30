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
package com.admincmd.virtualchest.events;

import com.admincmd.player.ACPlayer;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.BukkitListener;
import com.admincmd.virtualchest.chest.ACChest;
import com.admincmd.virtualchest.chest.ChestManager;
import com.admincmd.virtualchest.chest.VirtualChestHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class ChestCloseListener extends BukkitListener {
    
    @EventHandler
    public void onChestClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        ACLogger.debug("InventoryCloseEvent fired!" + inv.getHolder().getClass().getName());
        
        if (!(inv.getHolder() instanceof VirtualChestHolder holder)) {
            return;
        }
        
        ACPlayer owner = holder.getOwner();
        ACChest chest = ChestManager.getChest(owner);
        if (chest != null) {
            chest.update(e.getInventory());
        }
    }
    
}
