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
package com.admincmd.database;

import com.admincmd.Main;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;

import java.io.File;
import java.sql.SQLException;

public class DatabaseFactory {

    public static final String PLAYER_TABLE = "ac_player";
    public static final String HOME_TABLE = "ac_homes";
    public static final String SPAWN_TABLE = "ac_spawn";
    public static final String WORLD_TABLE = "ac_worlds";
    public static final String WARP_TABLE = "ac_warps";
    public static final String TP_REQUEST_TABLE = "ac_teleport_requests";
    private static Database db = null;

    public static void init() {
        if (Config.MYSQL_USE.getBoolean()) {
            db = new MySQL(Config.MYSQL_IP.getString(), Config.MYSQL_USER.getString(), Config.MYSQL_PASSWORD.getString(), Config.MYSQL_DATABASE.getString(), Config.MYSQL_PORT.getInteger());
        } else {
            db = new SQLite(new File(Main.getInstance().getDataFolder(), "Database.db"));
        }

        if (db.testConnection()) {
            ACLogger.info("The connection was successful!");
            createTables();
        } else {
            ACLogger.severe("Could not connect to the Database!");
        }

    }

    private static void createTables() {
        try {
            String PLAYER_TABLE_QUERY;
            String HOME_TABLE_QUERY;
            String SPAWN_TABLE_QUERY;
            String WORLD_TABLE_QUERY;
            String WARP_TABLE_QUERY;
            String TP_REQUEST_QUERY;
            if (db.getType() == Database.Type.SQLITE) {
                PLAYER_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + PLAYER_TABLE + " ("
                        + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "uuid varchar(64) NOT NULL,"
                        + "god BOOLEAN,"
                        + "invisible BOOLEAN,"
                        + "commandwatcher BOOLEAN,"
                        + "spy BOOLEAN,"
                        + "fly BOOLEAN,"
                        + "freeze BOOLEAN,"
                        + "nickname varchar(64) DEFAULT 'none',"
                        + "lastmsgfrom INTEGER,"
                        + "lastloc varchar(320),"
                        + "online BOOLEAN,"
                        + "server varchar(64)"
                        + ");";
                HOME_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + HOME_TABLE + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "playerid INTEGER NOT NULL,"
                        + "location varchar(320) NOT NULL,"
                        + "name varchar(64) NOT NULL"
                        + ");";
                SPAWN_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + SPAWN_TABLE + " ("
                        + "location TEXT NOT NULL"
                        + ");";

                WORLD_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + WORLD_TABLE + " ("
                        + "name varchar(64) PRIMARY KEY NOT NULL,"
                        + "paused BOOLEAN NOT NULL,"
                        + "time long NOT NULL,"
                        + "servername varchar(64) NOT NULL"
                        + ");";

                WARP_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + WARP_TABLE + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "location varchar(320) NOT NULL,"
                        + "name varchar(64) NOT NULL"
                        + ");";
                TP_REQUEST_QUERY = "CREATE TABLE IF NOT EXISTS " + TP_REQUEST_TABLE + " ("
                        + "requester INTEGER NOT NULL,"
                        + "receiver INTEGER NOT NULL,"
                        + "type varchar(64) NOT NULL"
                        + ");";
            } else {
                PLAYER_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + PLAYER_TABLE + " ("
                        + "ID INTEGER PRIMARY KEY AUTO_INCREMENT,"
                        + "uuid varchar(64) NOT NULL,"
                        + "god BOOLEAN,"
                        + "invisible BOOLEAN,"
                        + "commandwatcher BOOLEAN,"
                        + "spy BOOLEAN,"
                        + "fly BOOLEAN,"
                        + "freeze BOOLEAN,"
                        + "nickname varchar(64) DEFAULT 'none',"
                        + "lastmsgfrom INTEGER,"
                        + "lastloc varchar(320),"
                        + "online BOOLEAN,"
                        + "server varchar(64)"
                        + ");";
                HOME_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + HOME_TABLE + " ("
                        + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                        + "playerid INTEGER NOT NULL,"
                        + "location varchar(320) NOT NULL,"
                        + "name varchar(64) NOT NULL"
                        + ");";
                SPAWN_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + SPAWN_TABLE + " ("
                        + "location TEXT NOT NULL"
                        + ");";

                WORLD_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + WORLD_TABLE + " ("
                        + "name varchar(64) PRIMARY KEY NOT NULL,"
                        + "paused BOOLEAN NOT NULL,"
                        + "time long NOT NULL,"
                        + "servername varchar(64) NOT NULL"
                        + ");";
                WARP_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + WARP_TABLE + " ("
                        + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                        + "location varchar(320) NOT NULL,"
                        + "name varchar(64) NOT NULL"
                        + ");";
                TP_REQUEST_QUERY = "CREATE TABLE IF NOT EXISTS " + TP_REQUEST_TABLE + " ("
                        + "requester INTEGER NOT NULL,"
                        + "receiver INTEGER NOT NULL,"
                        + "type varchar(64) NOT NULL"
                        + ");";
            }
            db.executeStatement(PLAYER_TABLE_QUERY);
            db.executeStatement(HOME_TABLE_QUERY);
            db.executeStatement(SPAWN_TABLE_QUERY);
            db.executeStatement(WORLD_TABLE_QUERY);
            db.executeStatement(WARP_TABLE_QUERY);
            db.executeStatement(TP_REQUEST_QUERY);
        } catch (SQLException ex) {
            ACLogger.severe("Error creating database!", ex);
        }
    }

    public static Database getDatabase() {
        return db;
    }

}
