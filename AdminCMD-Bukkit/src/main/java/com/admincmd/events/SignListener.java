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
package com.admincmd.events;

import com.admincmd.Main;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.BukkitListener;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.Utils;
import com.admincmd.warp.WarpManager;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import com.admincmd.warp.ACWarp;
import org.bukkit.Bukkit;

public class SignListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(final SignChangeEvent e) {
        for (int i = 0; i < e.getLines().length; i++) {
            String line = e.getLines()[i];
            line = Utils.replaceColors(line);
            e.setLine(i, line);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignEdit(final SignChangeEvent e) {
        Player p = e.getPlayer();
        ACPlayer acp = PlayerManager.getPlayer(p);
        String[] lines = e.getLines();

        if (lines.length < 2) {
            return;
        }
        if (lines[0].equalsIgnoreCase("[command]")) {
            if (!p.hasPermission("admincmd.commandsign.create")) {
                Messager.sendMessage(acp, Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", "admincmd.commandsign.create"), Messager.MessageType.NONE);
                e.getBlock().breakNaturally();
                e.setCancelled(true);
                return;
            }

            for (int i = 0; i < lines.length; i++) {
                ChatColor color = null;
                if (i == 0) {
                    color = ChatColor.BLUE;
                } else if (i == 1) {
                    color = ChatColor.GOLD;
                }

                String line;
                if (color != null) {
                    line = color + lines[i];
                } else {
                    line = lines[i];
                }

                if (line.length() > 16) {
                    e.getBlock().breakNaturally();
                    Messager.sendMessage(acp, "The line is too long to fit on the sign!", Messager.MessageType.ERROR);
                    break;
                }

                e.setLine(i, line);
            }
        }

        if (lines[0].equalsIgnoreCase("[warp]")) {
            if (!p.hasPermission("admincmd.warpsign.create")) {
                Messager.sendMessage(acp, Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", "admincmd.warpsign.create"), Messager.MessageType.NONE);
                e.getBlock().breakNaturally();
                e.setCancelled(true);
                return;
            }

            for (int i = 0; i < lines.length; i++) {
                ChatColor color = null;
                if (i == 0) {
                    color = ChatColor.BLUE;
                } else if (i == 1) {
                    color = ChatColor.GOLD;
                }

                String line;
                if (color != null) {
                    line = color + lines[i];
                } else {
                    line = lines[i];
                }

                if (line.length() > 16) {
                    e.getBlock().breakNaturally();
                    Messager.sendMessage(acp, "The line is too long to fit on the sign!", Messager.MessageType.ERROR);
                    break;
                }

                e.setLine(i, line);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignCLick(final PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!(e.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        final Sign s = (Sign) e.getClickedBlock().getState();

        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                ACPlayer acp = PlayerManager.getPlayer(e.getPlayer());

                List<String> lines = new ArrayList<>();

                for (String string : s.getLines()) {
                    lines.add(Utils.removeColors(string));
                }

                if (lines.size() < 2) {
                    return;
                }

                if (lines.get(0).equalsIgnoreCase("[command]")) {
                    if (!e.getPlayer().hasPermission("admincmd.commandsign.use")) {
                        Messager.sendMessage(acp, Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", "admincmd.commandsign.use"), Messager.MessageType.NONE);
                        return;
                    }

                    String command = lines.get(1);
                    e.getPlayer().performCommand(command.replaceFirst("/", ""));
                    return;
                }

                if (lines.get(0).equalsIgnoreCase("[warp]")) {
                    if (!e.getPlayer().hasPermission("admincmd.warpsing.use")) {
                        Messager.sendMessage(acp, Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", "admincmd.warpsing.use"), Messager.MessageType.NONE);
                        return;
                    }
                    String warp = lines.get(1);
                    ACWarp w = WarpManager.getWarp(warp);
                    if (w != null) {
                        PlayerManager.teleport(w.getLocation(), PlayerManager.getPlayer(e.getPlayer()));
                    } else {
                        Messager.sendMessage(acp, Locales.WARP_NO_SUCH_WARP, Messager.MessageType.ERROR);
                    }
                }
            }
        });
    }
}
