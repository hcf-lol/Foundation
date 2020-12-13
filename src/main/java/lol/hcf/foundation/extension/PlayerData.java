package lol.hcf.foundation.extension;

import lol.hcf.antihashmap.extension.annotation.PreLoad;
import lol.hcf.foundation.gui.InventoryGui;
import org.bukkit.entity.Player;

@PreLoad
public class PlayerData {

    private InventoryGui boundGui;

    public InventoryGui getBoundGui() {
        return boundGui;
    }

    public PlayerData setBoundGui(InventoryGui boundGui) {
        this.boundGui = boundGui;
        return this;
    }

    public static PlayerData valueOf(Player player) {
        return ((PlayerExtension) player).getPlayerData();
    }

}
