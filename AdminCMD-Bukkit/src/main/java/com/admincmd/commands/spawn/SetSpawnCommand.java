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
package com.admincmd.commands.spawn;

import com.admincmd.commandapi.*;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.spawn.SpawnManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import org.bukkit.entity.Player;

@CommandHandler
public class SetSpawnCommand {

    @BaseCommand(command = "setspawn", sender = Sender.PLAYER, permission = "admincmd.spawn.setspawn", helpArguments = "", async = true)
    public CommandResult executeSetspawn(Player sender, CommandArgs args) {
        if (!args.isEmpty()) {
            return CommandResult.ERROR;
        }

        ACPlayer acp = PlayerManager.getPlayer(sender);
        SpawnManager.setSpawn(acp);
        return Messager.sendMessage(acp, Locales.SPAWN_SET, Messager.MessageType.INFO);
    }

}
