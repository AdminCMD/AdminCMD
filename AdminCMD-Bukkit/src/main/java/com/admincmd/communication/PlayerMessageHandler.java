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
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.MultiServerLocation;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PlayerMessageHandler {

    protected static void sendPlayerGamemode(ACPlayer target, Channel chn, String args) {
        String message = target.getID() + ":" + args;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(target.getServer());
        out.writeUTF(chn.getCompleteChannelName());
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    protected static void sendPlayerVanish(ACPlayer target, Channel chn) {
        String message = target.getID() + "";
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(target.getServer());
        out.writeUTF(chn.getCompleteChannelName());
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    protected static void sendPlayerKill(ACPlayer target, Channel chn) {
        String message = target.getID() + "";
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(target.getServer());
        out.writeUTF(chn.getCompleteChannelName());
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    protected static void sendPlayerHeal(ACPlayer target, Channel chn) {
        String message = target.getID() + "";
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(target.getServer());
        out.writeUTF(chn.getCompleteChannelName());
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    protected static void sendPlayerFly(ACPlayer target, Channel chn, String args) {
        String message = target.getID() + ":" + args;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(target.getServer());
        out.writeUTF(chn.getCompleteChannelName());
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    protected static void sendPlayerFeed(ACPlayer target, Channel chn) {
        String message = target.getID() + "";
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(target.getServer());
        out.writeUTF(chn.getCompleteChannelName());
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    protected static void sendClearInventory(ACPlayer target, Channel chn) {
        String message = target.getID() + "";
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(target.getServer());
        out.writeUTF(chn.getCompleteChannelName());
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    protected static void sendTeleport(ACPlayer p, String args, Channel chn) {
        MultiServerLocation location = MultiServerLocation.fromString(args);
        String message = p.getID() + "_" + args;
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(location.getServername());
        out.writeUTF(chn.getCompleteChannelName());
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    protected static void sendTeleportToPlayer(ACPlayer from, ACPlayer to, Channel chn) {
        String message = from.getID() + "_" + to.getID();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(to.getServer());
        out.writeUTF(chn.getCompleteChannelName());
        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
    }

    protected static void reactTeleport(String msg) {
        String[] split = msg.split("_");
        final int ID = Integer.valueOf(split[0]);
        final MultiServerLocation loc = MultiServerLocation.fromString(split[1]);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                final ACPlayer acp = PlayerManager.getPlayer(ID);
                PlayerManager.teleport(loc, acp);
            }
        }, 10);
    }

    protected static void reactTeleportToPlayer(String msg) {
        String[] split = msg.split("_");
        final int from = Integer.valueOf(split[0]);
        final int to = Integer.valueOf(split[1]);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                final ACPlayer acfrom = PlayerManager.getPlayer(from);
                final ACPlayer acto = PlayerManager.getPlayer(to);
                PlayerManager.teleport(acto, acfrom);
            }
        }, 10);
    }

    protected static void reactClearInventory(String msg) {
        int targetID = Integer.valueOf(msg);
        ACPlayer target = PlayerManager.getPlayer(targetID);
        target.getPlayer().getInventory().clear();
    }

    protected static void reactPlayerFeed(String msg) {
        int targetID = Integer.valueOf(msg);
        ACPlayer target = PlayerManager.getPlayer(targetID);
        target.getPlayer().setFoodLevel(15);
    }

    protected static void reactPlayerHeal(String msg) {
        int targetID = Integer.valueOf(msg);
        ACPlayer target = PlayerManager.getPlayer(targetID);
        Player p = target.getPlayer();
        p.setFoodLevel(15);
        p.setHealth(p.getMaxHealth());
    }

    protected static void reactPlayerFly(String msg) {
        String[] split = msg.split(":");
        int targetID = Integer.valueOf(split[0]);
        boolean fly = Boolean.valueOf(split[1]);
        ACPlayer target = PlayerManager.getPlayer(targetID);
        target.getPlayer().setAllowFlight(fly);
    }

    protected static void reactPlayerKill(String msg) {
        int targetID = Integer.valueOf(msg);
        ACPlayer target = PlayerManager.getPlayer(targetID);
        final Player p = target.getPlayer();
        Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                p.setHealth(0);
            }
        });
    }

    protected static void reactPlayerVanish(String msg) {
        int targetID = Integer.valueOf(msg);
        final ACPlayer target = PlayerManager.getPlayer(targetID);
        final Player p = target.getPlayer();
        Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (target.isInvisible()) {
                    for (Player op : Bukkit.getOnlinePlayers()) {
                        op.hidePlayer(Main.getInstance(), p);
                    }
                } else {
                    for (Player op : Bukkit.getOnlinePlayers()) {
                        op.showPlayer(Main.getInstance(), p);
                    }
                }
            }
        });
    }

    protected static void reactPlayerGamemode(String msg) {
        String[] split = msg.split(":");
        int targetID = Integer.valueOf(split[0]);
        final GameMode gm = GameMode.valueOf(split[1]);
        ACPlayer target = PlayerManager.getPlayer(targetID);
        final Player targetBukkit = target.getPlayer();
        Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                targetBukkit.setGameMode(gm);
            }
        });
    }
}
