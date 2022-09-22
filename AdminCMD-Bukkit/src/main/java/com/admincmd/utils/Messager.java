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
package com.admincmd.utils;

import com.admincmd.commandapi.CommandResult;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;

public class Messager {

    public static CommandResult sendMessage(ACPlayer s, String message, MessageType type) {
        PlayerManager.sendMessage(s, type.getPrefix() + message);
        return CommandResult.SUCCESS;
    }

    public static CommandResult sendMessage(ACPlayer s, Locales message, MessageType type) {
        PlayerManager.sendMessage(s, type.getPrefix() + message.getString());
        return CommandResult.SUCCESS;
    }

    public enum MessageType {
        INFO(Locales.MESSAGE_PREFIX_INFO.getString()),
        ERROR(Locales.MESSAGE_PREFIX_ERROR.getString()),
        NONE("");

        private final String prefix;

        private MessageType(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }
    }

}
