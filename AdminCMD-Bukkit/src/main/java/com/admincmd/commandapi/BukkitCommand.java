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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BukkitCommand extends Command {

    private CommandManager exe = null;
    private final List<String> aliases;

    protected BukkitCommand(String name, List<String> aliases) {
        super(name);
        this.aliases = aliases;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (exe != null) {
            exe.onCommand(sender, this, commandLabel, args);
        }
        return false;
    }

    public void setExecutor(CommandManager exe) {
        this.exe = exe;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alais, String[] args) {
        if (this.exe != null) {
            return exe.onTabComplete(sender, this, alais, args);
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

}
