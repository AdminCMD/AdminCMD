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
package com.admincmd.punishments;

import com.admincmd.commandapi.BaseCommand;
import com.admincmd.commandapi.CommandArgs;
import com.admincmd.commandapi.CommandArgs.Flag;
import com.admincmd.commandapi.CommandHandler;
import com.admincmd.commandapi.CommandResult;
import com.admincmd.commandapi.HelpPage;
import com.admincmd.commandapi.Sender;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.punishments.punishments.Punishment;
import com.admincmd.punishments.punishments.PunishmentManager;
import com.admincmd.punishments.punishments.PunishmentType;
import com.admincmd.utils.Messager;
import com.admincmd.utils.Utils;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandHandler
public class PunishmentCommands {

    private static final HelpPage kick = new HelpPage("kick");
    private static final HelpPage mute = new HelpPage("mute");
    private static final HelpPage ban = new HelpPage("ban");
    private static final HelpPage unmute = new HelpPage("unmute");
    private static final HelpPage unban = new HelpPage("unban");

    static {
        kick.addPage(" <player>", "Kicks the player with the standard reason");
        kick.addPage(" <player> <reason>", "Kicks the player from the server for the given reason.");
        kick.prepare();

        mute.addPage(" <player>", "Mutes the player with the standard time and standard reason");
        mute.addPage(" <player> <reason>", "Mutes the player for the standard time with the given reason");
        mute.addPage(" <player> -m <minutes> <reason>", "Mutes the player for the given minutes and the given reason");
        mute.addPage(" <player> -h <hours> <reason>", "Mutes the player for the given hours and the given reason");
        mute.addPage(" <player> -d <days> <reason>", "Mutes the player for the given days and the given reason");
        mute.addPage(" <player> forever <reason>", "Mutes the player forever with the given reason");
        mute.prepare();

        ban.addPage(" <player>", "Bans the player with the standard time and standard reason");
        ban.addPage(" <player> <reason>", "Bans the player for the standard time with the given reason");
        ban.addPage(" <player> -m <minutes> <reason>", "Bans the player for the given minutes and the given reason");
        ban.addPage(" <player> -h <hours> hours <reason>", "Bans the player for the given hours and the given reason");
        ban.addPage(" <player> -d <days> days <reason>", "Bans the player for the given days and the given reason");
        ban.addPage(" <player> forever <reason>", "Bans the player forever with the given reason");
        ban.prepare();

        unmute.addPage(" <player>", "Unmutes the player if he is muted.");
        unmute.prepare();

        unban.addPage(" <player>", "Unbans the player if he is banned.");
        unban.prepare();
    }

