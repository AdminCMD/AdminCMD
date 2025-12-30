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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.List;

public class BukkitCommand extends Command {

    private final List<String> aliases = new ArrayList<>();
    private final CommandManager cmdManager;

    protected BukkitCommand(String name, String[] aliasArr, CommandManager cmdManager) {
        super(name);
        for (String alias : aliasArr) {
            aliases.add(alias);
        }
        this.cmdManager = cmdManager;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return cmdManager.onCommand(sender, this, commandLabel, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return cmdManager.onTabComplete(sender, this, alias, args);
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

}
