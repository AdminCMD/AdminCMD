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
import com.admincmd.utils.MultiServerLocation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLWarp implements ACWarp {

    private final String name;

    public SQLWarp(String name) {
        this.name = name;
    }

    @Override
    public int getID() {
        try {
            PreparedStatement getNameStatement = DatabaseFactory.getDatabase().getPreparedStatement("SELECT id FROM " + DatabaseFactory.WARP_TABLE + " WHERE name = ?;");
            getNameStatement.setString(1, name);
            ResultSet rs = getNameStatement.executeQuery();

            int ret = -1;

            if (rs.next()) {
                ret = rs.getInt("id");
            }

            DatabaseFactory.getDatabase().closeStatement(getNameStatement);
            DatabaseFactory.getDatabase().closeResultSet(rs);
            return ret;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MultiServerLocation getLocation() {
        try {
            PreparedStatement getNameStatement = DatabaseFactory.getDatabase().getPreparedStatement("SELECT location FROM " + DatabaseFactory.WARP_TABLE + " WHERE name = ?;");
            getNameStatement.setString(1, name);
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
    public void setLocation(final MultiServerLocation loc) {
        try {
            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("UPDATE " + DatabaseFactory.WARP_TABLE + " SET location = ? WHERE name = ?;");
            st.setString(1, loc.toString());
            st.setString(2, name);
            st.executeUpdate();
            DatabaseFactory.getDatabase().closeStatement(st);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
