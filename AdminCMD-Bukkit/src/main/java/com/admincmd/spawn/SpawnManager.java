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
package com.admincmd.spawn;

import com.admincmd.database.Database;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import com.admincmd.utils.MultiServerLocation;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpawnManager {

    private static final Database db = DatabaseFactory.getDatabase();

    public static MultiServerLocation getSpawn(ACPlayer p) {
        if (!Config.GLOBAL_SPAWNS.getBoolean()) {
            try {
                PreparedStatement st = db.getPreparedStatement("SELECT * FROM ac_spawn");
                ResultSet rs = st.executeQuery();
                MultiServerLocation spawn = null;
                while (rs.next()) {
                    spawn = MultiServerLocation.fromString(rs.getString("location"));
                }
                db.closeResultSet(rs);
                db.closeStatement(st);

                if (spawn != null) {
                    return spawn;
                } else {
                    if (!PlayerManager.isOnThisServer(p)) {
                        return MultiServerLocation.fromLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
                    } else {
                        return MultiServerLocation.fromLocation(p.getPlayer().getWorld().getSpawnLocation());
                    }
                }
            } catch (SQLException ex) {
                ACLogger.severe("Error loading spawn", ex);
                return null;
            }
        } else {
            if (!PlayerManager.isOnThisServer(p)) {
                return MultiServerLocation.fromLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
            } else {
                return MultiServerLocation.fromLocation(p.getPlayer().getWorld().getSpawnLocation());
            }
        }
    }

    private static boolean checkIfExists() {
        try {
            PreparedStatement st = db.getPreparedStatement("SELECT * FROM ac_spawn");
            ResultSet rs = st.executeQuery();
            boolean exists = rs.next();
            db.closeResultSet(rs);
            db.closeStatement(st);
            return exists;
        } catch (SQLException ex) {
            ACLogger.severe("Error loading spawn", ex);
            return true;
        }
    }

    public static void setSpawn(ACPlayer p) {
        if (!Config.GLOBAL_SPAWNS.getBoolean()) {
            if (!checkIfExists()) {
                createSpawn(p.getLocation());
            } else {
                try {
                    PreparedStatement st = db.getPreparedStatement("UPDATE ac_spawn SET location = ?;");
                    st.setString(1, p.getLocation().toString());
                    st.executeUpdate();
                    db.closeStatement(st);
                } catch (SQLException ex) {
                    ACLogger.severe("Error saving spawn!", ex);
                }
            }
        } else {
            p.getPlayer().getWorld().setSpawnLocation(p.getLocation().getBukkitLocation());
        }
    }

    private static void createSpawn(MultiServerLocation s) {
        try {
            PreparedStatement pst = db.getPreparedStatement("INSERT INTO ac_spawn (location) VALUES (?);");
            pst.setString(1, s.toString());
            pst.executeUpdate();
            db.closeStatement(pst);
        } catch (SQLException ex) {
            ACLogger.severe("Error creating spawn!", ex);
        }
    }

}
