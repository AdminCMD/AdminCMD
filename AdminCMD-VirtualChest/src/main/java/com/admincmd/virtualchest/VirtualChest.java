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
package com.admincmd.virtualchest;

import com.admincmd.addon.Addon;
import com.admincmd.database.Database;
import com.admincmd.utils.ACLogger;
import com.admincmd.virtualchest.chest.ChestManager;

import java.sql.SQLException;

public class VirtualChest extends Addon {

    @Override
    public void enable() {
        createTable();
        ChestManager.init();

        registerEventListener("com.admincmd.virtualchest.events");
        registerCommands("com.admincmd.virtualchest.commands");
    }

    @Override
    public void disable() {
        ChestManager.save();
    }


    private void createTable() {
        try {
            Database db = getDB();

            String TABLE;

            if (db.getType() == Database.Type.MYSQL) {
                TABLE = "CREATE TABLE IF NOT EXISTS ac_virtualchest ("
                        + "ID INTEGER PRIMARY KEY AUTO_INCREMENT,"
                        + "owner INTEGER NOT NULL,"
                        + "inventory TEXT"
                        + ");";
            } else {
                TABLE = "CREATE TABLE IF NOT EXISTS ac_virtualchest ("
                        + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "owner INTEGER NOT NULL,"
                        + "inventory TEXT"
                        + ");";
            }

            db.executeStatement(TABLE);
        } catch (SQLException ex) {
            ACLogger.severe("Error creating VirtualChest tables.", ex);
        }
    }

}
