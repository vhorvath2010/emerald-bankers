package com.vhbob.emeraldbankers;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SummonBanker implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("summonBanker")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("banker.summon")) {
                    FileConfiguration config = EmeraldBankers.getPlugin().getConfig();
                    int cost = config.getInt("banker-cost");
                    if (player.hasPermission("banker.free")) {
                        cost = 0;
                    }
                    if (cost <= EmeraldBankers.getEconomy().getBalance(player)) {
                        // Take money
                        EmeraldBankers.getEconomy().withdrawPlayer(player, cost);
                        // Setup banker
                        Villager banker = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
                        banker.setCustomName(ChatColor.translateAlternateColorCodes('&', config.getString("banker-name")));
                        banker.setCustomNameVisible(true);
                        banker.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(config.getDouble("banker-hp"));
                        banker.setHealth(config.getDouble("banker-hp"));
                        banker.setAI(false);
                        banker.setCollidable(false);
                        player.sendMessage(ChatColor.GREEN + "Banker spawned!");
                    } else {
                        player.sendMessage(ChatColor.RED + "Error: You need " + cost + " deposited emeralds to do this!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission to summon a banker!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Error: You are not a player!");
            }
        }
        return false;
    }
}