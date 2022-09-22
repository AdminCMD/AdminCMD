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
import com.admincmd.utils.MultiServerLocation;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLHome implements ACHome {

    private final int ID;

    public SQLHome(int id) {
        this.ID = id;
    }

    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public String getName() {
        try {
            PreparedStatement getNameStatement = DatabaseFactory.getDatabase().getPreparedStatement("SELECT name FROM " + DatabaseFactory.HOME_TABLE + " WHERE id = ?;");
            getNameStatement.setInt(1, ID);
            ResultSet rs = getNameStatement.executeQuery();

            String ret = null;

            if (rs.next()) {
                ret = rs.getString("name");
            }

            DatabaseFactory.getDatabase().closeStatement(getNameStatement);
            DatabaseFactory.getDatabase().closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public MultiServerLocation getLocation() {
        try {
            PreparedStatement getNameStatement = DatabaseFactory.getDatabase().getPreparedStatement("SELECT location FROM " + DatabaseFactory.HOME_TABLE + " WHERE id = ?;");
            getNameStatement.setInt(1, ID);
            ResultSet rs = getNameStatement.executeQuery();

            MultiServerLocation ret = null;

            if (rs.next()) {
                ret = MultiServerLocation.fromString(rs.getString("location"));
            }

            DatabaseFactory.getDatabase().closeStatement(getNameStatement);
            DatabaseFactory.getDatabase().closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void setLocation(final MultiServerLocation newLoc) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.HOME_TABLE + " SET location = ? WHERE id = ?;");
                    st.setString(1, newLoc.toString());
                    st.setInt(2, ID);
                    st.executeUpdate();
                    DatabaseFactory.getDatabase().closeStatement(st);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public ACPlayer getOwner() {
        try {
            PreparedStatement getNameStatement = DatabaseFactory.getDatabase().getPreparedStatement("SELECT playerid FROM " + DatabaseFactory.HOME_TABLE + " WHERE id = ?;");
            getNameStatement.setInt(1, ID);
            ResultSet rs = getNameStatement.executeQuery();

            ACPlayer owner = null;

            if (rs.next()) {
                owner = PlayerManager.getPlayer(rs.getInt("playerid"));
            }

            DatabaseFactory.getDatabase().closeStatement(getNameStatement);
            DatabaseFactory.getDatabase().closeResultSet(rs);
            return owner;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
