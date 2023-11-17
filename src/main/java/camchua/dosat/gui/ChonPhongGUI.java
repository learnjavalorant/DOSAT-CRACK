// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.gui;

import camchua.dosat.Main;
import camchua.dosat.game.Game;
import camchua.dosat.game.PriceType;
import camchua.dosat.game.Status;
import camchua.dosat.gui.ChonPhongGUI;
import camchua.dosat.manager.FileManager;
import camchua.dosat.utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;

import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChonPhongGUI {
    public ArrayList<Inventory> pages = new ArrayList();
    public int currpage = 0;
    public static HashMap<Player, ChonPhongGUI> users = new HashMap();

    public ChonPhongGUI(Player p) {
        FileConfiguration config = FileManager.getFileConfig(FileManager.Files.CONFIG);
        Inventory page = this.gui();

        for(String arena : Main.getPlugin().getGames().keySet()) {
            Game g = Utils.getArena(arena);
            if (g.isActive()) {
                ItemStack newitem = null;
                if (g.getStatus() == Status.INVITING) {
                    ItemStack item = new ItemStack(Utils.matchMaterial(config.getString("Settings.ChonPhongGui.InvitingFormat.ID")), 1, (short)((byte)config.getInt("Settings.ChonPhongGui.InvitingFormat.Data")));
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Settings.ChonPhongGui.InvitingFormat.Name")).replace("<room>", arena));
                    ArrayList lores = new ArrayList();

                    for(String lore : config.getStringList("Settings.ChonPhongGui.InvitingFormat.Lore")) {
                        lores.add(ChatColor.translateAlternateColorCodes('&', lore).replace("<player1>", g.getPlayer1().getName()).replace("<player2>", g.getPlayer2().getName()).replace("<pricetype>", PriceType.getFormat(g.getPriceType())).replace("<price>", String.valueOf(g.getPrice())));
                    }

                    meta.setLore(lores);
                    item.setItemMeta(meta);
                    NBTItem nbt = new NBTItem(item);
                    nbt.setString("DoSat_Room_Name", arena);
                    nbt.setString("DoSat_Room_Status", "Inviting");
                    newitem = nbt.getItem().clone();
                }

                if (g.getStatus() == Status.COUNTDOWN || g.getStatus() == Status.RUNNING) {
                    ItemStack item = new ItemStack(Utils.matchMaterial(config.getString("Settings.ChonPhongGui.PlayingFormat.ID")), 1, (short)((byte)config.getInt("Settings.ChonPhongGui.PlayingFormat.Data")));
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Settings.ChonPhongGui.PlayingFormat.Name")).replace("<room>", arena));
                    ArrayList lores = new ArrayList();

                    for(String lore : config.getStringList("Settings.ChonPhongGui.PlayingFormat.Lore")) {
                        lores.add(ChatColor.translateAlternateColorCodes('&', lore).replace("<player1>", g.getPlayer1().getName()).replace("<player2>", g.getPlayer2().getName()).replace("<pricetype>", PriceType.getFormat(g.getPriceType())).replace("<price>", String.valueOf(g.getPrice())));
                    }

                    meta.setLore(lores);
                    item.setItemMeta(meta);
                    NBTItem nbt = new NBTItem(item);
                    nbt.setString("DoSat_Room_Name", arena);
                    nbt.setString("DoSat_Room_Status", "Playing");
                    newitem = nbt.getItem().clone();
                }

                if (g.getStatus() == Status.WAITING) {
                    ItemStack item = new ItemStack(Utils.matchMaterial(config.getString("Settings.ChonPhongGui.WaitingFormat.ID")), 1, (short)((byte)config.getInt("Settings.ChonPhongGui.WaitingFormat.Data")));
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config.getString("Settings.ChonPhongGui.WaitingFormat.Name")).replace("<room>", arena));
                    ArrayList lores = new ArrayList();

                    for(String lore : config.getStringList("Settings.ChonPhongGui.WaitingFormat.Lore")) {
                        lores.add(ChatColor.translateAlternateColorCodes('&', lore));
                    }

                    meta.setLore(lores);
                    item.setItemMeta(meta);
                    NBTItem nbt = new NBTItem(item);
                    nbt.setString("DoSat_Room_Name", arena);
                    nbt.setString("DoSat_Room_Status", "Waiting");
                    newitem = nbt.getItem().clone();
                }

                if (page.firstEmpty() == 45) {
                    this.pages.add(page);
                    page = this.gui();
                    page.addItem(new ItemStack[]{newitem});
                } else {
                    page.addItem(new ItemStack[]{newitem});
                }
            }
        }

        this.pages.add(page);
        if (users.containsKey(p)) {
            users.remove(p);
            p.openInventory((Inventory)this.pages.get(this.currpage));
            users.put(p, this);
        } else {
            p.openInventory((Inventory)this.pages.get(this.currpage));
            users.put(p, this);
        }

    }

    public Inventory gui() {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.ChonPhongGui.Name")));
        ItemStack nextpage = new ItemStack(Material.ARROW, 1);
        ItemMeta mnextpage = nextpage.getItemMeta();
        mnextpage.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.ChonPhongGui.NextPage")));
        nextpage.setItemMeta(mnextpage);
        inv.setItem(50, nextpage);
        ItemStack previouspage = new ItemStack(Material.ARROW, 1);
        ItemMeta mpreviouspage = previouspage.getItemMeta();
        mpreviouspage.setDisplayName(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.ChonPhongGui.PreviousPage")));
        previouspage.setItemMeta(mpreviouspage);
        inv.setItem(48, previouspage);
        return inv;
    }
}
