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
package com.admincmd.commands.mob;

import com.admincmd.Main;
import com.admincmd.commandapi.*;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@CommandHandler
public class SpawnmobCommand {

    @BaseCommand(command = "spawnmob", sender = Sender.PLAYER, permission = "admincmd.mob.spawnmob", helpArguments = "mobtype amount")
    public CommandResult executeSpawnmob(final Player sender, final CommandArgs args) {
        if (args.getLength() != 2 || !args.isInteger(1)) {
            return CommandResult.ERROR;
        }

        final ACPlayer acp = PlayerManager.getPlayer(sender);

        final World target = sender.getWorld();
        final int amount = args.getInt(1);

        EntityType type;
        try {
            type = EntityType.valueOf(args.getString(0).toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandResult.NOT_A_MOB;
        }

        if (!type.isSpawnable()) {
            return CommandResult.NOT_SPAWNABLE;
        }

        final EntityType clone = type;

        Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < amount; i++) {
                    target.spawnEntity(Utils.getLocationLooking(sender, 10), clone);
                }

                String msg = Locales.MOB_SPAWNED.getString().replaceAll("%num%", amount + "");
                Messager.sendMessage(acp, msg, Messager.MessageType.INFO);
            }
        });
        return CommandResult.SUCCESS;
    }

}
