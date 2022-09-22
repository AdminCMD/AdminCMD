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
package com.admincmd.bungeecord.maintenance;

import net.md_5.bungee.api.chat.TextComponent;

public class MotD {

    private final String line1, line2;

    public MotD(String line1, String line2) {
        this.line1 = line1;
        this.line2 = line2;
    }

    public TextComponent getMotd() {
        TextComponent ret = new TextComponent((line1 + "\n" + line2).replaceAll("&((?i)[0-9a-fk-or])", "§$1"));
        return ret;
    }

}
