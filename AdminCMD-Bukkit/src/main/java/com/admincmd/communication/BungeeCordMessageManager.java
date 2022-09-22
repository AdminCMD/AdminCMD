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
package com.admincmd.communication;

import com.admincmd.Main;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BungeeCordMessageManager implements PluginMessageListener {

    private static BungeeCordMessageManager INSTANCE;

    public static BungeeCordMessageManager getInstance() {
        return INSTANCE;
    }

    public static String getServerName() {
        return Config.BUNGEECORD_SERVERNAME.getString();
    }

    public static void setup() {
        INSTANCE = new BungeeCordMessageManager();
        if (Config.BUNGEECORD.getBoolean()) {
            Main.getInstance().getServer().getMessenger().registerIncomingPluginChannel(Main.getInstance(), "BungeeCord", INSTANCE);
            Main.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(Main.getInstance(), "BungeeCord");
        }
    }

    public void sendMessage(final ACPlayer p, final Channel channel, final MessageCommand cmd, final String args) {
        if (!Config.BUNGEECORD.getBoolean()) {
            return;
        }

        if (cmd == null) {
            ACLogger.severe("Tried to send a message but the MessageCommand is null! Args: " + args);
            return;
        }

        if (channel == null) {
            ACLogger.severe("Tried to send a message but the channel is null! Args: " + args + " MessageCommand: " + cmd);
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                switch (cmd) {
                    case SWITCH_SERVER: {
                        ServerMessageHandler.sendSwitchServer(p, args, cmd);
                        return;
                    }
                    case MESSAGE: {
                        ServerMessageHandler.sendMessage(p, args, cmd);
                        return;
                    }
                    case KICK: {
                        ServerMessageHandler.sendKickPlayer(p, args, cmd);
                        return;
                    }
                    case FORWARD: {
                        switch (channel) {
                            case TELEPORT: {
                                PlayerMessageHandler.sendTeleport(p, args, channel);
                                return;
                            }
                            case TELEPORT_PLAYER_TO_PLAYER: {
                                ACPlayer to = PlayerManager.getPlayer(Integer.valueOf(args));
                                PlayerMessageHandler.sendTeleportToPlayer(p, to, channel);
                                return;
                            }
                            case WORLD_TIME_SET: {
                                WorldMessageHandler.setTime(args, channel);
                                return;
                            }
                            case WORLD_TIME_PAUSE: {
                                WorldMessageHandler.setTimePause(args, channel);
                                return;
                            }
                            case WORLD_WEATHER_SET: {
                                WorldMessageHandler.setWeatherSun(args, channel);
                                return;
                            }
                            case KILL_MOBS: {
                                WorldMessageHandler.setKillMobs(args, channel);
                                return;
                            }
                            case CLEAR_INVENTORY: {
                                PlayerMessageHandler.sendClearInventory(p, channel);
                                return;
                            }
                            case FEED_PLAYER: {
                                PlayerMessageHandler.sendPlayerFeed(p, channel);
                                return;
                            }
                            case FLY_PLAYER: {
                                PlayerMessageHandler.sendPlayerFly(p, channel, args);
                                return;
                            }
                            case HEAL_PLAYER:
                                PlayerMessageHandler.sendPlayerHeal(p, channel);
                                return;
                            case KILL_PLAYER:
                                PlayerMessageHandler.sendPlayerKill(p, channel);
                                return;
                            case VANISH_PLAYER:
                                PlayerMessageHandler.sendPlayerVanish(p, channel);
                                return;
                            case GAMEMODE_PLAYER:
                                PlayerMessageHandler.sendPlayerGamemode(p, channel, args);
                                return;
                            default:
                                ACLogger.warn("Tried to send a message, but no method was made yet for channel " + channel);
                                return;
                        }

                    }
                    default:
                        ACLogger.warn("Tried to send a message, but no method was made yet for command " + cmd);
                        return;
                }
            }
        });

    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, final byte[] message) {
        if (!channel.equals("BungeeCord")) {
            return;
        }

        if (!Config.BUNGEECORD.getBoolean()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                ByteArrayDataInput in = ByteStreams.newDataInput(message);
                String subChannel = in.readUTF();

                ACLogger.debug(subChannel);

                if (subChannel.startsWith("AdminCMD")) {
                    short len = in.readShort();
                    byte[] msgbytes = new byte[len];
                    in.readFully(msgbytes);

                    DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                    String msg;

                    try {
                        msg = msgin.readUTF();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        msg = null;
                    }

                    if (msg == null) {
                        return;
                    }

                    if (subChannel.equals(Channel.TELEPORT.getCompleteChannelName())) {
                        PlayerMessageHandler.reactTeleport(msg);
                    } else if (subChannel.equals(Channel.TELEPORT_PLAYER_TO_PLAYER.getCompleteChannelName())) {
                        PlayerMessageHandler.reactTeleportToPlayer(msg);
                    } else if (subChannel.equals(Channel.WORLD_TIME_SET.getCompleteChannelName())) {
                        WorldMessageHandler.reactTimeSet(msg);
                    } else if (subChannel.equals(Channel.WORLD_TIME_PAUSE.getCompleteChannelName())) {
                        WorldMessageHandler.reactTimePause(msg);
                    } else if (subChannel.equals(Channel.WORLD_WEATHER_SET.getCompleteChannelName())) {
                        WorldMessageHandler.reactWeatherSet(msg);
                    } else if (subChannel.equals(Channel.KILL_MOBS.getCompleteChannelName())) {
                        WorldMessageHandler.reactKillMobs(msg);
                    } else if (subChannel.equals(Channel.CLEAR_INVENTORY.getCompleteChannelName())) {
                        PlayerMessageHandler.reactClearInventory(msg);
                    } else if (subChannel.equals(Channel.FEED_PLAYER.getCompleteChannelName())) {
                        PlayerMessageHandler.reactPlayerFeed(msg);
                    } else if (subChannel.equals(Channel.FLY_PLAYER.getCompleteChannelName())) {
                        PlayerMessageHandler.reactPlayerFly(msg);
                    } else if (subChannel.equals(Channel.HEAL_PLAYER.getCompleteChannelName())) {
                        PlayerMessageHandler.reactPlayerHeal(msg);
                    } else if (subChannel.equals(Channel.KILL_PLAYER.getCompleteChannelName())) {
                        PlayerMessageHandler.reactPlayerKill(msg);
                    } else if (subChannel.equals(Channel.VANISH_PLAYER.getCompleteChannelName())) {
                        PlayerMessageHandler.reactPlayerVanish(msg);
                    } else if (subChannel.equals(Channel.GAMEMODE_PLAYER.getCompleteChannelName())) {
                        PlayerMessageHandler.reactPlayerGamemode(msg);
                    } else {
                        ACLogger.warn("Unknown message channel: " + subChannel);
                    }
                }
            }
        });

    }

}
