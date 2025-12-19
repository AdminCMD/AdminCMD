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
package com.admincmd.player;

import com.admincmd.database.Database;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.MultiServerLocation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLPlayer implements ACPlayer {

    private final Database db = DatabaseFactory.getDatabase();
    private int id;

    public SQLPlayer(int id) {
        this.id = id;
    }

    public SQLPlayer(OfflinePlayer op) {
        try {
            PreparedStatement getNameStatement = db.getPreparedStatement("SELECT ID FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE nickname = ?;");
            getNameStatement.setString(1, op.getName());
            ResultSet rs = getNameStatement.executeQuery();

            int ret = -1;

            if (rs.next()) {
                ret = rs.getInt("ID");
            }

            db.closeStatement(getNameStatement);
            db.closeResultSet(rs);
            this.id = ret;
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public MultiServerLocation getLocation() {
        if (getPlayer() != null) {
            return MultiServerLocation.fromLocation(getPlayer().getLocation());
        } else {
            return null;
        }
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(getName());
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(getUUID());
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
            ACLogger.severe("Error getting player name", ex);
            return "unknown";
        }
    }

    @Override
    public UUID getUUID() {
        try {
            PreparedStatement getNameStatement = db.getPreparedStatement("SELECT uuid FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
            getNameStatement.setInt(1, id);
            ResultSet rs = getNameStatement.executeQuery();

            UUID ret = null;

            if (rs.next()) {
                ret = UUID.fromString(rs.getString("uuid"));
            }

            db.closeStatement(getNameStatement);
            db.closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe("Error getting player uuid", ex);
            return null;
        }
    }

    @Override
    public boolean isFly() {
        return getFromDB("fly");
    }

    @Override
    public void setFly(boolean fly) {
        setInDB("fly", fly);
    }

    @Override
    public boolean isGod() {
        return getFromDB("god");
    }

    @Override
    public void setGod(boolean god) {
        setInDB("god", god);
    }

    @Override
    public boolean isFreezed() {
        return getFromDB("freeze");
    }

    @Override
    public void setFreezed(boolean freeze) {
        setInDB("freeze", freeze);
    }

    @Override
    public boolean isInvisible() {
        return getFromDB("invisible");
    }

    @Override
    public void setInvisible(boolean inv) {
        setInDB("invisible", inv);
    }

    @Override
    public boolean isCMDWatcher() {
        return getFromDB("commandwatcher");
    }

    @Override
    public void setCMDWatcher(boolean cmdwatch) {
        setInDB("commandwatcher", cmdwatch);
    }

    @Override
    public boolean isSpy() {
        return getFromDB("spy");
    }

    @Override
    public void setSpy(boolean spy) {
        setInDB("spy", spy);
    }

    @Override
    public int getLastMSGFrom() {
        try {
            PreparedStatement getNameStatement = db.getPreparedStatement("SELECT lastmsgfrom FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
            getNameStatement.setInt(1, id);
            ResultSet rs = getNameStatement.executeQuery();

            int ret = -1;

            if (rs.next()) {
                ret = rs.getInt("lastmsgfrom");
            }

            db.closeStatement(getNameStatement);
            db.closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe("Error getting player last msg", ex);
            return -1;
        }
    }

    @Override
    public void setLastMSGFrom(final int userID) {
        try {
            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.PLAYER_TABLE + " SET lastmsgfrom = ? WHERE id = ?;");
            st.setInt(1, userID);
            st.setInt(2, id);
            st.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(st);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    private boolean getFromDB(String value) {
        try {
            PreparedStatement getNameStatement = db.getPreparedStatement("SELECT " + value + " FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
            getNameStatement.setInt(1, id);
            ResultSet rs = getNameStatement.executeQuery();

            boolean ret = false;

            if (rs.next()) {
                ret = rs.getBoolean(value);
            }

            db.closeStatement(getNameStatement);
            db.closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe("Error getting player " + value, ex);
            return false;
        }
    }

    private void setInDB(final String key, final boolean value) {
        try {
            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.PLAYER_TABLE + " SET " + key + " = ? WHERE id = ?;");
            st.setBoolean(1, value);
            st.setInt(2, id);
            st.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(st);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    @Override
    public boolean hasLastLoc() {
        try {
            PreparedStatement getNameStatement = db.getPreparedStatement("SELECT lastloc FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
            getNameStatement.setInt(1, id);
            ResultSet rs = getNameStatement.executeQuery();

            boolean ret = false;

            if (rs.next()) {
                String lastloc = rs.getString("lastloc");
                ret = !lastloc.equalsIgnoreCase("none");
            }

            db.closeStatement(getNameStatement);
            db.closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe("Error getting player lastloc", ex);
            return false;
        }
    }

    @Override
    public MultiServerLocation getLastLoc() {
        try {
            PreparedStatement getNameStatement = db.getPreparedStatement("SELECT lastloc FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
            getNameStatement.setInt(1, id);
            ResultSet rs = getNameStatement.executeQuery();

            MultiServerLocation ret = null;

            if (rs.next()) {
                String lastloc = rs.getString("lastloc");
                ret = MultiServerLocation.fromString(lastloc);
            }

            db.closeStatement(getNameStatement);
            db.closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe("Error getting player lastloc", ex);
            return null;
        }
    }

    @Override
    public void setLastLoc(final MultiServerLocation loc) {
        try {
            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.PLAYER_TABLE + " SET lastloc = ? WHERE id = ?;");
            st.setString(1, loc.toString());
            st.setInt(2, id);
            st.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(st);
            ACLogger.debug("Location set to: " + loc);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    @Override
    public boolean isOnline() {
        return getFromDB("online");
    }

    @Override
    public String getServer() {
        try {
            PreparedStatement getNameStatement = db.getPreparedStatement("SELECT server FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE ID = ?;");
            getNameStatement.setInt(1, id);
            ResultSet rs = getNameStatement.executeQuery();

            String ret = null;

            if (rs.next()) {
                ret = rs.getString("server");
            }
            db.closeStatement(getNameStatement);
            db.closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe("Error getting player server", ex);
            return null;
        }
    }

}
