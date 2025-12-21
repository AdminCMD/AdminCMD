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

import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.MultiServerLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class StoredSign implements InventorySign {

    private final MultiServerLocation loc;
    private final int owner;
    private final int ID;

    public StoredSign(MultiServerLocation loc, int owner, int ID) {
        this.owner = owner;
        this.loc = loc;
        this.ID = ID;
    }

    @Override
    public MultiServerLocation getLocation() {
        return loc;
    }

    @Override
    public int getID() {
        return ID;
    }


    @Override
    public ACPlayer getOwner() {
        return PlayerManager.getPlayer(owner);
    }

    @Override
    public List<Container> getConnectingChests() {
        if(!exists()) return null;
        Block b = loc.getBukkitLocation().getBlock();
        List<Container> ret = new ArrayList<>();
        for (BlockFace face : BlockFace.values()) {
            if(!face.isCartesian()) continue;
            Block next = b.getRelative(face);
            if(!next.getType().toString().endsWith("CHEST")) {
                continue;
            }
            if(next.getState() instanceof Container) {
                ret.add((Container) next.getState());
            }
        }
        return ret;
    }

    @Override
    public boolean isValid() {
        return exists() && getConnectingChests() != null && getConnectingChests().size() == 1;
    }

    @Override
    public Inventory getConnectingInventory() {
        if(!isValid()) {
            return null;
        }
        return Objects.requireNonNull(getConnectingChests()).get(0).getInventory();

    }

    @Override
    public boolean exists() {
        Block b = loc.getBukkitLocation().getBlock();
        return b.getType() != Material.AIR && b.getState() instanceof Sign;
    }

}
