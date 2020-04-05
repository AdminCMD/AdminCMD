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
package com.admincmd.commands.teleport;

import com.admincmd.commandapi.BaseCommand;
import com.admincmd.commandapi.CommandArgs;
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandResult;
import com.admincmd.commandapi.Sender;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

@CommandHandler
public class DownCommand {

    @BaseCommand(command = "down", sender = Sender.PLAYER, permission = "admincmd.tp.down", aliases = "tpdown", helpArguments = {"", "<-p player>"})
    public CommandResult executeDown(Player sender, CommandArgs args) {
        ACPlayer se = PlayerManager.getPlayer(sender);
        if (args.isEmpty()) {
            Location loc = sender.getLocation();
            Location target = loc.clone();
            Location target2 = loc.clone();

            boolean found = false;

            Location underFeet = loc.getBlock().getRelative(BlockFace.DOWN, 2).getLocation();

            boolean air = (underFeet.getBlock().getType() == Material.AIR);

            if (air) {
                for (int y = underFeet.getBlockY() - 1; y >= 0; y--) {
                    target.setY(y);
                    target2.setY(y + 1);
                    Location target3 = target2.clone();
                    target3.setY(y + 2);

                    if (target.getBlock().getType() != Material.AIR && target2.getBlock().getType() == Material.AIR && target3.getBlock().getType() == Material.AIR) {
                        found = true;
                        target.setY(y + 1);
                        break;
                    }
                }
            } else {

                for (int y = loc.getBlockY() - 2; y >= 0; y--) {
                    target.setY(y);
                    target2.setY(y + 1);

                    if (target.getBlock().getType() == Material.AIR && target2.getBlock().getType() == Material.AIR) {
                        found = true;
                        break;
                    }
                }
            }

            if (found) {
                sender.teleport(target);
                return Messager.sendMessage(se, Locales.TELEPORT_DOWN, Messager.MessageType.INFO);
            } else {
                return CommandResult.NO_SPACE;
            }
        } else {
            if (args.hasFlag("p") && args.getLength() == 2) {
                if (!sender.hasPermission("admincmd.tp.down.other")) {
                    return CommandResult.NO_PERMISSION_OTHER;
                }

                CommandArgs.Flag f = args.getFlag("p");
                if (!f.isPlayerOnThisServer()) {
                    return CommandResult.NOT_ONLINE;
                }
                Player p = f.getPlayer().getPlayer();
                Location loc = p.getLocation();
                Location target = loc.clone();
                Location target2 = loc.clone();

                boolean found = false;

                Location underFeet = loc.getBlock().getRelative(BlockFace.DOWN, 2).getLocation();

                boolean air = (underFeet.getBlock().getType() == Material.AIR);

                if (air) {
                    for (int y = underFeet.getBlockY() - 1; y >= 0; y--) {
                        target.setY(y);
                        target2.setY(y + 1);
                        Location target3 = target2.clone();
                        target3.setY(y + 2);

                        if (target.getBlock().getType() != Material.AIR && target2.getBlock().getType() == Material.AIR && target3.getBlock().getType() == Material.AIR) {
                            found = true;
                            target.setY(y + 1);
                            break;
                        }
                    }
                } else {

                    for (int y = loc.getBlockY() - 2; y >= 0; y--) {
                        target.setY(y);
                        target2.setY(y + 1);

                        if (target.getBlock().getType() == Material.AIR && target2.getBlock().getType() == Material.AIR) {
                            found = true;
                            break;
                        }
                    }
                }

                if (found) {
                    p.teleport(target);
                    Messager.sendMessage(f.getPlayer(), Locales.TELEPORT_DOWN, Messager.MessageType.INFO);
                    return Messager.sendMessage(se, Locales.TELEPORT_DOWN_OTHER.getString().replaceAll("%player%", Utils.replacePlayerPlaceholders(p)), Messager.MessageType.INFO);
                } else {
                    return CommandResult.NO_SPACE;
                }

            }
            return CommandResult.ERROR;
        }
    }

}
