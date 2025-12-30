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
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Locales;
import com.admincmd.world.WorldManager;
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

    private final HashMap<String, MethodContainer> cmds = new HashMap<>();
    private final CommandMap cmap;
    private final JavaPlugin plugin;
    private final Logger logger;

    public CommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        CommandMap map;
        try {
            final Field f = plugin.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            map = (CommandMap) f.get(plugin.getServer());
        } catch (Exception ex) {
            map = null;
            logger.log(Level.SEVERE, "", ex);
        }

        cmap = map;
    }

    private void registerCommand(BaseCommand bcmd) {
        if (cmap.getCommand(bcmd.command()) != null) {
            return;
        }
        BukkitCommand cmd = new BukkitCommand(bcmd.command(), bcmd.aliases(), this);
        cmap.register(plugin.getName().toLowerCase(), cmd);
    }

    private Object getCommandObject(Method m) throws Exception {
        return m.getDeclaringClass().getDeclaredConstructor().newInstance();
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

        ACLogger.debug("Registering Command: " + clazz.getName() + " Already registered commands: " + cmds.size());
        HashMap<String, HashMap<Sender, Method>> list = new HashMap<>();
        Method tabCompleteMethod = null;

        for (Method m : clazz.getDeclaredMethods()) {
            m.setAccessible(true);
            if (isBaseCommandMethod(m)) {
                ACLogger.debug(m.getName() + " Params: " + Arrays.toString(m.getParameterTypes()) + " is valid! Trying to register as command!");
                BaseCommand bc = m.getAnnotation(BaseCommand.class);
                registerCommand(bc);

                HashMap<Sender, Method> map = (list.containsKey(bc.command()) ? list.get(bc.command()) : new HashMap<>());
                map.put(bc.sender(), m);

                list.put(bc.command(), map);
            } else if (isTabCompleteMethod(m)) {
                ACLogger.debug(m.getName() + " Params: " + Arrays.toString(m.getParameterTypes()) + " is valid! Trying to register as tabcomplete!");
                tabCompleteMethod = m;
            } else {
                ACLogger.debug(m.getName() + " Params: " + Arrays.toString(m.getParameterTypes()) + " is not valid! Skipping!");
            }
        }

        for (String command : list.keySet()) {
            ACLogger.debug("Registering command " + command);
            HashMap<Sender, Method> map = list.get(command);
            ACLogger.debug("Methods found for Command: " + map.toString());

            if (cmds.containsKey(command)) {
                ACLogger.debug("Map already contains a key for the command " + command);
                MethodContainer container = cmds.get(command);
                for (Sender s : container.getMethodMap().keySet()) {
                    Method m = container.getMethod(s);
                    map.put(s, m);
                }
            }
            cmds.put(command, new MethodContainer(map, tabCompleteMethod));
        }
    }

    private Method getMethod(Command c, Sender sender) {
        if (!cmds.containsKey(c.getName())) {
            ACLogger.debug("Cmds does not contain a key for " + c.getName() + " ! " + cmds.toString());
            return null;
        }
        MethodContainer container = cmds.get(c.getName());
        ACLogger.debug("MethodContainer was not null! " + container.toString());
        Method m = container.methods().containsKey(sender) ? container.getMethod(sender) : container.getMethod(Sender.ALL);
        return m;
    }

    private Method getTabCompleteMethod(Command c) {
        if (!cmds.containsKey(c.getName())) {
            return null;
        }
        MethodContainer container = cmds.get(c.getName());
        Method m = container.getTabcomplete();
        return m;
    }

    private void runCommand(Method m, CommandSender s, CommandArgs a) {
        ACLogger.debug("RunCommand! Method: " + m.getName());
        BaseCommand bc = m.getAnnotation(BaseCommand.class);
        CommandResult cr;
        try {
            Object command = getCommandObject(m);
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

    /*
     * Bukkit method, just ignore it.
     * Commands will be executed by itself.
     */
    @Override
    public boolean onCommand(final CommandSender s, final Command c, final String string, final String[] args) {
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
        Method m = getMethod(c, sender);
        ACLogger.debug("Method " + (m == null ? "not found" : ("found! " + m.getName())));

        if (m != null) {
            ACLogger.debug("Method was not null! Executing...");

            BaseCommand bc = m.getAnnotation(BaseCommand.class);
            if (!bc.subCommand().trim().isBlank() && bc.subCommand().equalsIgnoreCase(a.getString(0))) {
                a = new CommandArgs(args, 1);
            }

            HelpPage help = new HelpPage(bc.command(), bc.helpArguments());
            if (!bc.helpArguments()[0].equalsIgnoreCase("addon")) {
                if (help.sendHelp(s, a)) {
                    return true;
                }
            }

            if (!bc.async()) {
                runCommand(m, s, a);
            } else {
                final Method finalM = m;
                final CommandArgs finalA = a;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        runCommand(finalM, s, finalA);
                    }
                }.runTaskAsynchronously(Main.getInstance());
            }
        } else {
            s.sendMessage(Locales.COMMAND_MESSAGES_WRONG_SENDER_TYPE.getString());
        }
        return true;
    }

    @Override
    public @NotNull
    List<String> onTabComplete(CommandSender cs, Command cmnd, String commandLabel, String[] strings) {
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

        Method tabCompleteMethod = getTabCompleteMethod(cmnd);
        if (tabCompleteMethod == null) {
            tabCompleteMethod = getTabCompleteMethod(cmnd);
        }

        if (tabCompleteMethod != null) {
            try {
                tabCompleteMethod.setAccessible(true);
                ret = (List<String>) tabCompleteMethod.invoke(tabCompleteMethod.getDeclaringClass().getDeclaredConstructor().newInstance(), cs, a, ret);
            } catch (Exception e) {
                ACLogger.severe(e);
            }
        }
        return ret;
    }

    private final boolean isBaseCommandMethod(Method m) {
        Class<?>[] paramTypes = m.getParameterTypes();
        return m.isAnnotationPresent(BaseCommand.class)
                && (m.getReturnType() == CommandResult.class)
                && (m.getParameterCount() == 2
                && (paramTypes[0] == CommandSender.class || CommandSender.class.isAssignableFrom(paramTypes[0]))
                && (paramTypes[1] == CommandArgs.class));
    }

    private final boolean isTabCompleteMethod(Method m) {
        Class<?>[] paramTypes = m.getParameterTypes();
        return (m.isAnnotationPresent(TabComplete.class)
                && m.getReturnType() == List.class
                && m.getParameterCount() == 3
                && (paramTypes[0] == CommandSender.class || CommandSender.class.isAssignableFrom(paramTypes[0]))
                && paramTypes[1] == CommandArgs.class
                && paramTypes[2] == List.class);
    }
}
