package com.admincmd.chestsigns.events;

import com.admincmd.chestsigns.inventorysigns.InventorySign;
import com.admincmd.chestsigns.inventorysigns.SignManager;
import com.admincmd.player.ACPlayer;
import com.admincmd.player.PlayerManager;
import com.admincmd.utils.BukkitListener;
import com.admincmd.utils.Locales;
import com.admincmd.utils.Messager;
import com.admincmd.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class SignListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    public void onSignEdit(final SignChangeEvent e) {
        Player p = e.getPlayer();
        ACPlayer acp = PlayerManager.getPlayer(p);
        String[] lines = e.getLines();

        if (lines.length < 2) {
            return;
        }
        if (lines[0].equalsIgnoreCase("[invsign]")) {
            if (!p.hasPermission("admincmd.inventorysign.create")) {
                Messager.sendMessage(acp, Locales.COMMAND_MESSAGES_NO_PERMISSION.getString().replaceAll("%perm%", "admincmd.inventorysign.create"), Messager.MessageType.NONE);
                e.getBlock().breakNaturally();
                e.setCancelled(true);
                return;
            }

            if (SignManager.hasSign(acp)) {
                Messager.sendMessage(acp, "You already have a InventorySign!", Messager.MessageType.ERROR);
                e.getBlock().breakNaturally();
                e.setCancelled(true);
                return;
            }

            InventorySign invSign = SignManager.createSign(acp, (Sign) e.getBlock().getState());;

            e.setLine(0, ChatColor.BLUE + "[InvSign]");
            e.setLine(1, ChatColor.GOLD + acp.getName());
            e.setLine(2, ChatColor.GREEN + "" + invSign.getID());
            e.setLine(3, null);


        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignCLick(final PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block b = e.getClickedBlock();
        if (b == null || !(b.getState() instanceof Sign s)) {
            return;
        }

        List<String> lines = new ArrayList<>();

        for (String string : s.getSide(Side.FRONT).getLines()) {
            lines.add(Utils.removeColors(string));
        }

        if (lines.isEmpty()) return;

        if (lines.get(0).equalsIgnoreCase("[invsign]")) {
            e.setCancelled(true);
            Messager.sendMessage(PlayerManager.getPlayer(e.getPlayer()), "This Sign is an InventorySign and cannot be changed.", Messager.MessageType.INFO);
        }
    }
}

