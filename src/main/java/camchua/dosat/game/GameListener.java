// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.game;

import camchua.dosat.Main;
import camchua.dosat.data.BountyData;
import camchua.dosat.manager.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {
    @EventHandler(
            priority = EventPriority.LOW
    )
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        PlayerData pd = (PlayerData)Main.getPlugin().getPlayer().get(p);
        if (pd != null) {
            Game g = pd.getGame();
            g.leave(p, true);
            g.stop();
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (Main.getPlugin().getPlayer().get(p) != null) {
            Game g = ((PlayerData)Main.getPlugin().getPlayer().get(p)).getGame();
            g.leave(p, true);
            g.stop();
        } else {
            BountyData pdata = BountyData.getData(p);
            if (pdata.isInviting()) {
                BountyData tdata = BountyData.getData(Bukkit.getPlayer(pdata.getInvitingPlayer()));
                pdata.setInviting(false);
                tdata.setInviting(false);
                Bukkit.getPlayer(pdata.getInvitingPlayer()).sendMessage(ChatColor.translateAlternateColorCodes('&', FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Message.InviteTimeOut")));
            }
        }

    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (!e.getMessage().contains("/pk dongy") && !e.getMessage().contains("/pk tuchoi") && Main.getPlugin().getPlayer().get(p) != null && ((PlayerData)Main.getPlugin().getPlayer().get(p)).getGame().getStatus() != Status.INVITING && ((PlayerData)Main.getPlugin().getPlayer().get(p)).getGame().getStatus() != Status.WAITING) {
            e.setCancelled(true);
        }

    }
}
