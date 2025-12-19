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
import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.communication.Channel;
import com.admincmd.communication.MessageCommand;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Config;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.world.ACWorld;
import com.admincmd.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;

@CommandHandler
public class KillallCommand {

    @BaseCommand(command = "killall", sender = Sender.PLAYER, permission = "admincmd.mob.killall", helpArguments = {"<-w world>", "<-t [monsters|animals|all]"})
    public CommandResult executeKillall(final Player sender, CommandArgs args) {
        ACWorld target;
        final ACPlayer acp = PlayerManager.getPlayer(sender);
        if (args.hasFlag("w")) {
            CommandArgs.Flag f = args.getFlag("w");
            if (!f.isWorld()) {
                return CommandResult.NOT_A_WORLD;
            }

            if (!sender.hasPermission("admincmd.mob.kill.other")) {
                return CommandResult.NO_PERMISSION_OTHER;
            }

            target = f.getWorld();
        } else {
            target = WorldManager.getWorld(sender.getWorld());
        }

        if (target == null) {
            return CommandResult.NOT_A_WORLD;
        }

        final MobType mobtype;

        if (args.hasFlag("t")) {
            CommandArgs.Flag f = args.getFlag("t");
            if (f.getString().equalsIgnoreCase("monsters")) {
                mobtype = MobType.MONSTER;
            } else {
                mobtype = MobType.ANIMAL;
            }
        } else {
            mobtype = MobType.ALL;
        }

        if (target.isOnThisServer()) {
            final World ta = Bukkit.getWorld(target.name());
            if (ta == null) {
                return CommandResult.NOT_A_WORLD;
            }

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                int killed = 0;
                for (Entity e : ta.getEntities()) {
                    if (e instanceof Player) {
                        continue;
                    }

                    if (mobtype != MobType.ALL) {
                        if (e instanceof Creature) {
                            if (e instanceof Monster l && mobtype == MobType.MONSTER) {
                                l.setHealth(0.0);
                                killed++;
                            } else if (e instanceof Animals l && mobtype == MobType.ANIMAL) {
                                l.setHealth(0.0);
                                killed++;
                            }
                        }
                    } else {
                        if (e instanceof LivingEntity l) {
                            l.setHealth(0.0);
                        } else {
                            e.remove();
                        }
                        killed++;
                    }
                }
                String msg = Locales.MOB_KILLALL.getString().replaceAll("%num%", killed + "").replaceAll("%world%", ta.getName());
                Messager.sendMessage(acp, msg, Messager.MessageType.INFO);
            });
            return CommandResult.SUCCESS;
        } else {
            String msg;
            if (Config.BUNGEECORD.getBoolean()) {
                msg = Locales.MOB_KILLALL.getString().replaceAll("%world%", target.server() + ":" + target.name()).replaceAll("%num%", "unknown");
            } else {
                msg = Locales.MOB_KILLALL.getString().replaceAll("%world%", target.name()).replaceAll("%num%", "unknown");
            }
            BungeeCordMessageManager.getInstance().sendMessage(PlayerManager.getPlayer(sender), Channel.KILL_MOBS, MessageCommand.FORWARD, target.server() + ":" + target.name() + ":" + mobtype);
            return Messager.sendMessage(acp, msg, Messager.MessageType.INFO);
        }
    }

    public enum MobType {
        MONSTER,
        ANIMAL,
        ALL
    }

}
