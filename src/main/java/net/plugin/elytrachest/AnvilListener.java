package net.plugin.elytrachest;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class AnvilListener implements Listener {

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        ItemStack left  = inv.getItem(0);
        ItemStack right = inv.getItem(1);

        if (left == null || right == null) return;

        ItemStack chestplate;
        ItemStack elytra;

        if (isChestplate(left) && right.getType() == Material.ELYTRA) {
            chestplate = left;
            elytra     = right;
        } else if (isChestplate(right) && left.getType() == Material.ELYTRA) {
            chestplate = right;
            elytra     = left;
        } else {
            return;
        }

        ItemStack result = chestplate.clone();

        for (Map.Entry<Enchantment, Integer> entry : elytra.getEnchantments().entrySet()) {
            Enchantment ench       = entry.getKey();
            int         elytraLvl = entry.getValue();
            int         chestLvl  = result.getEnchantmentLevel(ench);

            int finalLevel;
            if (chestLvl == 0) {
                finalLevel = elytraLvl;
            } else if (chestLvl == elytraLvl) {
                finalLevel = Math.min(chestLvl + 1, ench.getMaxLevel());
            } else {
                finalLevel = Math.max(chestLvl, elytraLvl);
            }
            result.addUnsafeEnchantment(ench, finalLevel);
        }

        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setGlider(true);
            meta.lore(List.of(
                Component.text("[Elytra]")
                    .color(NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
            ));
            result.setItemMeta(meta);
        }

        inv.setRepairCost(30);
        event.setResult(result);
    }

    private boolean isChestplate(ItemStack item) {
        return item.getType().name().endsWith("_CHESTPLATE");
    }
}