    @BaseCommand(command = "unmute", sender = Sender.ALL, permission = "admincmd.punishes.unmute")
    public CommandResult executeUnmute(CommandSender sender, CommandArgs args) {
        if (unmute.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        if (args.getLength() != 1) {
            return CommandResult.ERROR;
        }

        if (!args.isRegisteredPlayer(0)) {
            return CommandResult.NOT_A_PLAYER;
        }

        ACPlayer creator = null;
        if (sender instanceof Player) {
            creator = PlayerManager.getPlayer((Player) sender);
        }

        ACPlayer target = args.getPlayer(0);
        Punishment pu = PunishmentManager.getPunishment(target);
        if (pu == null || pu.getType() != PunishmentType.MUTE) {
            sender.sendMessage(Config.MESSAGE_NOT_PUNISHED.getString().replaceAll("%action%", PunishmentType.MUTE.getWord()).replaceAll("%player%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer())));
            return CommandResult.SUCCESS;
        }

        PunishmentManager.deletePunishment(pu);
        sender.sendMessage(Config.MESSAGE_UNPUNISHED_CREATOR.replace(target, pu));
        Messager.sendMessage(target, Config.MESSAGE_UNPUNISHED_TARGET.replace(creator, pu), Messager.MessageType.NONE);
        return CommandResult.SUCCESS;
    }

    @BaseCommand(command = "unban", sender = Sender.ALL, permission = "admincmd.punishes.unban")
    public CommandResult executeUnban(CommandSender sender, CommandArgs args) {
        if (unban.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        if (args.getLength() != 1) {
            return CommandResult.ERROR;
        }

        if (!args.isRegisteredPlayer(0)) {
            return CommandResult.NOT_A_PLAYER;
        }

        ACPlayer creator = null;
        if (sender instanceof Player) {
            creator = PlayerManager.getPlayer((Player) sender);
        }

        ACPlayer target = args.getPlayer(0);
        Punishment pu = PunishmentManager.getPunishment(target);
        if (pu == null || pu.getType() != PunishmentType.BAN) {
            sender.sendMessage(Config.MESSAGE_NOT_PUNISHED.getString().replaceAll("%action%", PunishmentType.BAN.getWord()).replaceAll("%player%", Utils.replacePlayerPlaceholders(target.getOfflinePlayer())));
            return CommandResult.SUCCESS;
        }

        PunishmentManager.deletePunishment(pu);
        sender.sendMessage(Config.MESSAGE_UNPUNISHED_CREATOR.replace(target, pu));
        Messager.sendMessage(target, Config.MESSAGE_UNPUNISHED_TARGET.replace(creator, pu), Messager.MessageType.NONE);
        return CommandResult.SUCCESS;
    }

    @BaseCommand(command = "kick", sender = Sender.ALL, permission = "admincmd.punishments.kick")
    public CommandResult executeKick(CommandSender sender, CommandArgs args) {
        if (kick.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        if (args.isEmpty()) {
            return CommandResult.ERROR;
        }

        if (!args.isRegisteredPlayer(0)) {
            return CommandResult.NOT_A_PLAYER;
        }

        ACPlayer creator = null;
        if (sender instanceof Player) {
            creator = PlayerManager.getPlayer((Player) sender);
        }

        ACPlayer target = args.getPlayer(0);
        if (!target.isOnline()) {
            return CommandResult.NOT_ONLINE;
        }

        if (args.getLength() == 1) {
            Punishment pu = new Punishment(target, creator, PunishmentType.KICK);
            PlayerManager.kickPlayer(target, pu.getReasonMessage());
            sender.sendMessage(Config.MESSAGE_CREATOR_KICK.replace(target, pu));
            return CommandResult.SUCCESS;
        } else {

            List<String> reason = args.getArgs().subList(1, args.getArgs().size());
            String reasonMsg = "";
            for (String arg : reason) {
                reasonMsg += (arg + " ");
            }
            Punishment pu = new Punishment(target, creator, PunishmentType.KICK, reasonMsg);
            PlayerManager.kickPlayer(target, pu.getReasonMessage());
            sender.sendMessage(Config.MESSAGE_CREATOR_KICK.replace(target, pu));
            return CommandResult.SUCCESS;
        }
    }

    @BaseCommand(command = "ban", sender = Sender.ALL, permission = "admincmd.punishments.ban")
    public CommandResult executeBan(CommandSender sender, CommandArgs args) {
        if (ban.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        if (args.isEmpty()) {
            return CommandResult.ERROR;
        }

        if (!args.isRegisteredPlayer(0)) {
            return CommandResult.NOT_A_PLAYER;
        }

        ACPlayer creator = null;
        if (sender instanceof Player) {
            creator = PlayerManager.getPlayer((Player) sender);
        }

        ACPlayer target = args.getPlayer(0);

        Punishment current = PunishmentManager.getPunishment(target);
        if (current != null) {
            sender.sendMessage(Config.MESSAGE_ALREADY_PUNISHED.replace(target, current));
            return CommandResult.SUCCESS;
        }

        if (args.getLength() == 1) {
            Punishment pu = new Punishment(target, creator, PunishmentType.BAN);
            PunishmentManager.savePunishment(pu);
            sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, pu));
            PlayerManager.kickPlayer(target, pu.getReasonMessage());
            return CommandResult.SUCCESS;
        } else {
            if (args.hasFlag("m")) {
                Flag minutes = args.getFlag("m");
                if (!minutes.isInteger()) {
                    return CommandResult.NOT_A_NUMBER;
                }
                int min = minutes.getInt();

                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(minutes.getString());
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }

                Punishment puNeu = new Punishment(target, creator, PunishmentType.BAN, min, reason);
                PunishmentManager.savePunishment(puNeu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, puNeu));
                PlayerManager.kickPlayer(target, puNeu.getReasonMessage());
                return CommandResult.SUCCESS;
            } else if (args.hasFlag("h")) {
                Flag hours = args.getFlag("h");
                if (!hours.isInteger()) {
                    return CommandResult.NOT_A_NUMBER;
                }
                int hr = hours.getInt();

                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(hours.getString());
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }

                int min = hr * 60;

                Punishment puNeu = new Punishment(target, creator, PunishmentType.BAN, min, reason);
                PunishmentManager.savePunishment(puNeu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, puNeu));
                PlayerManager.kickPlayer(target, puNeu.getReasonMessage());
                return CommandResult.SUCCESS;
            } else if (args.hasFlag("d")) {
                Flag days = args.getFlag("d");
                if (!days.isInteger()) {
                    return CommandResult.NOT_A_NUMBER;
                }
                int d = days.getInt();

                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(days.getString());
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }

                int min = d * 60 * 24;
                Punishment puNeu = new Punishment(target, creator, PunishmentType.BAN, min, reason);
                PunishmentManager.savePunishment(puNeu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, puNeu));
                PlayerManager.kickPlayer(target, puNeu.getReasonMessage());
                return CommandResult.SUCCESS;
            } else if (args.getString(1).equalsIgnoreCase("forever")) {
                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(args.getString(1));
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }
                Punishment puNeu = new Punishment(target, creator, PunishmentType.BAN, -1, reason);
                PunishmentManager.savePunishment(puNeu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, puNeu));
                PlayerManager.kickPlayer(target, puNeu.getReasonMessage());
                return CommandResult.SUCCESS;
            } else {
                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(args.getString(0));
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }

                Punishment pu = new Punishment(target, creator, PunishmentType.BAN, reason);
                PunishmentManager.savePunishment(pu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, pu));
                PlayerManager.kickPlayer(target, pu.getReasonMessage());
                return CommandResult.SUCCESS;
            }
        }
    }

    @BaseCommand(command = "mute", sender = Sender.ALL, permission = "admincmd.punishments.mute")
    public CommandResult executeMute(CommandSender sender, CommandArgs args) {
        if (mute.sendHelp(sender, args)) {
            return CommandResult.SUCCESS;
        }

        if (args.isEmpty()) {
            return CommandResult.ERROR;
        }

        if (!args.isRegisteredPlayer(0)) {
            return CommandResult.NOT_A_PLAYER;
        }

        ACPlayer creator = null;
        if (sender instanceof Player) {
            creator = PlayerManager.getPlayer((Player) sender);
        }

        ACPlayer target = args.getPlayer(0);

        Punishment current = PunishmentManager.getPunishment(target);
        if (current != null) {
            sender.sendMessage(Config.MESSAGE_ALREADY_PUNISHED.replace(target, current));
            return CommandResult.SUCCESS;
        }

        if (args.getLength() == 1) {
            Punishment pu = new Punishment(target, creator, PunishmentType.MUTE);
            PunishmentManager.savePunishment(pu);
            sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, pu));
            Messager.sendMessage(target, Config.MESSAGE_TARGET.replace(creator, pu), Messager.MessageType.NONE);
            return CommandResult.SUCCESS;
        } else {
            if (args.hasFlag("m")) {
                Flag minutes = args.getFlag("m");
                if (!minutes.isInteger()) {
                    return CommandResult.NOT_A_NUMBER;
                }
                int min = minutes.getInt();

                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(minutes.getString());
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }

                Punishment puNeu = new Punishment(target, creator, PunishmentType.MUTE, min, reason);
                PunishmentManager.savePunishment(puNeu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, puNeu));
                Messager.sendMessage(target, Config.MESSAGE_TARGET.replace(creator, puNeu), Messager.MessageType.NONE);
                return CommandResult.SUCCESS;
            } else if (args.hasFlag("h")) {
                Flag hours = args.getFlag("h");
                if (!hours.isInteger()) {
                    return CommandResult.NOT_A_NUMBER;
                }
                int hr = hours.getInt();

                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(hours.getString());
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }

                int min = hr * 60;

                Punishment puNeu = new Punishment(target, creator, PunishmentType.MUTE, min, reason);
                PunishmentManager.savePunishment(puNeu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, puNeu));
                Messager.sendMessage(target, Config.MESSAGE_TARGET.replace(creator, puNeu), Messager.MessageType.NONE);
                return CommandResult.SUCCESS;
            } else if (args.hasFlag("d")) {
                Flag days = args.getFlag("d");
                if (!days.isInteger()) {
                    return CommandResult.NOT_A_NUMBER;
                }
                int d = days.getInt();

                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(days.getString());
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }

                int min = d * 60 * 24;
                Punishment puNeu = new Punishment(target, creator, PunishmentType.MUTE, min, reason);
                PunishmentManager.savePunishment(puNeu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, puNeu));
                Messager.sendMessage(target, Config.MESSAGE_TARGET.replace(creator, puNeu), Messager.MessageType.NONE);
                return CommandResult.SUCCESS;
            } else if (args.getString(1).equalsIgnoreCase("forever")) {
                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(args.getString(1));
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }
                Punishment puNeu = new Punishment(target, creator, PunishmentType.MUTE, -1, reason);
                PunishmentManager.savePunishment(puNeu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, puNeu));
                Messager.sendMessage(target, Config.MESSAGE_TARGET.replace(creator, puNeu), Messager.MessageType.NONE);
                return CommandResult.SUCCESS;
            } else {
                String reason = "";

                List<String> reasonArguments = args.getArgsAfter(args.getString(0));
                for (String r : reasonArguments) {
                    reason += (r + " ");
                }

                Punishment pu = new Punishment(target, creator, PunishmentType.MUTE, reason);
                PunishmentManager.savePunishment(pu);
                sender.sendMessage(Config.MESSAGE_CREATOR.replace(target, pu));
                Messager.sendMessage(target, Config.MESSAGE_TARGET.replace(creator, pu), Messager.MessageType.NONE);
                return CommandResult.SUCCESS;
            }
        }
    }

}
