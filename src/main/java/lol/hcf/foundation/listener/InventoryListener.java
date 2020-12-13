package lol.hcf.foundation.listener;

import lol.hcf.foundation.extension.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        PlayerData data = PlayerData.valueOf((Player) event.getWhoClicked());
        if (data.getBoundGui() == null || !data.getBoundGui().equals(event.getInventory())) return;
        data.getBoundGui().onClick(event);
    }

}
