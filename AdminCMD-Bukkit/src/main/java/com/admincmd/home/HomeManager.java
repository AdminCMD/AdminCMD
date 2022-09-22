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
package com.admincmd.home;

import com.admincmd.Main;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HomeManager {

    private static final List<StoredHome> homes = new ArrayList<>();

    public static ACHome getHome(ACPlayer owner, String name) {
        if (Config.BUNGEECORD.getBoolean()) {
            try {
                PreparedStatement getStatement = DatabaseFactory.getDatabase().getPreparedStatement("SELECT id FROM " + DatabaseFactory.HOME_TABLE + " WHERE name = ? AND playerid = ?;");
                getStatement.setString(1, name);
                getStatement.setInt(2, owner.getID());
                ResultSet rs = getStatement.executeQuery();

                SQLHome ret = null;

                if (rs.next()) {
                    ret = new SQLHome(rs.getInt("id"));
                }

                DatabaseFactory.getDatabase().closeResultSet(rs);
                DatabaseFactory.getDatabase().closeStatement(getStatement);
                return ret;
            } catch (SQLException ex) {
                ACLogger.severe("Error getting home with name " + name + " from owner " + owner.getID(), ex);
                return null;
            }
        } else {
            for (StoredHome h : homes) {
                if (h.getName().equalsIgnoreCase(name) && h.getOwner().getID() == owner.getID()) {
                    return h;
                }
            }
            return null;
        }
    }

    public static void deleteHome(final ACHome h) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Config.BUNGEECORD.getBoolean()) {
                    try {
                        PreparedStatement s = DatabaseFactory.getDatabase().getPreparedStatement("DELETE FROM " + DatabaseFactory.HOME_TABLE + " WHERE id = ?;");
                        s.setInt(1, h.getID());
                        s.executeUpdate();
                        DatabaseFactory.getDatabase().closeStatement(s);
                    } catch (SQLException ex) {
                        ACLogger.severe("Error deleting home from Database " + h.getID(), ex);
                    }
                } else {
                    StoredHome sh = (StoredHome) h;
                    if (homes.contains(sh)) {
                        homes.remove(sh);
                    }
                    try {
                        PreparedStatement s = DatabaseFactory.getDatabase().getPreparedStatement("DELETE FROM " + DatabaseFactory.HOME_TABLE + " WHERE id = ?;");
                        s.setInt(1, h.getID());
                        s.executeUpdate();
                        DatabaseFactory.getDatabase().closeStatement(s);
                    } catch (SQLException ex) {
                        ACLogger.severe("Error deleting home from Database " + h.getID(), ex);
                    }
                }
            }
        });
    }

    public static List<String> getHomes(ACPlayer p) {
        List<String> ret = new ArrayList<>();
        if (Config.BUNGEECORD.getBoolean()) {
            try {
                PreparedStatement ps = DatabaseFactory.getDatabase().getPreparedStatement("SELECT name FROM " + DatabaseFactory.HOME_TABLE + " WHERE playerid = ?;");
                ps.setInt(1, p.getID());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ret.add(rs.getString("name"));
                }
                DatabaseFactory.getDatabase().closeResultSet(rs);
                DatabaseFactory.getDatabase().closeStatement(ps);
                return ret;
            } catch (SQLException ex) {
                ACLogger.severe("Error getting all homes from Player " + p.getID(), ex);
                return new ArrayList<>();
            }
        } else {
            for (StoredHome stored : homes) {
                if (stored.getOwner().getID() == p.getID()) {
                    ret.add(stored.getName());
                }
            }
            return ret;
        }
    }

    public static void createHome(final ACPlayer owner, final String name) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (Config.BUNGEECORD.getBoolean()) {
                    StoredHome stored = new StoredHome(owner, name);
                    String test = stored.getName();
                } else {
                    StoredHome stored = new StoredHome(owner, name);
                    homes.add(stored);
                }
            }
        });
    }

    public static void init() {
        if (!Config.BUNGEECORD.getBoolean()) {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        homes.clear();
                        Statement s = DatabaseFactory.getDatabase().getStatement();
                        ResultSet rs = s.executeQuery("SELECT * FROM ac_homes");

                        while (rs.next()) {
                            ACPlayer player = PlayerManager.getPlayer(rs.getInt("playerid"));
                            if (player == null) {
                                continue;
                            }
                            StoredHome h = new StoredHome(rs.getString("location"), player, rs.getString("name"), rs.getInt("id"));
                            if (h.getLocation().isOnThisServer()) {
                                homes.add(h);
                            }
                        }

                        DatabaseFactory.getDatabase().closeStatement(s);
                        DatabaseFactory.getDatabase().closeResultSet(rs);
                        ACLogger.info("Loaded " + homes.size() + " homes!");
                    } catch (SQLException ex) {
                        ACLogger.severe("Error loading the homes", ex);
                    }
                }
            });
        }
    }

    public static void save() {
        if (homes.isEmpty()) {
            ACLogger.info("Saved 0 homes!");
            return;
        }
        if (!Config.BUNGEECORD.getBoolean()) {
            int saved = 0;
            for (StoredHome home : homes) {
                if (!home.hasChanged()) {
                    continue;
                }
                try {
                    PreparedStatement ps = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.HOME_TABLE + " SET location = ? WHERE id = ?;");
                    ps.setString(1, home.getLocation().toString());
                    ps.setInt(2, home.getID());
                    ps.executeUpdate();
                    DatabaseFactory.getDatabase().closeStatement(ps);
                    saved++;
                } catch (SQLException ex) {
                    ACLogger.severe("Error saving homes", ex);
                }
            }
            homes.clear();
            ACLogger.info("Saved " + saved + " homes!");
        }
    }
}
