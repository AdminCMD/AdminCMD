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
package com.admincmd.chestsigns.inventorysigns;

import com.admincmd.chestsigns.ChestSigns;
import com.admincmd.database.Database;
import com.admincmd.player.ACPlayer;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.MultiServerLocation;
import org.bukkit.block.Sign;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public final class SignManager {

    private static final Map<Integer, StoredSign> signs = new HashMap<>();
    private static final Database db = ChestSigns.getInstance().getDB();

    public static void init() {
        try {
            PreparedStatement s = db.getPreparedStatement("SELECT * FROM ac_chestsigns");
            ResultSet rs = s.executeQuery();
            int loaded = 0;
            while (rs.next()) {
                StoredSign sign = new StoredSign(MultiServerLocation.fromString(rs.getString("location")), rs.getInt("owner"), rs.getInt("ID"));
                signs.put(rs.getInt("ID"), sign);
                loaded++;
            }

            ACLogger.info("Loaded " + loaded + " InventorySigns.");
        } catch (SQLException ex) {
            ACLogger.severe("Error loading InventorySigns.", ex);
        }
    }

    public static InventorySign createSign(ACPlayer p, Sign signBlock) {
        try {
            MultiServerLocation loc = MultiServerLocation.fromLocation(signBlock.getLocation());
            PreparedStatement st = db.getPreparedStatement("INSERT INTO ac_chestsigns (owner, location) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, p.getID());
            st.setString(2, loc.toString());

            int affectedRows = st.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating sign failed, no rows affected.");
            }

            int id;

            ResultSet generatedKeys = st.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating Sign failed, no ID obtained");
            }

            db.closeResultSet(generatedKeys);
            db.closeStatement(st);
            ACLogger.debug("Sign created");
                StoredSign sign = new StoredSign(loc, p.getID(), id);
                signs.put(id, sign);
        } catch (SQLException ex) {
            ACLogger.severe("Error creating VirtualChest", ex);
        }
        return getSign(p);
    }

    public static void deleteSign(InventorySign sign) {
        try {
            PreparedStatement s = db.getPreparedStatement("DELETE FROM ac_chestsigns WHERE id = ?;");
            s.setInt(1, sign.getID());
            s.execute();
            db.closeStatement(s);
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }
        if(sign.exists()) {
            sign.getLocation().getBukkitLocation().getBlock().breakNaturally();
        }
        signs.remove(sign.getID());
    }

    public static boolean hasSign(ACPlayer player) {
        return getSign(player) != null;
    }

    public static InventorySign getSign(ACPlayer p) {
        for (InventorySign sign : signs.values()) {
            if (sign.getOwner().getID() == p.getID()) {
                if(sign.exists()) {
                    return sign;
                } else {
                    deleteSign(sign);
                    return null;
                }
            }
        }
        return null;
    }

    public static InventorySign getSign(int signId) {
        if (signs.containsKey(signId)) {
            InventorySign sign = signs.get(signId);
            if(sign.exists()) {
                return sign;
            } else {
                deleteSign(sign);
                return null;
            }
        }
        return null;
    }

    public static InventorySign getSignFromLocation(MultiServerLocation loc) {
        for (InventorySign sign : signs.values()) {
            if (sign.getLocation().toString().equals(loc.toString())) {
                if(sign.exists()) {
                    return sign;
                } else {
                    deleteSign(sign);
                    return null;
                }
            }
        }
        return null;
    }
}
