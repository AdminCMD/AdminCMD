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
package com.admincmd.player;

import com.admincmd.Main;
import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.communication.Channel;
import com.admincmd.communication.MessageCommand;
import com.admincmd.database.Database;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import com.admincmd.utils.MultiServerLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerManager {

    private static final HashMap<String, StoredPlayer> players = new HashMap<>();
    private static final Database conn = DatabaseFactory.getDatabase();

    public static void init() {
        if (!Config.BUNGEECORD.getBoolean()) {
            players.clear();
            try {
                PreparedStatement s = conn.getPreparedStatement("SELECT * FROM ac_player");
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    StoredPlayer sp = new StoredPlayer(rs);
                    players.put(rs.getString("nickname"), sp);
                }
                conn.closeResultSet(rs);
                conn.closeStatement(s);
                ACLogger.info("Loaded " + players.size() + " players!");
            } catch (SQLException ex) {
                ACLogger.severe("Error loading the players!", ex);
            }
        }
    }

    public static List<ACPlayer> getOnlinePlayers() {
        List<ACPlayer> ret = new ArrayList<>();
        if (!Config.BUNGEECORD.getBoolean()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                ret.add(getPlayer(p));
            }
        } else {
            try {
                PreparedStatement s = conn.getPreparedStatement("SELECT id FROM ac_player WHERE online = ?;");
                s.setBoolean(1, true);
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    SQLPlayer sp = new SQLPlayer(rs.getInt("ID"));
                    ret.add(sp);
                }
                conn.closeResultSet(rs);
                conn.closeStatement(s);
            } catch (SQLException ex) {
                ACLogger.severe(ex);
            }
        }
        return ret;
    }

    public static void save() {
        if (!Config.BUNGEECORD.getBoolean()) {
            int saved = 0;
            for (StoredPlayer acp : players.values()) {
                if (!acp.hasChanged()) {
                    continue;
                }
                try {
                    PreparedStatement ps = conn.getPreparedStatement("UPDATE " + DatabaseFactory.PLAYER_TABLE + " SET god = ?, invisible = ?, commandwatcher = ?, spy = ?, fly = ?, freeze = ?, nickname = ?, lastmsgfrom = ?, lastloc = ? WHERE ID = ?;");
                    ps.setBoolean(1, acp.isGod());
                    ps.setBoolean(2, acp.isInvisible());
                    ps.setBoolean(3, acp.isCMDWatcher());
                    ps.setBoolean(4, acp.isSpy());
                    ps.setBoolean(5, acp.isFly());
                    ps.setBoolean(6, acp.isFreezed());
                    ps.setString(7, acp.getName());
                    ps.setInt(8, acp.getLastMSGFrom());
                    if (acp.hasLastLoc()) {
                        ps.setString(9, acp.getLastLoc().toString());
                    } else {
                        ps.setString(9, "none");
                    }
                    ps.setInt(10, acp.getID());
                    ps.executeUpdate();
                    conn.closeStatement(ps);
                    saved++;
                } catch (SQLException ex) {
                    ACLogger.severe("Error saving players", ex);
                }
            }
            players.clear();
            ACLogger.info("Saved " + saved + " players!");
        }
    }

    public static ACPlayer getPlayer(OfflinePlayer p) {
        if (Config.BUNGEECORD.getBoolean()) {
            return new SQLPlayer(p);
        } else {
            if (!players.containsKey(p.getName())) {
                StoredPlayer sp = new StoredPlayer(p.getUniqueId());
                players.put(p.getName(), sp);
                return sp;
            } else {
                return players.get(p.getName());
            }
        }
    }

    public static ACPlayer getPlayer(int id) {
        if (Config.BUNGEECORD.getBoolean()) {
            SQLPlayer ret = new SQLPlayer(id);
            return ret;
        } else {
            for (StoredPlayer sp : players.values()) {
                if (sp.getID() == id) {
                    return sp;
                }
            }
            return null;
        }
    }

    public static ACPlayer getPlayer(String name) {
        if (Config.BUNGEECORD.getBoolean()) {
            SQLPlayer ret = null;
            try {
                PreparedStatement s = conn.getPreparedStatement("SELECT ID FROM " + DatabaseFactory.PLAYER_TABLE + " WHERE nickname = ?;");
                s.setString(1, name);
                ResultSet rs = s.executeQuery();
                if (rs.next()) {
                    ret = new SQLPlayer(rs.getInt("ID"));
                }
                conn.closeResultSet(rs);
                conn.closeStatement(s);
            } catch (SQLException ex) {
                ACLogger.severe("Error loading the players!", ex);
            }
            return ret;
        } else {
            for (StoredPlayer sp : players.values()) {
                if (sp.getName().equalsIgnoreCase(name)) {
                    return sp;
                }
            }
            return null;
        }
    }

    public static void teleport(final MultiServerLocation loc, final ACPlayer acp) {
        if (loc.isOnThisServer()) {
            final Location targetLoc = loc.getBukkitLocation();
            final Player targetPlayer = acp.getPlayer();
            Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    targetPlayer.teleport(targetLoc);
                }
            });

        } else {
            BungeeCordMessageManager mgr = Main.getInstance().getMessageManager();
            mgr.sendMessage(acp, Channel.NONE, MessageCommand.SWITCH_SERVER, loc.getServername());
            mgr.sendMessage(acp, Channel.TELEPORT, MessageCommand.FORWARD, loc.toString());
        }
    }

    public static void teleport(ACPlayer target, ACPlayer toTP) {
        if (PlayerManager.isOnThisServer(target) && PlayerManager.isOnThisServer(toTP)) {
            final Player toTPbukkit = toTP.getPlayer();
            final Player targetBukkit = target.getPlayer();
            Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    toTPbukkit.teleport(targetBukkit);
                }
            });
        } else {
            BungeeCordMessageManager mgr = Main.getInstance().getMessageManager();
            mgr.sendMessage(toTP, Channel.NONE, MessageCommand.SWITCH_SERVER, target.getServer());
            mgr.sendMessage(toTP, Channel.TELEPORT_PLAYER_TO_PLAYER, MessageCommand.FORWARD, target.getID() + "");
        }
    }

    public static void sendMessage(ACPlayer receiver, String message) {
        if (isOnThisServer(receiver)) {
            receiver.getPlayer().sendMessage(message);
        } else {
            BungeeCordMessageManager.getInstance().sendMessage(receiver, Channel.NONE, MessageCommand.MESSAGE, message);
        }
    }

    public static void kickPlayer(ACPlayer target, String message) {
        BungeeCordMessageManager.getInstance().sendMessage(target, Channel.NONE, MessageCommand.KICK, message);
    }

    public static boolean isOnThisServer(ACPlayer target) {
        return target.getServer().equalsIgnoreCase(BungeeCordMessageManager.getServerName()) && target.getPlayer() != null;
    }
}
