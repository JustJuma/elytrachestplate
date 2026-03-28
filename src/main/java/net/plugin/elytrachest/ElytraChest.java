package net.plugin.elytrachest;

import org.bukkit.plugin.java.JavaPlugin;

public class ElytraChest extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new AnvilListener(), this);
        getServer().getPluginManager().registerEvents(new GrindstoneListener(this), this);
        getLogger().info("ElytraChest enabled.");
    }
}
