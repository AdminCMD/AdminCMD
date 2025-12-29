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
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandManager;
import com.admincmd.database.Database;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.utils.BukkitListener;
import com.admincmd.utils.ClassScanner;
import com.admincmd.utils.EventManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class Addon extends JavaPlugin {

    private static Addon INSTANCE;

    /**
     * Returns an instance of this class.
     *
     * @return {@link com.admincmd.addon.Addon}
     */
    public static Addon getInstance() {
        return INSTANCE;
    }

    /**
     * Returns an instance of the AdminCMD main class.
     *
     * @return {@link com.admincmd.Main}
     */
    public Main getAdminCMD() {
        return Main.getInstance();
    }

    /**
     * Returns the loaded database.
     *
     * @return {@link com.admincmd.database.Database}
     */
    public Database getDB() {
        return DatabaseFactory.getDatabase();
    }

    /**
     * Returns the commandmanager for this addon.
     *
     * @return {@link com.admincmd.commandapi.CommandManager}
     */
    public CommandManager getCommandManager() {
        return Main.getInstance().getCommandManager();
    }

    /**
     * Use #registerCommands
     *
     * @param clazz Class to register
     */
    @Deprecated
    public void registerCommand(Class<?> clazz) {
        getCommandManager().registerClass(clazz);
    }

    /**
     * Use #registerEventListener
     *
     * @param clazz Class to register
     */
    @Deprecated
    public void registerEvent(Class<? extends BukkitListener> clazz) {
        EventManager.registerEvent(clazz, this);
    }

    public void registerEventListener(@NotNull String eventsPath) {
        for (Class<? extends BukkitListener> clazz : ClassScanner.getClassesThatExtendClass(eventsPath, BukkitListener.class, INSTANCE.getClass())) {
            EventManager.registerEvent(clazz, this);
        }
    }

    public void registerCommands(@NotNull String commandsPath) {
        for (Class<?> clazz : ClassScanner.getClassesFromJarWithAnnotation(commandsPath, CommandHandler.class, INSTANCE.getClass())) {
            getCommandManager().registerClass(clazz);
        }
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        enable();
    }

    @Override
    public void onDisable() {
        disable();
        INSTANCE = null;
    }

    /**
     * Called in the onEnable Bukkit method after the commandManager is loaded
     * for this addon.
     */
    public abstract void enable();

    /**
     * Called in the onDisable Bukkit method.
     */
    public abstract void disable();

}
