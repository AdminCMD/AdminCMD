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
package com.admincmd.kits.kits;

import com.admincmd.database.Database;
import com.admincmd.kits.Kits;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import com.admincmd.utils.ItemSerialization;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

public class KitManager {

    private static final Database db = Kits.getInstance().getDB();
    private static final Map<Integer, CreationHolder> holders = new HashMap<>();

    public static void openKit(ACPlayer p, ACKit kit) {
        if (!p.isOnline()) {
            return;
        }

        if (!PlayerManager.isOnThisServer(p)) {
            return;
        }

        Bukkit.getScheduler().runTask(Kits.getInstance(), new Runnable() {
            @Override
            public void run() {
                p.getPlayer().openInventory(kit.getInventory());
            }
        });
    }

    public static void startCreation(ACPlayer player) {
        if (holders.containsKey(player.getID())) {
            holders.remove(player.getID());
        }

        holders.put(player.getID(), new CreationHolder());
        player.getPlayer().openInventory(holders.get(player.getID()).getInventory());
    }

    public static CreationHolder getHolder(ACPlayer p) {
        if (!holders.containsKey(p.getID())) {
            return null;
        }

        return holders.get(p.getID());
    }

    public static ACKit getKit(String name) {
        SQLkit ret = null;
        try {
            PreparedStatement ps = db.getPreparedStatement("SELECT id FROM ac_kits WHERE name = ?;");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret = new SQLkit(rs.getString("name"));
            }
            db.closeResultSet(rs);
            db.closeStatement(ps);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
        return ret;
    }

    public static ACKit createFromCreationHolder(CreationHolder holder) {
        ACKit ret = null;

        if (!holder.isReady()) {
            return null;
        }

        try {
            PreparedStatement st = db.getPreparedStatement("INSERT INTO ac_kits (name, inventory, time) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, holder.getName());
            st.setString(2, ItemSerialization.saveInventory(holder.getInventory()));
            st.setLong(3, holder.getTime());

            int affectedRows = st.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Kit failed, no rows affected.");
            }

            int id = -1;

            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            } else {
                String sql = Config.MYSQL_USE.getBoolean() ? "MySQL" : "SQLite";
                throw new SQLException("Creating Kit failed, no ID obtained. SQL type: " + sql);
            }

            db.closeResultSet(generatedKeys);
            db.closeStatement(st);

            ret = new SQLkit(holder.getName());
        } catch (SQLException ex) {
            ACLogger.severe("Error creating Kit", ex);
        }

        return ret;
    }

}
