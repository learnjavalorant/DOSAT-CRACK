// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.gui;

import camchua.dosat.Main;
import camchua.dosat.manager.FileManager;
import camchua.dosat.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GhepNgocGui implements Listener {
    private ItemStack inputIcon;
    private ItemStack resultIcon;
    private List<Player> viewers = new ArrayList();

    public GhepNgocGui() {
        this.loadInputIcon();
        this.loadResultIcon();
    }

    public void loadInputIcon() {
        FileConfiguration config = FileManager.getFileConfig(FileManager.Files.CONFIG);
        String id = config.getString("Settings.GhepNgocGui.InputIcon.ID");
        Material mat = null;

        try {
            mat = Utils.matchMaterial(id);
        } catch (Exception var9) {
            Bukkit.getConsoleSender().sendMessage("GhepNgocGui > Error Item Id: InputIcon");
            return;
        }

        int data = config.getInt("Settings.GhepNgocGui.InputIcon.Data");
        String displayName = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.GhepNgocGui.InputIcon.Name"));
        List<String> itemLore = new ArrayList();

        for(String lore : config.getStringList("Settings.GhepNgocGui.InputIcon.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));
        }

        ItemStack item = new ItemStack(mat, 1, (short)((byte)data));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(itemLore);
        item.setItemMeta(meta);
        this.inputIcon = item;
    }

    public void loadResultIcon() {
        FileConfiguration config = FileManager.getFileConfig(FileManager.Files.CONFIG);
        String id = config.getString("Settings.GhepNgocGui.ResultIcon.ID");
        Material mat = null;

        try {
            mat = Utils.matchMaterial(id);
        } catch (Exception var9) {
            Bukkit.getConsoleSender().sendMessage("GhepNgocGui > Error Item Id: ResultIcon");
            return;
        }

        int data = config.getInt("Settings.GhepNgocGui.ResultIcon.Data");
        String displayName = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.GhepNgocGui.ResultIcon.Name"));
        List<String> itemLore = new ArrayList();

        for(String lore : config.getStringList("Settings.GhepNgocGui.ResultIcon.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));
        }

        ItemStack item = new ItemStack(mat, 1, (short)((byte)data));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(itemLore);
        item.setItemMeta(meta);
        this.resultIcon = item;
    }

    public void open(Player p) {
        FileConfiguration config = FileManager.getFileConfig(FileManager.Files.CONFIG);
        String title = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.GhepNgocGui.Name"));
        int rows = config.getInt("Settings.GhepNgocGui.Rows");
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, title);

        for(String key : config.getConfigurationSection("Settings.GhepNgocGui.Items").getKeys(false)) {
            String id = config.getString("Settings.GhepNgocGui.Items." + key + ".ID");
            Material mat = null;

            try {
                mat = Utils.matchMaterial(id);
            } catch (Exception var19) {
                Bukkit.getConsoleSender().sendMessage("KhamNgocGui > Error Item Id: " + key);
                continue;
            }

            int data = config.getInt("Settings.GhepNgocGui.Items." + key + ".Data");
            String displayName = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.GhepNgocGui.Items." + key + ".Name"));
            List<String> itemLore = new ArrayList();

            for(String lore : config.getStringList("Settings.GhepNgocGui.Items." + key + ".Lore")) {
                itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            List<Integer> slots = config.getIntegerList("Settings.GhepNgocGui.Items." + key + ".Slot");
            ItemStack item = new ItemStack(mat, 1, (short)((byte)data));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(itemLore);
            item.setItemMeta(meta);

            for(int slot : slots) {
                inv.setItem(slot, item);
            }
        }

        String id = config.getString("Settings.GhepNgocGui.Confirm.ID");
        Material mat = null;

        label74: {
            try {
                mat = Utils.matchMaterial(id);
            } catch (Exception var18) {
                Bukkit.getConsoleSender().sendMessage("KhamNgocGui > Error Item Id: Confirm");
                break label74;
            }

            int data = config.getInt("Settings.GhepNgocGui.Confirm.Data");
            String displayName = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.GhepNgocGui.Confirm.Name"));
            List<String> itemLore = new ArrayList();

            for(String lore : config.getStringList("Settings.GhepNgocGui.Confirm.Lore")) {
                itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            int slot = config.getInt("Settings.GhepNgocGui.Confirm.Slot");
            ItemStack item = new ItemStack(mat, 1, (short)((byte)data));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(itemLore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
        }

        for(int slot : config.getIntegerList("Settings.GhepNgocGui.InputSlot")) {
            inv.setItem(slot, this.inputIcon);
        }

        int slot = config.getInt("Settings.GhepNgocGui.ResultSlot");
        inv.setItem(slot, this.resultIcon);
        p.openInventory(inv);
        if (!this.viewers.contains(p)) {
            this.viewers.add(p);
        }

    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();
        if (this.viewers.contains(p)) {
            if (e.getClickedInventory() != p.getOpenInventory().getBottomInventory()) {
                FileConfiguration config = FileManager.getFileConfig(FileManager.Files.CONFIG);
                e.setCancelled(true);
                ItemStack click = e.getCurrentItem();
                ItemStack cursor = e.getCursor();
                List<Integer> inputSlot = config.getIntegerList("Settings.GhepNgocGui.InputSlot");
                if (inputSlot.contains(e.getSlot())) {
                    if (Main.getPlugin().getGemsManager().isGems(click)) {
                        p.getInventory().addItem(new ItemStack[]{click.clone()});
                        e.getInventory().setItem(e.getSlot(), this.inputIcon);
                    }

                    if (!Utils.isEmptyItem(cursor)) {
                        e.getInventory().setItem(e.getSlot(), cursor.clone());
                        e.setCursor((ItemStack)null);
                    }

                    p.updateInventory();
                }

                int resultSlot = config.getInt("Settings.GhepNgocGui.ResultSlot");
                if (e.getSlot() == resultSlot) {
                    if (Main.getPlugin().getGemsManager().isGems(click)) {
                        p.getInventory().addItem(new ItemStack[]{click.clone()});
                        e.getInventory().setItem(e.getSlot(), this.resultIcon);
                    }

                    p.updateInventory();
                }

                int confirmSlot = config.getInt("Settings.GhepNgocGui.Confirm.Slot");
                if (e.getSlot() == confirmSlot) {
                    boolean checkGems = true;

                    for(int slot : inputSlot) {
                        ItemStack item = e.getInventory().getItem(slot);
                        if (!Main.getPlugin().getGemsManager().isGems(item)) {
                            checkGems = false;
                        }
                    }

                    if (!checkGems) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotGems")));
                        return;
                    }

                    String gemsType = "";
                    boolean checkType = true;

                    for(int slot : inputSlot) {
                        ItemStack item = e.getInventory().getItem(slot);
                        String type = Main.getPlugin().getGemsManager().getGemsType(item);
                        if (gemsType.isEmpty()) {
                            gemsType = type;
                        } else if (!gemsType.equalsIgnoreCase(type)) {
                            checkType = false;
                        }
                    }

                    if (!checkType) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.GemsNotEqualsType")));
                        return;
                    }

                    int gemsLevel = -1;
                    boolean checkLevel = true;

                    for(int slot : inputSlot) {
                        ItemStack item = e.getInventory().getItem(slot);
                        int level = Main.getPlugin().getGemsManager().getGemsLevel(item);
                        if (gemsLevel == -1) {
                            gemsLevel = level;
                        } else if (gemsLevel != level) {
                            checkLevel = false;
                        }
                    }

                    if (!checkLevel) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.GemsNotEqualsLevel")));
                        return;
                    }

                    if (Main.getPlugin().getGemsManager().getGems(gemsType).isMaxLevel(gemsLevel)) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.GemsMaxLevel")));
                        return;
                    }

                    double mergeChance = config.getDouble("Settings.GhepNgocGui.MergeChance");
                    boolean mergeSuccess = Utils.tile(mergeChance) || p.hasPermission("dosat.admin");
                    ItemStack result = mergeSuccess ? Main.getPlugin().getGemsManager().getGems(gemsType).getItem(gemsLevel + 1) : Main.getPlugin().getGemsManager().getGems(gemsType).getItem(gemsLevel);

                    for(int slot : inputSlot) {
                        ItemStack item = e.getInventory().getItem(slot);
                        if (item.getAmount() == 1) {
                            e.getInventory().setItem(slot, this.inputIcon);
                        } else {
                            item.setAmount(item.getAmount() - 1);
                        }
                    }

                    ItemStack item = e.getInventory().getItem(resultSlot);
                    if (Main.getPlugin().getGemsManager().isGems(item)) {
                        p.getInventory().addItem(new ItemStack[]{item.clone()});
                    }

                    String sound = mergeSuccess ? config.getString("Settings.GhepNgocGui.SuccessSound") : config.getString("Settings.GhepNgocGui.FailedSound");
                    p.playSound(p.getLocation(), Sound.valueOf(sound.toUpperCase()), 1.0F, 1.0F);
                    e.getInventory().setItem(resultSlot, result);
                    p.updateInventory();
                }

            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player)e.getPlayer();
        if (this.viewers.contains(p)) {
            this.viewers.remove(p);
        }

    }
}
