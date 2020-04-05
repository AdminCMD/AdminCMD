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
package com.admincmd.bungeecord;

import com.admincmd.bungeecord.database.DatabaseFactory;
import com.admincmd.bungeecord.events.PlayerJoinListener;
import com.admincmd.bungeecord.maintenance.MaintenanceCommand;
import com.admincmd.bungeecord.maintenance.MaintenanceEvents;
import com.admincmd.bungeecord.utils.Config;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    private static Main INSTANCE;

    @Override
    public void onEnable() {
        getLogger().info("Loading plugin....");
        INSTANCE = this;
        Config.load();
        DatabaseFactory.init();

        File favicon = getMaintenanceFavicon();

        if (!favicon.exists()) {
            try (InputStream in = getResourceAsStream("maintenance.png")) {
                Files.copy(in, favicon.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerJoinListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new MaintenanceEvents());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new MaintenanceCommand());

        getLogger().info("Done!");
    }

    @Override
    public void onDisable() {
        try {
            DatabaseFactory.getDatabase().closeConnection();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Main getInstance() {
        return INSTANCE;
    }

    public static File getMaintenanceFavicon() {
        return new File(getInstance().getDataFolder(), Config.ICON.getString());
    }

}
