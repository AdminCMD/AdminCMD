package com.admincmd.chestsigns;

import com.admincmd.addon.Addon;
import com.admincmd.chestsigns.inventorysigns.SignManager;
import com.admincmd.database.Database;
import com.admincmd.utils.ACLogger;
import com.admincmd.utils.Config;

import java.sql.SQLException;

public class ChestSigns extends Addon {

    @Override
    public void enable() {
        if (Config.BUNGEECORD.getBoolean()) {
            setEnabled(false);
            ACLogger.severe("Bungeecord enabled, Addon not compatible!");
            return;
        }
        createTable();
        SignManager.init();

        registerCommands("com.admincmd.chestsigns.commands");
        registerEventListener("com.admincmd.chestsigns.events");
    }

    @Override
    public void disable() {
    }


    private void createTable() {
        try {
            Database db = getDB();

            String TABLE;

            if (db.getType() == Database.Type.MYSQL) {
                TABLE = "CREATE TABLE IF NOT EXISTS ac_chestsigns ("
                        + "ID INTEGER PRIMARY KEY AUTO_INCREMENT,"
                        + "owner INTEGER NOT NULL,"
                        + "location VARCHAR(320)"
                        + ");";
            } else {
                TABLE = "CREATE TABLE IF NOT EXISTS ac_chestsigns ("
                        + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "owner INTEGER NOT NULL,"
                        + "location VARCHAR(320)"
                        + ");";
            }

            db.executeStatement(TABLE);
        } catch (SQLException ex) {
            ACLogger.severe("Error creating tables.", ex);
        }
    }

}
