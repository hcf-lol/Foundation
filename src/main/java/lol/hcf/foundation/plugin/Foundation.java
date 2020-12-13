package lol.hcf.foundation.plugin;

import lol.hcf.foundation.database.ConnectionHandler;
import lol.hcf.foundation.database.ConnectionManager;
import lol.hcf.foundation.database.DatabaseConfiguration;
import lol.hcf.foundation.listener.InventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Foundation extends JavaPlugin {

    private static Foundation instance;

    private ConnectionManager connectionManager;

    @Override
    public void onEnable() {
        Foundation.instance = this;

        DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration(new File(this.getDataFolder(), "database.yml"));

        super.getLogger().info("Connecting to databases");
        this.connectionManager = new ConnectionManager(databaseConfiguration);

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
    }

    @Override
    public void onDisable() {
        super.getLogger().info("Closing database connections");
        this.connectionManager.close();
    }

    public static ConnectionHandler getConnectionHandler() {
        return Foundation.instance.connectionManager;
    }
}
