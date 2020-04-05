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

import com.admincmd.database.Database;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.ItemSerialization;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class SQLChest implements ACChest {

    private final Database db = DatabaseFactory.getDatabase();
    private final int ID;

    public SQLChest(int id) {
        this.ID = id;
    }

    @Override
    public Inventory getInventory() {
        Inventory inv = null;
        ItemStack[] items = ItemSerialization.loadInventory(getString());
        inv = Bukkit.createInventory(null, 54, "§aVirtual Chest of: " + getOwner().getName());
        for (ItemStack item : items) {
            if (item != null) {
                inv.addItem(item);
            }
        }
        return inv;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public ACPlayer getOwner() {
        try {
            PreparedStatement s = db.getPreparedStatement("SELECT owner FROM ac_virtualchest WHERE ID = ?;");
            s.setInt(1, ID);
            ResultSet rs = s.executeQuery();
            int ownerID = -1;
            while (rs.next()) {
                ownerID = rs.getInt("owner");
            }
            db.closeResultSet(rs);
            db.closeStatement(s);
            return PlayerManager.getPlayer(ownerID);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
            return null;
        }
    }

    @Override
    public String getString() {
        try {
            PreparedStatement s = db.getPreparedStatement("SELECT inventory FROM ac_virtualchest WHERE ID = ?;");
            s.setInt(1, ID);
            ResultSet rs = s.executeQuery();
            String inv = "";
            while (rs.next()) {
                inv = rs.getString("inventory");
            }
            db.closeResultSet(rs);
            db.closeStatement(s);
            return inv;
        } catch (SQLException ex) {
            ACLogger.severe(ex);
            return "";
        }
    }

    @Override
    public void update(Inventory newInv) {
        try {
            PreparedStatement st = db.getPreparedStatement("UPDATE ac_virtualchest SET inventory = ? WHERE ID = ?;");
            st.setString(1, ItemSerialization.saveInventory(newInv));
            st.setInt(2, ID);
            st.executeUpdate();
            db.closeStatement(st);
        } catch (SQLException ex) {
            ACLogger.severe("Error saving chest!", ex);
        }
    }

    @Override
    public void clear() {
        try {
            PreparedStatement st = db.getPreparedStatement("UPDATE ac_virtualchest SET inventory = ? WHERE ID = ?;");
            st.setString(1, "");
            st.setInt(2, getID());
            st.executeUpdate();
            db.closeStatement(st);
        } catch (SQLException ex) {
            ACLogger.severe("Error saving chest!", ex);
        }
    }

}
