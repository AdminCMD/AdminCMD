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
package com.admincmd.kits;

import com.admincmd.addon.Addon;
import com.admincmd.database.Database;
import com.admincmd.kits.utils.Config;
import com.admincmd.utils.ACLogger;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Kits extends Addon {

    private static Kits INSTANCE;

    public static Kits getInstance() {
        return INSTANCE;
    }

    public static String transformReadable(long millis) {
        millis = (millis - System.currentTimeMillis());

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        String ret = "";
        if (days > 0) {
            ret += (days + " " + Config.STANDARD_DAYS.getString()) + " ";
        }

        if (hours > 0) {
            ret += (hours + " " + Config.STANDARD_HOURS.getString()) + " ";
        }

        if (minutes > 0) {
            ret += (minutes + " " + Config.STANDARD_MINUTES.getString()) + " ";
        }
        ret += (seconds + " " + Config.STANDARD_SECONDS.getString());
        return ret;
    }

    @Override
    public void enable() {
        INSTANCE = this;
        Config.load();
        createTable();

        registerCommands("com.admincmd.kits.commands");
    }

    @Override
    public void disable() {
    }

    private void createTable() {
        try {
            Database db = getDB();
            String TABLE;

            if (db.getType() == Database.Type.MYSQL) {
                TABLE = "CREATE TABLE IF NOT EXISTS ac_kits ("
                        + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                        + "name varchar(16) NOT NULL,"
                        + "inventory TEXT NOT NULL,"
                        + "time INTEGER NOT NULL"
                        + ");";
            } else {
                TABLE = "CREATE TABLE IF NOT EXISTS ac_kits ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "name varchar(16) NOT NULL,"
                        + "inventory TEXT NOT NULL,"
                        + "time INTEGER NOT NULL"
                        + ");";
            }

            db.executeStatement(TABLE);
        } catch (SQLException ex) {
            ACLogger.severe("Error creating Kits tables.", ex);
        }
    }

}
