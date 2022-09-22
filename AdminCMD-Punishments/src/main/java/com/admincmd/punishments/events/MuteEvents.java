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
package com.admincmd.punishments.events;

import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.punishments.Config;
import com.admincmd.punishments.punishments.Punishment;
import com.admincmd.punishments.punishments.PunishmentManager;
import com.admincmd.punishments.punishments.PunishmentType;
import com.admincmd.utils.BukkitListener;
import com.admincmd.utils.Messager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteEvents extends BukkitListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        ACPlayer sender = PlayerManager.getPlayer(event.getPlayer());
        Punishment pu = PunishmentManager.getPunishment(sender);
        if (pu == null) {
            return;
        }

        if (pu.getType() != PunishmentType.MUTE) {
            return;
        }

        //player is muted after here
        Messager.sendMessage(sender, Config.MESSAGE_TARGET.replace(pu.getCreator(), pu), Messager.MessageType.NONE);
        event.setCancelled(true);
    }

}
