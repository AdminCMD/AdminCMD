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
package com.admincmd.addon;

import com.admincmd.Main;
import com.admincmd.utils.ACLogger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AddonManager {

    private static final Map<String, Addon> addons = new HashMap<>();
    private static final PluginManager manager = Main.getInstance().getServer().getPluginManager();

    /**
     * Loads all the addons in the plugins/AdminCMD/addons folder.
     */
    public static void loadAddons() {
        ACLogger.info("Loading addons...");
        File folder = new File(Main.getInstance().getDataFolder(), "addons");

        if (folder.mkdirs()) {
            File[] subFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));

            if (subFiles == null || subFiles.length == 0) {
                ACLogger.info("No Addons installed.");
                return;
            }

            for (File f : subFiles) {
                ACLogger.info("Loading addon " + f.getName());
                try {
                    Plugin p = manager.loadPlugin(f);
                    if (!(p instanceof Addon a)) {
                        ACLogger.severe("Jar File is not an official AdminCMD Addon.");
                        continue;
                    }

                    manager.enablePlugin(a);
                    ACLogger.info("Loaded Addon " + a.getName());
                    addons.put(a.getName(), a);
                } catch (Exception ex) {
                    ACLogger.severe("Jar File could not be loaded!", ex);
                }
            }
        }
    }

    /**
     * Disables all the addons in the plugins/AdminCMD/addons folder
     */
    public static void disableAddons() {
        for (Addon a : addons.values()) {
            manager.disablePlugin(a);
        }
        addons.clear();
    }

    /**
     * Returns a loaded Addon
     *
     * @param name the name of the addon
     * @return {@link com.admincmd.addon.Addon}
     */
    public static Addon getAddon(String name) {
        return addons.get(name);
    }

}
