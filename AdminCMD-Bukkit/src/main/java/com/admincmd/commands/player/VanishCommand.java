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
package com.admincmd.commands.player;

import com.admincmd.Main;
import com.admincmd.commandapi.BaseCommand;
import com.admincmd.commandapi.CommandArgs;
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandResult;
import com.admincmd.commandapi.Sender;
import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.communication.Channel;
import com.admincmd.communication.MessageCommand;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandHandler
public class VanishCommand {

    @BaseCommand(command = "vanish", sender = Sender.PLAYER, permission = "admincmd.player.vanish", aliases = "invisible,poof,hide", helpArguments = {"", "<-p player>"})
    public CommandResult executeVanish(Player sender, CommandArgs args) {
        final ACPlayer se = PlayerManager.getPlayer(sender);
        if (args.isEmpty()) {
            final boolean value = !se.isInvisible();
            se.setInvisible(value);
            String s = se.isInvisible() ? Locales.COMMAND_MESSAGES_ENABLED.getString() : Locales.COMMAND_MESSAGES_DISABLED.getString();
            String msg = Locales.PLAYER_VANISH_TOGGLED_SELF.getString().replaceAll("%status%", s);
            Messager.sendMessage(se, msg, Messager.MessageType.INFO);
            Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (value) {
                        for (Player op : Bukkit.getOnlinePlayers()) {
                            op.hidePlayer(Main.getInstance(), se.getPlayer());
                        }
                    } else {
                        for (Player op : Bukkit.getOnlinePlayers()) {
                            op.showPlayer(Main.getInstance(), se.getPlayer());
                        }
                    }
                }
            });
            return CommandResult.SUCCESS;
        }

        if (args.hasFlag("p")) {
            if (!sender.hasPermission("admincmd.player.vanish.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }

            CommandArgs.Flag flag = args.getFlag("p");
            if (!flag.isRegisteredPlayer()) {
                return CommandResult.NOT_A_PLAYER;
            }

            final ACPlayer p = flag.getPlayer();
            final boolean value = !p.isInvisible();
            p.setInvisible(value);

            if (p.isOnline()) {
                if (PlayerManager.isOnThisServer(p)) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            if (value) {
                                for (Player op : Bukkit.getOnlinePlayers()) {
                                    op.hidePlayer(Main.getInstance(), p.getPlayer());
                                }
                            } else {
                                for (Player op : Bukkit.getOnlinePlayers()) {
                                    op.showPlayer(Main.getInstance(), p.getPlayer());
                                }
                            }
                        }
                    });
                } else {
                    BungeeCordMessageManager.getInstance().sendMessage(p, Channel.VANISH_PLAYER, MessageCommand.FORWARD, "");
                }
            }

            String s = value ? Locales.COMMAND_MESSAGES_ENABLED.getString() : Locales.COMMAND_MESSAGES_DISABLED.getString();
            String msgTarget = Locales.PLAYER_VANISH_TOGGLED_SELF.getString().replaceAll("%status%", s);
            String msgSender = Locales.PLAYER_VANISH_TOGGLED_OTHER.getString().replaceAll("%status%", s).replaceAll("%player%", Utils.replacePlayerPlaceholders(p.getOfflinePlayer()));
            Messager.sendMessage(p, msgTarget, Messager.MessageType.INFO);
            return Messager.sendMessage(se, msgSender, Messager.MessageType.INFO);
        }

        return CommandResult.ERROR;
    }

}
