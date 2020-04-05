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

import com.admincmd.commandapi.BaseCommand;
import com.admincmd.commandapi.CommandArgs;
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandResult;
import com.admincmd.commandapi.Sender;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.MultiServerLocation;
import com.admincmd.warp.WarpManager;
import org.bukkit.entity.Player;
import com.admincmd.warp.ACWarp;

@CommandHandler
public class EditWarpCommand {

    @BaseCommand(command = "editwarp", sender = Sender.PLAYER, permission = "admincmd.warp.edit", helpArguments = "<name>")
    public CommandResult executeEditwarp(Player sender, CommandArgs args) {
        if (args.getLength() != 1) {
            return CommandResult.ERROR;
        }
        
        ACPlayer se = PlayerManager.getPlayer(sender);

        ACWarp w = WarpManager.getWarp(args.getString(0));

        if (w == null) {
            return Messager.sendMessage(se, Locales.WARP_NO_SUCH_WARP, Messager.MessageType.ERROR);
        }
        
        w.setLocation(MultiServerLocation.fromLocation(sender.getLocation()));
        return Messager.sendMessage(se, Locales.WARP_UPDATED, Messager.MessageType.INFO);
    }

}
