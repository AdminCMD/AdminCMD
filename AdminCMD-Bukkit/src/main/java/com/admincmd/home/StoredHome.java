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

import com.admincmd.database.DatabaseFactory;
import com.admincmd.player.ACPlayer;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import com.admincmd.utils.MultiServerLocation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StoredHome implements ACHome {

    private final ACPlayer owner;
    private final String name;
    private MultiServerLocation loc;
    private int id = -1;
    private boolean changed = false;

    public StoredHome(ACPlayer owner, String name) {
        this.loc = owner.getLocation();
        this.owner = owner;
        this.name = name;

        try {

            PreparedStatement st = DatabaseFactory.getDatabase().getPreparedStatement("INSERT INTO " + DatabaseFactory.HOME_TABLE + "(playerid, location, name) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            if (st != null) {
                st.setInt(1, owner.getID());
                st.setString(2, owner.getLocation().toString());
                st.setString(3, name);

                int affectedRows = st.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating home failed, no rows affected.");
                }

                ResultSet generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                } else {
                    String sql = Config.MYSQL_USE.getBoolean() ? "MySQL" : "SQLite";
                    throw new SQLException("Creating Home failed, no ID obtained. SQL type: " + sql);
                }

                DatabaseFactory.getDatabase().closeResultSet(generatedKeys);
                DatabaseFactory.getDatabase().closeStatement(st);
            }
        } catch (SQLException ex) {
            ACLogger.severe("Error putting new home in database!", ex);
        }
    }

    public StoredHome(String serializedLocation, ACPlayer owner, String name, int id) {
        this.loc = MultiServerLocation.fromString(serializedLocation);
        this.owner = owner;
        this.name = name;
        this.id = id;
    }

    @Override
    public int ID() {
        return id;
    }

    @Override
    public MultiServerLocation getLocation() {
        return loc;
    }

    @Override
    public void setLocation(MultiServerLocation newLoc) {
        this.loc = newLoc;
        this.changed = true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ACPlayer getOwner() {
        return this.owner;
    }

    public boolean hasChanged() {
        return this.changed;
    }
}
