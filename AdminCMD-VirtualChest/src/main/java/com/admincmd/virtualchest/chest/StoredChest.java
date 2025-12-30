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
package com.admincmd.virtualchest.chest;

import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.ItemSerializationJson;
import com.admincmd.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class StoredChest implements ACChest {
    
    private final Inventory inv;
    private final int owner;
    private final int ID;
    
    public StoredChest(String inventory, int owner, int ID) {
        this.owner = owner;
        ItemStack[] items = ItemSerializationJson.loadInventory(inventory);
        
        inv = Bukkit.createInventory(new VirtualChestHolder(PlayerManager.getPlayer(owner)), 54, "§aVirtual Chest of: " + Utils.replacePlayerPlaceholders(getOwner().getOfflinePlayer()));
        
        for (int i = 0; i < items.length; i++) {
            inv.setItem(i, items[i]);
        }
        
        this.ID = ID;
    }
    
    @Override
    public int getID() {
        return ID;
    }
    
    @Override
    public Inventory getInventory() {
        return inv;
    }
    
    @Override
    public String getString() {
        return ItemSerializationJson.saveInventory(inv);
    }
    
    @Override
    public void clear() {
        inv.clear();
    }
    
    @Override
    public ACPlayer getOwner() {
        return PlayerManager.getPlayer(owner);
    }
    
    @Override
    public void update(Inventory newInv) {
        ACLogger.debug("VirtualChest Update fired!");
    }
}
