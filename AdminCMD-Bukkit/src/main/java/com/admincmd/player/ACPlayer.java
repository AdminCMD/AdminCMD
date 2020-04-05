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

public interface ACPlayer {

    public int getID();

    public MultiServerLocation getLocation();

    public Player getPlayer();

    public OfflinePlayer getOfflinePlayer();

    public String getName();

    public boolean isFly();

    public boolean isGod();

    public boolean isFreezed();

    public boolean isInvisible();

    public boolean isCMDWatcher();

    public boolean isSpy();

    public int getLastMSGFrom();

    public void setFly(boolean fly);

    public void setGod(boolean god);

    public void setFreezed(boolean freeze);

    public void setInvisible(boolean inv);

    public void setCMDWatcher(boolean cmdwatch);

    public void setSpy(boolean spy);

    public void setLastMSGFrom(int userID);

    public boolean hasLastLoc();

    public MultiServerLocation getLastLoc();
    
    public void setLastLoc(MultiServerLocation loc);
    
    public boolean isOnline();
    
    public String getServer();
}
