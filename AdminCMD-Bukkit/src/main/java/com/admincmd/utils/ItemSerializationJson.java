package com.admincmd.utils;

import com.google.gson.*;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.Map;
import org.bukkit.configuration.InvalidConfigurationException;

public final class ItemSerializationJson {

    private static final int CURRENT_VERSION = 2;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /* =========================
       SAVE (JSON)
       ========================= */
    public static String saveInventory(Inventory inventory) {
        JsonObject root = new JsonObject();
        root.addProperty("version", CURRENT_VERSION);
        root.addProperty("size", inventory.getSize());

        JsonObject items = new JsonObject();

        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && item.getType() != Material.AIR) {
                items.add(String.valueOf(slot), serializeItem(item));
            }
        }

        root.add("items", items);
        return GSON.toJson(root);
    }

    /* =========================
       LOAD (JSON)
       ========================= */
    public static ItemStack[] loadInventory(String data) {
        if (data == null || data.isBlank()) {
            return new ItemStack[0];
        }

        if (looksLikePaperYaml(data) && !isPaperServer()) {
            throw new IllegalStateException(
                    "Detected Paper-serialized ItemStack data, "
                    + "but server is not running Paper. "
                    + "Please migrate data on a Paper server first."
            );
        }

        if (ItemSerializationJson.isLegacyYaml(data)) {
            ACLogger.info("Inventory is Legacy YAML! Migrating to new Method");
            try {
                data = ItemSerializationJson.migrateLegacyYamlToJson(data);
                ACLogger.info("Inventory migrated to new JSON format. Loading from JSON...");
            } catch (InvalidConfigurationException ex) {
                ACLogger.severe("Inventory could not be migrated!", ex);
                return new ItemStack[0];
            }
        }

        JsonObject root = JsonParser.parseString(data).getAsJsonObject();

        int size = root.get("size").getAsInt();
        ItemStack[] contents = new ItemStack[size];

        JsonObject items = root.getAsJsonObject("items");
        if (items != null) {
            for (Map.Entry<String, JsonElement> entry : items.entrySet()) {
                int slot = Integer.parseInt(entry.getKey());
                contents[slot] = deserializeItem(entry.getValue().getAsJsonObject());
            }
        }

        return contents;
    }

    /* =========================
       MIGRATION
       ========================= */
    private static boolean isLegacyYaml(String data) {
        return data != null && data.contains(":") && data.contains("==: org.bukkit.inventory.ItemStack");
    }

    private static boolean looksLikePaperYaml(String data) {
        if (data == null) {
            return false;
        }

        return data.contains("components:")
                || data.contains("schema_version")
                || data.contains("DataVersion:")
                || data.contains("minecraft:");
    }

    private static boolean isPaperServer() {
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static String migrateLegacyYamlToJson(String legacyYaml) throws InvalidConfigurationException {
        if (isLegacyYaml(legacyYaml)) {
            //Load ItemStack Array the OldSchool way
            YamlConfiguration config = new YamlConfiguration();
            // Load the string
            config.loadFromString(legacyYaml);
            List<ItemStack> stacks = new ArrayList<>();
            // Try to parse this inventory
            for (String key : config.getKeys(false)) {
                int number = Integer.parseInt(key);
                // Size should always be bigger
                while (stacks.size() <= number) {
                    stacks.add(null);
                }
                stacks.set(number, (ItemStack) config.get(key));
            }
            ItemStack[] contents = stacks.toArray(new ItemStack[0]);
           
            //Now Migrate to JSON
            JsonObject root = new JsonObject();
            root.addProperty("version", CURRENT_VERSION);
            root.addProperty("size", contents.length);

            JsonObject items = new JsonObject();

            for (int slot = 0; slot < contents.length; slot++) {
                if (contents[slot] != null) {
                    items.add(String.valueOf(slot), serializeItem(contents[slot]));
                }
            }

            root.add("items", items);
            return GSON.toJson(root);
        } else {
            throw new IllegalStateException("Function was called, but String is not legacy yaml data!");
        }
    }

    /* =========================
       INTERNAL
       ========================= */
    private static JsonObject serializeItem(ItemStack item) {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("item", item);
        JsonObject obj = new JsonObject();
        obj.addProperty("yaml", yaml.saveToString());
        return obj;
    }

    private static ItemStack deserializeItem(JsonObject json) {
        try {
            String yamlString = json.get("yaml").getAsString();
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.loadFromString(yamlString);
            return yaml.getItemStack("item");
        } catch (Exception ex) {
            ACLogger.severe(ex);
            return null;
        }
    }
}
