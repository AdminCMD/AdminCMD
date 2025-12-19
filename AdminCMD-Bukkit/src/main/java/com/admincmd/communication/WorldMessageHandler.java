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
import com.admincmd.commands.mob.KillallCommand;
import com.admincmd.world.ACWorld;
import com.admincmd.world.WorldManager;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WorldMessageHandler {

    protected static void setTime(String args, Channel chn) {
        String[] split = args.split(":");
        String targetServer = split[0];
        String message = split[1] + ":" + split[2];
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(targetServer);
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

    protected static void setTimePause(String args, Channel chn) {
        String[] split = args.split(":");
        String targetServer = split[0];
        String message = split[1] + ":" + split[2];
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(targetServer);
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

    protected static void setWeatherSun(String args, Channel chn) {
        String[] split = args.split(":");
        String targetServer = split[0];
        String message = split[1];
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(targetServer);
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

    protected static void setKillMobs(String args, Channel chn) {
        String[] split = args.split(":");
        String targetServer = split[0];
        String message = split[1] + ":" + split[2];
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward");
        out.writeUTF(targetServer);
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


    protected static void reactTimeSet(String msg) {
        String[] split = msg.split(":");
        String worldName = split[0];
        long timeToSet = Long.valueOf(split[1]);
        ACWorld w = WorldManager.getWorld(worldName, BungeeCordMessageManager.getServerName());
        if (w != null) {
            WorldManager.setTime(w, timeToSet);
        }
    }

    protected static void reactTimePause(String msg) {
        String[] split = msg.split(":");
        String worldName = split[0];
        boolean pause = Boolean.valueOf(split[1]);
        ACWorld target = WorldManager.getWorld(worldName, BungeeCordMessageManager.getServerName());

        if (target != null) {
            WorldManager.pauseTime(target, pause);
        }
    }

    protected static void reactWeatherSet(String msg) {
        ACWorld target = WorldManager.getWorld(msg, BungeeCordMessageManager.getServerName());
        if (target != null) {
            World bukkitWorld = Bukkit.getWorld(target.getName());
            bukkitWorld.setStorm(false);
            bukkitWorld.setThundering(false);
        }
    }

    protected static void reactKillMobs(String msg) {
        String[] split = msg.split(":");
        String worldName = split[0];
        KillallCommand.MobType type = KillallCommand.MobType.valueOf(split[1]);
        ACWorld target = WorldManager.getWorld(worldName, BungeeCordMessageManager.getServerName());

        if (target == null) {
            return;
        }

        World bukkitWorld = Bukkit.getWorld(target.getName());
        for (Entity e : bukkitWorld.getEntities()) {
            if (e instanceof Player) {
                continue;
            }

            if (type != KillallCommand.MobType.ALL) {
                if (e instanceof Creature) {
                    if (e instanceof Monster && type == KillallCommand.MobType.MONSTER) {
                        LivingEntity l = (LivingEntity) e;
                        l.setHealth(0.0);
                    } else if (e instanceof Animals && type == KillallCommand.MobType.ANIMAL) {
                        LivingEntity l = (LivingEntity) e;
                        l.setHealth(0.0);
                    }
                }
            } else {
                e.remove();
            }
        }

    }

}
