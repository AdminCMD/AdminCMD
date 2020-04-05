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
package com.admincmd.virtualchest;

import com.admincmd.database.Database;
import com.admincmd.player.ACPlayer;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public final class ChestManager {

    private static final Map<Integer, StoredChest> chests = new HashMap<>();
    private static final Database db = VirtualChest.getInstance().getDB();
    
    public static void init() {
        if (!Config.BUNGEECORD.getBoolean()) {
            try {
                PreparedStatement s = db.getPreparedStatement("SELECT * FROM ac_virtualchest");
                ResultSet rs = s.executeQuery();
                int loaded = 0;
                while (rs.next()) {
                    StoredChest chest = new StoredChest(rs.getString("inventory"), rs.getInt("owner"), rs.getInt("ID"));
                    chests.put(rs.getInt("owner"), chest);
                    loaded++;
                }

                ACLogger.info("Loaded " + loaded + " chests.");
            } catch (SQLException ex) {
                ACLogger.severe("Error loading chests.", ex);
            }
        }
    }

    public static void save() {
        if (!Config.BUNGEECORD.getBoolean()) {
            int saved = 0;
            for (StoredChest c : chests.values()) {
                try {
                    PreparedStatement st = db.getPreparedStatement("UPDATE ac_virtualchest SET inventory = ? WHERE ID = ?;");
                    st.setString(1, c.getString());
                    st.setInt(2, c.getID());
                    st.executeUpdate();
                    db.closeStatement(st);
                    saved++;
                } catch (SQLException ex) {
                    ACLogger.severe("Error saving chest!", ex);
                }
            }
            ACLogger.info("Saved " + saved + " chests.");
        }
    }

    public static void createChest(ACPlayer p) {
        try {
            PreparedStatement st = db.getPreparedStatement("INSERT INTO ac_virtualchest (owner, inventory) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, p.getID());
            st.setString(2, "");

            int affectedRows = st.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating chest failed, no rows affected.");
            }

            int id = -1;

            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            } else {
                String sql = Config.MYSQL_USE.getBoolean() ? "MySQL" : "SQLite";
                throw new SQLException("Creating Chest failed, no ID obtained. SQL type: " + sql);
            }

            db.closeResultSet(generatedKeys);
            db.closeStatement(st);

            if (!Config.BUNGEECORD.getBoolean()) {
                StoredChest chest = new StoredChest("", p.getID(), id);
                chests.put(p.getID(), chest);
            }
        } catch (SQLException ex) {
            ACLogger.severe("Error creating VirtualChest", ex);
        }
    }

    public static boolean hasChest(ACPlayer player) {
        try {
            PreparedStatement s = db.getPreparedStatement("SELECT ID FROM ac_virtualchest WHERE owner = ?;");
            s.setInt(1, player.getID());
            ResultSet rs = s.executeQuery();
            boolean ret = rs.next();
            db.closeResultSet(rs);
            db.closeStatement(s);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe(ex);
            return false;
        }
    }

    public static ACChest getChest(ACPlayer p) {
        if (!Config.BUNGEECORD.getBoolean()) {
            if (!chests.containsKey(p.getID())) {
                createChest(p);
            }

            return chests.get(p.getID());
        } else {
            if (!hasChest(p)) {
                createChest(p);
            }

            int chestID = -1;

            try {
                PreparedStatement s = db.getPreparedStatement("SELECT ID FROM ac_virtualchest WHERE owner = ?;");
                s.setInt(1, p.getID());
                ResultSet rs = s.executeQuery();
                if (rs.next()) {
                    chestID = rs.getInt("ID");
                }
                db.closeResultSet(rs);
                db.closeStatement(s);
            } catch (SQLException ex) {
                ACLogger.severe(ex);
            }

            if (chestID == -1) {
                return null;
            }
            return new SQLChest(chestID);
        }
    }

}
