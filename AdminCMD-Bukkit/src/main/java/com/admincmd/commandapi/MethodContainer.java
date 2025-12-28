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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;

public record MethodContainer(HashMap<Sender, Method> methods, Method tabComplete) {

    Method getTabcomplete() {
        return tabComplete;
    }

    Method getMethod(Sender s) {
        return methods.get(s);
    }

    private Collection<Method> getMethods() {
        return methods.values();
    }

    HashMap<Sender, Method> getMethodMap() {
        return methods;
    }

    @Override
    public String toString() {
        String ret = "";
        for (Sender s : methods.keySet()) {
            Method m = methods.get(s);
            ret += ("Sender: " + s + " Method: " + m.getName() + " Class: " + m.getDeclaringClass().getName() + "\n");
        }
        return ret;
    }

}
