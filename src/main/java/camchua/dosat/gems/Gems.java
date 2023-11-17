// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.gems;

import camchua.dosat.utils.Utils;
import java.util.HashMap;
import java.util.List;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Gems {
    private ItemStack item;
    private HashMap<Integer, Double> attributes;
    private HashMap<String, Integer> hideEffect;
    private String type;

    public Gems(ItemStack item, HashMap<Integer, Double> attributes, HashMap<String, Integer> hideEffect, String type) {
        this.item = item;
        this.attributes = attributes;
        this.hideEffect = hideEffect;
        this.type = type;
    }

    public boolean isMaxLevel(int level) {
        return !this.attributes.containsKey(level + 1);
    }

    public void give(Player player, int level, int amount) {
        ItemStack item = this.getItem(level);
        item.setAmount(amount);
        player.getInventory().addItem(new ItemStack[]{item});
    }

    public ItemStack getItem(int level) {
        ItemStack item = this.item.clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replace("<level>", String.valueOf(level)).replace("<value>", String.valueOf(this.getAttributesValue(level))));
        List<String> itemLore = meta.getLore();

        for(int i = 0; i < itemLore.size(); ++i) {
            String lore = (String)itemLore.get(i);
            itemLore.set(i, lore.replace("<level>", String.valueOf(level)).replace("<value>", String.valueOf(this.getAttributesValue(level))));
        }

        meta.setLore(itemLore);
        item.setItemMeta(meta);
        NBTItem nbt = new NBTItem(item);
        nbt.setString("DoSat_Gems_Type", this.type);
        nbt.setInteger("DoSat_Gems_Level", Integer.valueOf(level));
        return nbt.getItem();
    }

    public double getAttributesValue(int level) {
        return this.attributes.containsKey(level) ? this.attributes.get(level) : 0.0D;
    }

    public void giveHideEffect(Player p) {
        for(String effectType : this.hideEffect.keySet()) {
            PotionEffectType effect = null;

            try {
                effect = PotionEffectType.getByName(effectType.toUpperCase());
            } catch (Exception var6) {
                continue;
            }

            int amplifier = this.hideEffect.get(effectType);
            if (Utils.isLegacy()) {
                p.addPotionEffect(new PotionEffect(effect, 60, amplifier, false, false));
            } else {
                p.addPotionEffect(new PotionEffect(effect, 60, amplifier, false, false, false));
            }
        }

    }

    public String getType() {
        return this.type;
    }
}
