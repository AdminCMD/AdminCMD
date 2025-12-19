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
package com.admincmd.bungeecord.utils;

import com.admincmd.bungeecord.Main;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("CallToPrintStackTrace")
public enum Config {

    MYSQL_IP("MySQL.IP", "127.0.0.1"),
    MYSQL_PORT("MySQL.Port", 3306),
    MYSQL_DATABASE("MySQL.Database", "minecraft"),
    MYSQL_USER("MySQL.User", "root"),
    MYSQL_PASSWORD("MySQL.Password", "password"),
    MAINTENANCE("Maintenance.Enabled", false),
    MOTD_LINE_1("Maintenance.Motd.Line1", "&c[AdminCMD] Server is in Maintenance"),
    MOTD_LINE_2("Maintenance.Motd.Line2", "&eWe are actually working on the server!"),
    KICKMESSAGE("Maintenance.KickMessages", "You got kicked because the Maintenance mode is enabled! (%perm%)"),
    ICON("Maintenance.Favicon", "maintenance.png"),
    VERSION("Maintenance.Version", "Maintenance"),
    KICK("Maintenance.KickOnMaintenance", true);

    private static final File f = new File(Main.getInstance().getDataFolder(), "config.yml");
    private static Configuration cfg;
    private final Object value;
    private final String path;

    Config(String path, Object val) {
        this.path = path;
        this.value = val;
    }

    public static void load() {
        if (Main.getInstance().getDataFolder().exists() || Main.getInstance().getDataFolder().mkdirs()) {
            try {
                if (!f.exists()) {
                    if (!f.createNewFile()) {
                        return;
                    }
                }

                cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);

                for (Config c : values()) {
                    if (!cfg.contains(c.getPath())) {
                        c.set(c.getDefaultValue());
                    }
                }
                reload();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void reload() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, f);
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getPath() {
        return path;
    }

    public Object getDefaultValue() {
        return value;
    }

    public boolean getBoolean() {
        return cfg.getBoolean(path);
    }

    public Favicon getFavicon() throws IOException {
        File icon = Main.getMaintenanceFavicon();
        return Favicon.create(ImageIO.read(icon));
    }

    public int getInteger() {
        return cfg.getInt(path);
    }

    public double getDouble() {
        return cfg.getDouble(path);
    }

    public String getString() {
        return cfg.getString(path).replaceAll("&((?i)[0-9a-fk-or])", "§$1");
    }

    public List<String> getStringList() {
        return cfg.getStringList(path);
    }

    public void set(Object value) {
        cfg.set(path, value);
        reload();
    }

}
