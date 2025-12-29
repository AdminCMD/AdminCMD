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
package com.admincmd.commandapi;

import com.admincmd.Main;
import com.admincmd.commandapi.CommandArgs.Flag;
import com.admincmd.home.HomeManager;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Locales;
import com.admincmd.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final HashMap<BaseCommand, MethodContainer> cmds = new HashMap<>();
    private final CommandMap cmap;
    private final JavaPlugin plugin;
    private final Logger logger;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        CommandMap map;
        try {
            final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            map = (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception ex) {
            map = null;
            logger.log(Level.SEVERE, "", ex);
        }

        cmap = map;
    }

    private void registerCommand(String name, List<String> aliases) {
        if (cmap.getCommand(name) != null) {
            return;
        }
        BukkitCommand cmd = new BukkitCommand(name, aliases);

        cmap.register(plugin.getName().toLowerCase(), cmd);
        cmd.setExecutor(this);
    }

    private BaseCommand getCommand(Command c, CommandArgs args, Sender sender) {
        BaseCommand ret = null;
        for (BaseCommand bc : cmds.keySet()) {
            if (!bc.command().equalsIgnoreCase(c.getName())) {
                continue;
            }
            if (bc.sender() == Sender.ALL || bc.sender() == sender) {
                if (bc.subCommand().isBlank() || args.isEmpty() || bc.subCommand().equalsIgnoreCase(args.getString(0))) {
                    ret = bc;
                }
            }
        }
        return ret;
    }

    private Object getCommandObject(Command c, Sender sender, CommandArgs args, BaseCommand bcmd) throws Exception {
        MethodContainer container = cmds.get(bcmd);
        Method me = container.getMethod(bcmd.sender());
        return me.getDeclaringClass().getDeclaredConstructor().newInstance();
    }

    /**
     * Use this to tell the system that there are commands in the class!
     *
     * @param clazz the classfile where your command /-s are stored.
     */
    public void registerClass(final Class<?> clazz) {
        if (!clazz.isAnnotationPresent(CommandHandler.class)) {
            plugin.getLogger().severe("Class is no CommandHandler");
            return;
        }

        ACLogger.debug("Registering Command: " + clazz.getName());

        HashMap<BaseCommand, HashMap<Sender, Method>> list = new HashMap<>();
        Method tabCompleteMethod = null;

        for (Method m : clazz.getDeclaredMethods()) {
            Class<?>[] paramTypes = m.getParameterTypes();
            if (m.isAnnotationPresent(BaseCommand.class)
                    && (m.getReturnType() == CommandResult.class)
                    && (paramTypes.length == 2
                    && (paramTypes[0] == CommandSender.class || CommandSender.class.isAssignableFrom(paramTypes[0]))
                    && (paramTypes[1] == CommandArgs.class))) {
                ACLogger.debug(m.getName() + " Params: " + m.getParameterTypes() + " is valid! Trying to register as command!");
                BaseCommand bc = m.getAnnotation(BaseCommand.class);

                List<String> aliases = new ArrayList<>();
                if (bc.aliases().contains(",")) {
                    aliases.addAll(Arrays.asList(bc.aliases().split(",")));
                } else if (!bc.aliases().isEmpty()) {
                    aliases.add(bc.aliases());
                }

                registerCommand(bc.command(), aliases);
                HashMap<Sender, Method> map = list.containsKey(bc) ? list.get(bc) : new HashMap<>();
                map.put(bc.sender(), m);
                list.put(bc, map);
            } else if (m.isAnnotationPresent(TabComplete.class)
                    && tabCompleteMethod == null
                    && m.getReturnType() == List.class
                    && (paramTypes.length == 3 && (paramTypes[0] == CommandSender.class || paramTypes[0].isAssignableFrom(CommandSender.class))
                    && paramTypes[1] == CommandArgs.class
                    && paramTypes[2] == List.class)) {
                ACLogger.debug(m.getName() + " Params: " + m.getParameterTypes() + " is valid! Trying to register as tabcomplete!");
                tabCompleteMethod = m;
            } else {
                ACLogger.debug(m.getName() + " Params: " + m.getParameterTypes() + " is not valid! Skipping!");
            }
        }

        for (BaseCommand command : list.keySet()) {
            HashMap<Sender, Method> map = list.get(command);

            if (cmds.containsKey(command)) {
                MethodContainer container = cmds.get(command);
                for (Sender s : container.getMethodMap().keySet()) {
                    Method m = container.getMethod(s);
                    map.put(s, m);
                }
            }
            cmds.put(command, new MethodContainer(map, tabCompleteMethod));
        }
    }

    private Method getMethod(Command c, Sender sender, CommandArgs args) {
        BaseCommand bcmd = getCommand(c, args, sender);
        ACLogger.debug("BaseCommand with Sender " + sender + " " + (bcmd == null ? "not found" : "found"));
        MethodContainer container = cmds.get(bcmd);
        if (container == null) {
            return null;
        }
        ACLogger.debug("MethodContainer was not null! " + container.toString());
        Method m = container.getMethod(sender);
        try {
            return m;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "", ex);
            return null;
        }
    }

    private Method getTabCompleteMethod(Command c, Sender sender, CommandArgs args) {
        BaseCommand bcmd = getCommand(c, args, sender);
        MethodContainer container = cmds.get(bcmd);
        if (container == null) {
            return null;
        }
        Method m = container.getTabcomplete();
        try {
            return m;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "", ex);
            return null;
        }
    }

    private void runCommand(BaseCommand bc, Method m, Sender sender, CommandSender s, Command c, CommandArgs a) {
        ACLogger.debug("RunCommand! Sender: " + sender + " Method: " + m.getName());
        CommandResult cr;
        try {
            Object command = getCommandObject(c, sender, a, bc);
            if (bc.permission() != null && !bc.permission().trim().isBlank()) {
                if (!s.hasPermission(bc.permission())) {
                    cr = CommandResult.NO_PERMISSION;
                } else {
                    if (a.hasFlag("p")) {
                        if (!s.hasPermission(bc.permission() + ".other")) {
                            cr = CommandResult.NO_PERMISSION_OTHER;
                        } else if (!a.getFlag("p").isRegisteredPlayer()) {
                            cr = CommandResult.NOT_A_PLAYER;
                        } else {
                            cr = (CommandResult) m.invoke(command, s, a);
                        }
                    } else {
                        cr = (CommandResult) m.invoke(command, s, a);
                    }
                }
            } else {
                cr = (CommandResult) m.invoke(command, s, a);
            }
        } catch (Exception e) {
            ACLogger.severe(e);
            cr = CommandResult.SUCCESS;
        }

        if (cr != null && cr.getMessage() != null) {
            String perm = bc.permission() != null ? bc.permission() : "";
            if (!perm.isBlank() && a.hasFlag("p")) {
                perm = perm + ".other";
            }
            s.sendMessage(cr.getMessage().replace("%cmd%", bc.command()).replace("%perm%", perm));
        }
    }

    private void executeCommand(Command c, CommandSender s, String[] args) {
        CommandArgs a = new CommandArgs(args);
        Sender sender;
        if (s instanceof Player) {
            sender = Sender.PLAYER;
        } else if (s instanceof CommandBlock || s instanceof BlockCommandSender) {
            sender = Sender.COMMANDBLOCK;
        } else if (s instanceof CommandMinecart) {
            sender = Sender.MINECART;
        } else if (s instanceof ConsoleCommandSender) {
            sender = Sender.CONSOLE;
        } else {
            sender = Sender.OTHER;
        }

        ACLogger.debug("Command " + c.getName() + " ran! Trying to find Method for Sender " + sender);
        Method m = getMethod(c, sender, a);
        if (m == null) {
            ACLogger.debug("Method in First try not found! Trying with Sender.ALL...");
            m = getMethod(c, Sender.ALL, a);
        }
        ACLogger.debug("Method " + (m == null ? "not found" : ("found! " + m.getName())));

        if (m != null) {
            ACLogger.debug("Method was not null! Executing...");
            m.setAccessible(true);

            BaseCommand bc = m.getAnnotation(BaseCommand.class);
            if (!bc.subCommand().trim().isBlank() && bc.subCommand().equalsIgnoreCase(a.getString(0))) {
                a = new CommandArgs(args, 1);
            }

            HelpPage help = new HelpPage(bc.command(), bc.helpArguments());
            if (!bc.helpArguments()[0].equalsIgnoreCase("addon")) {
                if (help.sendHelp(s, a)) {
                    return;
                }
            }

            Method finalM = m;
            CommandArgs finalA = a;

            if (!bc.async()) {
                runCommand(bc, finalM, sender, s, c, finalA);
            } else {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        runCommand(bc, finalM, sender, s, c, finalA);
                    }
                }.runTaskAsynchronously(Main.getInstance());
            }
        } else {
            s.sendMessage(Locales.COMMAND_MESSAGES_WRONG_SENDER_TYPE.getString());
        }
    }

    /*
     * Bukkit method, just ignore it.
     * Commands will be executed by itself.
     */
    @Override
    public boolean onCommand(final @NotNull CommandSender cs, final @NotNull Command cmnd, final @NotNull String string, final String[] strings) {
        executeCommand(cmnd, cs, strings);
        return true;
    }

    @Override
    public @NotNull
    List<String> onTabComplete(@NotNull CommandSender cs, @NotNull Command cmnd, @NotNull String commandLabel, String[] strings) {
        Sender sender;
        if (cs instanceof Player) {
            sender = Sender.PLAYER;
        } else if (cs instanceof CommandBlock || cs instanceof BlockCommandSender) {
            sender = Sender.COMMANDBLOCK;
        } else if (cs instanceof CommandMinecart) {
            sender = Sender.MINECART;
        } else if (cs instanceof ConsoleCommandSender) {
            sender = Sender.CONSOLE;
        } else {
            sender = Sender.OTHER;
        }
        CommandArgs a = new CommandArgs(strings);
        List<String> ret = new ArrayList<>();

        if (strings.length == 1 && strings[0].isBlank()) {
            ret.add("help");
        }

        if (a.hasFlag("p")) {
            Flag f = a.getFlag("p");
            if (f.getString() == null || f.getString().isBlank()) {
                for (ACPlayer p : PlayerManager.getALLPlayers()) {
                    ret.add(p.getName());
                }
            }

        }

        if (a.hasFlag("w")) {
            Flag f = a.getFlag("w");
            if (f.getString() == null || f.getString().isBlank()) {
                ret.addAll(WorldManager.getWorldNames());
            }
        }

        Method tabCompleteMethod = getTabCompleteMethod(cmnd, sender, a);
        if (tabCompleteMethod == null) {
            tabCompleteMethod = getTabCompleteMethod(cmnd, Sender.ALL, a);
        }

        if (tabCompleteMethod != null) {
            try {
                tabCompleteMethod.setAccessible(true);
                ret = (List<String>) tabCompleteMethod.invoke(tabCompleteMethod.getDeclaringClass().getDeclaredConstructor().newInstance(), cs, a, ret);

                BaseCommand bcmd = getCommand(cmnd, a, sender);
                HelpPage help = new HelpPage(bcmd.command(), bcmd.helpArguments());
                //TODO: AutoAdd Help stuff
            } catch (Exception e) {
                ACLogger.severe(e);
            }
        }
        return ret;
    }
}
