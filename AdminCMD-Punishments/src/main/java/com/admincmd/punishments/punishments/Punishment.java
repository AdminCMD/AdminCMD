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
package com.admincmd.punishments.punishments;

import com.admincmd.player.ACPlayer;
import com.admincmd.punishments.Punishments;
import com.admincmd.punishments.utils.Config;

@SuppressWarnings("ClassCanBeRecord")
public class Punishment {

    private final ACPlayer target, creator;
    private final PunishmentType type;
    private final long expiresAt;
    private final int id;
    private final String reason;

    protected Punishment(ACPlayer target, ACPlayer creator, PunishmentType type, long expiresAt, int ID, String reason) {
        this.target = target;
        this.creator = creator;
        this.type = type;
        this.expiresAt = expiresAt;
        this.id = ID;
        this.reason = reason;
    }


    public Punishment(ACPlayer target, ACPlayer creator, PunishmentType type) {
        this.target = target;
        this.creator = creator;
        this.type = type;
        this.expiresAt = (System.currentTimeMillis() + Punishments.minutesInMiliseconds(Config.STANDARD_TIME.getInteger()));
        this.id = -1;
        this.reason = Config.STANDARD_REASON.getString();
    }


    public Punishment(ACPlayer target, ACPlayer creator, PunishmentType type, String reason) {
        this.target = target;
        this.creator = creator;
        this.type = type;
        this.expiresAt = (System.currentTimeMillis() + Punishments.minutesInMiliseconds(Config.STANDARD_TIME.getInteger()));
        this.id = -1;
        this.reason = reason;
    }


    public Punishment(ACPlayer target, ACPlayer creator, PunishmentType type, int minutes) {
        this.target = target;
        this.creator = creator;
        this.type = type;
        if (minutes > 0) {
            this.expiresAt = (System.currentTimeMillis() + Punishments.minutesInMiliseconds(minutes));
        } else {
            this.expiresAt = -1;
        }
        this.id = -1;
        this.reason = Config.STANDARD_REASON.getString();
    }


    public Punishment(ACPlayer target, ACPlayer creator, PunishmentType type, int minutes, String reason) {
        this.target = target;
        this.creator = creator;
        this.type = type;
        if (minutes > 0) {
            this.expiresAt = (System.currentTimeMillis() + Punishments.minutesInMiliseconds(minutes));
        } else {
            this.expiresAt = -1;
        }
        this.id = -1;
        this.reason = reason;
    }

    public ACPlayer getTarget() {
        return target;
    }

    public int getID() {
        return id;
    }

    public ACPlayer getCreator() {
        return creator;
    }

    public String getReason() {
        return this.reason;
    }

    public PunishmentType getType() {
        return type;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        if (!isForever()) {
            return expiresAt < System.currentTimeMillis();
        } else {
            return false;
        }
    }

    public String getReadableTime() {
        return Punishments.transformReadable(expiresAt);
    }

    public boolean isForever() {
        return expiresAt == -1;
    }

    public String getReasonMessage() {
        if (type != PunishmentType.KICK) {
            return Config.MESSAGE_TARGET.replace(creator, this);
        } else {
            return Config.MESSAGE_TARGET_KICK.replace(creator, this);
        }
    }
}
