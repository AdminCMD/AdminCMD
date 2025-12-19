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
package com.admincmd.warp;

import com.admincmd.database.DatabaseFactory;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import com.admincmd.utils.MultiServerLocation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WarpManager {

    private static final List<StoredWarp> warps = new ArrayList<>();

    public static ACWarp getWarp(String name) {
        if (!Config.BUNGEECORD.getBoolean()) {
            for (ACWarp w : warps) {

                if (w.getName() != null && w.getName().equalsIgnoreCase(name)) {
                    return w;
                }
            }
            return null;
        } else {
            ACWarp ret = null;
            try {
                PreparedStatement ps = DatabaseFactory.getDatabase().getPreparedStatement("SELECT id FROM " + DatabaseFactory.WARP_TABLE + " WHERE name = ?;");
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ret = new SQLWarp(name);
                }
                DatabaseFactory.getDatabase().closeResultSet(rs);
                DatabaseFactory.getDatabase().closeStatement(ps);
            } catch (SQLException ex) {
                ACLogger.severe(ex);
            }
            return ret;
        }
    }

    public static void deleteWarp(final ACWarp w) {
        if (!Config.BUNGEECORD.getBoolean() && w instanceof StoredWarp) {
            StoredWarp sw = (StoredWarp) w;
            if (warps.contains(sw)) {
                warps.remove(sw);
            }
        }
        try {
            PreparedStatement s = DatabaseFactory.getDatabase().getPreparedStatement("DELETE FROM " + DatabaseFactory.WARP_TABLE + " WHERE id = ?;");
            s.setInt(1, w.getID());
            s.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(s);
        } catch (SQLException ex) {
            ACLogger.severe("Error deleting warp from Database " + w.getID(), ex);
        }
    }

    public static List<String> getWarps() {
        if (Config.BUNGEECORD.getBoolean()) {
            List<String> ret = new ArrayList<>();
            try {
                PreparedStatement ps = DatabaseFactory.getDatabase().getPreparedStatement("SELECT name FROM " + DatabaseFactory.WARP_TABLE + ";");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ret.add(rs.getString("name"));
                }
                DatabaseFactory.getDatabase().closeResultSet(rs);
                DatabaseFactory.getDatabase().closeStatement(ps);
            } catch (SQLException ex) {
                ACLogger.severe(ex);
            }
            return ret;
        } else {
            List<String> ret = new ArrayList<>();
            for (ACWarp w : warps) {
                ret.add(w.getName());
            }
            return ret;
        }
    }

    public static void createWarp(final MultiServerLocation target, final String name) {
        if (getWarp(name) == null) {
            if (Config.BUNGEECORD.getBoolean()) {
                try {
                    PreparedStatement sta = DatabaseFactory.getDatabase().getPreparedStatement("INSERT INTO " + DatabaseFactory.WARP_TABLE + " (location, name) VALUES (?, ?);");
                    sta.setString(1, target.toString());
                    sta.setString(2, name);
                    sta.executeUpdate();
                    DatabaseFactory.getDatabase().closeStatement(sta);
                } catch (SQLException ex) {
                    ACLogger.severe(ex);
                }
            } else {
                StoredWarp warp = new StoredWarp(name, target);
                warps.add(warp);
            }
        }
    }

    public static void init() {
        if (!Config.BUNGEECORD.getBoolean()) {
            warps.clear();
            try {
                Statement s = DatabaseFactory.getDatabase().getStatement();
                ResultSet rs = s.executeQuery("SELECT * FROM " + DatabaseFactory.WARP_TABLE);

                while (rs.next()) {
                    StoredWarp w = new StoredWarp(rs);
                    if (w.getLocation().isOnThisServer()) {
                        warps.add(w);
                    }
                }

                DatabaseFactory.getDatabase().closeStatement(s);
                DatabaseFactory.getDatabase().closeResultSet(rs);
                ACLogger.info("Loaded " + warps.size() + " warps!");
            } catch (SQLException ex) {
                ACLogger.severe("Error loading the warps", ex);
            }
        }
    }

    public static void save() {
        if (warps.isEmpty()) {
            return;
        }
        if (!Config.BUNGEECORD.getBoolean()) {
            int saved = 0;

            for (StoredWarp w : warps) {
                if (!w.hasChanged()) {
                    continue;
                }
                try {
                    PreparedStatement sta = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.WARP_TABLE + " SET location = ? WHERE id = ?;");
                    sta.setString(1, w.getLocation().toString());
                    sta.setInt(2, w.getID());
                    sta.executeUpdate();
                    DatabaseFactory.getDatabase().closeStatement(sta);
                } catch (SQLException ex) {
                    ACLogger.severe("Error saving warps!", ex);
                }
            }

            warps.clear();
            ACLogger.info("Saved " + saved + " warps!");
        }
    }

}
