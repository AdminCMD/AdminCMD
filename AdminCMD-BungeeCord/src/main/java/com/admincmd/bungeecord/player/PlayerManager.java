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
package com.admincmd.bungeecord.player;

import com.admincmd.bungeecord.Main;
import com.admincmd.bungeecord.database.Database;
import com.admincmd.bungeecord.database.DatabaseFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerManager {

    private static final Database conn = DatabaseFactory.getDatabase();

    public static ACPlayer getPlayer(int id) {
        SQLPlayer ret = null;
        try {
            PreparedStatement s = conn.getPreparedStatement("SELECT uuid FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
            s.setInt(1, id);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                ret = new SQLPlayer(id, UUID.fromString(rs.getString("uuid")));
            }
            conn.closeResultSet(rs);
            conn.closeStatement(s);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;

    }

    public static ACPlayer getPlayer(UUID uuid) {
        SQLPlayer ret = null;
        try {
            PreparedStatement s = conn.getPreparedStatement("SELECT ID FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE uuid = ?;");
            s.setString(1, uuid.toString());
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                ret = new SQLPlayer(rs.getInt("ID"), uuid);
            }
            conn.closeResultSet(rs);
            conn.closeStatement(s);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static void createPlayer(ProxiedPlayer player) {
        try {
            Database db = DatabaseFactory.getDatabase();
            PreparedStatement s = db.getPreparedStatement("INSERT INTO " + DatabaseFactory.PLAYER_TABLE + " (uuid, god, invisible, commandwatcher, spy, fly, freeze, nickname, lastmsgfrom, online, server, lastloc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            s.setString(1, player.getUniqueId().toString());
            s.setBoolean(2, false);
            s.setBoolean(3, false);
            s.setBoolean(4, false);
            s.setBoolean(5, false);
            s.setBoolean(6, false);
            s.setBoolean(7, false);
            s.setString(8, player.getName());
            s.setInt(9, -1);
            s.setBoolean(10, player.isConnected());
            s.setString(11, player.getServer().getInfo().getName());
            s.setString(12, "none");

            int affectedRows = s.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Player failed, no rows affected.");
            }

            ResultSet generatedKeys = s.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
            } else {                
                throw new SQLException("Creating player failed, no ID obtained.");
            }

            DatabaseFactory.getDatabase().closeResultSet(generatedKeys);
            DatabaseFactory.getDatabase().closeStatement(s);
            Main.getInstance().getLogger().info("New Player " + player.getName() + " registered in database.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
