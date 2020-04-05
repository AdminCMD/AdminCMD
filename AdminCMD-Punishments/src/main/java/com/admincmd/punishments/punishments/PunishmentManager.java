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
package com.admincmd.punishments.punishments;

import com.admincmd.database.Database;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.punishments.Punishments;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;

public class PunishmentManager {

    //Player who was punished, Punishment
    private static final Map<ACPlayer, Punishment> punishments = new HashMap<>();
    private static Database db;

    public static void init() {
        db = Punishments.getInstance().getDB();
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getInstance(), new Runnable() {
            @Override
            public void run() {
                punishments.clear();
                try {
                    PreparedStatement ps = db.getPreparedStatement("SELECT * FROM ac_punishments;");
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        ACPlayer target = PlayerManager.getPlayer(rs.getInt("target"));
                        int creatorID = rs.getInt("creator");
                        String reason = rs.getString("reason");

                        ACPlayer creator = null;
                        if (creatorID != -1) {
                            creator = PlayerManager.getPlayer(creatorID);
                        }

                        PunishmentType type = PunishmentType.valueOf(rs.getString("type"));
                        long expires = rs.getLong("expires");
                        Punishment pu = new Punishment(target, creator, type, expires, id, reason);
                        if (pu.isExpired()) {
                            deletePunishment(pu);
                        } else {
                            if (!Config.BUNGEECORD.getBoolean()) {
                                punishments.put(target, pu);
                            }
                        }
                    }
                    db.closeResultSet(rs);
                    db.closeStatement(ps);
                } catch (SQLException ex) {
                    ACLogger.severe(ex);
                }
            }
        });
    }

    public static void savePunishment(Punishment pu) {
        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement st = db.getPreparedStatement("INSERT INTO ac_punishments (target, creator, type, expires, reason) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
                    st.setInt(1, pu.getTarget().getID());
                    if (pu.getCreator() != null) {
                        st.setInt(2, pu.getCreator().getID());
                    } else {
                        st.setInt(2, -1);
                    }
                    st.setString(3, pu.getType().toString());
                    st.setLong(4, pu.getExpiresAt());
                    st.setString(5, pu.getReason());

                    int affectedRows = st.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException("Creating punishment failed, no rows affected.");
                    }

                    int id = -1;

                    ResultSet generatedKeys = st.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        id = generatedKeys.getInt(1);
                    } else {
                        String sql = Config.MYSQL_USE.getBoolean() ? "MySQL" : "SQLite";
                        throw new SQLException("Creating punishment failed, no ID obtained. SQL type: " + sql);
                    }

                    db.closeResultSet(generatedKeys);
                    db.closeStatement(st);

                    if (!Config.BUNGEECORD.getBoolean()) {
                        Punishment newPunish = new Punishment(pu.getTarget(), pu.getCreator(), pu.getType(), pu.getExpiresAt(), id, pu.getReason());
                        punishments.put(newPunish.getTarget(), newPunish);
                    }
                } catch (SQLException ex) {
                    ACLogger.severe("Error creating Punishment", ex);
                }
            }
        });
    }

    public static void deletePunishment(Punishment pu) {
        if (punishments.containsValue(pu)) {
            punishments.remove(pu.getTarget());
        }

        Bukkit.getScheduler().runTaskAsynchronously(Punishments.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement ps = db.getPreparedStatement("DELETE FROM ac_punishments WHERE id = ?;");
                    ps.setInt(1, pu.getID());
                    ps.executeUpdate();
                    db.closeStatement(ps);
                } catch (SQLException ex) {
                    ACLogger.severe(ex);
                }
            }
        });
    }

    public static Punishment getPunishment(ACPlayer target) {
        if (punishments.containsKey(target)) {
            return punishments.get(target);
        }

        if (!Config.BUNGEECORD.getBoolean()) {
            return null;
        }

        Punishment ret = null;

        try {
            PreparedStatement ps = db.getPreparedStatement("SELECT * FROM ac_punishments WHERE target = ?;");
            ps.setInt(1, target.getID());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                ACPlayer creator = PlayerManager.getPlayer(rs.getInt("creator"));
                PunishmentType type = PunishmentType.valueOf(rs.getString("type"));
                long expires = rs.getLong("expires");
                String reason = rs.getString("reason");
                ret = new Punishment(target, creator, type, expires, id, reason);
            }
            db.closeResultSet(rs);
            db.closeStatement(ps);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }

        if (ret != null && ret.isExpired()) {
            deletePunishment(ret);
            return null;
        }

        return ret;

    }

    public static boolean isPunished(ACPlayer target) {
        return getPunishment(target) != null;
    }

}
