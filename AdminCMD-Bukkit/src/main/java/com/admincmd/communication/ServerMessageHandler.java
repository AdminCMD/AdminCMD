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
package com.admincmd.communication;

import com.admincmd.Main;
import com.admincmd.player.ACPlayer;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;

public class ServerMessageHandler {

    protected static void sendSwitchServer(ACPlayer p, String args, MessageCommand cmd) {
        ByteArrayDataOutput switchServer = ByteStreams.newDataOutput();
        switchServer.writeUTF(cmd.getCommand());
        switchServer.writeUTF(p.getName());
        switchServer.writeUTF(args);
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", switchServer.toByteArray());
    }

    protected static void sendKickPlayer(ACPlayer p, String args, MessageCommand cmd) {
        ByteArrayDataOutput kick = ByteStreams.newDataOutput();
        kick.writeUTF(cmd.getCommand());
        kick.writeUTF(p.getName());
        kick.writeUTF(args);
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", kick.toByteArray());
    }

    protected static void sendMessage(ACPlayer target, String args, MessageCommand cmd) {
        ByteArrayDataOutput sendMessage = ByteStreams.newDataOutput();
        sendMessage.writeUTF(cmd.getCommand());
        sendMessage.writeUTF(target.getName());
        sendMessage.writeUTF(args);
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", sendMessage.toByteArray());

    }
}
