/*
 * This file is part of AdminCMD
 * Copyright (C) 2014 AdminCMD Team
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

import com.admincmd.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ACLogger {

    private static final Logger logger = Logger.getLogger("AdminCMD");
    private static final String PREFIX = "[AdminCMD] ";

    /**
     * Logs information to console
     *
     * @param message The message to log
     */
    public static void info(final String message) {
        logger.log(Level.INFO, PREFIX + message);
    }

    /**
     * Logs warnings to console
     *
     * @param message The warning to log
     */
    public static void warn(final String message) {
        logger.log(Level.WARNING, PREFIX + message);
    }

    /**
     * Logs errors to console
     *
     * @param message The error to log
     */
    public static void severe(final String message) {
        logger.log(Level.SEVERE, PREFIX + message);
    }

    /**
     * Logs errors and exceptions to console
     *
     * @param message The error to log
     * @param ex      The exception to log
     */
    public static void severe(final String message, final Throwable ex) {
        logger.log(Level.SEVERE, PREFIX + message, ex);
        printError(message, ex);
    }

    public static void severe(final Throwable ex) {
        logger.log(Level.SEVERE, "", ex);
        printError("", ex);
    }

    /**
     * Logs debug messages to console
     *
     * @param message The debug message to log
     */
    public static void debug(final String message) {
        if (!Config.DEBUG.getBoolean()) {
            return;
        }
        logger.log(Level.INFO, "[DEBUG] " + message);
        writeToDebug(message);
    }

    /**
     * Logs debug messages and exceptions to console
     *
     * @param message The debug message to log
     * @param ex      The exception to log
     */
    public static void debug(final String message, final Throwable ex) {
        if (!Config.DEBUG.getBoolean()) {
            return;
        }
        logger.log(Level.INFO, "[DEBUG] " + message);
        writeToDebug(message, ex);
    }

    private static String prefix() {
        DateFormat date = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss] ");
        Calendar cal = Calendar.getInstance();
        return date.format(cal.getTime());
    }

    private static void writeToDebug(final String message) {
        Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            BufferedWriter bw = null;
            File file = new File(Main.getInstance().getDataFolder(), "logs" + File.separator + "debugs");
            if (file.exists() || file.mkdirs()) {
                try {
                    DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String d = "Debug - " + date.format(cal.getTime());

                    bw = new BufferedWriter(new FileWriter(file + File.separator + d + ".log", true));
                    bw.write(prefix() + ":" + message);
                    bw.newLine();
                } catch (Exception ex) {
                    ACLogger.severe(ex);
                } finally {
                    try {
                        if (bw != null) {
                            bw.flush();
                            bw.close();
                        }
                    } catch (Exception ex) {
                        ACLogger.severe(ex);
                    }
                }
            }
        }, 0);
    }

    private static void writeToDebug(final String message, final Throwable t) {
        Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            BufferedWriter bw = null;
            File file = new File(Main.getInstance().getDataFolder(), "logs" + File.separator + "debugs");
            if (file.exists() || file.mkdirs()) {
                try {
                    DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String d = "Debug - " + date.format(cal.getTime());

                    bw = new BufferedWriter(new FileWriter(file + File.separator + d + ".log", true));
                    bw.newLine();
                    bw.newLine();
                    bw.write("///////////////////////////////////////////////////////////////////////////////");
                    bw.newLine();
                    bw.newLine();
                    bw.write(prefix() + ": An Exception happened!");
                    bw.newLine();
                    bw.write(prefix() + message);
                    bw.newLine();
                    bw.write(getStackTrace(t));
                    bw.newLine();
                    bw.newLine();
                    bw.write("///////////////////////////////////////////////////////////////////////////////");
                    bw.newLine();
                    bw.newLine();
                } catch (Exception ex) {
                    ACLogger.severe(ex);
                } finally {
                    try {
                        if (bw != null) {
                            bw.flush();
                            bw.close();
                        }
                    } catch (Exception ex) {
                        ACLogger.severe(ex);
                    }
                }
            }
        }, 0);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static void printError(final String message, final Throwable t) {
        Main.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            BufferedWriter bw = null;
            File file = new File(Main.getInstance().getDataFolder(), "logs" + File.separator + "errors");
            if (file.exists() || file.mkdirs()) {
                try {
                    DateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String d = "Errors - " + date.format(cal.getTime());

                    bw = new BufferedWriter(new FileWriter(file + File.separator + d + ".log", true));
                    bw.write(prefix() + ": An Exception happened!");
                    bw.newLine();
                    bw.write(prefix() + message);
                    bw.newLine();
                    bw.write(getStackTrace(t));
                    bw.newLine();
                    bw.newLine();
                    bw.write("///////////////////////////////////////////////////////////////////////////////");
                    bw.newLine();
                    bw.newLine();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (bw != null) {
                            bw.flush();
                            bw.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, 0);
    }

    private static String getStackTrace(Throwable t) {
        StringBuilder ret = new StringBuilder(prefix() + ": " + t + ": " + t.getMessage());

        StackTraceElement[] elements = t.getStackTrace();

        for (StackTraceElement element : elements) {
            ret.append("\n").append(prefix()).append(": ").append(element);
        }

        return ret.toString();
    }

}
