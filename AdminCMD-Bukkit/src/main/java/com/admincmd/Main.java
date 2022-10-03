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
package com.admincmd;

import com.admincmd.addon.AddonManager;
import com.admincmd.commandapi.CommandManager;
import com.admincmd.commands.home.DelhomeCommand;
import com.admincmd.commands.home.HomeCommand;
import com.admincmd.commands.home.SethomeCommand;
import com.admincmd.commands.maintenance.MaintenanceCommand;
import com.admincmd.commands.mob.KillallCommand;
import com.admincmd.commands.mob.SpawnmobCommand;
import com.admincmd.commands.player.*;
import com.admincmd.commands.server.ReloadCommand;
import com.admincmd.commands.spawn.SetSpawnCommand;
import com.admincmd.commands.spawn.SpawnCommand;
import com.admincmd.commands.teleport.*;
import com.admincmd.commands.warps.DelWarpCommand;
import com.admincmd.commands.warps.EditWarpCommand;
import com.admincmd.commands.warps.SetWarpCommand;
import com.admincmd.commands.warps.WarpCommand;
import com.admincmd.commands.world.*;
import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.events.*;
import com.admincmd.home.HomeManager;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.*;
import com.admincmd.warp.WarpManager;
import com.admincmd.world.WorldManager;
import de.jeter.updatechecker.SpigotUpdateChecker;
import de.jeter.updatechecker.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class Main extends JavaPlugin {

    private static Main INSTANCE;
    private final CommandManager manager = new CommandManager(this);
    private UpdateChecker updatechecker;

    /**
     * Returns an instance of this class.
     *
     * @return {@link com.admincmd.Main}
     */
    public static Main getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        INSTANCE = this;

        //test
        getDataFolder().mkdirs();
        File f = new File(getDataFolder(), "admincmd.db");
        if (f.exists()) {
            //Old AdminCMD version found
            ACLogger.warn("Old AdminCMD version was found! Renaming the AdminCMd folder to AdminCMD-Old!");
            getDataFolder().renameTo(new File(getDataFolder().getParentFile(), "AdminCMD-Old"));
        }

        Config.load();
        Locales.load();

        if (Config.CHECK_UPDATE.getBoolean()) {
            updatechecker = new SpigotUpdateChecker(this, 75678);
        }

        if (Config.ENABLE_METRICS.getBoolean()) {
            Metrics metrics = new Metrics(this, 7742);
            getLogger().info("Thanks for using bstats, it was enabled!");
        }

        BungeeCordMessageManager.setup();

        DatabaseFactory.init();

        PlayerManager.init();
        WorldManager.init();
        HomeManager.init();
        WarpManager.init();

        registerCommands();
        registerEvents();

        if (checkForVault()) {
            if (!Vault.setupChat()) {
                ACLogger.severe("Vault could not be set up.");
            }
            ACLogger.info("Hooked into Vault.");
        }

        if (!Config.BUNGEECORD.getBoolean()) {
            if (checkForProtocolLib()) {
                File file = new File(getDataFolder(), Config.MAINTENANCE_FAVICON.getString());
                if (!file.exists()) {
                    saveResource("maintenance.png", true);
                    ACLogger.info("Saved maintenance.png from the jar file.");
                }
                ProtocolLibManager.setup();
                ACLogger.info("Hooked into ProtocolLib. Maintenance Feature is enabled.");
                manager.registerClass(MaintenanceCommand.class);
            } else {
                ACLogger.info("ProtocolLib NOT installed. Maintenance Feature is disabled on.");
                Config.MAINTENANCE_ENABLE.set(false, true);
            }
        }
        AddonManager.loadAddons();

        long timeTook = System.currentTimeMillis() - start;
        ACLogger.info("Plugin start took " + timeTook + " milliseconds");
    }

    @Override
    public void onDisable() {
        AddonManager.disableAddons();

        PlayerManager.save();
        WorldManager.save();
        HomeManager.save();
        WarpManager.save();

        try {
            DatabaseFactory.getDatabase().closeConnection();
        } catch (SQLException ex) {
            ACLogger.severe(ex);
        }

        System.gc();
    }

    public BungeeCordMessageManager getMessageManager() {
        return BungeeCordMessageManager.getInstance();
    }

    public boolean checkForVault() {
        Plugin pl = getServer().getPluginManager().getPlugin("Vault");
        return pl != null && pl.isEnabled();
    }

    public boolean checkForProtocolLib() {
        Plugin pl = getServer().getPluginManager().getPlugin("ProtocolLib");
        return pl != null && pl.isEnabled();
    }

    public UpdateChecker getUpdateChecker() {
        return this.updatechecker;
    }

    private void registerCommands() {
        manager.registerClass(ReloadCommand.class);
        manager.registerClass(CommandWatcherCommand.class);
        manager.registerClass(EnderchestCommand.class);
        manager.registerClass(FlyCommand.class);
        manager.registerClass(GamemodeCommand.class);
        manager.registerClass(GodCommand.class);
        manager.registerClass(HealCommand.class);
        manager.registerClass(ListCommand.class);
        manager.registerClass(LocationCommand.class);
        manager.registerClass(MsgCommand.class);
        manager.registerClass(OpeninvCommand.class);
        manager.registerClass(ReplyCommand.class);
        manager.registerClass(SpyCommand.class);
        manager.registerClass(VanishCommand.class);
        manager.registerClass(DelhomeCommand.class);
        manager.registerClass(HomeCommand.class);
        manager.registerClass(SethomeCommand.class);
        manager.registerClass(DayCommand.class);
        manager.registerClass(NightCommand.class);
        manager.registerClass(SunCommand.class);
        manager.registerClass(TimeCommand.class);
        manager.registerClass(KillallCommand.class);
        manager.registerClass(SpawnmobCommand.class);
        manager.registerClass(SetSpawnCommand.class);
        manager.registerClass(SpawnCommand.class);
        manager.registerClass(DownCommand.class);
        manager.registerClass(TopCommand.class);
        manager.registerClass(TpaCommand.class);
        manager.registerClass(BackCommand.class);
        manager.registerClass(TpAllCommand.class);
        manager.registerClass(TPCommand.class);
        manager.registerClass(DelWarpCommand.class);
        manager.registerClass(EditWarpCommand.class);
        manager.registerClass(SetWarpCommand.class);
        manager.registerClass(WarpCommand.class);
        manager.registerClass(KillCommand.class);
        manager.registerClass(ClearCommand.class);
        manager.registerClass(FreezeCommand.class);
        manager.registerClass(FeedCommand.class);
        manager.registerClass(WorldListCommand.class);
        manager.registerClass(WhoisCommand.class);
    }

    private void registerEvents() {
        EventManager.registerEvent(PlayerJoinListener.class);
        EventManager.registerEvent(PlayerCommandListener.class);
        EventManager.registerEvent(WorldListener.class);
        EventManager.registerEvent(PlayerDamageListener.class);
        EventManager.registerEvent(PlayerDeathListener.class);
        EventManager.registerEvent(SignListener.class);
        EventManager.registerEvent(TeleportListener.class);
        EventManager.registerEvent(PlayerMoveListener.class);
    }

}
