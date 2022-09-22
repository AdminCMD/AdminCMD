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
import com.admincmd.utils.ACLogger;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StoredWorld implements ACWorld {

    private long moment;
    private boolean timePaused;
    private String serverName;
    private String name;
    private boolean hasChanged = false;

    public StoredWorld(ResultSet rs) {
        try {
            this.name = rs.getString("name");
            this.timePaused = rs.getBoolean("paused");
            this.moment = rs.getLong("time");
            this.serverName = rs.getString("servername");
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
    }

    public StoredWorld(World w) {
        this.name = w.getName();
        this.timePaused = false;
        this.moment = w.getTime();
        this.serverName = BungeeCordMessageManager.getServerName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getPausedTime() {
        return this.moment;
    }

    @Override
    public void setPausedTime(long time) {
        this.hasChanged = true;
        this.moment = time;
    }

    @Override
    public boolean isPaused() {
        return this.timePaused;
    }

    @Override
    public void setPaused(boolean pause) {
        this.hasChanged = true;
        this.timePaused = pause;
    }

    @Override
    public String getServer() {
        return this.serverName;
    }

    @Override
    public boolean isOnThisServer() {
        return this.serverName.equalsIgnoreCase(BungeeCordMessageManager.getServerName());
    }

    public boolean hasChanged() {
        return this.hasChanged;
    }
}
