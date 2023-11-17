// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.task;

import camchua.dosat.Main;
import camchua.dosat.data.GemsData;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class UpdateStatsTask implements Runnable {
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            camchua.dosat.data.PlayerData playerData = camchua.dosat.data.PlayerData.getData(p);
            this.updateHealth(playerData);
            this.updateSpeed(playerData);
            this.updateDamage(playerData);
            this.updateDefense(playerData);
        }

    }

    void updateHealth(camchua.dosat.data.PlayerData playerData) {
        Player p = playerData.getPlayer();
        GemsData.GemsObject obj = GemsData.getData(p.getName());
        if (obj.getGemsList().containsKey("Health")) {
            Main.getPlugin().getGemsManager().getGems("Health").giveHideEffect(p);
            int gemsLevel = obj.getGemsList().get("Health");
            double cur_health = Main.getPlugin().getGemsManager().getGems("Health").getAttributesValue(gemsLevel);
            double pre_health = Main.getPlugin().getGemsManager().getGems("Health").getAttributesValue(playerData.getPreHealthLevel());
            if (playerData.getPreHealthLevel() == 0) {
                playerData.setPreHealthLevel(gemsLevel);
            }

            if (gemsLevel != playerData.getPreHealthLevel()) {
                gemsLevel = playerData.getPreHealthLevel();
            }

            double health = p.getMaxHealth();
            if (playerData.getPreHealth() == 0.0D) {
                playerData.setPreHealth(health);
            }

            if (health != playerData.getPreHealth()) {
                health = playerData.getPreHealth();
            }

            health = health - pre_health + cur_health;
            boolean set = playerData.getPreHealth() != health;
            playerData.setPreHealth(health);
            if (set) {
                p.setMaxHealth(health);
            }

        }
    }

    void updateSpeed(camchua.dosat.data.PlayerData playerData) {
        Player p = playerData.getPlayer();
        GemsData.GemsObject obj = GemsData.getData(p.getName());
        if (obj.getGemsList().containsKey("Speed")) {
            Main.getPlugin().getGemsManager().getGems("Speed").giveHideEffect(p);
            int gemsLevel = obj.getGemsList().get("Speed");
            float cur_speed = (float)Main.getPlugin().getGemsManager().getGems("Speed").getAttributesValue(gemsLevel);
            float pre_speed = (float)Main.getPlugin().getGemsManager().getGems("Speed").getAttributesValue(playerData.getPreSpeedLevel());
            if (playerData.getPreSpeedLevel() == 0) {
                playerData.setPreSpeedLevel(gemsLevel);
            }

            if (gemsLevel != playerData.getPreSpeedLevel()) {
                gemsLevel = playerData.getPreSpeedLevel();
            }

            float speed = p.getWalkSpeed();
            if (playerData.getPreSpeed() == 0.0F) {
                playerData.setPreSpeed(speed);
            }

            if (speed != playerData.getPreSpeed()) {
                speed = playerData.getPreSpeed();
            }

            speed = speed - pre_speed + cur_speed;
            boolean set = playerData.getPreSpeed() != speed;
            playerData.setPreSpeed(speed);
            if (set) {
                p.setWalkSpeed(speed);
            }

        }
    }

    void updateDamage(camchua.dosat.data.PlayerData playerData) {
        Player p = playerData.getPlayer();
        GemsData.GemsObject obj = GemsData.getData(p.getName());
        if (obj.getGemsList().containsKey("Damage")) {
            Main.getPlugin().getGemsManager().getGems("Damage").giveHideEffect(p);
            int gemsLevel = obj.getGemsList().get("Damage");
            double cur_damage = Main.getPlugin().getGemsManager().getGems("Damage").getAttributesValue(gemsLevel);
            double pre_damage = Main.getPlugin().getGemsManager().getGems("Damage").getAttributesValue(playerData.getPreDamageLevel());
            if (playerData.getPreDamageLevel() == 0) {
                playerData.setPreDamageLevel(gemsLevel);
            }

            if (gemsLevel != playerData.getPreDamageLevel()) {
                gemsLevel = playerData.getPreDamageLevel();
            }

            double damage = p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
            if (playerData.getPreDamage() == 0.0D) {
                playerData.setPreDamage(damage);
            }

            if (damage != playerData.getPreDamage()) {
                damage = playerData.getPreDamage();
            }

            damage = damage - pre_damage + cur_damage;
            boolean set = playerData.getPreDamage() != pre_damage;
            playerData.setPreDamage(damage);
            if (set) {
                p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
            }

        }
    }

    void updateDefense(camchua.dosat.data.PlayerData playerData) {
        Player p = playerData.getPlayer();
        GemsData.GemsObject obj = GemsData.getData(p.getName());
        if (obj.getGemsList().containsKey("Defense")) {
            Main.getPlugin().getGemsManager().getGems("Defense").giveHideEffect(p);
        }
    }
}
