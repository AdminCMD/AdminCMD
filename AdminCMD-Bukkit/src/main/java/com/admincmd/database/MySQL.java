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

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {

    private final String host, user, password, dbName;
    private final int port;

    /**
     * Creates a new instance for MySQL databases.
     * Statements are done by {@link com.admincmd.admincmd.database.MySQL#executeStatement(java.lang.String) }
     *
     * @param host the host where the mysql server is on.
     * @param user the username of the database-account
     * @param password the password of the suer for the database account
     * @param dbName the name of the database
     * @param port the port of the database server
     */
    public MySQL(String host, String user, String password, String dbName, int port) {
        super("com.mysql.jdbc.Driver", Type.MYSQL);
        this.host = host;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
        this.port = port;
    }

    @Override
    public void reactivateConnection() throws SQLException {
        String dsn = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        setConnection(DriverManager.getConnection(dsn, user, password));
    }

}
