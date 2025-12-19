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
package com.admincmd.database;

import com.admincmd.utils.ACLogger;
import org.intellij.lang.annotations.MagicConstant;

import java.sql.*;

public abstract class Database {

    private final Type type;
    private Connection conn = null;

    public Database(String driver, Type type) {
        this.type = type;
        try {
            Class<?> d = Class.forName(driver);
            Object o = d.getDeclaredConstructor().newInstance();
            if (!(o instanceof Driver dr)) {
                ACLogger.severe("Driver is not an instance of the Driver class!");
            } else {
                DriverManager.registerDriver(dr);
            }
        } catch (Exception ex) {
            ACLogger.severe("Driver not found! " + driver, ex);
        }
    }

    /**
     * Gets the type of the loaded database.
     *
     * @return {@link com.admincmd.database.Database.Type}
     */
    public Type getType() {
        return type;
    }

    /**
     * Tests the database connection.
     *
     * @return true if connection was successful
     */
    public final boolean testConnection() {
        try {
            getConnection();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Gets the connection
     *
     * @return the database connection
     * @throws SQLException if connection failed
     */
    public final Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            reactivateConnection();
        }
        return conn;
    }

    /**
     * Used to reactivate the connection
     *
     * @param conn if connection failed
     */
    public final void setConnection(Connection conn) {
        this.conn = conn;
    }

    /**
     * closes the actual connection to the database
     *
     * @throws SQLException if connection can't be closed.
     */
    public final void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    /**
     * closes a statement
     *
     * @param s the statement to close
     * @throws SQLException if the statement can't be closed
     */
    public final void closeStatement(Statement s) throws SQLException {
        if (s != null) {
            s.close();
        }

    }

    /**
     * closes a resultset
     *
     * @param rs the resultset to close
     * @throws SQLException if the ResultSet can't be closed
     */
    public final void closeResultSet(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * creates a new statement
     *
     * @return The created statement
     * @throws SQLException if Statement can't be created
     */
    public final Statement getStatement() throws SQLException {
        return getConnection().createStatement();
    }

    /**
     * creates a new PreparedStatement
     *
     * @param query The query for the prepared statement
     * @return the PreparedStatement
     * @throws SQLException if the Statement can't be created.
     */
    public final PreparedStatement getPreparedStatement(String query) throws SQLException {
        return getConnection().prepareStatement(query);
    }

    /**
     * Creates a new PreparedStatement
     *
     * @param query   The SQL Query
     * @param options Either Statement.RETURN_GENERATED_KEYS or Statement.NO_GENERATED_KEYS
     * @return PreparedStatement
     */
    public final PreparedStatement getPreparedStatement(String query, @MagicConstant(intValues = {Statement.RETURN_GENERATED_KEYS, Statement.NO_GENERATED_KEYS}) int options) throws SQLException {
        return getConnection().prepareStatement(query, options);
    }

    /**
     * Creates a new Statement and executes it
     *
     * @param query the query to execute
     * @throws SQLException if statement can't be executed
     */
    public final void executeStatement(String query) throws SQLException {
        Statement s = getStatement();
        s.execute(query);
        closeStatement(s);
    }

    /**
     * Reactivates the connection to the Database.
     *
     * @throws SQLException if connection failed
     */
    public abstract void reactivateConnection() throws SQLException;

    public enum Type {

        SQLITE,
        MYSQL
    }

}
