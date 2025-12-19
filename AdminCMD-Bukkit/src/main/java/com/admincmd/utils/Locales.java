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
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public enum Locales {

    MESSAGE_PREFIX_INFO("Prefix.Info", "&a[INFO]&7 "),
    MESSAGE_PREFIX_ERROR("Prefix.Error", "&4[ERROR]&7 "),
    COMMAND_MESSAGES_WRONG_SENDER_TYPE("CommandMessages.WrongSenderType", "&4[ERROR] &7You can't execute this Command!"),
    COMMAND_MESSAGES_NO_PERMISSION("CommandMessages.NoPermission", "&4[ERROR] &7You don't have permission to do that! &c(%perm%)"),
    COMMAND_MESSAGES_NO_PERMISSION_OTHER("CommandMessages.NoPermissionOther", "&4[ERROR] &7You don't have permission to do that! &c(%perm%.other)"),
    COMMAND_MESSAGES_WRONG_USAGE("CommandMessages.WrongUsage", "&c[ERROR] &7Wrong usage! Please use &6/%cmd% help &7 to see the correct usage!"),
    COMMAND_MESSAGES_NOT_ONLINE("CommandMessages.TargetNotFound", "&c[ERROR] &7That player is not online."),
    COMMAND_MESSAGES_NOT_A_PLAYER("CommandMessages.NotAPlayer", "&c[ERROR] &7That is not a player."),
    COMMAND_MESSAGES_NOT_A_NUMBER("CommandMessages.NotANumber", "&c[ERROR] &7This is not a number!"),
    COMMAND_MESSAGES_NOT_A_WORLD("CommandMessages.WorldNotFound", "&c[ERROR] &7The world is not existing!"),
    COMMAND_MESSAGES_ENABLED("CommandMessages.Enabled", "enabled"),
    COMMAND_MESSAGES_DISABLED("CommandMessages.Disabled", "disabled"),
    COMMAND_MESSAGES_NOT_A_MOB("CommandMessages.NotAMob", "&c[ERROR] &7This is not a mob!"),
    COMMAND_MESSAGES_NOT_A_GAMEMODE("CommandMessages.NotAGamemode", "&c[ERROR] &7This is not a gamemode!"),
    COMMAND_MESSAGES_NOT_SPAWNABLE("CommandMessages.NotSpawnable", "&c[ERROR] &7This mob is not spawnable!"),
    COMMAND_MESSAGES_NO_FREE_SPACE("CommandMessages.NoSpace", "&c[ERROR] &7There is no space to teleport to the given location!"),
    COMMAND_MESSAGES_SELF_PLAYER("CommandMessages.SelfPlayer", "&c[ERROR] &7You can't use that command with yourself!"),
    HELP("Help.Help", "Help"),
    HELP_SERVER_ACRELOAD_1("Help.Server.Reload.1", "Reloads the whole server."),
    HELP_SERVER_ACRELOAD_2("Help.Server.Reload.2", "Reloads a single plugin."),
    HELP_PLAYER_CW_1("Help.Player.CW.1", "Toggles commandwatcher"),
    HELP_PLAYER_CW_2("Help.Player.CW.2", "Toggles commandwatcher for the player"),
    HELP_PLAYER_FLY_1("Help.Player.Fly.1", "Toggles flying"),
    HELP_PLAYER_FLY_2("Help.Player.Fly.2", "Toggles flying for the player"),
    HELP_PLAYER_FREEZE_1("Help.Player.Freeze.1", "Toggles freeze mode"),
    HELP_PLAYER_FREEZE_2("Help.Player.Freeze.2", "Toggles freeze mode for the player"),
    HELP_PLAYER_FEED_1("Help.Player.Feed.1", "Fills your hunger"),
    HELP_PLAYER_FEED_2("Help.Player.Feed.2", "Fills the given players hunger"),
    HELP_PLAYER_CLEAR_1("Help.Player.Clear.1", "Clears your inventory"),
    HELP_PLAYER_CLEAR_2("Help.Player.Clear.2", "Clears the players inventory"),
    HELP_PLAYER_KILL_1("Help.Player.Kill.1", "Kill yourself"),
    HELP_PLAYER_KILL_2("Help.Player.Kill.2", "Kill the given Player"),
    HELP_PLAYER_GOD_1("Help.Player.God.1", "Toggles godmode"),
    HELP_PLAYER_GOD_2("Help.Player.God.2", "Toggles godmode for the player"),
    HELP_PLAYER_ENDERCHEST_1("Help.Player.Enderchest.1", "Opens your enderchest"),
    HELP_PLAYER_ENDERCHEST_2("Help.Player.Enderchest.2", "Opens the enderchest of the player"),
    HELP_PLAYER_GAMEMODE_1("Help.Player.Gamemode.1", "Toggles your gamemode."),
    HELP_PLAYER_GAMEMODE_3("Help.Player.Gamemode.2", "Changes your gamemode to the given mode."),
    HELP_PLAYER_GAMEMODE_4("Help.Player.Gamemode.3", "Changes the gamemode of the player to the given mode."),
    HELP_PLAYER_HEAL_1("Help.Player.Heal.1", "Fully restores your health"),
    HELP_PLAYER_HEAL_2("Help.Player.Heal.2", "Fully restores the health of the player"),
    HELP_PLAYER_OPENINV_1("Help.Player.OpenInv.1", "Opens your own inventory..."),
    HELP_PLAYER_OPENINV_2("Help.Player.OpenInv.2", "Opens the inventory of the player"),
    HELP_PLAYER_LOC_1("Help.Player.Location.1", "Returns your coordinate location"),
    HELP_PLAYER_LOC_2("Help.Player.Location.2", "Returns the coordinate location of the player"),
    HELP_PLAYER_MSG_1("Help.Player.Message.1", "Sends a private message to the given Player"),
    HELP_PLAYER_REPLY_1("Help.Player.Reply.1", "Sends a private message to the last player who messaged you"),
    HELP_PLAYER_SPY_1("Help.Player.Spy.1", "Toggles spy mode"),
    HELP_PLAYER_SPY_2("Help.Player.Spy.2", "Toggles spy mode for the player"),
    HELP_PLAYER_LIST_1("Help.Player.List.1", "Returns a list of all online players"),
    HELP_PLAYER_VANISH_1("Help.Player.Vanish.1", "Toggles vanish mode"),
    HELP_PLAYER_VANISH_2("Help.Player.Vanish.2", "Toggles vanish mode for the player"),
    HELP_PLAYER_WHOIS_1("Help.Player.Whois.1", "Shows your Player infos"),
    HELP_PLAYER_WHOIS_2("Help.Player.Whois.2", "Shows the infos of the given Player"),
    HELP_PLAYER_MUTE_1("Help.Player.Mute.1", "Toggles mute mode for the player"),
    HELP_TELEPORT_HOME_1("Help.Home.Home.1", "List's all homes"),
    HELP_TELEPORT_HOME_2("Help.Home.Home.2", "Teleports you to the given home"),
    HELP_TELEPORT_SETHOME_1("Help.Home.Sethome.1", "Creates a new home at your location with the given name or updates an existing home"),
    HELP_TELEPORT_DELHOME_1("Help.Home.Delhome.1", "Deletes the given home"),
    HELP_TELEPORT_WARP_1("Help.Warp.Warp.1", "List's all warps"),
    HELP_TELEPORT_WARP_2("Help.Warp.Warp.2", "Teleports you to the given warp"),
    HELP_TELEPORT_SETWARP_1("Help.Warp.Setwarp.1", "Creates a new warp at your location with the given name"),
    HELP_TELEPORT_EDITWARP_1("Help.Warp.Editwarp.1", "Sets the location of the given warp to your location"),
    HELP_TELEPORT_DELWARP_1("Help.Warp.Delwarp.1", "Deletes the given warp"),
    HELP_TELEPORT_DOWN_1("Help.Teleport.Down.1", "Teleports you to the next free space under you"),
    HELP_TELEPORT_DOWN_2("Help.Teleport.Down.2", "Teleports the given Player to the next free space under him"),
    HELP_TELEPORT_TOP_1("Help.Teleport.Up.1", "Teleports you to the next free space above you"),
    HELP_TELEPORT_TOP_2("Help.Teleport.Up.2", "Teleports the given Player to the next free space above him"),
    HELP_TELEPORT_BACK_1("Help.Teleport.Back.1", "Teleports you to the last location you teleported from."),
    HELP_TELEPORT_BACK_2("Help.Teleport.Back.2", "Teleports the given player to the last location he teleported from."),
    HELP_TELEPORT_TPALL_1("Help.Teleport.All.1", "Teleports all players to your location."),
    HELP_TELEPORT_TPALL_2("Help.Teleport.All.2", "Teleports all players to the given players Location"),
    HELP_TPREQUEST_TPA_1("Help.Teleport.Tpa.1", "Accept or deny a teleport request."),
    HELP_TPREQUEST_TPA_2("Help.Teleport.Tpa.2", "Send a teleport request to teleport yourself to the given Player"),
    HELP_TPREQUEST_TPA_3("Help.Teleport.Tpa.3", "Send a teleport request to teleport the given Player to you"),
    HELP_TELEPORT_TP_1("Help.Teleport.TP.1", "Teleports you to the given coordinates in your current world"),
    HELP_TELEPORT_TP_2("Help.Teleport.TP.2", "Teleports you to the given coordinates in the given world"),
    HELP_TELEPORT_TP_3("Help.Teleport.TP.3", "Teleports you to the given Player"),
    HELP_TELEPORT_TP_4("Help.Teleport.TP.4", "Teleports the first player to the second player"),
    HELP_TELEPORT_TPHERE_1("Help.Teleport.TPHere.1", "Teleports the given Player to you"),
    HELP_WORLD_WORLDS_1("Help.World.List.1", "Lists all the worlds loaded."),
    HELP_WORLD_SUN_1("Help.World.Sun.1", "Sets the weather to sun in your current world."),
    HELP_WORLD_SUN_2("Help.World.Sun.2", "Sets the weather to sun in the given world"),
    HELP_WORLD_DAY_1("Help.World.Day.1", "Sets the time to daytime in your current world."),
    HELP_WORLD_DAY_2("Help.World.Day.2", "Sets the time to daytime in the given world"),
    HELP_WORLD_NIGHT_1("Help.World.Night.1", "Sets the time to nighttime in your current world."),
    HELP_WORLD_NIGHT_2("Help.World.Night.2", "Sets the time to nighttime in the given world"),
    HELP_WORLD_TIME_1("Help.World.Time.1", "Sets the time to daytime in your current world or the given world"),
    HELP_WORLD_TIME_2("Help.World.Time.2", "Sets the time to nighttime in your current world or the given world"),
    HELP_WORLD_TIME_3("Help.World.Time.3", "Sets the time to the given time in your current world or the given world"),
    HELP_WORLD_TIME_4("Help.World.Time.4", "Pauses the time in your current world or the given world"),
    HELP_WORLD_TIME_5("Help.World.Time.5", "Unpauses the time in your current world or the given world"),
    HELP_MOB_KILLALL_1("Help.Mob.Killall.1", "Kills all the mobs in your or the given world"),
    HELP_MOB_KILLALL_2("Help.Mob.Killall.2", "Kills all the mobs in your or the given world which you provide."),
    HELP_MOB_SPAWNMOB_1("Help.Mob.Spawnmob.1", "Spawns the given mob the given amount times to the Locfation you are looking to"),
    HELP_SPAWNS_SETSPAWN_1("Help.Spawn.Setspawn.1", "Sets the spawnpoint to your location."),
    HELP_SPAWNS_SPAWN_1("Help.Spawn.Spawn.1", "Teleports you to the spawnpoint"),
    HELP_SPAWNS_SPAWN_2("Help.Spawn.Spawn.2", "Teleports the given player to the spawnpoint"),
    HELP_MAINTENANCEE_MAINTENANCE_1("Help.Maintenance.Toggle.1", "Shows the current maintenance status"),
    HELP_MAINTENANCEE_MAINTENANCE_2("Help.Maintenance.Toggle.2", "Enables maintenance mode"),
    HELP_MAINTENANCEE_MAINTENANCE_3("Help.Maintenance.Toggle.3", "Disables maintenance mode"),
    HOME_HOME("Home.Home", "Homes"),
    HOME_SET("Home.Set", "The home was added."),
    HOME_DELETED("Home.Deleted", "The home %home% was deleted."),
    HOME_UPDATED("Home.Updated", "The home was updated to your current location."),
    HOME_NOHOME("Home.NoHome", "You have not set this home. Try /home to list your homes."),
    HOME_ALREADY_EXISTING("Home.AlreadyExisting", "This home is already existing! Try /home to list all homes."),
    WARP_WARP("Warp.Warp", "Warps"),
    WARP_SET("Warp.Set", "The warp was added."),
    WARP_DELETED("Warp.Deleted", "The warp %warp% was deleted."),
    WARP_UPDATED("Warp.Updated", "The warp was updated to your current location."),
    WARP_NO_SUCH_WARP("Warp.NoSuchExisitingWarp", "There is no Warp with the given name!"),
    WARP_ALREADY_EXISTING("Warp.AlreadyExisting", "This warp is already existing! Try /warp to list all warps."),
    PLAYER_CW_TOGGLED("Player.CW.Toggled", "Your commandwatcher is now %status%"),
    PLAYER_CW_TOGGLED_OTHER("Player.CW.ToggledOther", "%player%&7's commandwatcher is now %status%"),
    PLAYER_CW_RAN("Player.CW.Ran", "%player%&7 ran Command: %command%"),
    PLAYER_GOD_TOGGLED("Player.God.Toggled", "Your godmode is now %status%"),
    PLAYER_GOD_TOGGLED_OTHER("Player.God.ToggledOther", "%player%&7's godmode is now %status%"),
    PLAYER_FLY_TOGGLED("Player.Fly.Toggled", "Your fly mode is now %status%"),
    PLAYER_FLY_TOGGLED_OTHER("Player.Fly.ToggledOther", "%player%&7's fly mode is now %status%"),
    PLAYER_FREEZE_TOGGLED("Player.Freeze.Toggled", "Your freeze mode is now %status%"),
    PLAYER_FREEZE_TOGGLED_OTHER("Player.Freeze.ToggledOther", "%player%&7's freeze mode is now %status%"),
    PLAYER_FREEZE_MOVEMENT_DENIED("Player.Freeze.Denied", "&4You are not allows to move because you have been frozen!"),
    PLAYER_GAMEMODE_CHANGED("Player.Gamemode.Changed", "Your gamemode has been changed to %status%"),
    PLAYER_GAMEMODE_CHANGED_OTHER("Player.Gamemode.ChangedOther", "%player%&7's gamemode has been changed to %status%"),
    PLAYER_MUTED("Player.Muted.Chat", "You are muted!"),
    PLAYER_HEAL_SELF("Player.Heal.Self", "Your health has been fully restored!"),
    PLAYER_HEAL_OTHER("Player.Heal.Other", "%player%&7's health has been fully restored!"),
    PLAYER_KILL_SELF("Player.Kill.Self", "You have been killed!!"),
    PLAYER_KILL_OTHER("Player.Kill.Other", "%player%&7 has been killed!"),
    PLAYER_CLEAR_SELF("Player.Clear.Self", "Your inventory has been cleared!"),
    PLAYER_CLEAR_OTHER("Player.Clear.Other", "%player%&7's inventory was cleared!"),
    PLAYER_FEED_SELF("Player.Feed.Self", "Your hunger has been filled!"),
    PLAYER_FEED_OTHER("Player.Feed.Other", "%player%&7's hunger has been filled!"),
    PLAYER_LOCATION_SELF("Player.Loc.Self", "Your coordinates are X: %x%  Y: %y%  Z: %z%"),
    PLAYER_LOCATION_OTHER("Player.Loc.Other", "%player%&7's coordinates are X: %x%  Y: %y%  Z: %z%"),
    PLAYER_MSG_FORMAT("Player.Message.Format", "&6[%sender%&6]&a->&6[%target%&6]&a: &7%message%"),
    PLAYER_REPLY_NO_LAST_MESSAGE("Player.Reply.NoLastPlayer", "There is no last player who messaged you!"),
    PLAYER_SPY_TOGGLED_SELF("Player.Spy.Toggled.Self", "Your spy mode is now %status%"),
    PLAYER_SPY_TOGGLED_OTHER("Player.Spy.Toggled.Other", "%player%&7's spy mode is now %status%"),
    PLAYER_LIST_FORMAT("Player.List.Format", "&6Online Players: %playerList%"),
    PLAYER_VANISH_TOGGLED_SELF("Player.Vanish.Toggled.Self", "Your invisible mode is now %status%"),
    PLAYER_VANISH_TOGGLED_OTHER("Player.Vanish.Toggled.Other", "%player%&7's invisible mode is now %status%"),
    PLAYER_MUTE_TOGGLED_SELF("Player.Mute.Toggled.Self", "Your mute has been %status%"),
    PLAYER_MUTE_TOGGLED_OTHER("Player.Mute.Toggled.Other", "%player%&7's mute is now %status%"),
    SERVER_RELOAD_FULL("Server.Reload.Full", "The server has been reloaded."),
    SERVER_RELOAD_NOT_FOUND("Server.Reload.NotFound", "There is no plugin by that name installed."),
    SERVER_RELOAD_SINGLE("Server.Reload.Single", "The plugin has been reloaded."),
    WORLD_WORLD_LOADED("World.List", "The following worlds are loaded: %worlds%"),
    WORLD_WEATHER_CLEAR("World.Weather.Sun", "The weather in %world% was set to sun"),
    WORLD_TIME_PAUSED("World.Time.Pause", "The time in world %world% is now paused."),
    WORLD_TIME_UNPAUSED("World.Time.Unpause", "The time in world %world% is now unpaused."),
    WORLD_TIME_SET("World.Time.Set", "The time in world %world% has been set to %time%."),
    WORLD_DAY_SET("World.Day.Set", "The time in world %world% has been set to daytime."),
    WORLD_NIGHT_SET("World.Night.Set", "The time in world %world% has been set to nighttime."),
    MOB_KILLALL("Mob.Killall", "%num% mobs have been killed in world %world%."),
    MOB_SPAWNED("Mob.Spawn", "%num% mobs have been spawned at the location you are looking too."),
    SPAWN_SET("Spawn.Set", "The spawnpoint has been set to your location."),
    SPAWN_TP("Spawn.Teleport.You", "Teleported to the spawnpoint."),
    SPAWN_TP_OTHER("Spawn.Teleport.Other", "%player%&7 was teleported to the spawnpoint."),
    TELEPORT_DOWN("Teleport.Down.You", "Teleported down."),
    TELEPORT_DOWN_OTHER("Teleport.Down.Other", "%player%&7 got teleported down!"),
    TELEPORT_UP("Teleport.Up.You", "Teleported up."),
    TELEPORT_UP_OTHER("Teleport.Up.Other", "%player%&7 got teleported up!"),
    TELEPORT_BACK("Teleport.back.You", "You got teleported back to your last location."),
    TELEPORT_BACK_OTHER("Teleport.back.other", "%player% &7got teleproted back to his last location."),
    TELEPORT_ALL("Teleport.all.You", "You teleported all players to your location."),
    TELEPORT_ALL_OTHER("Teleport.all.other", "You teleported all players to %player%'s location."),
    TELEPORT_BACK_NO_LOCATION("Teleport.back.error", "There is no last location saved!"),
    TELEPORT_TPA_ALREADY_HAS_REQUEST("Teleport.tpa.alreadyHasRequest", "%player%&7 already has a request from another player! Wait 120 seconds or until he accepted it."),
    TELEPORT_TPA_NO_REQUEST("Teleport.tpa.noRequest", "There is no request for you!"),
    TELEPORT_TPA_SENT_TARGET("Teleport.tpa.sent.target.to", "%player%&7 wants to teleport to your location. Type /tpa yes to accept it!"),
    TELEPORT_TPAHERE_SENT_TARGET("Teleport.tpa.sent.target.here", "%player%&7 wants to teleport you to his location. Type /tpa yes to accept it!"),
    TELEPORT_TPA_SENT_REQUESTER("Teleport.tpa.sent.requester", "A teleport request has been sent to %player%&7!"),
    TELEPORT_TPA_ACCEPT_TARGET("Teleport.tpa.accept.target", "Teleporting %player%&7 to your location!"),
    TELEPORT_TPA_ACCEPT_REQUEST("Teleport.tpa.accept.requester", "Teleporting you to %player%&7!"),
    TELEPORT_TPA_DENY_RECEIVER("Teleport.tpa.deny.receiver", "Teleport request from %player%&7 was denied!"),
    TELEPORT_TPA_DENY_REQUESTER("Teleport.tpa.deny.requester", "Your teleport request for %player%&7 was denied!"),
    TELEPORT_TPA_TIMEOUT_TARGET("Teleport.tpa.timeout.target", "Teleport request from %player%&7 has timed out!"),
    TELEPORT_TPA_TIMEOUT_REQUEST("Teleport.tpa.timeout.requester", "Your teleport request to %player%&7 has timed out!"),
    MAINTENANCE_TOGGLED("Maintenance.Toggled", "Maintenance is now %status%."),
    MAINTENANCE_NO_JOIN("Maintenance.JoinNotAllowed", "You cant join because the Maintenance mode is enabled! &c(%perm%)"),
    MAINTENANCE_KICK("Maintenance.Kick", "You got kicked because the Maintenance mode is enabled! &c(%perm%)"),
    MAINTENANCE_FAIL("Maintenance.Fail", "Maintenance is already %status%"),
    UPDATE_FOUND("Messages.UpdateFound", "&a[AdminCMD]&7 A new update has been found on SpigotMC. Current version: %oldversion New version: %newversion"),
    ;

    private static final File f = new File(Main.getInstance().getDataFolder(), "locales.yml");
    private static YamlConfiguration cfg;
    private final Object value;
    private final String path;

    private Locales(String path, Object val) {
        this.path = path;
        this.value = val;
    }

    public static void load() {
        Main.getInstance().getDataFolder().mkdirs();
        reload(false);
        for (Locales c : values()) {
            if (!cfg.contains(c.getPath())) {
                c.set(c.getDefaultValue(), false);
            }
        }
        try {
            cfg.save(f);
        } catch (IOException ex) {
            ACLogger.severe("Failed loading Locales file!", ex);
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

    public Object getDefaultValue() {
        return value;
    }

    public String getString() {
        return Utils.replaceColors(cfg.getString(path));
    }

    public String replacePlayer(OfflinePlayer p) {
        return getString().replaceAll("%player%", Utils.replacePlayerPlaceholders(p));
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
