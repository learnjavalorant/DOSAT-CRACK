// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.manager;

import camchua.dosat.Main;
import camchua.dosat.gems.Gems;
import camchua.dosat.manager.FileManager;
import camchua.dosat.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GemsManager {
    private Main plugin;
    private List<Gems> gemsList = new ArrayList();

    public GemsManager(Main plugin) {
        this.plugin = plugin;
        this.load();
    }

    private void load() {
        FileConfiguration config = FileManager.getFileConfig(FileManager.Files.GEMS);

        for(String key : config.getKeys(false)) {
            ItemStack item = null;
            Material mat = null;
            String id = config.getString(key + ".item.id");

            try {
                mat = Utils.matchMaterial(id);
            } catch (Exception var17) {
                Bukkit.getConsoleSender().sendMessage("Gems > Error Gems Id: " + key);
                continue;
            }

            int data = config.getInt(key + ".item.data");
            String displayName = ChatColor.translateAlternateColorCodes('&', config.getString(key + ".item.name"));
            List<String> itemLore = new ArrayList();

            for(String lore : config.getStringList(key + ".item.lore")) {
                itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            item = new ItemStack(mat, 1, (short)((byte)data));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(itemLore);
            item.setItemMeta(meta);
            HashMap<Integer, Double> attributes = new HashMap();
            if (config.contains(key + ".attributes")) {
                for(String lv : config.getConfigurationSection(key + ".attributes").getKeys(false)) {
                    int level = 0;

                    try {
                        level = Integer.parseInt(lv);
                    } catch (Exception var18) {
                        continue;
                    }

                    double value = config.getDouble(key + ".attributes." + lv);
                    attributes.put(level, value);
                }
            }

            HashMap<String, Integer> hideEffect = new HashMap();
            if (config.contains(key + ".hide-effect")) {
                for(String effectType : config.getConfigurationSection(key + ".hide-effect").getKeys(false)) {
                    int amplifier = config.getInt(key + ".hide-effect." + effectType);
                    hideEffect.put(effectType, amplifier);
                }
            }

            this.gemsList.add(new Gems(item, attributes, hideEffect, key));
        }

    }

    public boolean hasType(String type) {
        for(Gems gems : this.gemsList) {
            if (gems.getType().equalsIgnoreCase(type)) {
                return true;
            }
        }

        return false;
    }

    public Gems getGems(String type) {
        for(Gems gems : this.gemsList) {
            if (gems.getType().equalsIgnoreCase(type)) {
                return gems;
            }
        }

        return null;
    }

    public boolean isGems(ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        return nbt.hasKey("DoSat_Gems_Type") && nbt.hasKey("DoSat_Gems_Level");
    }

    public boolean isGems(ItemStack item, String type) {
        NBTItem nbt = new NBTItem(item);
        if (nbt.hasKey("DoSat_Gems_Type") && nbt.hasKey("DoSat_Gems_Level")) {
            String gemsType = nbt.getString("DoSat_Gems_Type");
            return gemsType.equalsIgnoreCase(type);
        } else {
            return false;
        }
    }

    public int getGemsLevel(ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        return nbt.hasKey("DoSat_Gems_Type") && nbt.hasKey("DoSat_Gems_Level") ? nbt.getInteger("DoSat_Gems_Level") : 0;
    }

    public String getGemsType(ItemStack item) {
        NBTItem nbt = new NBTItem(item);
        return nbt.hasKey("DoSat_Gems_Type") && nbt.hasKey("DoSat_Gems_Level") ? nbt.getString("DoSat_Gems_Type") : "";
    }
}
