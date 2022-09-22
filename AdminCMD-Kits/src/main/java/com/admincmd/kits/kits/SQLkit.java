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
package com.admincmd.kits.kits;

import com.admincmd.database.Database;
import com.admincmd.kits.Kits;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.ItemSerialization;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLkit implements ACKit {

    private final String name;
    private final Database db = Kits.getInstance().getDB();

    protected SQLkit(String name) {
        this.name = name;
    }

    @Override
    public Inventory getInventory() {
        try {
            Inventory ret = Bukkit.createInventory(null, 54, "Kit: " + name);
            PreparedStatement p = db.getPreparedStatement("SELECT inventory FROM ac_kits WHERE name = ?;");
            p.setString(1, name);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                ItemStack[] contents = ItemSerialization.loadInventory(rs.getString("inventory"));
                ret.addItem(contents);
            }
            db.closeResultSet(rs);
            db.closeStatement(p);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe(ex);
            return null;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getTime() {
        try {
            long ret = -1;
            PreparedStatement p = db.getPreparedStatement("SELECT time FROM ac_kits WHERE name = ?;");
            p.setString(1, name);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                ret = rs.getLong("time");
            }
            db.closeResultSet(rs);
            db.closeStatement(p);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe(ex);
            return -1;
        }
    }

    @Override
    public int getID() {
        try {
            int ret = -1;
            PreparedStatement p = db.getPreparedStatement("SELECT id FROM ac_kits WHERE name = ?;");
            p.setString(1, name);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                ret = rs.getInt("id");
            }
            db.closeResultSet(rs);
            db.closeStatement(p);
            return ret;
        } catch (SQLException ex) {
            ACLogger.severe(ex);
            return -1;
        }
    }

}
