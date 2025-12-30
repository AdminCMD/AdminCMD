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
package com.admincmd.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Deprecated
/**
 * Legacy Class for Serializing Ivnentories.
 *
 * @deprecated Use {@link com.admincmd.utils.ItemSerializationJson}
 */
public class ItemSerialization {

    /**
     *
     * @param inventory
     * @return
     * @deprecated use
     * {@link com.admincmd.utils.ItemSerializationJson#saveInventory(org.bukkit.inventory.Inventory)}
     */
    @Deprecated
    public static String saveInventory(Inventory inventory) {
        return ItemSerializationJson.saveInventory(inventory);
    }

    /**
     *
     * @param data
     * @return
     * @deprecated use
     * {@link com.admincmd.utils.ItemSerializationJson#loadInventory(java.lang.String)}
     */
    @Deprecated
    public static ItemStack[] loadInventory(String data) {       
        return ItemSerializationJson.loadInventory(data);
    }
}
