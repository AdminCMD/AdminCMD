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

public enum CommandResult {

    /**
     * Command was executed successful
     */
    SUCCESS(null),
    /**
     * The user has not the permission to execute the command
     */
    NO_PERMISSION(Locales.COMMAND_MESSAGES_NO_PERMISSION.getString()),
    /**
     * The user has not the permission to execute this command for other
     * players.
     */
    NO_PERMISSION_OTHER(Locales.COMMAND_MESSAGES_NO_PERMISSION_OTHER.getString()),
    /**
     * The sender has used the command wrong. Sends help message
     */
    ERROR(Locales.COMMAND_MESSAGES_WRONG_USAGE.getString()),
    /**
     * Tells the sender that the given player in the arguments is not online
     */
    NOT_ONLINE(Locales.COMMAND_MESSAGES_NOT_ONLINE.getString()),
    /**
     * Tells the sender that the given String is not a player
     */
    NOT_A_PLAYER(Locales.COMMAND_MESSAGES_NOT_A_PLAYER.getString()),
    /**
     * Tells the sender that the given argument has to be a number
     */
    NOT_A_NUMBER(Locales.COMMAND_MESSAGES_NOT_A_NUMBER.getString()),
    /**
     * Tells the sender that the given argument has to be a world
     */
    NOT_A_WORLD(Locales.COMMAND_MESSAGES_NOT_A_WORLD.getString()),
    /**
     * Tells the sender that the given argument is not a mobname
     */
    NOT_A_MOB(Locales.COMMAND_MESSAGES_NOT_A_MOB.getString()),

    NOT_A_GAMEMODE(Locales.COMMAND_MESSAGES_NOT_A_GAMEMODE.getString()),
    /**
     * Tells the sender that the given mob cannot be spawned
     */
    NOT_SPAWNABLE(Locales.COMMAND_MESSAGES_NOT_SPAWNABLE.getString()),
    /**
     * Tells the sender that there is no free space for him to teleport to a location.
     */
    NO_SPACE(Locales.COMMAND_MESSAGES_NO_FREE_SPACE.getString()),

    /**
     * Tells the sender that this Command is not made for his sender type
     */
    WRONG_SENDER(Locales.COMMAND_MESSAGES_WRONG_SENDER_TYPE.getString()),

    NOT_YOURSELF(Locales.COMMAND_MESSAGES_SELF_PLAYER.getString());

    private final String msg;

    private CommandResult(String msg) {
        this.msg = msg;
    }

    protected String getMessage() {
        return this.msg;
    }
}
