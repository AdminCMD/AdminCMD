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
package com.admincmd.chestsigns.commands;

import com.admincmd.Main;
import com.admincmd.chestsigns.inventorysigns.InventorySign;
import com.admincmd.chestsigns.inventorysigns.SignManager;
import com.admincmd.commandapi.*;
import com.admincmd.commandapi.CommandArgs.Flag;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Messager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandHandler
public class SignCommands {

    private static final HelpPage invsign = new HelpPage("invsign");
    private static final HelpPage delInvSign = new HelpPage("delinvsign");

    static {
        invsign.addPage("", "Opens the inventory of the Block connecting to your InvSign");
        invsign.addPage(" <-p player>", "Opens the inventory of the Block connecting to the InvSign of the given player");
        invsign.prepare();

        delInvSign.addPage("", "Deletes your InvSign (same as breaking it)");
        delInvSign.addPage(" <-p player>", "Deletes the InvSign of the given Player");
        delInvSign.prepare();
    }

    @BaseCommand(command = "delinvsign", sender = Sender.PLAYER, permission = "admincmd.inventorysign.break")
    public CommandResult executeClearChest(Player sender, CommandArgs args) {
        if (delInvSign.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        ACPlayer se = PlayerManager.getPlayer(sender);

        if (args.isEmpty()) {
            InventorySign sign = SignManager.getSign(se);
            if(sign != null) {
                SignManager.deleteSign(sign);
            }
            return Messager.sendMessage(se, "Successfully deleted your sign.", Messager.MessageType.INFO);
        } else {
            if (args.hasFlag("p")) {
                if (!sender.hasPermission("admincmd.inventorysign.break.other")) {
                    return CommandResult.NO_PERMISSION_OTHER;
                }

                Flag f = args.getFlag("p");
                if (!f.isRegisteredPlayer()) {
                    return CommandResult.NOT_A_PLAYER;
                }

                ACPlayer bp = f.getPlayer();
                InventorySign sign = SignManager.getSign(bp);
                if(sign != null) {
                    SignManager.deleteSign(sign);
                }
                return Messager.sendMessage(se, "Successfully removed the sign of " + bp.getName(), Messager.MessageType.INFO);
            }
        }

        return CommandResult.ERROR;
    }

    @BaseCommand(command = "invsign", sender = Sender.PLAYER, permission = "admincmd.inventorysign.open", aliases = "is")
    public CommandResult executeChest(final Player sender, CommandArgs args) {
        if (invsign.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        ACPlayer se = PlayerManager.getPlayer(sender);

        if (args.isEmpty()) {
            InventorySign sign = SignManager.getSign(se);
            if(sign == null || !sign.exists() || !sign.isValid()) {
                return Messager.sendMessage(se, "Your sign is not connecting to ONE chest or Container!", Messager.MessageType.ERROR);
            }
                final Inventory inv = sign.getConnectingInventory();
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> sender.openInventory(inv));
            return CommandResult.SUCCESS;
        } else {
            if (args.hasFlag("p")) {
                if (!sender.hasPermission("admincmd.virtualchest.chest.other")) {
                    return CommandResult.NO_PERMISSION_OTHER;
                }

                Flag f = args.getFlag("p");
                if (!f.isRegisteredPlayer()) {
                    return CommandResult.NOT_A_PLAYER;
                }

                ACPlayer bp = f.getPlayer();
                InventorySign sign = SignManager.getSign(bp);
                if(sign == null || !sign.exists() || !sign.isValid()) {
                    return Messager.sendMessage(se, "The Player's sign is not connecting to ONE chest or Container!", Messager.MessageType.ERROR);
                }
                final Inventory inv = sign.getConnectingInventory();
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> sender.openInventory(inv));
                return CommandResult.SUCCESS;
            }
        }

        return CommandResult.ERROR;
    }

}
