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
import com.admincmd.utils.BukkitListener;
import com.admincmd.utils.Config;
import com.admincmd.utils.Locales;
import de.jeter.updatechecker.Result;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener extends BukkitListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Config.MAINTENANCE_ENABLE.getBoolean()) {
            if (!e.getPlayer().hasPermission("admincmd.maintenance.bypass")) {
                String msg = Locales.MESSAGE_PREFIX_ERROR.getString() + Locales.MAINTENANCE_NO_JOIN.getString().replaceAll("%perm%", "admincmd.maintenance.bypass");
                e.getPlayer().kickPlayer(msg);
                return;
            }
        }

        if (Config.CHECK_UPDATE.getBoolean() && e.getPlayer().hasPermission("admincmd.notifyupdate") && Main.getInstance().getUpdateChecker() != null) {
            if (Main.getInstance().getUpdateChecker().getResult() == Result.UPDATE_FOUND) {
                e.getPlayer().sendMessage(Locales.UPDATE_FOUND.getString().replaceAll("%oldversion", Main.getInstance().getDescription().getVersion()).replaceAll("%newversion", Main.getInstance().getUpdateChecker().getLatestRemoteVersion()));
            }
        }

        final ACPlayer bp = PlayerManager.getPlayer(e.getPlayer());

        if (bp.isInvisible()) {
            for (Player op : Bukkit.getOnlinePlayers()) {
                op.hidePlayer(Main.getInstance(), e.getPlayer());
            }
            e.setJoinMessage(null);
        }

        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            e.getPlayer().setAllowFlight(bp.isFly());
            if (bp.isFly()) {
                e.getPlayer().setFlying(bp.isFly());
            }
        }

        for (Player op : Bukkit.getOnlinePlayers()) {
            ACPlayer ocp = PlayerManager.getPlayer(op);
            if (ocp.isInvisible()) {
                e.getPlayer().hidePlayer(Main.getInstance(), op);
            }
        }
    }

}
