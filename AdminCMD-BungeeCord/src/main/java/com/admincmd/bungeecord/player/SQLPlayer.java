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

import com.admincmd.bungeecord.database.Database;
import com.admincmd.bungeecord.database.DatabaseFactory;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SQLPlayer implements ACPlayer {

    private final int id;
    private final UUID uuid;
    private final Database db = DatabaseFactory.getDatabase();

    public SQLPlayer(int id, UUID uuid) {
        this.id = id;
        this.uuid = uuid;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public String getName() {
        try {
            PreparedStatement getNameStatement = db.getPreparedStatement("SELECT nickname FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
            getNameStatement.setInt(1, id);
            ResultSet rs = getNameStatement.executeQuery();

            String ret = "unknown";

            if (rs.next()) {
                ret = rs.getString("nickname");
            }

            db.closeStatement(getNameStatement);
            db.closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "unknown";
        }
    }

    @Override
    public boolean isOnline() {
        try {
            PreparedStatement getNameStatement = db.getPreparedStatement("SELECT online FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
            getNameStatement.setInt(1, id);
            ResultSet rs = getNameStatement.executeQuery();

            boolean ret = false;

            if (rs.next()) {
                ret = rs.getBoolean("online");
            }

            db.closeStatement(getNameStatement);
            db.closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void setOnline(boolean online) {
        try {
            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.PLAYER_TABLE + " SET online = ? WHERE id = ?;");
            st.setBoolean(1, online);
            st.setInt(2, id);
            st.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(st);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getServer() {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        if (player == null || !player.isConnected()) {
            try {
                PreparedStatement getNameStatement = db.getPreparedStatement("SELECT server FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
                getNameStatement.setInt(1, id);
                ResultSet rs = getNameStatement.executeQuery();

                String ret = "unknown";

                if (rs.next()) {
                    ret = rs.getString("server");
                }

                db.closeStatement(getNameStatement);
                db.closeResultSet(rs);
                return ret;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return "unknown";
            }
        } else {
            return player.getServer().getInfo().getName();
        }
    }

    @Override
    public void setServer(String server) {
        try {
            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.PLAYER_TABLE + " SET server = ? WHERE id = ?;");
            st.setString(1, server);
            st.setInt(2, id);
            st.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(st);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setName(String name) {
        try {
            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.PLAYER_TABLE + " SET nickname = ? WHERE id = ?;");
            st.setString(1, name);
            st.setInt(2, id);
            st.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(st);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
