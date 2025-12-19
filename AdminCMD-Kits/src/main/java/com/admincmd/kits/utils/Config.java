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
package com.admincmd.kits.utils;

import com.admincmd.kits.Kits;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Utils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum Config {

    STANDARD_DAYS("Settings.Standard.Days", "Day(s)", "The word for days"),
    STANDARD_HOURS("Settings.Standard.Hours", "Hour(s)", "The word for hours"),
    STANDARD_MINUTES("Settings.Standard.Minutes", "Minute(s)", "The word for minutes"),
    STANDARD_SECONDS("Settings.Standard.Seconds", "Second(s)", "The word for seconds"),
    ;

    private static final File f = new File(Kits.getInstance().getDataFolder(), "config.yml");
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
        if (Kits.getInstance().getDataFolder().exists() || Kits.getInstance().getDataFolder().mkdirs()) {
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
                ACLogger.severe(ex);
            }
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
        String str = cfg.getString(path);
        if (str != null) {
            return Utils.replaceColors(str);
        } else {
            return "";
        }
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
                ACLogger.severe(ex);
            }
            reload(false);
        }
    }

}
