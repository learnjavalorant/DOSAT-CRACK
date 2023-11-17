// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.gui;

import camchua.dosat.data.TruyNaData;
import camchua.dosat.game.PriceType;
import camchua.dosat.gui.TruyNaListGui;
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

public class TruyNaListGui {
    public static ArrayList<Inventory> pages = new ArrayList();
    public int currpage = 0;
    public static HashMap<Player, TruyNaListGui> users = new HashMap();

    public TruyNaListGui(Player p) {
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
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaListGui.Name")));
        ItemStack nextpage = new ItemStack(Material.ARROW, 1);
        ItemMeta mnextpage = nextpage.getItemMeta();
        mnextpage.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaListGui.NextPage")));
        nextpage.setItemMeta(mnextpage);
        inv.setItem(50, nextpage);
        ItemStack previouspage = new ItemStack(Material.ARROW, 1);
        ItemMeta mpreviouspage = previouspage.getItemMeta();
        mpreviouspage.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaListGui.PreviousPage")));
        previouspage.setItemMeta(mpreviouspage);
        inv.setItem(48, previouspage);
        ItemStack deselect = new ItemStack(Material.BARRIER, 1);
        ItemMeta mdeselect = deselect.getItemMeta();
        mdeselect.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaListGui.Deselect")));
        deselect.setItemMeta(mdeselect);
        inv.setItem(49, deselect);
        return inv;
    }

    public static void updateInv() {
        List<Inventory> pages = new ArrayList();
        Inventory page = gui();

        for(String player : TruyNaData.listTruyNa().keySet()) {
            TruyNaData.TruyNaObject truyNaObject = TruyNaData.getTruyNa(player);
            ItemStack item = new ItemStack(Utils.isLegacy() ? Utils.matchMaterial("SKULL_ITEM") : Utils.matchMaterial("PLAYER_HEAD"), 1, (short)(Utils.isLegacy() ? 3 : 0));
            SkullMeta meta = (SkullMeta)item.getItemMeta();
            meta.setOwner(player);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaListGui.Format.Name")).replace("<player>", player));
            List<String> lores = new ArrayList();

            for(String lore : FileManager.getFileConfig(FileManager.Files.CONFIG).getStringList("Settings.TruyNaListGui.Format.Lore")) {
                lores.add(ChatColor.translateAlternateColorCodes('&', lore).replace("<owner>", truyNaObject.getOwner()).replace("<price>", "" + truyNaObject.getPriceValue()).replace("<pricetype>", PriceType.getFormat(PriceType.valueOf(truyNaObject.getPriceType().toUpperCase()))));
            }

            meta.setLore(lores);
            item.setItemMeta(meta);
            NBTItem nbti = new NBTItem(item);
            if (!nbti.hasKey("DoSat_Player")) {
                nbti.setString("DoSat_Player", player);
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
        TruyNaListGui.pages = (ArrayList)pages;
    }
}
