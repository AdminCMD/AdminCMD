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
package com.admincmd.punishments;

import com.admincmd.player.ACPlayer;
import com.admincmd.punishments.punishments.Punishment;
import com.admincmd.utils.Utils;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public enum Config {

    STANDARD_TIME("Settings.Standard.Time", 5, "The standard minutes a player gets punished"),
    STANDARD_REASON("Settings.Standard.Reason", "No reason provided.", "The standard reason why someone gets punished"),
    STANDARD_FOREVER("Settings.Standard.Forever", "Forever", "The word for forever"),
    STANDARD_DAYS("Settings.Standard.Days", "Day(s)", "The word for days"),
    STANDARD_HOURS("Settings.Standard.Hours", "Hour(s)", "The word for hours"),
    STANDARD_MINUTES("Settings.Standard.Minutes", "Minute(s)", "The word for minutes"),
    STANDARD_SECONDS("Settings.Standard.Seconds", "Second(s)", "The word for seconds"),
    STANDARD_MUTED("Settings.Standard.Muted", "muted", "The word for muted"),
    STANDARD_KICKED("Settings.Standard.Kicked", "kicked", "The word for kicked"),
    STANDARD_BANED("Settings.Standard.Banned", "banned", "The word for banned"),
    MESSAGE_CREATOR("Messages.Creator.Standard", "&cYou %action% &e%player% &cuntil &e%time% &cbecause: &7%reason%", "The message a player gets when he punishes someone"),
    MESSAGE_TARGET("Messages.Target.Standard", "&cYou have been %action% by &e%player% &cuntil &e%time% &cbecause: &7%reason%", "The Message someone gets when he is punished."),
    MESSAGE_TARGET_KICK("Messages.Target.Kick", "&cYou have been %action% by &e%player% &cbecause: &7%reason%", "The Message someone gets when he is kicked."),
    MESSAGE_CREATOR_KICK("Messages.Creator.Kick", "&cYou %action% &e%player% &cbecause: &7%reason%", "The message a player gets when he kicks someone"),
    MESSAGE_NOT_PUNISHED("Messages.NotPunished", "&e%player% &cis not %action%.", "Message when a player is not punished"),
    MESSAGE_ALREADY_PUNISHED("Messages.AlreadyPunished", "&e%player% &cis already %action%.", "Message when a player is already punished"),
    MESSAGE_UNPUNISHED_TARGET("Messages.Unpunished.Target", "&e%player% &cun%action% you.", "Message when someone gets unpunished"),
    MESSAGE_UNPUNISHED_CREATOR("Messages.Unpunished.Creator", "&cYou un%action% &e%player%", "Message when someone unpunishes a player");

    private static final File f = new File(Punishments.getInstance().getDataFolder(), "config.yml");
    private static YamlConfiguration cfg;
    private final Object value;
    private final String path;
    private final String description;
    private Config(String path, Object val, String description) {
        this.path = path;
        this.value = val;
        this.description = description;
    }

    public static void load() {
        Punishments.getInstance().getDataFolder().mkdirs();
        reload(false);
        String header = "";
        for (Config c : values()) {
            header += c.getPath() + ": " + c.getDescription() + System.lineSeparator();
            if (!cfg.contains(c.getPath())) {
                c.set(c.getDefaultValue(), false);
            }
        }
        cfg.options().header(header);
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

    public String replace(ACPlayer player, Punishment pu) {
        String creator = "CONSOLE";
        if (player != null) {
            creator = Utils.replacePlayerPlaceholders(player.getOfflinePlayer());
        }

        return getString().replaceAll("%player%", creator).replaceAll("%reason%", pu.getReason()).replaceAll("%time%", pu.getReadableTime()).replaceAll("%action%", pu.getType().getWord());
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
        return Utils.replaceColors(cfg.getString(path));
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
