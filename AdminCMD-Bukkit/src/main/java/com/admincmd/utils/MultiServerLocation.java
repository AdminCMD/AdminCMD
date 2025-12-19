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

import com.admincmd.communication.BungeeCordMessageManager;
import com.admincmd.world.ACWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MultiServerLocation {

    private double x, y, z;
    private float pitch, yaw;
    private String worldname;
    private String severname;

    public MultiServerLocation(double x, double y, double z, float pitch, float yaw, String worldname, String severname) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.worldname = worldname;
        this.severname = severname;
    }

    public MultiServerLocation(double x, double y, double z, ACWorld world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = 0;
        this.yaw = 0;
        this.worldname = world.name();
        this.severname = world.server();
    }

    public static String serialLocation(MultiServerLocation loc) {
        int pitch = Integer.parseInt(String.valueOf(loc.getPitch()).split("\\.")[0]);
        int yaw = Integer.parseInt(String.valueOf(loc.getYaw()).split("\\.")[0]);
        return loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getWorldname() + ";" + yaw + ";" + pitch + ";" + loc.getServername();
    }

    public static MultiServerLocation fromString(String s) {
        String[] a = s.split(";");
        double x = Double.parseDouble(a[0]);
        double y = Double.parseDouble(a[1]);
        double z = Double.parseDouble(a[2]);
        String world = a[3];
        int yaw = Integer.parseInt(a[4]);
        int pitch = Integer.parseInt(a[5]);
        String servername = a[6];
        return new MultiServerLocation(x, y, z, pitch, yaw, world, servername);
    }

    public static MultiServerLocation fromLocation(Location loc) {
        if (loc != null && loc.getWorld() != null) {
            return new MultiServerLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw(), loc.getWorld().getName(), BungeeCordMessageManager.getServerName());
        } else {
            return null;
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public String getWorldname() {
        return worldname;
    }

    public void setWorldname(String worldname) {
        this.worldname = worldname;
    }

    public String getServername() {
        return severname;
    }

    public void setServername(String severname) {
        this.severname = severname;
    }

    public Location getBukkitLocation() {
        return new Location(Bukkit.getWorld(worldname), x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return serialLocation(this);
    }

    public boolean isOnThisServer() {
        return this.severname.equalsIgnoreCase(BungeeCordMessageManager.getServerName());
    }

    public boolean compareTo(MultiServerLocation loc) {
        return loc.pitch == this.pitch && loc.yaw == this.yaw && loc.severname.equals(this.severname) && loc.worldname.equals(this.worldname) && loc.x == this.x && loc.y == this.y && loc.z == this.z;
    }

}
