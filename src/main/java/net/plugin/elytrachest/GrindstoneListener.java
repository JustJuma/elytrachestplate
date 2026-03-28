package net.plugin.elytrachest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class GrindstoneListener implements Listener {

    private final JavaPlugin plugin;

    public GrindstoneListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGrindstonePrepare(PrepareGrindstoneEvent event) {
        ItemStack input = event.getInventory().getItem(0);
        if (input == null || !isCombined(input)) return;

        // Output: plain chestplate, no enchants, no glider, no lore
        ItemStack output = new ItemStack(input.getType());
        event.setResult(output);
    }

    @EventHandler
    public void onGrindstoneClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof GrindstoneInventory inv)) return;
        if (event.getRawSlot() != 2) return; // slot 2 is the result slot
        if (event.getCurrentItem() == null) return;

        ItemStack input = inv.getItem(0);
        if (input == null || !isCombined(input)) return;

        Player player = (Player) event.getWhoClicked();

        // After the click resolves, give the player a plain elytra
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            ItemStack elytra = new ItemStack(Material.ELYTRA);
            if (player.getInventory().firstEmpty() == -1) {
                // Inventory full: drop at feet
                player.getWorld().dropItemNaturally(player.getLocation(), elytra);
            } else {
                player.getInventory().addItem(elytra);
            }
        });
    }

    private boolean isCombined(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.lore() == null) return false;
        for (Component line : meta.lore()) {
            // Serialize to plain text and check
            String plain = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
                    .plainText().serialize(line);
            if (plain.equals("[Elytra]")) return true;
        }
        return false;
    }
}
