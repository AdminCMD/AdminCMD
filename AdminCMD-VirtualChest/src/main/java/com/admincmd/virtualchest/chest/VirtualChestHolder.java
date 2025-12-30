package com.admincmd.virtualchest.chest;

import com.admincmd.player.ACPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class VirtualChestHolder implements InventoryHolder {

    private final ACPlayer owner;

    public VirtualChestHolder(ACPlayer owner) {
        this.owner = owner;
    }

    public ACPlayer getOwner() {
        return owner;
    }

    public boolean isVirtualChest() {
        return true;
    }

    @Override
    public Inventory getInventory() {
        return null; // nicht benötigt
    }
}
