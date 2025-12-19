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

import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.utils.ACLogger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public record SQLWorld(String name, String server) implements ACWorld {

    @Override
    public long getPausedTime() {
        long ret = 0;
        try {
            PreparedStatement ps = DatabaseFactory.getDatabase().getPreparedStatement("SELECT time FROM " + DatabaseFactory.WORLD_TABLE + " WHERE name = ? AND servername = ?;");
            ps.setString(1, name);
            ps.setString(2, server);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getLong("time");
            }
            DatabaseFactory.getDatabase().closeResultSet(rs);
            DatabaseFactory.getDatabase().closeStatement(ps);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
        return ret;
    }

    @Override
    public void setPausedTime(final long time) {
        try {
            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.WORLD_TABLE + " SET time = ? WHERE name = ? AND servername = ?;");
            st.setLong(1, time);
            st.setString(2, name);
            st.setString(3, server);
            st.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(st);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    @Override
    public boolean isPaused() {
        boolean ret = false;
        try {
            PreparedStatement ps = DatabaseFactory.getDatabase().getPreparedStatement("SELECT paused FROM " + DatabaseFactory.WORLD_TABLE + " WHERE name = ? AND servername = ?;");
            ps.setString(1, name);
            ps.setString(2, server);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret = rs.getBoolean("paused");
            }
            DatabaseFactory.getDatabase().closeResultSet(rs);
            DatabaseFactory.getDatabase().closeStatement(ps);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
        return ret;
    }

    @Override
    public void setPaused(final boolean pause) {
        try {
            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.WORLD_TABLE + " SET paused = ? WHERE name = ? AND servername = ?;");
            st.setBoolean(1, pause);
            st.setString(2, name);
            st.setString(3, server);
            st.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(st);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    @Override
    public boolean isOnThisServer() {
        return this.server.equalsIgnoreCase(BungeeCordMessageManager.getServerName());
    }
}
