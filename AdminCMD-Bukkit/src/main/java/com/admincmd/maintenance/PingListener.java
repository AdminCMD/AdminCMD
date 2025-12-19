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
package com.admincmd.maintenance;

import com.admincmd.Main;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;
import com.admincmd.utils.MotD;
import com.admincmd.utils.ProtocolLibManager;
import com.comphenix.protocol.PacketType.Status.Server;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.comphenix.protocol.wrappers.WrappedServerPing.CompressedImage;

import java.io.File;
import java.io.FileInputStream;

public class PingListener {

    private final Main main = Main.getInstance();

    public void addPingResponsePacketListener() {
        ProtocolManager mgr = ProtocolLibManager.getManager();
        try {
            mgr.addPacketListener(new PacketAdapter(PacketAdapter.params(main, Server.SERVER_INFO).serverSide().listenerPriority(ListenerPriority.HIGHEST).optionAsync()) {
                                      public void onPacketSending(PacketEvent event) {
                                          try {
                                              if (!Config.MAINTENANCE_ENABLE.getBoolean()) {
                                                  return;
                                              }
                                              WrappedServerPing ping = event.getPacket().getServerPings().getValues().get(0);
                                              String pingMessage = "[Maintenance]";
                                              ping.setVersionProtocol(-1);
                                              ping.setVersionName(pingMessage);
                                              MotD motd = new MotD(Config.MAINTENANCE_MOTD_LINE1.getString(), Config.MAINTENANCE_MOTD_LINE2.getString());
                                              ping.setMotD(motd.getMotd());
                                              File iconfile = new File(Main.getInstance().getDataFolder().getAbsolutePath() + File.separator + Config.MAINTENANCE_FAVICON.getString());
                                              if (iconfile.exists()) {
                                                  CompressedImage favicon = CompressedImage.fromPng(new FileInputStream(iconfile));
                                                  ping.setFavicon(favicon);
                                              }
                                              event.getPacket().getServerPings().getValues().set(0, ping);
                                          } catch (Exception e) {
                                              ACLogger.severe(e);
                                          }
                                      }
                                  }
            );
        } catch (Exception e) {
            ACLogger.severe(e);
        }
    }
}
