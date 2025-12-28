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
package com.admincmd.commands.warps;

import com.admincmd.commandapi.*;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.warp.ACWarp;
import com.admincmd.warp.WarpManager;
import com.google.common.base.Joiner;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandHandler
public class WarpCommand {

    @BaseCommand(command = "warp", sender = Sender.PLAYER, permission = "admincmd.warp.tp", helpArguments = {"", "<name>"}, async = true)
    public CommandResult executeWarp(Player p, CommandArgs args) {
        ACPlayer se = PlayerManager.getPlayer(p);
        if (args.getLength() > 1) {
            return CommandResult.ERROR;
        }

        if (args.isEmpty()) {
            String warps = Locales.WARP_WARP.getString() + " (" + WarpManager.getWarps().size() + "): §6" + Joiner.on(", ").join(WarpManager.getWarps());
            return Messager.sendMessage(se, warps, Messager.MessageType.INFO);
        } else {
            ACWarp w = WarpManager.getWarp(args.getString(0));
            if (w != null) {

                String permission = "admincmd.warp.tp." + w.name();
                if (!p.hasPermission(permission)) {
                    return Messager.sendMessage(se, Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", permission), Messager.MessageType.ERROR);
                }

                PlayerManager.teleport(w.getLocation(), se);
                return CommandResult.SUCCESS;
            } else {
                return Messager.sendMessage(se, Locales.WARP_NO_SUCH_WARP, Messager.MessageType.ERROR);
            }
        }
    }
    
    @TabComplete(command = "warp")
    public List<String> onTabComplete(CommandSender sender, CommandArgs args, List<String> tabs) {
        tabs.addAll(WarpManager.getWarps());
        return tabs;
    }

}
