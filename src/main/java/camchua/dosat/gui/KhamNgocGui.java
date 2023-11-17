// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.gui;

import camchua.dosat.Main;
import camchua.dosat.data.GemsData;
import camchua.dosat.gems.Gems;
import camchua.dosat.manager.FileManager;
import camchua.dosat.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class KhamNgocGui implements Listener {
    private ItemStack emptyGems;
    private List<Player> viewers = new ArrayList();

    public KhamNgocGui() {
        this.loadEmptyGems();
    }

    public void loadEmptyGems() {
        FileConfiguration config = FileManager.getFileConfig(FileManager.Files.CONFIG);
        String id = config.getString("Settings.KhamNgocGui.EmptyGems.ID");
        Material mat = null;

        try {
            mat = Utils.matchMaterial(id);
        } catch (Exception var9) {
            Bukkit.getConsoleSender().sendMessage("KhamNgocGui > Error Item Id: Empty Gems");
            return;
        }

        int data = config.getInt("Settings.KhamNgocGui.EmptyGems.Data");
        String displayName = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.KhamNgocGui.EmptyGems.Name"));
        List<String> itemLore = new ArrayList();

        for(String lore : config.getStringList("Settings.KhamNgocGui.EmptyGems.Lore")) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));
        }

        ItemStack item = new ItemStack(mat, 1, (short)((byte)data));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(itemLore);
        item.setItemMeta(meta);
        this.emptyGems = item;
    }

    public void open(Player p) {
        FileConfiguration config = FileManager.getFileConfig(FileManager.Files.CONFIG);
        String title = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.KhamNgocGui.Name"));
        int rows = config.getInt("Settings.KhamNgocGui.Rows");
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, rows * 9, title);

        for(String key : config.getConfigurationSection("Settings.KhamNgocGui.Items").getKeys(false)) {
            String id = config.getString("Settings.KhamNgocGui.Items." + key + ".ID");
            Material mat = null;

            try {
                mat = Utils.matchMaterial(id);
            } catch (Exception var18) {
                Bukkit.getConsoleSender().sendMessage("KhamNgocGui > Error Item Id: " + key);
                continue;
            }

            int data = config.getInt("Settings.KhamNgocGui.Items." + key + ".Data");
            String displayName = ChatColor.translateAlternateColorCodes('&', config.getString("Settings.KhamNgocGui.Items." + key + ".Name"));
            List<String> itemLore = new ArrayList();

            for(String lore : config.getStringList("Settings.KhamNgocGui.Items." + key + ".Lore")) {
                itemLore.add(ChatColor.translateAlternateColorCodes('&', lore));
            }

            List<Integer> slots = config.getIntegerList("Settings.KhamNgocGui.Items." + key + ".Slot");
            ItemStack item = new ItemStack(mat, 1, (short)((byte)data));
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(itemLore);
            item.setItemMeta(meta);

            for(int slot : slots) {
                inv.setItem(slot, item);
            }
        }

        int[] slots = new int[]{config.getInt("Settings.KhamNgocGui.SpeedSlot"), config.getInt("Settings.KhamNgocGui.DamageSlot"), config.getInt("Settings.KhamNgocGui.DefenseSlot"), config.getInt("Settings.KhamNgocGui.HealthSlot")};

        for(int slot : slots) {
            inv.setItem(slot, this.emptyGems);
        }

        GemsData.GemsObject gemsData = GemsData.getData(p.getName());

        for(String gemsType : gemsData.getGemsList().keySet()) {
            int level = gemsData.getGemsList().get(gemsType);
            Gems gems = Main.getPlugin().getGemsManager().getGems(gemsType);
            int slot = config.getInt("Settings.KhamNgocGui." + gemsType + "Slot");
            ItemStack icon = gems.getItem(level);
            inv.setItem(slot, icon);
        }

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
                e.setCancelled(true);
                GemsData.GemsObject obj = GemsData.getData(p.getName());
                Set<String> gemsList = obj.getGemsList().keySet();
                ItemStack click = e.getCurrentItem();
                ItemStack cursor = e.getCursor();
                FileConfiguration config = FileManager.getFileConfig(FileManager.Files.CONFIG);
                String gems = "Speed";
                int slot = config.getInt("Settings.KhamNgocGui." + gems + "Slot");
                if (e.getSlot() == slot) {
                    if (gemsList.contains(gems)) {
                        int currentLevel = obj.getGemsList().get(gems);
                        Main.getPlugin().getGemsManager().getGems(gems).give(p, currentLevel, 1);
                        if (!Utils.isEmptyItem(cursor)) {
                            if (Main.getPlugin().getGemsManager().isGems(cursor, gems)) {
                                if (cursor.getAmount() == 1) {
                                    e.setCursor((ItemStack)null);
                                } else {
                                    cursor.setAmount(cursor.getAmount() - 1);
                                }

                                int gemsLevel = Main.getPlugin().getGemsManager().getGemsLevel(cursor);
                                camchua.dosat.data.PlayerData.delete(p);
                                obj.getGemsList().replace(gems, gemsLevel);
                                e.getInventory().setItem(slot, Main.getPlugin().getGemsManager().getGems(gems).getItem(gemsLevel));
                            } else {
                                camchua.dosat.data.PlayerData.delete(p);
                                obj.getGemsList().remove(gems);
                                e.getInventory().setItem(slot, this.emptyGems);
                            }
                        } else {
                            camchua.dosat.data.PlayerData.delete(p);
                            obj.getGemsList().remove(gems);
                            e.getInventory().setItem(slot, this.emptyGems);
                        }
                    } else if (!Utils.isEmptyItem(cursor) && Main.getPlugin().getGemsManager().isGems(cursor, gems)) {
                        if (cursor.getAmount() == 1) {
                            e.setCursor((ItemStack)null);
                        } else {
                            cursor.setAmount(cursor.getAmount() - 1);
                        }

                        int gemsLevel = Main.getPlugin().getGemsManager().getGemsLevel(cursor);
                        camchua.dosat.data.PlayerData.delete(p);
                        obj.getGemsList().put(gems, gemsLevel);
                        e.getInventory().setItem(slot, Main.getPlugin().getGemsManager().getGems(gems).getItem(gemsLevel));
                    }

                    p.updateInventory();
                }

                gems = "Damage";
                slot = config.getInt("Settings.KhamNgocGui." + gems + "Slot");
                if (e.getSlot() == slot) {
                    if (gemsList.contains(gems)) {
                        int currentLevel = obj.getGemsList().get(gems);
                        Main.getPlugin().getGemsManager().getGems(gems).give(p, currentLevel, 1);
                        if (!Utils.isEmptyItem(cursor)) {
                            if (Main.getPlugin().getGemsManager().isGems(cursor, gems)) {
                                if (cursor.getAmount() == 1) {
                                    e.setCursor((ItemStack)null);
                                } else {
                                    cursor.setAmount(cursor.getAmount() - 1);
                                }

                                int gemsLevel = Main.getPlugin().getGemsManager().getGemsLevel(cursor);
                                camchua.dosat.data.PlayerData.delete(p);
                                obj.getGemsList().replace(gems, gemsLevel);
                                e.getInventory().setItem(slot, Main.getPlugin().getGemsManager().getGems(gems).getItem(gemsLevel));
                            } else {
                                camchua.dosat.data.PlayerData.delete(p);
                                obj.getGemsList().remove(gems);
                                e.getInventory().setItem(slot, this.emptyGems);
                            }
                        } else {
                            camchua.dosat.data.PlayerData.delete(p);
                            obj.getGemsList().remove(gems);
                            e.getInventory().setItem(slot, this.emptyGems);
                        }
                    } else if (!Utils.isEmptyItem(cursor) && Main.getPlugin().getGemsManager().isGems(cursor, gems)) {
                        if (cursor.getAmount() == 1) {
                            e.setCursor((ItemStack)null);
                        } else {
                            cursor.setAmount(cursor.getAmount() - 1);
                        }

                        int gemsLevel = Main.getPlugin().getGemsManager().getGemsLevel(cursor);
                        camchua.dosat.data.PlayerData.delete(p);
                        obj.getGemsList().put(gems, gemsLevel);
                        e.getInventory().setItem(slot, Main.getPlugin().getGemsManager().getGems(gems).getItem(gemsLevel));
                    }

                    p.updateInventory();
                }

                gems = "Health";
                slot = config.getInt("Settings.KhamNgocGui." + gems + "Slot");
                if (e.getSlot() == slot) {
                    if (gemsList.contains(gems)) {
                        int currentLevel = obj.getGemsList().get(gems);
                        Main.getPlugin().getGemsManager().getGems(gems).give(p, currentLevel, 1);
                        if (!Utils.isEmptyItem(cursor)) {
                            if (Main.getPlugin().getGemsManager().isGems(cursor, gems)) {
                                if (cursor.getAmount() == 1) {
                                    e.setCursor((ItemStack)null);
                                } else {
                                    cursor.setAmount(cursor.getAmount() - 1);
                                }

                                int gemsLevel = Main.getPlugin().getGemsManager().getGemsLevel(cursor);
                                camchua.dosat.data.PlayerData.delete(p);
                                obj.getGemsList().replace(gems, gemsLevel);
                                e.getInventory().setItem(slot, Main.getPlugin().getGemsManager().getGems(gems).getItem(gemsLevel));
                            } else {
                                camchua.dosat.data.PlayerData.delete(p);
                                obj.getGemsList().remove(gems);
                                e.getInventory().setItem(slot, this.emptyGems);
                            }
                        } else {
                            camchua.dosat.data.PlayerData.delete(p);
                            obj.getGemsList().remove(gems);
                            e.getInventory().setItem(slot, this.emptyGems);
                        }
                    } else if (!Utils.isEmptyItem(cursor) && Main.getPlugin().getGemsManager().isGems(cursor, gems)) {
                        if (cursor.getAmount() == 1) {
                            e.setCursor((ItemStack)null);
                        } else {
                            cursor.setAmount(cursor.getAmount() - 1);
                        }

                        int gemsLevel = Main.getPlugin().getGemsManager().getGemsLevel(cursor);
                        camchua.dosat.data.PlayerData.delete(p);
                        obj.getGemsList().put(gems, gemsLevel);
                        e.getInventory().setItem(slot, Main.getPlugin().getGemsManager().getGems(gems).getItem(gemsLevel));
                    }

                    p.updateInventory();
                }

                gems = "Defense";
                slot = config.getInt("Settings.KhamNgocGui." + gems + "Slot");
                if (e.getSlot() == slot) {
                    if (gemsList.contains(gems)) {
                        int currentLevel = obj.getGemsList().get(gems);
                        Main.getPlugin().getGemsManager().getGems(gems).give(p, currentLevel, 1);
                        if (!Utils.isEmptyItem(cursor)) {
                            if (Main.getPlugin().getGemsManager().isGems(cursor, gems)) {
                                if (cursor.getAmount() == 1) {
                                    e.setCursor((ItemStack)null);
                                } else {
                                    cursor.setAmount(cursor.getAmount() - 1);
                                }

                                int gemsLevel = Main.getPlugin().getGemsManager().getGemsLevel(cursor);
                                camchua.dosat.data.PlayerData.delete(p);
                                obj.getGemsList().replace(gems, gemsLevel);
                                e.getInventory().setItem(slot, Main.getPlugin().getGemsManager().getGems(gems).getItem(gemsLevel));
                            } else {
                                camchua.dosat.data.PlayerData.delete(p);
                                obj.getGemsList().remove(gems);
                                e.getInventory().setItem(slot, this.emptyGems);
                            }
                        } else {
                            camchua.dosat.data.PlayerData.delete(p);
                            obj.getGemsList().remove(gems);
                            e.getInventory().setItem(slot, this.emptyGems);
                        }
                    } else if (!Utils.isEmptyItem(cursor) && Main.getPlugin().getGemsManager().isGems(cursor, gems)) {
                        if (cursor.getAmount() == 1) {
                            e.setCursor((ItemStack)null);
                        } else {
                            cursor.setAmount(cursor.getAmount() - 1);
                        }

                        int gemsLevel = Main.getPlugin().getGemsManager().getGemsLevel(cursor);
                        camchua.dosat.data.PlayerData.delete(p);
                        obj.getGemsList().put(gems, gemsLevel);
                        e.getInventory().setItem(slot, Main.getPlugin().getGemsManager().getGems(gems).getItem(gemsLevel));
                    }

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
