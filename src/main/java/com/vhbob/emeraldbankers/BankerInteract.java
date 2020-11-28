package com.vhbob.emeraldbankers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BankerInteract implements Listener {

    private FileConfiguration config = EmeraldBankers.getPlugin().getConfig();

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

    // Method to open a bank for a player
    public void openBank(Player p, int size) {
        String title = ChatColor.translateAlternateColorCodes('&', config.getString("bank-title"));
        Inventory bank = Bukkit.createInventory(null, size, title);
        int emeralds = (int) EmeraldBankers.getEconomy().getBalance(p);
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
