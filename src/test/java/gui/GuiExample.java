package gui;

import lol.hcf.foundation.gui.InventoryGui;
import lol.hcf.foundation.gui.ItemInfo;
import lol.hcf.foundation.gui.element.impl.ButtonElement;
import lol.hcf.foundation.gui.element.impl.StaticElement;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerJoinEvent;

public class GuiExample {
    public void onPlayerJoin(PlayerJoinEvent event) {
        InventoryGui gui = new InventoryGui(event.getPlayer(), 3);

        gui.getMargin().set(1, 1);

        gui.addElement(new ButtonElement<>(new StaticElement(new ItemInfo(Material.IRON_AXE)), (player) -> player.sendMessage("you clicked the axe")));

        gui.write();
        gui.display();
    }
}
