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
package com.admincmd.commands.home;

import com.admincmd.commandapi.*;
import com.admincmd.commandapi.CommandArgs.Flag;
import com.admincmd.home.ACHome;
import com.admincmd.home.HomeManager;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.google.common.base.Joiner;
import org.bukkit.command.CommandSender;
import java.util.List;
import org.bukkit.entity.Player;

@CommandHandler
public class HomeCommand {
    
    @BaseCommand(command = "home", sender = Sender.PLAYER, permission = "admincmd.home.tp", helpArguments = {"", "<name>", "<-p player>", "<-p player> <-h home>"})
    public CommandResult executeHome(Player p, CommandArgs args) {
        ACPlayer acp = PlayerManager.getPlayer(p);
        
        if (args.isEmpty()) {
            //List own homes
            String homes = Locales.HOME_HOME.getString() + " (" + HomeManager.getHomes(acp).size() + "): §6" + Joiner.on(", ").join(HomeManager.getHomes(acp));
            return Messager.sendMessage(acp, homes, Messager.MessageType.INFO);
        } else {
            ACHome target;
            if (!args.hasFlag("p")) {
                target = HomeManager.getHome(acp, args.getString(0));
                PlayerManager.teleport(target.getLocation(), acp);
                return CommandResult.SUCCESS;
            } else {
                if (!p.hasPermission("admincmd.home.tp.other")) {
                    return CommandResult.NO_PERMISSION_OTHER;
                }
                
                Flag playerFlag = args.getFlag("p");
                if (!playerFlag.isRegisteredPlayer()) {
                    return CommandResult.NOT_A_PLAYER;
                }
                ACPlayer targetPlayer = args.getFlag("p").getPlayer();
                
                if (!args.hasFlag("h")) {
                    //List homes of other Player
                    List<String> homes = HomeManager.getHomes(targetPlayer);
                    String homeString = Locales.HOME_HOME.getString() + " (" + homes.size() + "): §6" + Joiner.on(", ").join(homes);
                    return Messager.sendMessage(acp, homeString, Messager.MessageType.INFO);
                }

                //Teleport to home of other Player
                Flag homeFlag = args.getFlag("h");
                target = HomeManager.getHome(targetPlayer, homeFlag.getString());
                if (target != null) {
                    PlayerManager.teleport(target.getLocation(), acp);
                    return CommandResult.SUCCESS;
                } else {
                    return Messager.sendMessage(acp, Locales.HOME_NOHOME, Messager.MessageType.ERROR);
                }
            }
        }
    }
    
    @TabComplete(command = "home")
    public List<String> onTabComplete(CommandSender sender, CommandArgs args, List<String> tabs) {
        if (args.hasFlag("p") && sender.hasPermission("admincmd.home.tp.other")) {
            Flag player = args.getFlag("p");
            if (player.isRegisteredPlayer()) {
                tabs.addAll(HomeManager.getHomes(player.getPlayer()));
            }
        } else {
            if (sender instanceof Player) {
                ACPlayer p = PlayerManager.getPlayer((Player) sender);
                tabs.addAll(HomeManager.getHomes(p));
            }
        }
        return tabs;
    }
    
}
