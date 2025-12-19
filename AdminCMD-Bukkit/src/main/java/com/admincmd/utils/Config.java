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

import com.admincmd.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum Config {

    MYSQL_USE("MySQL.Use", false, "true=Use MySQL, false=use SQLite"),
    MYSQL_IP("MySQL.IP", "127.0.0.1", "The ip of the mysql server"),
    MYSQL_PORT("MySQL.Port", 3306, "The port of the MySQL server"),
    MYSQL_DATABASE("MySQL.Database", "minecraft", "The name of the dataabse"),
    MYSQL_USER("MySQL.User", "root", "The user of the database"),
    MYSQL_PASSWORD("MySQL.Password", "password", "The password of the user"),
    GLOBAL_SPAWNS("Options.WorldSpawns", false, "Set to true if you want one spawn for every world. false means one spawn for the whole server."),
    DIRECT_RESPAWN("Options.DirectRespawn", true, "Set to false if you want players to manually click the respawn button."),
    DEBUG("Options.Debug", false, "Enables debugging chat."),
    ENABLE_METRICS("Options.enable-bstats", true, "Should the plugin pass statistics to bstats?"),

    BUNGEECORD("Options.Bungeecord.Enable", false, "This enables the bungeecord mode. In the bungeecord mode everything gets written instantly to the database. makes only sense when using MySQL"),
    BUNGEECORD_SERVERNAME("Options.Bungeecord.Servername", "server1", "If BungeeCord mode is enabled, this has to be the exact same name as in your BungeeCord server configuration!!!"),
    MAINTENANCE_ENABLE("Options.Maintenance.Enable", false, "Enables the maintenance mode. Can alse be enabled by command."),
    MAINTENANCE_KICK("Options.Maintenance.Kick", true, "Should all Players be kicked when maintenance gets enabled?"),
    MAINTENANCE_FAVICON("Options.Maintenance.Favicon", "maintenance.png", "Which favicon should be used when the maintenance mode is enabled?"),
    MAINTENANCE_MOTD_LINE1("Options.Maintenance.Line1", "&c[AdminCMD] Server is in Maintenance", "Message shown in the motd line 1."),
    MAINTENANCE_MOTD_LINE2("Options.Maintenance.Line2", "&eWe are actually working on the server!", "Message shown in the motd line 2."),
    MESSAGE_FORMAT("Player.Format", "%prefix%name%suffix", "Here you can add the prefix or suffixes to playernames."),
    ;

    private static final File f = new File(Main.getInstance().getDataFolder(), "config.yml");
    private static YamlConfiguration cfg;
    private final Object value;
    private final String path;
    private final String description;

    Config(String path, Object val, String description) {
        this.path = path;
        this.value = val;
        this.description = description;
    }

    public static void load() {
        Main.getInstance().getDataFolder().mkdirs();
        reload(false);
        List<String> header = new ArrayList<>();
        for (Config c : values()) {
            header.add(c.getPath() + ": " + c.getDescription() + System.lineSeparator());
            if (!cfg.contains(c.getPath())) {
                c.set(c.getDefaultValue(), false);
            }
        }
        cfg.options().setHeader(header);
        try {
            cfg.save(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void reload(boolean complete) {
        if (!complete) {
            cfg = YamlConfiguration.loadConfiguration(f);
            return;
        }
        load();
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public Object getDefaultValue() {
        return value;
    }

    public boolean getBoolean() {
        return cfg.getBoolean(path);
    }

    public int getInteger() {
        return cfg.getInt(path);
    }

    public double getDouble() {
        return cfg.getDouble(path);
    }

    public String getString() {
        return cfg.getString(path);
    }

    public List<String> getStringList() {
        return cfg.getStringList(path);
    }

    public void set(Object value, boolean save) {
        cfg.set(path, value);
        if (save) {
            try {
                cfg.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            reload(false);
        }
    }

}
