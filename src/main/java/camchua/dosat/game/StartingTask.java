// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.game;

import camchua.dosat.Main;
import camchua.dosat.manager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StartingTask implements Runnable {
    private int timer = FileManager.getFileConfig(FileManager.Files.CONFIG).getInt("Settings.StartingTime");
    private int id;
    private Game game;

    public StartingTask(Game g) {
        this.game = g;
        this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), this, 20L, 20L);
    }

    public void run() {
        --this.timer;
        if (this.game.getPlayers().size() != 2) {
            this.stop();

            for(Player p : this.game.getPlayers()) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.ForceStop")));
            }

            this.game.forceStop();
        }

        if (this.timer <= 0) {
            this.stop();
            this.game.start();
        } else {
            for(Player p : this.game.getPlayers()) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.Countdown")).replace("<time>", String.valueOf(this.timer)));
            }
        }

    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(this.id);
    }
}
