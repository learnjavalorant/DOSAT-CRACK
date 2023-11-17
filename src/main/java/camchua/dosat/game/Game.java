// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.game;

import camchua.dosat.Main;
import camchua.dosat.data.BountyData;
import camchua.dosat.manager.FileManager;
import camchua.dosat.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Game {
    private String name;
    private Location spawn1;
    private Location spawn2;
    private Player player1;
    private Player player2;
    private List<Player> players;
    private Status status;
    private StartingTask starting;
    private boolean active;
    private double price;
    private PriceType pricetype;
    private String loser;

    public Game(String name, Location spawn1, Location spawn2, boolean active) {
        this.name = name;
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
        this.status = Status.WAITING;
        this.active = active;
        this.loser = "";
    }

    public void inviting(Player p1, Player p2) {
        this.status = Status.INVITING;
        this.player1 = p1;
        this.player2 = p2;
        Main.getPlugin().getPlayer().put(p1, new PlayerData(p1, this));
        Main.getPlugin().getPlayer().put(p2, new PlayerData(p2, this));
        this.players = new ArrayList();
        this.players.add(p1);
        this.players.add(p2);
    }

    public void remove_inviting(Player p) {
        this.status = Status.WAITING;

        try {
            Main.getPlugin().getPlayer().remove(p);
        } catch (Exception var3) {
        }

    }

    public void join() {
        if (this.player1.isInsideVehicle()) {
            this.player1.leaveVehicle();
        }

        this.player1.teleport(this.spawn1);
        this.freeze(this.player1);
        if (this.player2.isInsideVehicle()) {
            this.player2.leaveVehicle();
        }

        this.player2.teleport(this.spawn2);
        this.freeze(this.player2);
        BountyData p1data = BountyData.getData(this.player1);
        if (p1data.isDoSat()) {
            p1data.setDoSat(false);
            Main.getPlugin().scoreboard.getTeam("RedGlowing").removeEntry(this.player1.getName());
            this.player1.setGlowing(false);
        }

        BountyData p2data = BountyData.getData(this.player2);
        if (p2data.isDoSat()) {
            p2data.setDoSat(false);
            Main.getPlugin().scoreboard.getTeam("RedGlowing").removeEntry(this.player2.getName());
            this.player2.setGlowing(false);
        }

        this.countdown();
    }

    public void leave(Player p, boolean lose) {
        if (lose) {
            this.loser = p.getName();
        }

        this.players.remove(p);
        ((PlayerData)Main.getPlugin().getPlayer().get(p)).restore(p);
        Main.getPlugin().getPlayer().remove(p);
        BountyData.getData(p).setPlaying(false);
    }

    public void countdown() {
        this.status = Status.COUNTDOWN;
        this.starting = new StartingTask(this);
    }

    public void start() {
        this.status = Status.RUNNING;
        this.unFreeze(this.player1);
        this.unFreeze(this.player2);
        this.heal(this.player1);
        this.heal(this.player2);
        this.player1.setGameMode(GameMode.SURVIVAL);
        this.player2.setGameMode(GameMode.SURVIVAL);
        this.player1.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.MatchStart")));
        this.player2.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.MatchStart")));
    }

    public void stop() {
        this.status = Status.WAITING;
        Player winner = null;

        for(Player win : this.players) {
            winner = win;
            this.heal(win);
        }

        this.leave(winner, false);
        this.players.clear();
        Utils.giveBalance(winner, this.pricetype, this.price * 2.0D);
        String broadcast = FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.WinBroadcast");
        if (!broadcast.equalsIgnoreCase("none")) {
            Bukkit.broadcastMessage(broadcast.replace("&", "§").replace("<winner>", winner.getName()).replace("<loser>", this.loser).replace("<price>", String.valueOf(this.price * 2.0D)).replace("<pricetype>", PriceType.getFormat(this.pricetype)));
        }

    }

    public void forceStop() {
        this.status = Status.WAITING;
        Player winner = null;

        for(Player win : this.players) {
            winner = win;
            this.heal(win);
            Main.getPlugin().getPlayer().remove(win);
        }

        try {
            this.leave(this.player1, false);
        } catch (Exception var5) {
        }

        try {
            this.leave(this.player2, false);
        } catch (Exception var4) {
        }

        this.players.clear();
        Utils.giveBalance(winner, this.pricetype, this.price * 2.0D);
    }

    public void stopDraw() {
        this.status = Status.WAITING;
        this.player1.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.Draw")));
        this.player2.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.Draw")));
        Utils.giveBalance(this.player1, this.pricetype, this.price);
        Utils.giveBalance(this.player2, this.pricetype, this.price);

        try {
            this.players.remove(this.player1);
            ((PlayerData)Main.getPlugin().getPlayer().get(this.player1)).restore(this.player1);
            Main.getPlugin().getPlayer().remove(this.player1);
        } catch (Exception var3) {
        }

        try {
            this.players.remove(this.player2);
            ((PlayerData)Main.getPlugin().getPlayer().get(this.player2)).restore(this.player2);
            Main.getPlugin().getPlayer().remove(this.player2);
        } catch (Exception var2) {
        }

        this.players.clear();
    }

    private void heal(Player p) {
        for(PotionEffect ef : p.getActivePotionEffects()) {
            p.removePotionEffect(ef.getType());
        }

        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
    }

    public void freeze(Player p) {
        p.setGameMode(GameMode.SURVIVAL);
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 23423525, -10, false, false));
        p.setWalkSpeed(1.0E-4F);
        p.setFoodLevel(1);
        p.setAllowFlight(false);
        p.setFlying(false);
    }

    public void unFreeze(Player p) {
        p.removePotionEffect(PotionEffectType.JUMP);
        p.setWalkSpeed(0.2F);
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public String getName() {
        return this.name;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setSpawn1(Location loc) {
        this.spawn1 = loc;
    }

    public void setSpawn2(Location loc) {
        this.spawn2 = loc;
    }

    public void setStatus(Status s) {
        this.status = s;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double p) {
        this.price = p;
    }

    public void setPriceType(PriceType pt) {
        this.pricetype = pt;
    }

    public Player getPlayer1() {
        return this.player1;
    }

    public Player getPlayer2() {
        return this.player2;
    }

    public PriceType getPriceType() {
        return this.pricetype;
    }
}
