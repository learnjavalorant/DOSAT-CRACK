// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.gui;

import camchua.dosat.gui.TruyNaGui;
import camchua.dosat.manager.FileManager;
import camchua.dosat.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class TruyNaGui {
    public static ArrayList<Inventory> pages = new ArrayList();
    public int currpage = 0;
    public static HashMap<Player, TruyNaGui> users = new HashMap();

    public TruyNaGui(Player p) {
        if (users.containsKey(p)) {
            users.remove(p);
            p.openInventory((Inventory)pages.get(this.currpage));
            users.put(p, this);
        } else {
            p.openInventory((Inventory)pages.get(this.currpage));
            users.put(p, this);
        }

    }

    public static Inventory gui() {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaGui.Name")));
        ItemStack nextpage = new ItemStack(Material.ARROW, 1);
        ItemMeta mnextpage = nextpage.getItemMeta();
        mnextpage.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaGui.NextPage")));
        nextpage.setItemMeta(mnextpage);
        inv.setItem(50, nextpage);
        ItemStack previouspage = new ItemStack(Material.ARROW, 1);
        ItemMeta mpreviouspage = previouspage.getItemMeta();
        mpreviouspage.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaGui.PreviousPage")));
        previouspage.setItemMeta(mpreviouspage);
        inv.setItem(48, previouspage);
        return inv;
    }

    public static void updateInv() {
        List<Inventory> pages = new ArrayList();
        Inventory page = gui();

        for(Player online : Bukkit.getOnlinePlayers()) {
            ItemStack item = new ItemStack(Utils.isLegacy() ? Utils.matchMaterial("SKULL_ITEM") : Utils.matchMaterial("PLAYER_HEAD"), 1, (short)(Utils.isLegacy() ? 3 : 0));
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            meta.setOwner(online.getName());
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaGui.Format.Name")).replace("<player>", online.getName()));
            List<String> lores = new ArrayList();

            for(String lore : FileManager.getFileConfig(FileManager.Files.CONFIG).getStringList("Settings.TruyNaGui.Format.Lore")) {
                lores.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            meta.setLore(lores);
            item.setItemMeta(meta);
            NBTItem nbti = new NBTItem(item);
            if (!nbti.hasKey("DoSat_Player")) {
                nbti.setString("DoSat_Player", online.getName());
            }

            ItemStack newitem = nbti.getItem();
            if (page.firstEmpty() == 45) {
                pages.add(page);
                page = gui();
                page.addItem(new ItemStack[]{newitem});
            } else {
                page.addItem(new ItemStack[]{newitem});
            }
        }

        pages.add(page);
        TruyNaGui.pages = (ArrayList)pages;
    }
}
