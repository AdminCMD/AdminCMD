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

public class StoredWarp implements ACWarp {

    private int id = -1;
    private String name;
    private MultiServerLocation loc;
    private boolean hasChanged = false;

    public StoredWarp(ResultSet rs) {
        try {
            this.id = rs.getInt("id");
            this.name = rs.getString("name");
            this.loc = MultiServerLocation.fromString(rs.getString("location"));
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    public StoredWarp(String name, MultiServerLocation loc) {
        try {
            PreparedStatement sta = DatabaseFactory.getDatabase().getPreparedStatement("INSERT INTO " + DatabaseFactory.WARP_TABLE + " (location, name) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            sta.setString(1, loc.toString());
            sta.setString(2, name);
            int affectedRows = sta.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating warp failed, no rows affected.");
            }

            ResultSet generatedKeys = sta.getGeneratedKeys();
            if (generatedKeys.next()) {
                this.id = generatedKeys.getInt(1);
            } else {
                String sql = Config.MYSQL_USE.getBoolean() ? "MySQL" : "SQLite";
                throw new SQLException("Creating warp failed, no ID obtained. SQL type: " + sql);
            }
            DatabaseFactory.getDatabase().closeStatement(sta);
            DatabaseFactory.getDatabase().closeResultSet(generatedKeys);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MultiServerLocation getLocation() {
        return loc;
    }

    @Override
    public void setLocation(MultiServerLocation loc) {
        this.hasChanged = true;
        this.loc = loc;
    }

    public boolean hasChanged() {
        return this.hasChanged;
    }

}
