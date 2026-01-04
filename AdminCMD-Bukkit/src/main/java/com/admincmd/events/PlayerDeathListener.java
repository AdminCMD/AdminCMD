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

import com.admincmd.Main;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.spawn.SpawnManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.BukkitListener;
import com.admincmd.utils.Config;
import com.admincmd.utils.MultiServerLocation;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathListener extends BukkitListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(final PlayerRespawnEvent e) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            final ACPlayer acp = PlayerManager.getPlayer(e.getPlayer());
            MultiServerLocation s = SpawnManager.getSpawn(acp);
            if (s != null) {
                PlayerManager.teleport(s, acp);
            }
        }, 10);
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
        ACPlayer p = PlayerManager.getPlayer(e.getEntity());
        MultiServerLocation loc = MultiServerLocation.fromLocation(e.getEntity().getLocation());
        Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            ACLogger.debug("Player death location set.");
            p.setLastLoc(loc);
        }, 20);

        if (Config.DIRECT_RESPAWN.getBoolean()) {
            Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> e.getEntity().spigot().respawn());
        }
    }

}
