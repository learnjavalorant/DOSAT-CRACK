// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.utils;

import camchua.dosat.Main;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import camchua.dosat.game.Game;
import camchua.dosat.game.PriceType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {
    private static boolean LEGACY = true;
    private static String VERSION = "";

    public static void createArena(String name) {
        Game g = new Game(name, (Location)null, (Location)null, false);
        Main.getPlugin().getGames().put(name, g);
    }

    public static void deleteArena(String name) {
        Main.getPlugin().getGames().remove(name);
    }

    public static Game getArena(String name) {
        return (Game)Main.getPlugin().getGames().get(name);
    }

    public static void setSpawn1(String arena, Location loc) {
        Game g = getArena(arena);
        g.setSpawn1(loc);
    }

    public static void setSpawn2(String arena, Location loc) {
        Game g = getArena(arena);
        g.setSpawn2(loc);
    }

    public static void setActive(String arena, boolean active) {
        Game g = getArena(arena);
        g.setActive(active);
    }

    public static void giveBalance(OfflinePlayer p, PriceType pt, double v) {
        if (pt == PriceType.MONEY) {
            Main.econ.depositPlayer(p, v);
        }

        if (pt == PriceType.POINTS) {
            Main.points.getAPI().give(p.getUniqueId(), (int)v);
        }

    }

    public static void takeBalance(Player p, PriceType pt, double v) {
        if (pt == PriceType.MONEY) {
            Main.econ.withdrawPlayer(p, v);
        }

        if (pt == PriceType.POINTS) {
            Main.points.getAPI().take(p.getUniqueId(), (int)v);
        }

    }

    public static double getBalance(Player p, PriceType pt) {
        if (pt == PriceType.MONEY) {
            return Main.econ.getBalance(p);
        } else {
            return pt == PriceType.POINTS ? (double)Main.points.getAPI().look(p.getUniqueId()) : 0.0D;
        }
    }

    public static void checkVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        VERSION = version;
        String[] versionSplit = version.split("\\.");
        int major = Integer.parseInt(versionSplit[0]);
        int minor = Integer.parseInt(versionSplit[1]);
        int micro = 0;

        try {
            micro = Integer.parseInt(versionSplit[2]);
        } catch (Exception var6) {
        }

        if (minor >= 13) {
            LEGACY = false;
        }

    }

    public static boolean isLegacy() {
        return LEGACY;
    }

    public static Material matchMaterial(String mat) {
        return LEGACY ? Material.matchMaterial(mat) : Material.matchMaterial(mat, LEGACY);
    }

    public static List<String> listRegion(Player p) {
        List<String> listRegion = new ArrayList();
        if (LEGACY) {
            try {
                Class wgBukkit = Class.forName("com.sk89q.worldguard.bukkit.WGBukkit");
                Object regionManager = wgBukkit.getDeclaredMethod("getRegionManager", World.class).invoke((Object)null, p.getWorld());
                Object applicableRegions = regionManager.getClass().getMethod("getApplicableRegions", Location.class).invoke(regionManager, p.getLocation());
                Iterator iter = (Iterator)applicableRegions.getClass().getMethod("iterator").invoke(applicableRegions);

                while(iter.hasNext()) {
                    Object protectedRegion = iter.next();
                    String regionName = (String)protectedRegion.getClass().getMethod("getId").invoke(protectedRegion);
                    listRegion.add(regionName);
                }
            } catch (Exception var14) {
            }
        } else {
            try {
                Class bukkitAdapter = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
                Class wgClass = Class.forName("com.sk89q.worldguard.WorldGuard");
                Object weLocation = bukkitAdapter.getDeclaredMethod("adapt", Location.class).invoke((Object)null, p.getLocation());
                Object wgInstance = wgClass.getDeclaredMethod("getInstance").invoke((Object)null);
                Object flatroom = wgInstance.getClass().getMethod("getPlatform").invoke(wgInstance);
                Object regionContainer = flatroom.getClass().getMethod("getRegionContainer").invoke(flatroom);
                Object regionQuery = regionContainer.getClass().getMethod("createQuery").invoke(regionContainer);
                Object applicableRegionSet = regionQuery.getClass().getMethod("getApplicableRegions", weLocation.getClass()).invoke(regionQuery, weLocation);
                Iterator iter = (Iterator)applicableRegionSet.getClass().getMethod("iterator").invoke(applicableRegionSet);

                while(iter.hasNext()) {
                    Object protectedRegion = iter.next();
                    String regionName = (String)protectedRegion.getClass().getMethod("getId").invoke(protectedRegion);
                    listRegion.add(regionName);
                }
            } catch (Exception var13) {
            }
        }

        return listRegion;
    }

    public static boolean isEmptyItem(ItemStack item) {
        if (item == null) {
            return true;
        } else {
            return item.getType().equals(Material.AIR);
        }
    }

    public static boolean tile(double tile) {
        double rate = tile * 100.0D;
        int random = (new Random()).nextInt(10000);
        return (double)random <= rate;
    }
}
