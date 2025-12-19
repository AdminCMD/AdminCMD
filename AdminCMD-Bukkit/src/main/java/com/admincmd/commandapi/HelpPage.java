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

import com.admincmd.utils.Locales;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public final class HelpPage {

    private final List<CommandHelp> helpPages = new ArrayList<>();
    private final List<String> HELP_TEXT = new ArrayList<>();
    private final String command;

    /**
     * Creates a new helppage for the provided command.
     *
     * @param command the command this HelpPage is for
     */
    public HelpPage(String command) {
        this.command = command;
    }

    /**
     * Automatically gets all help texts from the given arguments of the
     * {@link com.admincmd.utils.Locales} and prepares the message.
     *
     * @param command   the command this HelpPage is for
     * @param arguments the arguments to search for
     */
    public HelpPage(String command, String... arguments) {
        this.command = command;

        for (int i = 0; i < arguments.length; i++) {
            int num = i + 1;

            for (Locales loc : Locales.values()) {
                if (!loc.toString().toLowerCase().contains("help")) {
                    continue;
                }

                String[] l = loc.toString().toLowerCase().split("_");

                for (String s : l) {
                    if (!s.equalsIgnoreCase(command)) {
                        continue;
                    }
                    if (loc.toString().toLowerCase().contains("" + num)) {
                        if (arguments[i].isEmpty()) {
                            addPage(arguments[i], loc.getString());
                        } else {
                            addPage(" " + arguments[i], loc.getString());
                        }
                    }
                }

            }
        }

        prepare();
    }

    /**
     * Prepares the help message
     */
    public void prepare() {
        if (helpPages.isEmpty()) {
            return;
        }
        HELP_TEXT.add("§a------------------------§1" + Locales.HELP.getString() + "§a-------------------------");
        for (CommandHelp ch : helpPages) {
            HELP_TEXT.add("§6/" + ch.getText());
        }
        HELP_TEXT.add("§a-----------------------------------------------------");
    }

    /**
     * Manually add a message to a given argument.
     *
     * @param argument    the argument the message is for
     * @param description the message
     */
    public void addPage(String argument, String description) {
        helpPages.add(new CommandHelp(command + argument, description));
    }

    /**
     * Checks if the sender wants to see the help by typing /command help or
     * /command ? and sends the text if he did.
     *
     * @param s    the commandsender
     * @param args the arguments
     * @return true if helptext was sent
     */
    public boolean sendHelp(CommandSender s, CommandArgs args) {
        if (args.getLength() == 1 && (args.getString(0).equalsIgnoreCase("?") || args.getString(0).equalsIgnoreCase("help")) && !HELP_TEXT.isEmpty()) {
            for (String string : HELP_TEXT) {
                s.sendMessage(string);
            }
            return true;
        }
        return false;
    }

    private static class CommandHelp {

        private final String FULL_TEXT;

        public CommandHelp(String cmd, String description) {
            this.FULL_TEXT = "§6" + cmd + "§7" + " - " + description;
        }

        public String getText() {
            return FULL_TEXT;
        }

    }

}