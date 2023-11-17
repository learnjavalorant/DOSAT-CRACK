// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.data;

import camchua.dosat.Main;
import java.util.HashMap;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class PlayerData {
    private static HashMap<Player, camchua.dosat.data.PlayerData> data = new HashMap();
    private Player player;
    private double preHealth = 0.0D;
    private int preHealthLevel = 0;
    private float preSpeed = 0.0F;
    private int preSpeedLevel = 0;
    private double preDamage = 0.0D;
    private int preDamageLevel = 0;
    private double preDefense = 0.0D;
    private int preDefenseLevel = 0;

    public static camchua.dosat.data.PlayerData getData(Player p) {
        if (!data.containsKey(p)) {
            camchua.dosat.data.PlayerData playerData = new camchua.dosat.data.PlayerData(p);
            data.put(p, playerData);
            return playerData;
        } else {
            return (camchua.dosat.data.PlayerData)data.get(p);
        }
    }

    public static void delete(Player p) {
        if (data.containsKey(p)) {
            camchua.dosat.data.PlayerData playerData = (camchua.dosat.data.PlayerData)data.remove(p);
            double preHealth = playerData.getPreHealth();
            if (preHealth > 0.0D) {
                int preHealthLevel = playerData.getPreHealthLevel();
                double healthValue = Main.getPlugin().getGemsManager().getGems("Health").getAttributesValue(preHealthLevel);
                double health = preHealth - healthValue;
                p.setMaxHealth(health);
            }

            float preSpeed = playerData.getPreSpeed();
            if (preSpeed > 0.0F) {
                int preSpeedLevel = playerData.getPreSpeedLevel();
                float speedValue = (float)Main.getPlugin().getGemsManager().getGems("Speed").getAttributesValue(preSpeedLevel);
                float speed = preSpeed - speedValue;
                p.setWalkSpeed(speed);
            }

            double preDamage = playerData.getPreDamage();
            if (preDamage > 0.0D) {
                int preDamageLevel = playerData.getPreDamageLevel();
                double damageValue = Main.getPlugin().getGemsManager().getGems("Damage").getAttributesValue(preDamageLevel);
                double damage = preDamage - damageValue;
                p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
            }

            double preDefense = playerData.getPreDefense();
            if (preDefense > 0.0D) {
                int preDefenseLevel = playerData.getPreDefenseLevel();
                double defenseValue = Main.getPlugin().getGemsManager().getGems("Defense").getAttributesValue(preDefenseLevel);
                double defense = preDefense - defenseValue;
                p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(defense);
            }
        }

    }

    public PlayerData(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public double getPreHealth() {
        return this.preHealth;
    }

    public void setPreHealth(double preHealth) {
        this.preHealth = preHealth;
    }

    public int getPreHealthLevel() {
        return this.preHealthLevel;
    }

    public void setPreHealthLevel(int preHealthLevel) {
        this.preHealthLevel = preHealthLevel;
    }

    public float getPreSpeed() {
        return this.preSpeed;
    }

    public void setPreSpeed(float preSpeed) {
        this.preSpeed = preSpeed;
    }

    public int getPreSpeedLevel() {
        return this.preSpeedLevel;
    }

    public void setPreSpeedLevel(int preSpeedLevel) {
        this.preSpeedLevel = preSpeedLevel;
    }

    public double getPreDamage() {
        return this.preDamage;
    }

    public void setPreDamage(double preDamage) {
        this.preDamage = preDamage;
    }

    public int getPreDamageLevel() {
        return this.preDamageLevel;
    }

    public void setPreDamageLevel(int preDamageLevel) {
        this.preDamageLevel = preDamageLevel;
    }

    public double getPreDefense() {
        return this.preDefense;
    }

    public void setPreDefense(double preDefense) {
        this.preDefense = preDefense;
    }

    public int getPreDefenseLevel() {
        return this.preDefenseLevel;
    }

    public void setPreDefenseLevel(int preDefenseLevel) {
        this.preDefenseLevel = preDefenseLevel;
    }
}
