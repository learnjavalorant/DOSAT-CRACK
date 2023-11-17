// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat;

import camchua.dosat.Main;
import camchua.dosat.data.BountyData;
import camchua.dosat.data.DataStorage;
import camchua.dosat.data.GemsData;
import camchua.dosat.data.TruyNaData;
import camchua.dosat.game.*;
import camchua.dosat.gems.Gems;
import camchua.dosat.gui.ChonPhongGUI;
import camchua.dosat.gui.GhepNgocGui;
import camchua.dosat.gui.KhamNgocGui;
import camchua.dosat.gui.ThachDauGUI;
import camchua.dosat.gui.TruyNaGui;
import camchua.dosat.gui.TruyNaListGui;
import camchua.dosat.listener.EventListener;
import camchua.dosat.manager.FileManager;
import camchua.dosat.manager.GemsManager;
import camchua.dosat.task.UpdateStatsTask;
import camchua.dosat.utils.ItemBuilder;
import camchua.dosat.utils.TruyNaInput;
import camchua.dosat.utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Main extends JavaPlugin implements Listener {
    private static Main main;
    public static Economy econ;
    public static PlayerPoints points;
    private Map<Player, PlayerData> players = new HashMap();
    private LinkedHashMap<String, Game> games = new LinkedHashMap();
    public Scoreboard scoreboard;
    private static boolean premium = false;
    private static boolean run = false;
    private boolean actionbar;
    private boolean o = false;
    private List<String> n = new ArrayList();
    public HashMap<String, String> invite = new HashMap();
    public HashMap<String, String> inviting = new HashMap();
    private HashMap<String, String> trackLocation = new HashMap();
    private HashMap<Player, TruyNaInput> truynaInput = new HashMap();
    private GemsManager gemsManager;
    private KhamNgocGui khamNgocGui;
    private GhepNgocGui ghepNgocGui;

    public static Main getPlugin() {
        return main;
    }

    public Map<Player, PlayerData> getPlayer() {
        return this.players;
    }

    public Map<String, Game> getGames() {
        return this.games;
    }

    public void onEnable() {
        main = this;
        Utils.checkVersion();
        FileManager.setup(this);
        this.actionbar = FileManager.getFileConfig(FileManager.Files.CONFIG).getBoolean("Settings.ActionBar");
        this.setupEconomy();
        this.gemsManager = new GemsManager(this);
        this.khamNgocGui = new KhamNgocGui();
        this.ghepNgocGui = new GhepNgocGui();
        setupPlayerPoints();
        this.loadArenas();
        this.disableWarnASW();
        this.scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
        this.scoreboard.registerNewTeam("RedGlowing");
        this.scoreboard.getTeam("RedGlowing").setPrefix("" + ChatColor.RED);
        this.scoreboard.getTeam("RedGlowing").setColor(ChatColor.RED);
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getPluginManager().registerEvents(this.khamNgocGui, this);
        Bukkit.getPluginManager().registerEvents(this.ghepNgocGui, this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            boolean ds = DataStorage.getData(p.getName()).getBoolean("DoSat");
            boolean tn = DataStorage.getData(p.getName()).getBoolean("TruyNa");
            int time = DataStorage.getData(p.getName()).getInt("Time");
            double bounty = DataStorage.getData(p.getName()).getDouble("Bounty");
            BountyData.addData(p, ds, tn, time, bounty);
            camchua.dosat.data.PlayerData.getData(p);
        }

        TruyNaData.loadFromConfig();
        GemsData.loadFromConfig();
        Bukkit.getConsoleSender().sendMessage("§f-----------------------------------");
        Bukkit.getConsoleSender().sendMessage("§eDoSat is sold by Di Hoa Store");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§fAuthor: §eCamChua_VN, Keny");
        Bukkit.getConsoleSender().sendMessage("§fVersion: §ev1.4.1");
        Bukkit.getConsoleSender().sendMessage("§fServer version: §e1.12 -> 1.20");
        Bukkit.getConsoleSender().sendMessage("§fSupport: §eBukkit, Spigot, Paper");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§fWebsite: §ewww.dihoastore.com");
        Bukkit.getConsoleSender().sendMessage("§f-----------------------------------");
        premium = true;
        run = true;
        int var8 = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    BountyData data = BountyData.getData(p);
                    if (data.isDoSat() && Main.this.actionbar) {
                        String dosat = ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.DoSat"));
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(dosat));
                        if (!Main.this.scoreboard.getTeam("RedGlowing").hasEntry(p.getName())) {
                            Main.this.scoreboard.getTeam("RedGlowing").addEntry(p.getName());
                        }
                    }

                    if (data.isTruyNa()) {
                        data.setTime(data.getTime() - 1);
                        if (data.getTime() <= 0) {
                            data.setTruyNa(false);
                            data.setBounty(0.0D);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.HetTruyNa")));
                        }
                    }

                    if (data.isInviting()) {
                        data.setInvitingTime(data.getInvitingTime() - 1);
                        if (data.getInvitingTime() <= 0) {
                            data.setInviting(false);
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.InviteTimeOut")));
                            if (Main.this.getPlayer().containsKey(p)) {
                                ((PlayerData)Main.this.getPlayer().get(p)).getGame().remove_inviting(p);
                            }
                        }
                    }
                }

            }
        }, 20L, 20L);
        int var9 = Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
            public void run() {
                ThachDauGUI.updateInv();
                TruyNaGui.updateInv();
                TruyNaListGui.updateInv();
            }
        }, 0L, 100L);
        int track = Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            String trackMsg = ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TrackLocation"));

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (this.trackLocation.containsKey(p.getName())) {
                    String target = (String) this.trackLocation.get(p.getName());
                    if (Bukkit.getOfflinePlayer(target).isOnline() && TruyNaData.isTruyNa(target)) {
                        Player targetPlayer = Bukkit.getPlayer(target);
                        int x = targetPlayer.getLocation().getBlockX();
                        int y = targetPlayer.getLocation().getBlockY();
                        int z = targetPlayer.getLocation().getBlockZ();
                        String world = targetPlayer.getLocation().getWorld().getName();
                        int distance = 0;
                        if (world.equals(p.getLocation().getWorld().getName())) {
                            distance = (int) targetPlayer.getLocation().distance(p.getLocation());
                        }

                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(trackMsg.replace("<player>", target).replace("<x>", "" + x).replace("<y>", "" + y).replace("<z>", "" + z).replace("<world>", world).replace("<distance>", "" + distance)));
                    }
                }
            }

        }, 0L, 20L);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new UpdateStatsTask(), 20L, 20L);
        this.o = run;
        if (this.n.isEmpty() && "a".equals("a") && Integer.parseInt("1") == 1 && !this.o) {
            this.n = null;
        }

        if (this.n == null) {
            PluginManager pm = Bukkit.getServer().getPluginManager();
            pm.disablePlugin(this);
        }

    }

    public void onDisable() {
        this.scoreboard.getTeam("RedGlowing").unregister();

        for(Entry<Player, BountyData> e : BountyData.getData().entrySet()) {
            FileConfiguration data = DataStorage.getData(((Player)e.getKey()).getName());
            data.set("DoSat", ((BountyData)e.getValue()).isDoSat());
            data.set("TruyNa", ((BountyData)e.getValue()).isTruyNa());
            data.set("Time", ((BountyData)e.getValue()).getTime());
            data.set("Bounty", ((BountyData)e.getValue()).getBounty());
            DataStorage.saveData(data, ((Player)e.getKey()).getName());
        }

        for(Player p : Bukkit.getOnlinePlayers()) {
            camchua.dosat.data.PlayerData.delete(p);
        }

        TruyNaData.saveToConfig();
        GemsData.saveToConfig();
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                return false;
            } else {
                econ = (Economy)rsp.getProvider();
                return econ != null;
            }
        }
    }

    public static boolean setupPlayerPoints() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
        if (plugin != null && plugin.isEnabled()) {
            points = (PlayerPoints)PlayerPoints.class.cast(plugin);
            return points != null;
        } else {
            return false;
        }
    }

    public void openGui(Player p) {
        Inventory inv = Bukkit.createInventory((InventoryHolder)null, 45, ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.PkGui.Name")));

        for(String key : FileManager.getFileConfig(FileManager.Files.CONFIG).getConfigurationSection("Settings.PkGui.Items").getKeys(false)) {
            ItemStack item = ItemBuilder.build(FileManager.Files.CONFIG, "Settings.PkGui.Items." + key, p.getName());
            Object s = FileManager.getFileConfig(FileManager.Files.CONFIG).get("Settings.PkGui.Items." + key + ".Slot");
            if (s instanceof Integer) {
                inv.setItem((Integer)s, item);
            } else {
                for(Object slot : (List)s) {
                    inv.setItem((Integer) slot, item);
                }
            }
        }

        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();
        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.PkGui.Name")))) {
            e.setCancelled(true);
            if (e.getSlot() == FileManager.getFileConfig(FileManager.Files.CONFIG).getInt("Settings.PkGui.Items.PkOn.Slot")) {
                if (!p.hasPermission("pk.on")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                    return;
                }

                if (BountyData.getData(p).isDoSat()) {
                    return;
                }

                BountyData.getData(p).setDoSat(true);
                this.scoreboard.getTeam("RedGlowing").addEntry(p.getName());
                p.setGlowing(true);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.BatDoSat")));
            }

            if (e.getSlot() == FileManager.getFileConfig(FileManager.Files.CONFIG).getInt("Settings.PkGui.Items.PkOff.Slot")) {
                if (BountyData.getData(p).isTruyNa()) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.IsTruyNa")));
                    return;
                }

                if (!BountyData.getData(p).isDoSat()) {
                    return;
                }

                if (BountyData.getData(p).isPlaying()) {
                    return;
                }

                BountyData.getData(p).setDoSat(false);
                this.scoreboard.getTeam("RedGlowing").removeEntry(p.getName());
                p.setGlowing(false);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TatDoSat")));
            }

            if (e.getSlot() == FileManager.getFileConfig(FileManager.Files.CONFIG).getInt("Settings.PkGui.Items.RemoveBounty.Slot")) {
                this.xoatoi(p);
            }

            if (e.getSlot() == FileManager.getFileConfig(FileManager.Files.CONFIG).getInt("Settings.PkGui.Items.PkThachDau.Slot")) {
                new ThachDauGUI(p);
            }
        }

        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaGui.Name")))) {
            e.setCancelled(true);
            TruyNaGui gui = (TruyNaGui)TruyNaGui.users.get(p);
            if (e.getSlot() >= 0 && e.getSlot() <= 44) {
                if (e.getCurrentItem() == null) {
                    return;
                }

                if (e.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                String player = this.parsePlayer(e.getCurrentItem());
                if (player.equalsIgnoreCase(p.getName())) {
                    return;
                }

                if (TruyNaData.isTruyNa(player)) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.PlayerIsTruyNa")));
                    return;
                }

                if (this.truynaInput.containsKey(p)) {
                    this.truynaInput.remove(p);
                }

                this.truynaInput.put(p, new TruyNaInput(player));
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TypingPriceType")));
                p.closeInventory();
            }

            if (e.getSlot() == 50) {
                if (gui.currpage >= TruyNaGui.pages.size() - 1) {
                    return;
                }

                ++gui.currpage;
                p.openInventory((Inventory)TruyNaGui.pages.get(gui.currpage));
            }

            if (e.getSlot() == 48 && gui.currpage > 0) {
                --gui.currpage;
                p.openInventory((Inventory)ThachDauGUI.pages.get(gui.currpage));
            }
        }

        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.TruyNaListGui.Name")))) {
            e.setCancelled(true);
            TruyNaListGui gui = (TruyNaListGui)TruyNaListGui.users.get(p);
            if (e.getSlot() >= 0 && e.getSlot() <= 44) {
                if (e.getCurrentItem() == null) {
                    return;
                }

                if (e.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                String player = this.parsePlayer(e.getCurrentItem());
                if (player.equalsIgnoreCase(p.getName())) {
                    return;
                }

                if (this.trackLocation.containsKey(p.getName())) {
                    this.trackLocation.remove(p.getName());
                }

                this.trackLocation.put(p.getName(), player);
                p.closeInventory();
            }

            if (e.getSlot() == 50) {
                if (gui.currpage >= TruyNaGui.pages.size() - 1) {
                    return;
                }

                ++gui.currpage;
                p.openInventory((Inventory)TruyNaGui.pages.get(gui.currpage));
            }

            if (e.getSlot() == 48 && gui.currpage > 0) {
                --gui.currpage;
                p.openInventory((Inventory)ThachDauGUI.pages.get(gui.currpage));
            }

            if (e.getSlot() == 49) {
                if (this.trackLocation.containsKey(p.getName())) {
                    this.trackLocation.remove(p.getName());
                }

                p.closeInventory();
            }
        }

        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.ThachDauGui.Name")))) {
            e.setCancelled(true);
            ThachDauGUI gui = (ThachDauGUI)ThachDauGUI.users.get(p);
            if (e.getSlot() >= 0 && e.getSlot() <= 44) {
                if (e.getCurrentItem() == null) {
                    return;
                }

                if (e.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                String player = this.parsePlayer(e.getCurrentItem());
                if (player.equalsIgnoreCase(p.getName())) {
                    return;
                }

                if (this.invite.containsKey(p.getName())) {
                    this.invite.replace(p.getName(), player);
                } else {
                    this.invite.put(p.getName(), player);
                }

                new ChonPhongGUI(p);
            }

            if (e.getSlot() == 50) {
                if (gui.currpage >= ThachDauGUI.pages.size() - 1) {
                    return;
                }

                ++gui.currpage;
                p.openInventory((Inventory)ThachDauGUI.pages.get(gui.currpage));
            }

            if (e.getSlot() == 48 && gui.currpage > 0) {
                --gui.currpage;
                p.openInventory((Inventory)ThachDauGUI.pages.get(gui.currpage));
            }
        }

        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.ChonPhongGui.Name")))) {
            e.setCancelled(true);
            ChonPhongGUI gui = (ChonPhongGUI)ChonPhongGUI.users.get(p);
            if (e.getSlot() >= 0 && e.getSlot() <= 44) {
                if (e.getCurrentItem() == null) {
                    return;
                }

                if (e.getCurrentItem().getType() == Material.AIR) {
                    return;
                }

                NBTItem nbt = new NBTItem(e.getCurrentItem());
                String room = nbt.getString("DoSat_Room_Name");
                String status = nbt.getString("DoSat_Room_Status");
                if (status.equals("Inviting") || status.equals("Playing")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.RoomBusy")));
                    return;
                }

                if (this.inviting.containsKey(p.getName())) {
                    this.inviting.replace(p.getName(), (String)this.invite.get(p.getName()) + ":none:0:" + room);
                } else {
                    this.inviting.put(p.getName(), (String)this.invite.get(p.getName()) + ":none:0:" + room);
                }

                p.closeInventory();
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TypingPriceType")));
            }

            if (e.getSlot() == 50) {
                if (gui.currpage >= gui.pages.size() - 1) {
                    return;
                }

                ++gui.currpage;
                p.openInventory((Inventory)gui.pages.get(gui.currpage));
            }

            if (e.getSlot() == 48 && gui.currpage > 0) {
                --gui.currpage;
                p.openInventory((Inventory)gui.pages.get(gui.currpage));
            }
        }

    }

    @EventHandler
    public void onAsyncPlayerChat(final AsyncPlayerChatEvent e) {
        final Player p = e.getPlayer();
        if (this.inviting.containsKey(p.getName())) {
            e.setCancelled(true);
            if (e.getMessage().toLowerCase().equals("huy")) {
                this.inviting.remove(p.getName());
                return;
            }

            String in = (String)this.inviting.get(p.getName());
            final String player = in.split(":")[0];
            final String pricetype = in.split(":")[1];
            double price = Double.parseDouble(in.split(":")[2]);
            String room = in.split(":")[3];
            if (pricetype.equalsIgnoreCase("none")) {
                if (!e.getMessage().equalsIgnoreCase("money") && !e.getMessage().equalsIgnoreCase("points")) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TypingPriceType")));
                    return;
                }

                this.inviting.replace(p.getName(), player + ":" + e.getMessage() + ":" + price + ":" + room);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TypingBet")));
                return;
            }

            if (price <= 0.0D) {
                if (Double.parseDouble(e.getMessage()) <= 0.0D) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TypingBet")));
                    return;
                }

                final Game g = Utils.getArena(room);
                if (g.getStatus() != Status.WAITING) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.RoomBusy")));
                    return;
                }

                try {
                    (new BukkitRunnable() {
                        public void run() {
                            g.inviting(p, Bukkit.getPlayer(player));
                            g.setPriceType(PriceType.getByKey(pricetype.toUpperCase()));
                            p.performCommand("pk thachdau " + (String)Main.this.invite.get(p.getName()) + " " + Double.parseDouble(e.getMessage()) + " " + pricetype);
                        }
                    }).runTaskLater(this, 0L);
                } catch (Exception var11) {
                    var11.printStackTrace();
                }

                this.inviting.remove(p.getName());
                return;
            }
        }

        if (this.truynaInput.containsKey(p)) {
            e.setCancelled(true);
            if (e.getMessage().toLowerCase().equals("huy")) {
                this.truynaInput.remove(p.getName());
                return;
            }

            final TruyNaInput input = (TruyNaInput)this.truynaInput.get(p);
            int step = input.getStep();
            switch(step) {
                case 1:
                    if (!e.getMessage().equalsIgnoreCase("money") && !e.getMessage().equalsIgnoreCase("points")) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TypingPriceType")));
                        return;
                    }

                    input.setPriceType(e.getMessage().toLowerCase());
                    input.nextStep();
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TypingBet")));
                    break;
                case 2:
                    try {
                        final double priceValue = Double.parseDouble(e.getMessage());
                        if (priceValue <= 0.0D) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TypingBet")));
                            return;
                        }

                        if (Utils.getBalance(p, PriceType.valueOf(input.getPriceType().toUpperCase())) < priceValue) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotMoney")));
                            return;
                        }

                        (new BukkitRunnable() {
                            public void run() {
                                Utils.takeBalance(p, PriceType.valueOf(input.getPriceType().toUpperCase()), priceValue);
                                TruyNaData.truyna(input.getTarget(), p.getName(), input.getPriceType(), priceValue);
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TruyNaBroadcast")).replace("<player>", p.getName()).replace("<target>", input.getTarget()).replace("<price>", "" + priceValue).replace("<pricetype>", PriceType.getFormat(PriceType.valueOf(input.getPriceType().toUpperCase()))));
                                Main.this.truynaInput.remove(p);
                            }
                        }).runTaskLater(this, 1L);
                    } catch (Exception var12) {
                        var12.printStackTrace();
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TypingBet")));
                    }
            }
        }

    }

    public String parsePlayer(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        return nbti.hasNBTData() && nbti.hasKey("DoSat_Player") ? nbti.getString("DoSat_Player") : "";
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.o && !premium) {
            return true;
        } else if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                return true;
            } else {
                this.openGui((Player)sender);
                return true;
            }
        } else {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("off")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    Player p = (Player)sender;
                    if (BountyData.getData(p).isTruyNa()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.IsTruyNa")));
                        return true;
                    }

                    if (!BountyData.getData(p).isDoSat()) {
                        return true;
                    }

                    if (BountyData.getData(p).isPlaying()) {
                        return true;
                    }

                    BountyData.getData(p).setDoSat(false);
                    this.scoreboard.getTeam("RedGlowing").removeEntry(p.getName());
                    p.setGlowing(false);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TatDoSat")));
                    return true;
                }

                if (args[0].equalsIgnoreCase("on")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    if (!sender.hasPermission("pk.on")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    Player p = (Player)sender;
                    if (BountyData.getData(p).isDoSat()) {
                        return true;
                    }

                    BountyData.getData(p).setDoSat(true);
                    this.scoreboard.getTeam("RedGlowing").addEntry(p.getName());
                    p.setGlowing(true);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.BatDoSat")));
                    return true;
                }

                if (args[0].equalsIgnoreCase("xoatoi")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    Player p = (Player)sender;
                    this.xoatoi(p);
                    return true;
                }

                if (args[0].equalsIgnoreCase("huytruyna")) {
                    if (!FileManager.getFileConfig(FileManager.Files.CONFIG).getBoolean("Settings.Feature.TruyNaNangCao")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.FeatureOff")));
                        return true;
                    }

                    if (args.length == 1) {
                        return true;
                    }

                    if (!(sender instanceof Player)) {
                        String target = args[1];
                        if (!TruyNaData.isTruyNa(target)) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TargetNotTruyNa")));
                            return true;
                        }

                        TruyNaData.remove(target);
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.RemoveTruyNa")).replace("<player>", "Console").replace("<target>", target));
                        return true;
                    }

                    Player p = (Player)sender;
                    boolean bypass = p.isOp() || p.hasPermission("dosat.admin") || p.hasPermission("*");
                    String target = args[1];
                    TruyNaData.TruyNaObject truyNaObject = TruyNaData.getTruyNa(target);
                    if (!truyNaObject.getOwner().equals(p.getName()) && !bypass) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.CantRemoveTruyNa")));
                        return true;
                    }

                    TruyNaData.remove(target);
                    String priceType = truyNaObject.getPriceType();
                    double priceValue = truyNaObject.getPriceValue();
                    Utils.giveBalance(Bukkit.getOfflinePlayer(truyNaObject.getOwner()), PriceType.valueOf(priceType.toUpperCase()), priceValue);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.RemoveTruyNa")).replace("<player>", p.getName()).replace("<target>", target));
                    return true;
                }

                if (args[0].equalsIgnoreCase("truyna")) {
                    if (!FileManager.getFileConfig(FileManager.Files.CONFIG).getBoolean("Settings.Feature.TruyNaNangCao")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.FeatureOff")));
                        return true;
                    }

                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    Player p = (Player)sender;
                    if (args.length == 1) {
                        new TruyNaGui(p);
                        return true;
                    }

                    if (args.length == 2) {
                        String arg = args[1];
                        if (arg.equalsIgnoreCase("list")) {
                            new TruyNaListGui(p);
                        }

                        return true;
                    }

                    if (args.length == 3) {
                        return true;
                    }

                    String target = args[1];
                    String priceType = args[2];
                    double priceValue = Math.max(1.0D, Double.parseDouble(args[3]));
                    if (!Bukkit.getOfflinePlayer(target).isOnline()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotOnline")));
                        return true;
                    }

                    if (TruyNaData.isTruyNa(target)) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.PlayerIsTruyNa")));
                        return true;
                    }

                    if (!priceType.equalsIgnoreCase("money") && !priceType.equalsIgnoreCase("points")) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.InvalidPriceType")));
                        return true;
                    }

                    if (Utils.getBalance(p, PriceType.valueOf(priceType.toUpperCase())) < priceValue) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotMoney")));
                        return true;
                    }

                    Utils.takeBalance(p, PriceType.valueOf(priceType.toUpperCase()), priceValue);
                    TruyNaData.truyna(target, p.getName(), priceType, priceValue);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TruyNaBroadcast")).replace("<player>", p.getName()).replace("<target>", target).replace("<price>", "" + priceValue).replace("<pricetype>", PriceType.getFormat(PriceType.valueOf(priceType.toUpperCase()))));
                    return true;
                }

                if (args[0].equalsIgnoreCase("create")) {
                    if (!sender.hasPermission("dosat.admin")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    if (args.length == 1) {
                        return true;
                    }

                    String mess = args[1];
                    FileConfiguration arenas = FileManager.getFileConfig(FileManager.Files.ARENAS);
                    if (arenas.get("Arenas." + mess) != null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaExist")));
                        return true;
                    }

                    arenas.set("Arenas." + mess + ".Active", false);
                    arenas.set("Arenas." + mess + ".Spawn.Spawn1", "world:0:0:0:0:0");
                    arenas.set("Arenas." + mess + ".Spawn.Spawn2", "world:0:0:0:0:0");
                    FileManager.saveFileConfig(arenas, FileManager.Files.ARENAS);
                    Utils.createArena(mess);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaCreated")).replace("<name>", mess));
                    return true;
                }

                if (args[0].equalsIgnoreCase("delete")) {
                    if (!sender.hasPermission("dosat.admin")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    if (args.length == 1) {
                        return true;
                    }

                    String mess = args[1];
                    FileConfiguration arenas = FileManager.getFileConfig(FileManager.Files.ARENAS);
                    if (arenas.get("Arenas." + mess) == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaNotExist")));
                        return true;
                    }

                    arenas.set("Arenas." + mess, (Object)null);
                    FileManager.saveFileConfig(arenas, FileManager.Files.ARENAS);
                    Utils.deleteArena(mess);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaDeleted")).replace("<name>", mess));
                    return true;
                }

                if (args[0].equalsIgnoreCase("setspawn1")) {
                    if (!sender.hasPermission("dosat.admin")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    if (args.length == 1) {
                        return true;
                    }

                    String mess = args[1];
                    FileConfiguration arenas = FileManager.getFileConfig(FileManager.Files.ARENAS);
                    if (arenas.get("Arenas." + mess) == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaNotExist")));
                        return true;
                    }

                    Player p = (Player)sender;
                    String message = p.getLocation().getWorld().getName();
                    String pricetype = String.valueOf(p.getLocation().getX());
                    String y = String.valueOf(p.getLocation().getY());
                    String z = String.valueOf(p.getLocation().getZ());
                    String yaw = String.valueOf(p.getLocation().getYaw());
                    String pitch = String.valueOf(p.getLocation().getPitch());
                    String loc = message + ":" + pricetype + ":" + y + ":" + z + ":" + yaw + ":" + pitch;
                    arenas.set("Arenas." + mess + ".Spawn.Spawn1", loc);
                    FileManager.saveFileConfig(arenas, FileManager.Files.ARENAS);
                    Utils.setSpawn1(mess, new Location(Bukkit.getWorld(message), Double.parseDouble(pricetype), Double.parseDouble(y), Double.parseDouble(z), Float.parseFloat(yaw), Float.parseFloat(pitch)));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaSpawnSet")).replace("<name>", mess));
                    return true;
                }

                if (args[0].equalsIgnoreCase("setspawn2")) {
                    if (!sender.hasPermission("dosat.admin")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    if (args.length == 1) {
                        return true;
                    }

                    String mess = args[1];
                    FileConfiguration arenas = FileManager.getFileConfig(FileManager.Files.ARENAS);
                    if (arenas.get("Arenas." + mess) == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaNotExist")));
                        return true;
                    }

                    Player p = (Player)sender;
                    String message = p.getLocation().getWorld().getName();
                    String pricetype = String.valueOf(p.getLocation().getX());
                    String y = String.valueOf(p.getLocation().getY());
                    String z = String.valueOf(p.getLocation().getZ());
                    String yaw = String.valueOf(p.getLocation().getYaw());
                    String pitch = String.valueOf(p.getLocation().getPitch());
                    String loc = message + ":" + pricetype + ":" + y + ":" + z + ":" + yaw + ":" + pitch;
                    arenas.set("Arenas." + mess + ".Spawn.Spawn2", loc);
                    FileManager.saveFileConfig(arenas, FileManager.Files.ARENAS);
                    Utils.setSpawn2(mess, new Location(Bukkit.getWorld(message), Double.parseDouble(pricetype), Double.parseDouble(y), Double.parseDouble(z), Float.parseFloat(yaw), Float.parseFloat(pitch)));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaSpawnSet")).replace("<name>", mess));
                    return true;
                }

                if (args[0].equalsIgnoreCase("toggle")) {
                    if (!sender.hasPermission("dosat.admin")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    if (args.length == 1) {
                        return true;
                    }

                    String mess = args[1];
                    FileConfiguration arenas = FileManager.getFileConfig(FileManager.Files.ARENAS);
                    if (arenas.get("Arenas." + mess) == null) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaNotExist")));
                        return true;
                    }

                    if (arenas.getBoolean("Arenas." + mess + ".Active")) {
                        arenas.set("Arenas." + mess + ".Active", false);
                    } else {
                        arenas.set("Arenas." + mess + ".Active", true);
                    }

                    FileManager.saveFileConfig(arenas, FileManager.Files.ARENAS);
                    Utils.setActive(mess, arenas.getBoolean("Arenas." + mess + ".Active"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ArenaToggle")).replace("<name>", mess).replace("<active>", String.valueOf(arenas.getBoolean("Arenas." + mess + ".Active"))));
                    return true;
                }

                if (args[0].equalsIgnoreCase("list")) {
                    if (!sender.hasPermission("dosat.admin")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    FileConfiguration arenas = FileManager.getFileConfig(FileManager.Files.ARENAS);

                    for(String name : getPlugin().getGames().keySet()) {
                        for(String message : FileManager.getFileConfig(FileManager.Files.CONFIG).getStringList("Message.ArenaList")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message.replace("<arena>", name).replace("<status>", Utils.getArena(name).getStatus().toString()).replace("<active>", String.valueOf(Utils.getArena(name).isActive())).replace("<spawn1>", arenas.getString("Arenas." + name + ".Spawn.Spawn1")).replace("<spawn2>", arenas.getString("Arenas." + name + ".Spawn.Spawn2"))));
                        }
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("thachdau")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    Player p = (Player)sender;
                    if (args.length == 1) {
                        new ThachDauGUI(p);
                        return true;
                    }

                    String name = args[1];
                    if (name.equalsIgnoreCase(p.getName())) {
                        return true;
                    }

                    if (args.length == 2) {
                        if (!Bukkit.getOfflinePlayer(name).isOnline()) {
                            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotOnline")));
                            return true;
                        }

                        if (this.invite.containsKey(p.getName())) {
                            this.invite.replace(p.getName(), name);
                        } else {
                            this.invite.put(p.getName(), name);
                        }

                        new ChonPhongGUI(p);
                        return true;
                    }

                    double price = Double.parseDouble(args[2]);
                    String pricetype = args.length == 3 ? "Money" : args[3];
                    if (price <= 0.0D) {
                        return true;
                    }

                    Game g = ((PlayerData)this.getPlayer().get(p)).getGame();
                    if (Utils.getBalance(Bukkit.getPlayer(name), PriceType.getByKey(pricetype.toUpperCase())) < price) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.PlayerNotMoney")));
                        g.remove_inviting(p);
                        g.remove_inviting(Bukkit.getPlayer(name));
                        return true;
                    }

                    if (Utils.getBalance(p, PriceType.getByKey(pricetype.toUpperCase())) < price) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotMoney")));
                        g.remove_inviting(p);
                        g.remove_inviting(Bukkit.getPlayer(name));
                        return true;
                    }

                    if (BountyData.getData(p).isInviting()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.IsInviting")));
                        g.remove_inviting(p);
                        g.remove_inviting(Bukkit.getPlayer(name));
                        return true;
                    }

                    if (BountyData.getData(p).isTruyNa()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.IsInviting")));
                        g.remove_inviting(p);
                        g.remove_inviting(Bukkit.getPlayer(name));
                        return true;
                    }

                    if (!BountyData.getData(Bukkit.getPlayer(name)).isInviting() && !BountyData.getData(Bukkit.getPlayer(name)).isPlaying()) {
                        BountyData pdata = BountyData.getData(p);
                        BountyData tdata = BountyData.getData(Bukkit.getPlayer(name));
                        pdata.setInviting(true);
                        tdata.setInviting(true);
                        pdata.setInvitingTime(10);
                        tdata.setInvitingTime(10);
                        pdata.setInvitingPrice(price);
                        tdata.setInvitingPrice(price);
                        pdata.setInvitingPlayer(name);
                        tdata.setInvitingPlayer(p.getName());
                        pdata.setInvitingSend(true);
                        tdata.setInvitingSend(false);
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.Invited")).replace("<player>", name).replace("<money>", String.valueOf(price)).replace("<type>", PriceType.getFormat(g.getPriceType())));

                        for(String mess : FileManager.getFileConfig(FileManager.Files.CONFIG).getStringList("Message.ReceiveInvited")) {
                            Bukkit.getPlayer(name).sendMessage(ChatColor.translateAlternateColorCodes('&', mess).replace("<player>", p.getName()).replace("<money>", String.valueOf(price)).replace("<type>", PriceType.getFormat(g.getPriceType())));
                        }

                        return true;
                    }

                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.PlayerIsInviting")));
                    g.remove_inviting(p);
                    g.remove_inviting(Bukkit.getPlayer(name));
                    return true;
                }

                if (args[0].equalsIgnoreCase("tuchoi")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    Player p = (Player)sender;
                    BountyData data = BountyData.getData(p);
                    if (data.isInvitingSend()) {
                        return true;
                    }

                    BountyData tdata = BountyData.getData(Bukkit.getPlayer(data.getInvitingPlayer()));
                    if (!data.isInviting()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoInvite")));
                        return true;
                    }

                    data.setInviting(false);
                    tdata.setInviting(false);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.RejectInvite")));
                    Bukkit.getPlayer(data.getInvitingPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.InviteRejected")));
                    if (this.getPlayer().containsKey(p)) {
                        ((PlayerData)this.getPlayer().get(p)).getGame().remove_inviting(p);
                        this.getPlayer().remove(p);
                    }

                    if (this.getPlayer().containsKey(Bukkit.getPlayer(data.getInvitingPlayer()))) {
                        ((PlayerData)this.getPlayer().get(Bukkit.getPlayer(data.getInvitingPlayer()))).getGame().remove_inviting(Bukkit.getPlayer(data.getInvitingPlayer()));
                        this.getPlayer().remove(Bukkit.getPlayer(data.getInvitingPlayer()));
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("dongy")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    Player p = (Player)sender;
                    BountyData data = BountyData.getData(p);
                    if (data.isInvitingSend()) {
                        return true;
                    }

                    if (data.isTruyNa()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.IsTruyNa")));
                        return true;
                    }

                    BountyData tdata = BountyData.getData(Bukkit.getPlayer(data.getInvitingPlayer()));
                    if (!data.isInviting()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoInvite")));
                        return true;
                    }

                    if (tdata.isTruyNa()) {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.PlayerIsTruyNa")));
                        return true;
                    }

                    data.setInviting(false);
                    tdata.setInviting(false);
                    Game g = ((PlayerData)this.getPlayer().get(p)).getGame();
                    g.join();
                    g.setPrice(data.getInvitingPrice());
                    Utils.takeBalance(p, g.getPriceType(), data.getInvitingPrice());
                    Utils.takeBalance(Bukkit.getPlayer(data.getInvitingPlayer()), g.getPriceType(), data.getInvitingPrice());
                    data.setPlaying(true);
                    tdata.setPlaying(true);
                    return true;
                }

                if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("dosat.admin")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    sender.sendMessage("§aReloading...");

                    try {
                        FileManager.setup(this);
                        this.actionbar = FileManager.getFileConfig(FileManager.Files.CONFIG).getBoolean("Settings.ActionBar");
                        this.gemsManager = new GemsManager(this);
                        this.khamNgocGui.loadEmptyGems();

                        for(Player player : Bukkit.getOnlinePlayers()) {
                            camchua.dosat.data.PlayerData.delete(player);
                        }

                        sender.sendMessage("§aReload complete.");
                    } catch (Exception var24) {
                        var24.printStackTrace();
                        sender.sendMessage("§cReload failed.");
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("help")) {
                    if (!sender.hasPermission("dosat.help")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    for(String mess : FileManager.getFileConfig(FileManager.Files.CONFIG).getStringList("Message.Help")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', mess));
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("teamlist")) {
                    sender.sendMessage("Team List:");

                    for(Team t : this.scoreboard.getTeams()) {
                        sender.sendMessage("- " + t.getName());
                        sender.sendMessage(" Entry:" + t.getEntries());
                        sender.sendMessage(" Color:" + t.getColor().name());
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("team")) {
                    Player p = (Player)sender;
                    sender.sendMessage("Team: " + this.scoreboard.getPlayerTeam(p).getName());
                    return true;
                }

                if (args[0].equalsIgnoreCase("givengoc")) {
                    if (!FileManager.getFileConfig(FileManager.Files.CONFIG).getBoolean("Settings.Feature.KhamNgoc")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.FeatureOff")));
                        return true;
                    }

                    if (!sender.hasPermission("dosat.givengoc")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoPerm")));
                        return true;
                    }

                    if (args.length >= 1 && args.length <= 3) {
                        return true;
                    }

                    String type = args[1];
                    String targetName = "";
                    if (args.length > 4) {
                        targetName = args[4];
                    }

                    if (!this.gemsManager.hasType(type)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NoGemsType")));
                        return true;
                    }

                    if (!targetName.isEmpty() && !Bukkit.getOfflinePlayer(targetName).isOnline()) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotOnline")));
                        return true;
                    }

                    Player target = targetName.isEmpty() ? (Player)sender : Bukkit.getPlayer(targetName);
                    Gems gems = this.gemsManager.getGems(type);
                    int level = Integer.parseInt(args[2]);
                    int amount = Integer.parseInt(args[3]);
                    gems.give(target, level, amount);
                    return true;
                }

                if (args[0].equalsIgnoreCase("khamngoc")) {
                    if (!FileManager.getFileConfig(FileManager.Files.CONFIG).getBoolean("Settings.Feature.KhamNgoc")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.FeatureOff")));
                        return true;
                    }

                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    this.khamNgocGui.open((Player)sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("ghepngoc")) {
                    if (!FileManager.getFileConfig(FileManager.Files.CONFIG).getBoolean("Settings.Feature.KhamNgoc")) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.FeatureOff")));
                        return true;
                    }

                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    this.ghepNgocGui.open((Player)sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("nenmau")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    Player p = (Player)sender;
                    p.setHealthScaled(true);
                    p.setHealthScale(20.0D);
                    return true;
                }

                if (args[0].equalsIgnoreCase("huynenmau")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotPlayer")));
                        return true;
                    }

                    Player p = (Player)sender;
                    p.setHealthScaled(false);
                    return true;
                }
            }

            return false;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!BountyData.containsData(p)) {
            boolean dosat = false;
            boolean truyna = false;
            int time = 0;
            double bounty = 0.0D;
            if (p.hasPlayedBefore() && DataStorage.checkData(p.getName())) {
                dosat = DataStorage.getData(p.getName()).getBoolean("DoSat");
                truyna = DataStorage.getData(p.getName()).getBoolean("TruyNa");
                time = DataStorage.getData(p.getName()).getInt("Time");
                bounty = DataStorage.getData(p.getName()).getDouble("Bounty");
            }

            BountyData.addData(p, dosat, truyna, time, bounty);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (p.getKiller() instanceof Player) {
            Player killer = p.getKiller();
            String killerIP = killer.getAddress().getAddress().getHostAddress();
            String targetIP = p.getAddress().getAddress().getHostAddress();
            if (FileManager.getFileConfig(FileManager.Files.CONFIG).getBoolean("Settings.CheckIP") && killerIP.equals(targetIP)) {
                return;
            }

            if (BountyData.getData(p).isPlaying() && BountyData.getData(killer).isPlaying()) {
                return;
            }

            if (!BountyData.getData(killer).isDoSat()) {
                return;
            }

            if (this.inPvpZone(p)) {
                return;
            }

            BountyData data = BountyData.getData(killer);
            BountyData pdata = BountyData.getData(p);
            if (data.isDoSat() && pdata.isDoSat()) {
                return;
            }

            if (!data.isTruyNa()) {
                killer.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.BatTruyNa")));
            }

            data.setTruyNa(true);
            data.setTime(data.getTime() + FileManager.getFileConfig(FileManager.Files.CONFIG).getInt("Settings.BountyTime"));
            data.setBounty(data.getBounty() + (double)FileManager.getFileConfig(FileManager.Files.CONFIG).getInt("Settings.BountyMoney"));
        }

    }

    @EventHandler
    public void truyNaCheck(PlayerDeathEvent e) {
        if (FileManager.getFileConfig(FileManager.Files.CONFIG).getBoolean("Settings.Feature.TruyNaNangCao")) {
            Player p = e.getEntity();
            if (p.getKiller() instanceof Player) {
                Player killer = p.getKiller();
                if (!TruyNaData.isTruyNa(p.getName())) {
                    return;
                }

                TruyNaData.TruyNaObject truyNaObject = TruyNaData.getTruyNa(p.getName());
                String priceType = truyNaObject.getPriceType();
                double priceValue = truyNaObject.getPriceValue();
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.TruyNaReceiveBroadcast")).replace("<player>", killer.getName()).replace("<target>", p.getName()).replace("<price>", "" + priceValue).replace("<pricetype>", PriceType.getFormat(PriceType.valueOf(priceType.toUpperCase()))));
                Utils.giveBalance(killer, PriceType.valueOf(priceType.toUpperCase()), priceValue);
                TruyNaData.remove(p.getName());
            }

        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (BountyData.getData(p) != null && BountyData.getData(p).isTruyNa()) {
            e.setCancelled(true);
        }

    }

    public void xoatoi(Player p) {
        if (!BountyData.getData(p).isTruyNa()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotTruyNa")));
        } else if (econ.getBalance(p) < BountyData.getData(p).getBounty()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.NotMoney")));
        } else {
            BountyData data = BountyData.getData(p);
            econ.withdrawPlayer(p, data.getBounty());
            data.setBounty(0.0D);
            data.setTime(0);
            data.setTruyNa(false);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.XoaToi")));
        }

    }

    public void loadArenas() {
        FileConfiguration arenas = FileManager.getFileConfig(FileManager.Files.ARENAS);
        boolean hasData = arenas.getConfigurationSection("Arenas") != null;
        if (hasData) {
            for(String arena : arenas.getConfigurationSection("Arenas").getKeys(false)) {
                boolean active = arenas.getBoolean("Arenas." + arena + ".Active");
                Location spawn1 = this.getLocFromString(arenas.getString("Arenas." + arena + ".Spawn.Spawn1"));
                Location spawn2 = this.getLocFromString(arenas.getString("Arenas." + arena + ".Spawn.Spawn2"));
                Game g = new Game(arena, spawn1, spawn2, active);
                this.getGames().put(arena, g);
            }
        }

    }

    public Location getLocFromString(String s) {
        String[] h = s.split(":");
        return new Location(Bukkit.getServer().getWorld(h[0]), Double.parseDouble(h[1]), Double.parseDouble(h[2]), Double.parseDouble(h[3]), Float.parseFloat(h[4]), Float.parseFloat(h[5]));
    }

    @EventHandler(
            priority = EventPriority.MONITOR,
            ignoreCancelled = true
    )
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            Entity def = e.getEntity();
            Entity atk = e.getDamager();
            if (e.getDamager() instanceof Projectile) {
                Projectile pro = (Projectile)e.getDamager();
                atk = (Entity)pro.getShooter();
            }

            if (def instanceof Player && atk instanceof Player) {
                Player defender = (Player)def;
                Player attacker = (Player)atk;
                BountyData attackerData = BountyData.getData(attacker);
                BountyData defenderData = BountyData.getData(defender);
                if (!attackerData.isDoSat() && defenderData.isDoSat()) {
                    return;
                }

                if (this.inPvpZone(attacker) && this.inPvpZone(defender)) {
                    return;
                }

                if (defenderData.isPlaying()) {
                    return;
                }

                if (attackerData.isDoSat()) {
                    return;
                }

                if (TruyNaData.isTruyNa(defender.getName())) {
                    return;
                }

                e.setCancelled(true);
            }
        }

    }

    public boolean inPvpZone(Player p) {
        for(String pvpzone : FileManager.getFileConfig(FileManager.Files.CONFIG).getStringList("Settings.PvPZone")) {
            for(String pvp : Utils.listRegion(p)) {
                if (pvp.equals(pvpzone)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void disableWarnASW() {
        File aswf = new File(this.getDataFolder().getParentFile(), "\\AutoSaveWorld\\config.yml");
        if (aswf.exists()) {
            YamlConfiguration asw = new YamlConfiguration();

            try {
                asw.load(aswf);
            } catch (Exception var5) {
            }

            if (asw.getBoolean("networkwatcher.mainthreadnetaccess.warn")) {
                asw.set("networkwatcher.mainthreadnetaccess.warn", false);

                try {
                    asw.save(aswf);
                } catch (Exception var4) {
                }

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "asw reload");
            }
        }

    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (p.isSneaking() && e.getRightClicked() != null && e.getRightClicked() instanceof Player) {
            Player target = (Player)e.getRightClicked();
            p.performCommand("pk thachdau " + target.getName());
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        BountyData data = BountyData.getData(e.getPlayer());
        if (data.isDoSat() && !data.isTruyNa()) {
            data.setDoSat(false);
            this.scoreboard.getTeam("RedGlowing").removeEntry(e.getPlayer().getName());
            e.getPlayer().setGlowing(false);
        }

    }

    @EventHandler(
            priority = EventPriority.LOW
    )
    public void onTruyNaDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        BountyData data = BountyData.getData(p);
        if (data.isTruyNa() && p.getKiller() instanceof Player) {
            Player killer = p.getKiller();
            double bounty = data.getBounty();
            econ.depositPlayer(killer, bounty);
            killer.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.KillBounty")).replace("<player>", p.getName()).replace("<bounty>", String.valueOf(bounty)));
        }

    }

    public GemsManager getGemsManager() {
        return this.gemsManager;
    }

    public KhamNgocGui getKhamNgocGui() {
        return this.khamNgocGui;
    }

    public GhepNgocGui getGhepNgocGui() {
        return this.ghepNgocGui;
    }
}
