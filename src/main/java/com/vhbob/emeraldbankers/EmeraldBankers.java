package com.vhbob.emeraldbankers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class EmeraldBankers extends JavaPlugin {

    private static EmeraldBankers plugin;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        // Setup the economy
        if (!setupEconomy() ) {
            Bukkit.getConsoleSender().sendMessage(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        plugin = this;
        // Load commands and events
        getCommand("summonBanker").setExecutor(new SummonBanker());
        Bukkit.getPluginManager().registerEvents(new BankerInteract(), this);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabled Emerald Bankers");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static EmeraldBankers getPlugin() {
        return plugin;
    }

    public static Economy getEconomy() {
        return econ;
    }

}
