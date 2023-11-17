// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.data;

import camchua.dosat.data.BountyData;
import java.util.HashMap;
import org.bukkit.entity.Player;

public class BountyData {
    private static HashMap<Player, BountyData> data = new HashMap();
    private Player player;
    private boolean dosat;
    private boolean truyna;
    private int time;
    private double bounty;
    private boolean inviting;
    private int inviting_time;
    private String inviting_player;
    private double inviting_price;
    private boolean inviting_send;
    private boolean playing;

    public static BountyData getData(Player p) {
        return (BountyData)data.get(p);
    }

    public static HashMap<Player, BountyData> getData() {
        return data;
    }

    public static boolean containsData(Player p) {
        return data.containsKey(p);
    }

    public static void addData(Player p, boolean ds, boolean tn, int t, double b) {
        data.put(p, new BountyData(p, ds, tn, t, b));
    }

    public BountyData(Player p, boolean ds, boolean tn, int t, double b) {
        this.player = p;
        this.dosat = ds;
        this.truyna = tn;
        this.time = t;
        this.bounty = b;
        this.inviting = false;
        this.inviting_time = 0;
        this.inviting_player = "";
        this.inviting_price = 0.0D;
        this.inviting_send = false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isDoSat() {
        return this.dosat;
    }

    public void setDoSat(boolean ds) {
        this.dosat = ds;
    }

    public boolean isTruyNa() {
        return this.truyna;
    }

    public void setTruyNa(boolean tn) {
        this.truyna = tn;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int t) {
        this.time = t;
    }

    public double getBounty() {
        return this.bounty;
    }

    public void setBounty(double b) {
        this.bounty = b;
    }

    public boolean isInviting() {
        return this.inviting;
    }

    public void setInviting(boolean i) {
        this.inviting = i;
    }

    public int getInvitingTime() {
        return this.inviting_time;
    }

    public void setInvitingTime(int t) {
        this.inviting_time = t;
    }

    public String getInvitingPlayer() {
        return this.inviting_player;
    }

    public void setInvitingPlayer(String p) {
        this.inviting_player = p;
    }

    public double getInvitingPrice() {
        return this.inviting_price;
    }

    public void setInvitingPrice(double p) {
        this.inviting_price = p;
    }

    public boolean isInvitingSend() {
        return this.inviting_send;
    }

    public void setInvitingSend(boolean s) {
        this.inviting_send = s;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void setPlaying(boolean p) {
        this.playing = p;
    }
}
