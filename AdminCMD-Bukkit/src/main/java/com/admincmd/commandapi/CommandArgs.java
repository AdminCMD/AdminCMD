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

import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.Config;
import com.admincmd.world.ACWorld;
import com.admincmd.world.WorldManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;

public class CommandArgs {

    private final Map<String, Flag> flags = new HashMap<>();
    private final List<String> args;

    protected CommandArgs(String[] args) {
        this.args = parseArgs(args);
    }

    protected CommandArgs(String[] args, int start) {
        String newArgs = "";
        for (int i = start; i < args.length; i++) {
            newArgs += args[i] + " ";
        }
        this.args = parseArgs(newArgs.split(" "));
    }

    private List<String> parseArgs(final String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            list.add(args[i]);
            String arg = args[i];
            if (arg.charAt(0) == '-' && arg.length() > 1 && arg.matches("-[a-zA-Z]")) {
                String character = arg.replaceFirst("-", "");
                try {
                    if (args[i + 1] != null) {
                        String value = args[i + 1];
                        Flag flag = new Flag(value, character);
                        flags.put(character, flag);
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
        }

        return list;
    }
    
    public List<String> getArgsAfter(String toCheck) {
        int toStart = 1 + args.indexOf(toCheck);
        return args.subList(toStart, args.size());
    }

    /**
     * Returns the flag by the given param
     *
     * @param flag the param
     * @return {@link com.admincmd.admincmd.commandapi.CommandArgs.Flag}
     */
    public Flag getFlag(final String flag) {
        return flags.get(flag);
    }

    /**
     * Checks if a flag was specified
     *
     * @param flag the flag to search for
     * @return true if flag was found
     */
    public boolean hasFlag(final String flag) {
        return flags.containsKey(flag);
    }

    /**
     * Parses an argument to a String
     *
     * @param index the position of the argument
     * @return the argument
     */
    public String getString(int index) {
        return args.get(index);
    }

    /**
     * Parses an argument to an integer
     *
     * @param index the position of the argument
     * @return the argument as int
     */
    public int getInt(int index) {
        return Integer.valueOf(args.get(index));
    }

    /**
     * parses an argument to a double
     *
     * @param index the position of the argument
     * @return the argument as double
     */
    public double getDouble(int index) {
        return Double.valueOf(args.get(index));
    }

    /**
     * Parses an argument to a Player on the server. Throws a
     * NullPointerException if the player is not online.
     *
     * @param index the position of the argument
     * @return the argument as Player
     */
    public ACPlayer getPlayer(int index) {
        return PlayerManager.getPlayer(args.get(index));
    }

    /**
     * Checks if the argument at the index is a Player on the server.
     *
     * @param index the position of the argument
     * @return true if player is found
     */
    public boolean isPlayerOnThisServer(int index) {
        return Bukkit.getPlayer(args.get(index)) != null;
    }

    public boolean isRegisteredPlayer(int index) {
        return PlayerManager.getPlayer(args.get(index)) != null;
    }

    public boolean isGameMode(int index) {
        try {
            if (isInteger(index)) {
                int num = getInt(index);
                GameMode gamemode = GameMode.getByValue(num);
                return true;
            } else {
                String gm = args.get(index);
                GameMode gamemode = GameMode.valueOf(gm.toUpperCase());
                return true;
            }
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public GameMode getGameMode(int index) {
        GameMode ret = null;
        try {
            if (isInteger(index)) {
                int num = getInt(index);
                ret = GameMode.getByValue(num);
            } else {
                String gm = args.get(index);
                ret = GameMode.valueOf(gm.toUpperCase());
            }
        } catch (IllegalArgumentException ex) {
            return ret;
        }

        return ret;
    }

    /**
     * Checks if the argument at the index is an integer
     *
     * @param index the position of the argument
     * @return true if it is a number
     */
    public boolean isInteger(int index) {
        try {
            Integer.valueOf(args.get(index));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the argument at the index is a double
     *
     * @param index the position of the argument
     * @return true if it is a number
     */
    public boolean isDouble(int index) {
        try {
            Double.valueOf(args.get(index));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks if the argument at the index is a loaded World
     *
     * @param index the position of the argument
     * @return true if it is a world name
     */
    public boolean isWorld(int index) {
        String servername;
        String worldname;
        if (args.get(index).contains(":") && Config.BUNGEECORD.getBoolean()) {
            String[] split = args.get(index).split(":");
            servername = split[0];
            worldname = split[1];
        } else {
            servername = BungeeCordMessageManager.getServerName();
            worldname = args.get(index);
        }

        return WorldManager.getWorld(worldname, servername) != null;
    }

    /**
     * Gets a world by name at the given index
     *
     * @param index the position of the argument
     * @return {@link com.admincmd.world.StoredWorld}
     */
    public ACWorld getWorld(int index) {
        String servername;
        String worldname;
        if (args.get(index).contains(":") && Config.BUNGEECORD.getBoolean()) {
            String[] split = args.get(index).split(":");
            servername = split[0];
            worldname = split[1];
        } else {
            servername = BungeeCordMessageManager.getServerName();
            worldname = args.get(index);
        }

        ACWorld ret = WorldManager.getWorld(worldname, servername);
        return ret;
    }

    /**
     * Checks if there are any arguments
     *
     * @return true if there are no arguments given
     */
    public boolean isEmpty() {
        return args.isEmpty();
    }

    /**
     * @return the size of the arguments
     */
    public int getLength() {
        return args.size();
    }

    /**
     * @return a List containing all arguments
     */
    public List<String> getArgs() {
        return args;
    }

    public class Flag {

        private final String value;
        private final String flag;

        protected Flag(String value, String flag) {
            this.value = value;
            this.flag = flag;
        }

        /**
         * @return the param of the flag.
         */
        public String getFlag() {
            return flag;
        }

        /**
         * @return the value of the flag
         */
        public String getString() {
            return value;
        }

        /**
         * parses the value to an Integer
         *
         * @return the value as Integer
         */
        public int getInt() {
            return Integer.valueOf(value);
        }

        /**
         * parses the value to a double
         *
         * @return the value as Double
         */
        public double getDouble() {
            return Double.valueOf(value);
        }

        /**
         * Checks if the value is an Integer
         *
         * @return true if it is an Integer
         */
        public boolean isInteger() {
            try {
                Integer.valueOf(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        /**
         * Checks if the value is a double
         *
         * @return true if it is a double
         */
        public boolean isDouble() {
            try {
                Double.valueOf(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        /**
         * Parses the value to a Player on the server.
         */
        public ACPlayer getPlayer() {
            return PlayerManager.getPlayer(value);
        }

        /**
         * Checks if there is a Player online with the given name.
         *
         * @return true if a player is online with the name.
         */
        public boolean isPlayerOnThisServer() {
            return Bukkit.getPlayer(value) != null;
        }

        public boolean isRegisteredPlayer() {
            return PlayerManager.getPlayer(value) != null;
        }

        public boolean isGameMode() {
            try {
                if (isInteger()) {
                    int num = getInt();
                    GameMode gamemode = GameMode.getByValue(num);
                    return true;
                } else {
                    String gm = getString();
                    GameMode gamemode = GameMode.valueOf(gm.toUpperCase());
                    return true;
                }
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }

        public GameMode getGameMode() {
            GameMode ret = null;
            try {
                if (isInteger()) {
                    int num = getInt();
                    ret = GameMode.getByValue(num);
                } else {
                    String gm = getString();
                    ret = GameMode.valueOf(gm.toUpperCase());
                }
            } catch (IllegalArgumentException ex) {
                return ret;
            }

            return ret;
        }

        /**
         * Checks if there is a world loaded with the value of the flag.
         *
         * @return true if a world is loaded.
         */
        public boolean isWorld() {
            String servername;
            String worldname;
            if (value.contains(":") && Config.BUNGEECORD.getBoolean()) {
                String[] split = value.split(":");
                servername = split[0];
                worldname = split[1];
            } else {
                servername = BungeeCordMessageManager.getServerName();
                worldname = value;
            }

            return WorldManager.getWorld(worldname, servername) != null;
        }

        /**
         * Parses the value to a world.
         *
         * @return {@link com.admincmd.world.StoredWorld}
         */
        public ACWorld getWorld() {
            String servername;
            String worldname;
            if (value.contains(":") && Config.BUNGEECORD.getBoolean()) {
                String[] split = value.split(":");
                servername = split[0];
                worldname = split[1];
            } else {
                servername = BungeeCordMessageManager.getServerName();
                worldname = value;
            }

            ACWorld ret = WorldManager.getWorld(worldname, servername);
            return ret;
        }

    }

}
