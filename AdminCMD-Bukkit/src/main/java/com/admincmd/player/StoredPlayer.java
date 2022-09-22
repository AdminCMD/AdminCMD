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

import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.database.Database;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import com.admincmd.utils.MultiServerLocation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class StoredPlayer implements ACPlayer {

    public int id;
    private UUID uuid;
    private boolean fly = false;
    private boolean god = false;
    private boolean freeze = false;
    private boolean invisible = false;
    private boolean cmdwatcher = false;
    private boolean spy = false;
    private MultiServerLocation lastLoc = null;
    private int lastIDFrom = 0;
    private final Database db = DatabaseFactory.getDatabase();
    private boolean hasChanged = false;

    public StoredPlayer(UUID uuid) {
        try {
            this.uuid = uuid;

            PreparedStatement s = db.getPreparedStatement("INSERT INTO " + DatabaseFactory.PLAYER_TABLE + " (uuid, god, invisible, commandwatcher, spy, fly, freeze, nickname, lastmsgfrom, lastloc, online, server) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            s.setString(1, uuid.toString());
            s.setBoolean(2, false);
            s.setBoolean(3, false);
            s.setBoolean(4, false);
            s.setBoolean(5, false);
            s.setBoolean(6, false);
            s.setBoolean(7, false);
            s.setString(8, Bukkit.getOfflinePlayer(uuid).getName());
            s.setInt(9, -1);
            s.setString(10, "none");
            s.setBoolean(11, false);
            s.setString(12, BungeeCordMessageManager.getServerName());

            int affectedRows = s.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating Player failed, no rows affected.");
            }

            ResultSet generatedKeys = s.getGeneratedKeys();
            if (generatedKeys.next()) {
                this.id = generatedKeys.getInt(1);
            } else {
                String sql = Config.MYSQL_USE.getBoolean() ? "MySQL" : "SQLite";
                throw new SQLException("Creating player failed, no ID obtained. SQL type: " + sql);
            }

            DatabaseFactory.getDatabase().closeResultSet(generatedKeys);
            DatabaseFactory.getDatabase().closeStatement(s);
        } catch (SQLException ex) {
            Logger.getLogger(StoredPlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StoredPlayer(ResultSet rs) {
        try {
            this.id = rs.getInt("ID");
            this.uuid = UUID.fromString(rs.getString("uuid"));
            this.fly = rs.getBoolean("fly");
            this.god = rs.getBoolean("god");
            this.freeze = rs.getBoolean("freeze");
            this.invisible = rs.getBoolean("invisible");
            this.cmdwatcher = rs.getBoolean("commandwatcher");
            this.spy = rs.getBoolean("spy");
            this.lastIDFrom = rs.getInt("lastmsgfrom");
            this.lastLoc = rs.getString("lastloc").equalsIgnoreCase("none") ? null : MultiServerLocation.fromString(rs.getString("lastloc"));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public MultiServerLocation getLocation() {
        if (Bukkit.getPlayer(uuid) != null) {
            return MultiServerLocation.fromLocation(Bukkit.getPlayer(uuid).getLocation());
        }
        return null;
    }

    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public String getName() {
        return getOfflinePlayer().getName();
    }

    @Override
    public boolean isFly() {
        return fly;
    }

    @Override
    public boolean isGod() {
        return god;
    }

    @Override
    public boolean isFreezed() {
        return freeze;
    }

    @Override
    public boolean isInvisible() {
        return invisible;
    }

    @Override
    public boolean isCMDWatcher() {
        return cmdwatcher;
    }

    @Override
    public boolean isSpy() {
        return spy;
    }

    @Override
    public int getLastMSGFrom() {
        return lastIDFrom;
    }

    @Override
    public void setFly(boolean fly) {
        this.hasChanged = true;
        this.fly = fly;
    }

    @Override
    public void setGod(boolean god) {
        this.hasChanged = true;
        this.god = god;
    }

    @Override
    public void setFreezed(boolean freeze) {
        this.hasChanged = true;
        this.freeze = freeze;
    }

    @Override
    public void setInvisible(boolean inv) {
        this.hasChanged = true;
        this.invisible = inv;
    }

    @Override
    public void setCMDWatcher(boolean cmdwatch) {
        this.hasChanged = true;
        this.cmdwatcher = cmdwatch;
    }

    @Override
    public void setSpy(boolean spy) {
        this.hasChanged = true;
        this.spy = spy;
    }

    @Override
    public void setLastMSGFrom(int userID) {
        this.hasChanged = true;
        this.lastIDFrom = userID;
    }

    @Override
    public boolean hasLastLoc() {
        return this.lastLoc != null;
    }

    @Override
    public MultiServerLocation getLastLoc() {
        return this.lastLoc;
    }

    @Override
    public void setLastLoc(MultiServerLocation loc) {
        ACLogger.debug("Location set to: " + loc.toString());
        this.hasChanged = true;
        this.lastLoc = loc;
    }

    @Override
    public boolean isOnline() {
        return Bukkit.getServer().getPlayer(uuid) != null;
    }

    @Override
    public String getServer() {
        return BungeeCordMessageManager.getServerName();
    }
    
    public boolean hasChanged() {
        return this.hasChanged;
    }

}
