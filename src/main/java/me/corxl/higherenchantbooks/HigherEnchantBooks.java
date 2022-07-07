package me.corxl.higherenchantbooks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class HigherEnchantBooks extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        try {
            this.registerRecipes();
            this.getLogger().info("Plugin made by Corxl. Discord: Corxl#5755");
        } catch (Exception e) {
            this.getLogger().info(ChatColor.RED + "An error has occurred with the config. Delete the config to regenerate to defaults.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerRecipes() {
        HashMap<String, ArrayList<ItemStack>> items = new HashMap<>();

        this.getConfig().getConfigurationSection("tools").getKeys(false).forEach(k -> {
            if (!items.containsKey(k)) {
                items.put(k, new ArrayList<>());
            }
            List<String> mats = this.getConfig().getStringList("tools." + k + ".item-types");
            HashMap<Enchantment, Integer> enchantments = new HashMap<>();
            this.getConfig().getConfigurationSection("tools." + k + ".enchantments").getKeys(false).forEach(e -> {
                enchantments.put(Enchantment.getByName(e), this.getConfig().getInt("tools." + k + ".enchantments." + e));
            });

            mats.forEach(m -> {
                ItemStack item = new ItemStack(Material.getMaterial(m));
                enchantments.forEach(item::addUnsafeEnchantment);
                if (this.getConfig().getString("tools." + k + ".custom-name")!=null && this.getConfig().getString("tools." + k + ".custom-name").length() > 0) {
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("tools." + k + ".custom-name")));
                    item.setItemMeta(meta);
                }
                items.get(k).add(item);
            });
        });
        items.forEach((k, v)->{
            v.forEach(i -> {
                ShapedRecipe r = new ShapedRecipe(new NamespacedKey(this, i.getType().name() + "_" + k), i);
                r.shape("OOO", "OMO", "OOO");
                Material upgradeMat = Material.getMaterial(this.getConfig().getString("tools." + k + ".upgrade-material"));
                r.setIngredient('O', upgradeMat);
                r.setIngredient('M', new RecipeChoice.ExactChoice(new ItemStack(i.getType())));
                this.getServer().addRecipe(r);
            });
        });
    }
}
