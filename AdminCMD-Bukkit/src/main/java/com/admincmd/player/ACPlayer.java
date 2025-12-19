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
package com.admincmd.player;

import com.admincmd.utils.MultiServerLocation;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface ACPlayer {

    int getID();

    MultiServerLocation getLocation();

    Player getPlayer();

    OfflinePlayer getOfflinePlayer();

    String getName();

    UUID getUUID();

    boolean isFly();

    void setFly(boolean fly);

    boolean isGod();

    void setGod(boolean god);

    boolean isFreezed();

    void setFreezed(boolean freeze);

    boolean isInvisible();

    void setInvisible(boolean inv);

    boolean isCMDWatcher();

    void setCMDWatcher(boolean cmdwatch);

    boolean isSpy();

    void setSpy(boolean spy);

    int getLastMSGFrom();

    void setLastMSGFrom(int userID);

    boolean hasLastLoc();

    MultiServerLocation getLastLoc();

    void setLastLoc(MultiServerLocation loc);

    boolean isOnline();

    String getServer();
}
