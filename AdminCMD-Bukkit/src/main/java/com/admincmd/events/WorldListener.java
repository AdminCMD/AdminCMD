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
package com.admincmd.events;

import com.admincmd.utils.BukkitListener;
import com.admincmd.world.ACWorld;
import com.admincmd.world.StoredWorld;
import com.admincmd.world.WorldManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldListener extends BukkitListener {
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWorldLoad(final WorldLoadEvent e) {
        ACWorld acw = WorldManager.getWorld(e.getWorld());
        if (acw == null) {
            StoredWorld nvc = new StoredWorld(e.getWorld());
            WorldManager.createWorld(nvc);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWorldUnload(WorldUnloadEvent e) {
        ACWorld acw = WorldManager.getWorld(e.getWorld());
        if (acw != null) {
            WorldManager.unloadWorld(acw);
        }
    }
    
}
