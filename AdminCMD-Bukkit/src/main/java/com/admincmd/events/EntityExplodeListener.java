package com.admincmd.events;

import com.admincmd.utils.BukkitListener;
import com.admincmd.utils.Config;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeListener extends BukkitListener {

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.CREEPER && !Config.CREEPER_BLOCK_DAMAGE.getBoolean()) {
            event.blockList().clear();
            return;
        }
    }

}
