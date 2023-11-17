// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.game;

import camchua.dosat.manager.FileManager;
import org.bukkit.Bukkit;

public enum PriceType {
    MONEY("Money"),
    POINTS("Points");

    private String key;

    private PriceType(String k) {
        this.key = k;
    }

    public String getKey() {
        return this.key;
    }

    public static String getFormat(PriceType pt) {
        return FileManager.getFileConfig(FileManager.Files.CONFIG).getString("Settings.Format." + pt.getKey());
    }

    public static PriceType getByKey(String key) {
        PriceType[] var4;
        for(PriceType pt : var4 = values()) {
            if (pt.getKey().equalsIgnoreCase(key)) {
                return pt;
            }
        }

        Bukkit.broadcastMessage("null");
        return null;
    }

    // $FF: synthetic method
    private static PriceType[] $values() {
        return new PriceType[]{MONEY, POINTS};
    }
}
