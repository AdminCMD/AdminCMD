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

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class CreationHolder {

    private final Inventory inv;
    private String name = null;
    private long time = -1;

    public CreationHolder() {
        this.inv = Bukkit.createInventory(null, 54, "Kit Creation");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Inventory getInventory() {
        return inv;
    }

    public boolean isReady() {
        return this.name != null && this.time != -1 && this.inv.getContents().length > 0;
    }

}
