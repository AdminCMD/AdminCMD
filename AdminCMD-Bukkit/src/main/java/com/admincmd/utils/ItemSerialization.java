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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemSerialization {

    public static String saveInventory(Inventory inventory) {
        YamlConfiguration config = new YamlConfiguration();

        // Save every element in the list
        saveInventory(inventory, config);
        return config.saveToString();
    }

    private static void saveInventory(Inventory inventory, ConfigurationSection destination) {
        // Save every element in the list
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);

            // Don't store NULL entries
            if (item != null) {
                destination.set(Integer.toString(i), item);
            }
        }
    }

    public static ItemStack[] loadInventory(String data) {
        try {
            YamlConfiguration config = new YamlConfiguration();

            // Load the string
            config.loadFromString(data);
            return loadInventory(config);
        } catch (InvalidConfigurationException ex) {
            ACLogger.severe(ex);
            return new ItemStack[]{};
        }
    }

    private static ItemStack[] loadInventory(ConfigurationSection source) throws InvalidConfigurationException {
        List<ItemStack> stacks = new ArrayList<>();

        try {
            // Try to parse this inventory
            for (String key : source.getKeys(false)) {
                int number = Integer.parseInt(key);

                // Size should always be bigger
                while (stacks.size() <= number) {
                    stacks.add(null);
                }

                stacks.set(number, (ItemStack) source.get(key));
            }
        } catch (NumberFormatException e) {
            throw new InvalidConfigurationException("Expected a number.", e);
        }

        // Return result
        return stacks.toArray(new ItemStack[0]);
    }

}
