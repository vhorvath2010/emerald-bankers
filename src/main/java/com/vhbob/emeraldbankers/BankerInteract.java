package com.vhbob.emeraldbankers;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class BankerInteract implements Listener {

    private FileConfiguration config = EmeraldBankers.getPlugin().getConfig();
    private HashMap<Player, Integer> bankBal;

    public BankerInteract() {
        bankBal = new HashMap<Player, Integer>();
    }

    @EventHandler
    public void onClickBanker(PlayerInteractAtEntityEvent e) {
        // Check if they clicked a banker
        String bankerName = ChatColor.translateAlternateColorCodes('&', config.getString("banker-name"));
        if (e.getRightClicked().getCustomName().equalsIgnoreCase(bankerName)) {
            // Open banker GUIs
            if (e.getPlayer().hasPermission("bank.double")) {
                openBank(e.getPlayer(), 27);
            } else if (e.getPlayer().hasPermission("bank.single")) {
                openBank(e.getPlayer(), 54);
            } else {
                e.getPlayer().sendMessage(ChatColor.RED + "Error: You do not have permission to use a bank!");
            }
        }
    }

    @EventHandler
    public void onCloseBank(InventoryCloseEvent e) {
        if (bankBal.containsKey(e.getPlayer())) {
            Player p = (Player) e.getPlayer();
            // Calculate ending emeralds
            int endEmeralds = 0;
            for (ItemStack item : e.getInventory()) {
                if (item == null || item.getType() == null) {
                    continue;
                }
                if (item.getType() == Material.EMERALD) {
                    endEmeralds += item.getAmount();
                } else if (item.getType() == Material.EMERALD_BLOCK) {
                    endEmeralds += item.getAmount() * 9;
                }
            }
            // Update user balance based on difference
            int diff = endEmeralds - bankBal.get(p);
            if (diff < 0) {
                EmeraldBankers.getEconomy().withdrawPlayer(p, Math.abs(diff));
            } else {
                EmeraldBankers.getEconomy().depositPlayer(p, diff);
            }
            bankBal.remove(p);
        }
    }

    // Method to open a bank for a player
    public void openBank(Player p, int size) {
        // Open inv
        String title = ChatColor.translateAlternateColorCodes('&', config.getString("bank-title"));
        Inventory bank = Bukkit.createInventory(null, size, title);
        // Add emeralds
        int emeralds = (int) EmeraldBankers.getEconomy().getBalance(p);
        // Remove ones from player's inventory
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || item.getType() == null) {
                continue;
            }
            if (item.getType() == Material.EMERALD) {
                emeralds -= item.getAmount();
            } else if (item.getType() == Material.EMERALD_BLOCK) {
                emeralds -= item.getAmount() * 9;
            }
        }
        // Add bank emeralds to inv bank
        bankBal.put(p, emeralds);
        while (emeralds > 0) {
            if (emeralds >= 64) {
                bank.addItem(new ItemStack(Material.EMERALD, 64));
                emeralds -= 64;
            } else {
                bank.addItem(new ItemStack(Material.EMERALD, emeralds));
                break;
            }
        }
        p.openInventory(bank);
    }

}
