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
package com.admincmd.world;

import com.admincmd.Main;
import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.communication.Channel;
import com.admincmd.communication.MessageCommand;
import com.admincmd.database.Database;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorldManager {

    private static final List<ACWorld> worlds = new ArrayList<>();
    private static final Database conn = DatabaseFactory.getDatabase();

    public static void init() {
        for (World w : Bukkit.getWorlds()) {
            if (getWorld(w) == null) {
                try {
                    PreparedStatement ps = conn.getPreparedStatement("SELECT * FROM " + DatabaseFactory.WORLD_TABLE + " WHERE name = ? and servername = ?;");
                    ps.setString(1, w.getName());
                    ps.setString(2, BungeeCordMessageManager.getServerName());
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        createWorld(new StoredWorld(w));
                    }
                    conn.closeResultSet(rs);
                    conn.closeStatement(ps);
                } catch (SQLException ex) {
                    ACLogger.severe("Error checking if world exists in database!", ex);
                }
            }
        }

        worlds.clear();

        try {
            PreparedStatement st = conn.getPreparedStatement("SELECT * FROM " + DatabaseFactory.WORLD_TABLE + ";");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                ACWorld toCheck;
                if (!Config.BUNGEECORD.getBoolean()) {
                    toCheck = new StoredWorld(rs);
                    if (toCheck.isOnThisServer()) {
                        worlds.add(toCheck);
                    }
                } else {
                    toCheck = new SQLWorld(rs.getString("name"), rs.getString("servername"));
                }

                if (toCheck.isOnThisServer() && toCheck.isPaused()) {
                    Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new ResetTime(toCheck), 20 * 3, 20 * 3);
                }
            }

            conn.closeResultSet(rs);
            conn.closeStatement(st);
            ACLogger.info("Loaded " + worlds.size() + " worlds!");

        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    public static List<ACWorld> getWorlds() {
        if (!Config.BUNGEECORD.getBoolean()) {
            return worlds;
        } else {
            List<ACWorld> list = new ArrayList<>();
            try {
                PreparedStatement st = conn.getPreparedStatement("SELECT name, servername FROM " + DatabaseFactory.WORLD_TABLE + ";");
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    SQLWorld world = new SQLWorld(rs.getString("name"), rs.getString("servername"));
                    list.add(world);
                }

                conn.closeResultSet(rs);
                conn.closeStatement(st);
            } catch (SQLException ex) {
                ACLogger.severe(ex);
            }
            return list;
        }

    }

    public static List<String> getWorldNames() {
        List<String> names = new ArrayList<>();
        for (ACWorld world : getWorlds()) {
            if (Config.BUNGEECORD.getBoolean()) {
                names.add(world.getServer() + ":" + world.getName());
            } else {
                names.add(world.getName());
            }
        }
        return names;
    }

    public static ACWorld getWorld(String name, String server) {
        for (ACWorld w : getWorlds()) {
            if (w.getServer().equalsIgnoreCase(server) && w.getName().equalsIgnoreCase(name)) {
                return w;
            }
        }
        return null;
    }

    public static ACWorld getWorld(World w) {
        for (ACWorld world : getWorlds()) {
            if (world.getServer().equalsIgnoreCase(BungeeCordMessageManager.getServerName())) {
                if (world.getName().equalsIgnoreCase(w.getName())) {
                    return world;
                }
            }
        }
        return null;
    }

    public static void createWorld(final ACWorld w) {
        try {
            PreparedStatement s = conn.getPreparedStatement("INSERT INTO ac_worlds (name, paused, time, servername) VALUES (?, ?, ?, ?);");
            s.setString(1, w.getName());
            s.setBoolean(2, w.isPaused());
            s.setLong(3, w.getPausedTime());
            s.setString(4, w.getServer());
            s.executeUpdate();
            conn.closeStatement(s);
            ACLogger.info("World " + w.getName() + " was put into the database.");

            if (!Config.BUNGEECORD.getBoolean()) {
                worlds.add(w);
            }
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    public static void save() {
        if (!Config.BUNGEECORD.getBoolean()) {
            int saved = 0;
            for (ACWorld w : worlds) {
                if (w instanceof StoredWorld) {
                    StoredWorld sw = (StoredWorld) w;
                    if (!sw.hasChanged()) {
                        continue;
                    }
                    try {
                        PreparedStatement st = conn.getPreparedStatement("UPDATE " + DatabaseFactory.WORLD_TABLE + " SET paused = ?, time = ? WHERE name = ? AND servername = ?;");
                        st.setBoolean(1, w.isPaused());
                        st.setLong(2, w.getPausedTime());
                        st.setString(3, w.getName());
                        st.setString(4, w.getServer());
                        st.executeUpdate();
                        conn.closeStatement(st);
                        saved++;
                    } catch (SQLException ex) {
                        ACLogger.severe(ex);
                    }
                }
            }
            worlds.clear();
            ACLogger.info("Saved " + saved + " worlds!");
        }
    }

    public static void unloadWorld(ACWorld vcw) {
        worlds.remove(vcw);
    }

    public static void setTime(final ACWorld world, final long time) {
        if (world.isOnThisServer()) {
            World bukkitworld = Bukkit.getWorld(world.getName());
            bukkitworld.setTime(time);
        } else {
            BungeeCordMessageManager.getInstance().sendMessage(null, Channel.WORLD_TIME_SET, MessageCommand.FORWARD, world.getServer() + ":" + world.getName() + ":" + time);
        }
    }

    public static void pauseTime(ACWorld world, boolean pause) {
        world.setPaused(pause);
        if (world.isOnThisServer()) {
            World bukkitWorld = Bukkit.getWorld(world.getName());
            world.setPausedTime(bukkitWorld.getTime());
            if (pause) {
                Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new ResetTime(world), 20 * 3, 20 * 3);
            }
        } else {
            BungeeCordMessageManager.getInstance().sendMessage(null, Channel.WORLD_TIME_PAUSE, MessageCommand.FORWARD, world.getServer() + ":" + world.getName() + ":" + pause);
        }
    }

    public static void setSun(final ACWorld world) {
        if (world.isOnThisServer()) {
            World bukkitWorld = Bukkit.getWorld(world.getName());
            bukkitWorld.setStorm(false);
        } else {
            BungeeCordMessageManager.getInstance().sendMessage(null, Channel.WORLD_WEATHER_SET, MessageCommand.FORWARD, world.getServer() + ":" + world.getName());
        }
    }
}
