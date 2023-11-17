// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.listener;

import camchua.dosat.Main;
import camchua.dosat.data.GemsData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        camchua.dosat.data.PlayerData.getData(p);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        camchua.dosat.data.PlayerData.delete(p);
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player)e.getEntity();
            GemsData.GemsObject obj = GemsData.getData(p.getName());
            if (obj.getGemsList().keySet().contains("Defense")) {
                int gemsLevel = obj.getGemsList().get("Defense");
                double defValue = Main.getPlugin().getGemsManager().getGems("Defense").getAttributesValue(gemsLevel);
                double dmg = e.getDamage();
                double newDmg = dmg - dmg * (defValue / (defValue + 50.0D));
                e.setDamage(newDmg);
            }
        }
    }
}
