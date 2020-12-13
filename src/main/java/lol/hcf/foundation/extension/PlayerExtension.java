package lol.hcf.foundation.extension;

import lol.hcf.antihashmap.extension.ClassExtension;
import lol.hcf.antihashmap.extension.annotation.ClassTarget;

import static lol.hcf.antihashmap.extension.ClassExtension.*;

@ClassTarget(inject = START + CRAFT_BUKKIT + "entity" + PACKAGE_SEPARATOR + "CraftPlayer" + END, extension = PlayerData.class)
public interface PlayerExtension extends ClassExtension {
    PlayerData getPlayerData();
}
