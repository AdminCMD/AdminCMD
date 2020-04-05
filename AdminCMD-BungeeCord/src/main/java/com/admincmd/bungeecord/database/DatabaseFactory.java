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
package com.admincmd.bungeecord.database;

import com.admincmd.bungeecord.Main;
import com.admincmd.bungeecord.utils.Config;
import java.sql.SQLException;

public class DatabaseFactory {

    private static Database db = null;
    public static final String PLAYER_TABLE = "ac_player";    

    public static void init() {
        db = new MySQL(Config.MYSQL_IP.getString(), Config.MYSQL_USER.getString(), Config.MYSQL_PASSWORD.getString(), Config.MYSQL_DATABASE.getString(), Config.MYSQL_PORT.getInteger());
        if (db.testConnection()) {
            Main.getInstance().getLogger().info("The connection was successful!");
            createTables();
        } else {
            Main.getInstance().getLogger().severe("Could not connect to the Database!");
        }

    }

    private static void createTables() {
        try {
            String PLAYER_TABLE_QUERY;            

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
            db.executeStatement(PLAYER_TABLE_QUERY);            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Database getDatabase() {
        return db;
    }

}
