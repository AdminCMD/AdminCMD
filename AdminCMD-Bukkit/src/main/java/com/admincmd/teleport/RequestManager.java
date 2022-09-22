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
package com.admincmd.teleport;

import com.admincmd.Main;
import com.admincmd.database.Database;
import com.admincmd.database.DatabaseFactory;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RequestManager {

    //Creator, Request
    private static final Map<Integer, ACTeleportRequest> tpList = new HashMap<>();
    private static final Database db = DatabaseFactory.getDatabase();

    public static boolean receiverHasRequest(ACPlayer receiver) {
        if (!Config.BUNGEECORD.getBoolean()) {
            for (ACTeleportRequest request : tpList.values()) {
                if (request.getReceiver().getID() == receiver.getID()) {
                    return true;
                }
            }
            return false;
        } else {
            boolean ret = false;
            try {
                PreparedStatement s = db.getPreparedStatement("SELECT * FROM " + DatabaseFactory.TP_REQUEST_TABLE + " WHERE receiver = ?;");
                s.setInt(1, receiver.getID());
                ResultSet rs = s.executeQuery();
                ret = rs.next();
                db.closeResultSet(rs);
                db.closeStatement(s);
            } catch (SQLException ex) {
                ACLogger.severe(ex);
            }
            return ret;
        }
    }

    public static boolean creatorHasRequest(ACPlayer creator) {
        if (!Config.BUNGEECORD.getBoolean()) {
            return tpList.containsKey(creator.getID());
        } else {
            boolean ret = false;
            try {
                PreparedStatement s = db.getPreparedStatement("SELECT * FROM " + DatabaseFactory.TP_REQUEST_TABLE + " WHERE requester = ?;");
                s.setInt(1, creator.getID());
                ResultSet rs = s.executeQuery();
                ret = rs.next();
                db.closeResultSet(rs);
                db.closeStatement(s);
            } catch (SQLException ex) {
                ACLogger.severe(ex);
            }
            return ret;
        }
    }

    public static void createRequest(final ACPlayer requester, final ACPlayer receiver, final RequestType type) {
        final ACTeleportRequest toAdd = new StoredRequest(requester, receiver, type);
        if (!Config.BUNGEECORD.getBoolean()) {
            tpList.put(requester.getID(), toAdd);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        PreparedStatement s = db.getPreparedStatement("INSERT INTO " + DatabaseFactory.TP_REQUEST_TABLE + " (requester, receiver, type) VALUES (?, ?, ?);");
                        s.setInt(1, requester.getID());
                        s.setInt(2, receiver.getID());
                        s.setString(3, type.toString());
                        s.executeUpdate();
                        db.closeStatement(s);
                    } catch (SQLException ex) {
                        ACLogger.severe(ex);
                    }
                }
            });
        }

        Messager.sendMessage(requester, Locales.TELEPORT_TPA_SENT_REQUESTER.replacePlayer(receiver.getOfflinePlayer()), Messager.MessageType.INFO);
        if (type == RequestType.TPHERE) {
            Messager.sendMessage(receiver, Locales.TELEPORT_TPAHERE_SENT_TARGET.replacePlayer(requester.getOfflinePlayer()), Messager.MessageType.INFO);
        } else {
            Messager.sendMessage(receiver, Locales.TELEPORT_TPA_SENT_TARGET.replacePlayer(requester.getOfflinePlayer()), Messager.MessageType.INFO);
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                denyAfterTime(toAdd);
            }
        }, 5 * 20 * 60);
    }

    public static ACTeleportRequest getRequestFromReceiver(ACPlayer receiver) {
        if (!Config.BUNGEECORD.getBoolean()) {
            for (ACTeleportRequest request : tpList.values()) {
                if (request.getReceiver().getID() == receiver.getID()) {
                    return request;
                }
            }
            return null;
        } else {
            ACTeleportRequest ret = null;
            try {
                PreparedStatement s = db.getPreparedStatement("SELECT * FROM " + DatabaseFactory.TP_REQUEST_TABLE + " WHERE receiver = ?;");
                s.setInt(1, receiver.getID());
                ResultSet rs = s.executeQuery();
                if (rs.next()) {
                    ret = new StoredRequest(PlayerManager.getPlayer(rs.getInt("requester")), receiver, RequestType.valueOf(rs.getString("type")));
                }
                db.closeResultSet(rs);
                db.closeStatement(s);
            } catch (SQLException ex) {
                ACLogger.severe(ex);
            }
            return ret;
        }
    }

    public static ACTeleportRequest getRequestFromCreator(ACPlayer creator) {
        if (!Config.BUNGEECORD.getBoolean()) {
            if (!creatorHasRequest(creator)) {
                return null;
            }
            return tpList.get(creator.getID());
        } else {
            ACTeleportRequest ret = null;
            try {
                PreparedStatement s = db.getPreparedStatement("SELECT * FROM " + DatabaseFactory.TP_REQUEST_TABLE + " WHERE requester = ?;");
                s.setInt(1, creator.getID());
                ResultSet rs = s.executeQuery();
                if (rs.next()) {
                    ret = new StoredRequest(creator, PlayerManager.getPlayer(rs.getInt("receiver")), RequestType.valueOf(rs.getString("type")));
                }
                db.closeResultSet(rs);
                db.closeStatement(s);
            } catch (SQLException ex) {
                ACLogger.severe(ex);
            }
            return ret;
        }
    }

    public static void denyRequestFromReceiver(final ACPlayer receiver) {
        ACTeleportRequest request = getRequestFromReceiver(receiver);
        if (!Config.BUNGEECORD.getBoolean()) {
            if (request != null) {
                tpList.remove(request.getRequester().getID());
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        PreparedStatement s = db.getPreparedStatement("DELETE FROM " + DatabaseFactory.TP_REQUEST_TABLE + " WHERE receiver = ?;");
                        s.setInt(1, receiver.getID());
                        s.executeUpdate();
                        db.closeStatement(s);
                    } catch (SQLException ex) {
                        ACLogger.severe(ex);
                    }
                }
            });
        }
        Messager.sendMessage(receiver, Locales.TELEPORT_TPA_DENY_RECEIVER.replacePlayer(request.getRequester().getOfflinePlayer()), Messager.MessageType.INFO);
        Messager.sendMessage(request.getRequester(), Locales.TELEPORT_TPA_DENY_REQUESTER.replacePlayer(receiver.getOfflinePlayer()), Messager.MessageType.INFO);
    }

    public static void denyRequestFromCreator(final ACPlayer creator) {
        ACTeleportRequest request = getRequestFromCreator(creator);
        if (!Config.BUNGEECORD.getBoolean()) {
            if (tpList.containsKey(creator.getID())) {
                tpList.remove(creator.getID());
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        PreparedStatement s = db.getPreparedStatement("DELETE FROM " + DatabaseFactory.TP_REQUEST_TABLE + " WHERE requester = ?;");
                        s.setInt(1, creator.getID());
                        s.executeUpdate();
                        db.closeStatement(s);
                    } catch (SQLException ex) {
                        ACLogger.severe(ex);
                    }
                }
            });
        }
        Messager.sendMessage(request.getReceiver(), Locales.TELEPORT_TPA_DENY_RECEIVER.replacePlayer(creator.getOfflinePlayer()), Messager.MessageType.INFO);
        Messager.sendMessage(creator, Locales.TELEPORT_TPA_DENY_REQUESTER.replacePlayer(request.getReceiver().getOfflinePlayer()), Messager.MessageType.INFO);
    }

    public static void acceptRequest(final ACPlayer receiver) {
        final ACTeleportRequest request = getRequestFromReceiver(receiver);
        if (request != null) {
            if (!request.getRequester().isOnline()) {
                Messager.sendMessage(receiver, Locales.COMMAND_MESSAGES_NOT_ONLINE, Messager.MessageType.ERROR);
                denyRequestFromReceiver(receiver);
                return;
            }

            if (request.getType() == RequestType.TPHERE) {
                Messager.sendMessage(receiver, Locales.TELEPORT_TPA_ACCEPT_REQUEST.replacePlayer(request.getRequester().getOfflinePlayer()), Messager.MessageType.INFO);
                Messager.sendMessage(request.getRequester(), Locales.TELEPORT_TPA_ACCEPT_TARGET.replacePlayer(request.getReceiver().getOfflinePlayer()), Messager.MessageType.INFO);
                PlayerManager.teleport(request.getRequester(), receiver);
            } else {
                Messager.sendMessage(request.getRequester(), Locales.TELEPORT_TPA_ACCEPT_REQUEST.replacePlayer(request.getReceiver().getOfflinePlayer()), Messager.MessageType.INFO);
                Messager.sendMessage(receiver, Locales.TELEPORT_TPA_ACCEPT_TARGET.replacePlayer(request.getRequester().getOfflinePlayer()), Messager.MessageType.INFO);
                PlayerManager.teleport(receiver, request.getRequester());
            }

            if (!Config.BUNGEECORD.getBoolean()) {
                tpList.remove(request.getRequester().getID());
            } else {
                Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PreparedStatement s = db.getPreparedStatement("DELETE FROM " + DatabaseFactory.TP_REQUEST_TABLE + " WHERE requester = ? AND receiver = ?;");
                            s.setInt(1, request.getRequester().getID());
                            s.setInt(2, request.getReceiver().getID());
                            s.executeUpdate();
                            db.closeStatement(s);
                        } catch (SQLException ex) {
                            ACLogger.severe(ex);
                        }
                    }
                });
            }
        }
    }

    private static void denyAfterTime(final ACTeleportRequest request) {
        if (request == null) {
            return;
        }

        if (!Config.BUNGEECORD.getBoolean()) {
            tpList.remove(request.getRequester().getID());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        PreparedStatement s = db.getPreparedStatement("DELETE FROM " + DatabaseFactory.TP_REQUEST_TABLE + " WHERE requester = ? AND receiver = ?;");
                        s.setInt(1, request.getRequester().getID());
                        s.setInt(2, request.getReceiver().getID());
                        s.executeUpdate();
                        db.closeStatement(s);
                    } catch (SQLException ex) {
                        ACLogger.severe(ex);
                    }
                }
            });
        }

        Messager.sendMessage(request.getReceiver(), Locales.TELEPORT_TPA_TIMEOUT_TARGET.replacePlayer(request.getRequester().getOfflinePlayer()), Messager.MessageType.INFO);
        Messager.sendMessage(request.getRequester(), Locales.TELEPORT_TPA_TIMEOUT_REQUEST.replacePlayer(request.getReceiver().getOfflinePlayer()), Messager.MessageType.INFO);
    }

}
