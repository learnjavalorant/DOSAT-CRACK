// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.game;

import camchua.dosat.data.BountyData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerData {
    private Player player;
    private Game game;
    private Location loc;

    public PlayerData(Player p, Game g) {
        this.player = p;
        this.game = g;
        this.loc = p.getLocation();
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void restore(Player p) {
        if (BountyData.getData(p).isTruyNa()) {
            BountyData.getData(p).setTruyNa(false);
            p.teleport(this.loc);
            BountyData.getData(p).setTruyNa(true);
        } else {
            p.teleport(this.loc);
        }

    }
}
