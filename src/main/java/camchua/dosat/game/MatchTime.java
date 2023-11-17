/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package camchua.dosat.game;

import camchua.dosat.Main;
import camchua.dosat.game.Game;
import camchua.dosat.manager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MatchTime
implements Runnable {
    private int timer = FileManager.getFileConfig(FileManager.Files.CONFIG).getInt("Settings.MatchTime");
    private int id;
    private Game game;

    public MatchTime(Game g) {
        this.game = g;
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)Main.getPlugin(), (Runnable)this, 20L, 20L);
    }

    @Override
    public void run() {
        --this.timer;
        if (this.game.getPlayers().size() != 2) {
            this.stop();
        }
        if (this.timer <= 0) {
            this.stop();
            this.game.stopDraw();
        }
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.id);
    }
}

