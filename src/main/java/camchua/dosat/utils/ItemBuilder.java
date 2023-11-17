// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.utils;

import camchua.dosat.data.BountyData;
import camchua.dosat.manager.FileManager;
import camchua.dosat.utils.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder {
    private Material material = Material.STONE;
    private int amount = 1;
    private String name;
    private List<String> lores;
    private byte data = 0;
    private String skullowner;
    private boolean glow = false;

    public static ItemStack build(FileManager.Files file, String path, String playername) {
        FileConfiguration config = FileManager.getFileConfig(file);
        ItemBuilder builder = new ItemBuilder();
        builder.material(Material.matchMaterial(config.getString(path + ".ID")));
        if (config.contains(path + ".Amount")) {
            builder.amount(config.getInt(path + ".Amount"));
        }

        if (config.contains(path + ".Data")) {
            builder.data((byte)config.getInt(path + ".Data"));
        }

        if (config.contains(path + ".Name")) {
            builder.name(ChatColor.translateAlternateColorCodes('&', config.getString(path + ".Name")));
        }

        if (config.contains(path + ".Lore")) {
            List<String> lores = new ArrayList();

            for(String lore : config.getStringList(path + ".Lore")) {
                lores.add(ChatColor.translateAlternateColorCodes('&', lore).replace("<time>", String.valueOf(BountyData.getData(Bukkit.getPlayer(playername)).getTime())).replace("<bounty>", String.valueOf(BountyData.getData(Bukkit.getPlayer(playername)).getBounty())));
            }

            builder.lore(lores);
        }

        if (config.contains(path + ".Glow")) {
            builder.glow(config.getBoolean(path + ".Glow"));
        }

        return builder.build();
    }

    public ItemBuilder material(Material m) {
        this.material = m;
        return this;
    }

    public ItemBuilder amount(int a) {
        this.amount = a;
        return this;
    }

    public ItemBuilder name(String n) {
        this.name = n;
        return this;
    }

    public ItemBuilder lore(List<String> l) {
        this.lores = l;
        return this;
    }

    public ItemBuilder data(byte d) {
        this.data = d;
        return this;
    }

    public ItemBuilder glow(boolean g) {
        this.glow = g;
        return this;
    }

    public ItemBuilder skull(String s) {
        this.skullowner = s;
        return this;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(this.material, this.amount, (short)this.data);
        ItemMeta meta = item.getItemMeta();
        SkullMeta smeta = null;
        if (this.data > 0) {
            item.setDurability((short)this.data);
        }

        if (this.name != null) {
            meta.setDisplayName(this.name);
        }

        if (this.lores != null) {
            meta.setLore(this.lores);
        }

        if (this.glow) {
            meta.addEnchant(Enchantment.DURABILITY, 4, true);
            meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
        }

        item.setItemMeta(meta);
        if (this.skullowner != null) {
            smeta = (SkullMeta)item.getItemMeta();
            smeta.setOwner(this.skullowner);
            item.setItemMeta(smeta);
        }

        return item;
    }
}
