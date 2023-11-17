// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.data;

import camchua.dosat.data.TruyNaData;
import camchua.dosat.manager.FileManager;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;

public class TruyNaData {
    private static HashMap<String, TruyNaData.TruyNaObject> truyna = new HashMap();

    public static HashMap<String, TruyNaData.TruyNaObject> listTruyNa() {
        return truyna;
    }

    public static boolean isTruyNa(String playerName) {
        return truyna.containsKey(playerName);
    }

    public static TruyNaData.TruyNaObject getTruyNa(String playerName) {
        return (TruyNaData.TruyNaObject)truyna.get(playerName);
    }

    public static void remove(String playerName) {
        if (truyna.containsKey(playerName)) {
            truyna.remove(playerName);
        }

    }

    public static void truyna(String playerName, String owner, String priceType, double priceValue) {
        truyna.put(playerName, new TruyNaData.TruyNaObject(owner, priceType, priceValue));
    }

    public static void saveToConfig() {
        FileConfiguration data = FileManager.getFileConfig(FileManager.Files.DATA);
        data.set("TruyNa", (Object)null);

        for(String key : truyna.keySet()) {
            TruyNaData.TruyNaObject obj = (TruyNaData.TruyNaObject)truyna.get(key);
            data.set("TruyNa." + key + ".Owner", obj.getOwner());
            data.set("TruyNa." + key + ".PriceType", obj.getPriceType());
            data.set("TruyNa." + key + ".PriceValue", obj.getPriceValue());
        }

        FileManager.saveFileConfig(data, FileManager.Files.DATA);
    }

    public static void loadFromConfig() {
        FileConfiguration data = FileManager.getFileConfig(FileManager.Files.DATA);
        if (data.contains("TruyNa")) {
            for(String key : data.getConfigurationSection("TruyNa").getKeys(false)) {
                String owner = data.getString("TruyNa." + key + ".Owner");
                String priceType = data.getString("TruyNa." + key + ".PriceType");
                double priceValue = data.getDouble("TruyNa." + key + ".PriceValue");
                truyna.put(key, new TruyNaData.TruyNaObject(owner, priceType, priceValue));
            }
        }

    }

    public static class TruyNaObject {
        private String owner;
        private String priceType;
        private double priceValue;

        public TruyNaObject(String owner, String priceType, double priceValue) {
            this.owner = owner;
            this.priceType = priceType;
            this.priceValue = priceValue;
        }

        public String getOwner() {
            return this.owner;
        }

        public String getPriceType() {
            return this.priceType;
        }

        public double getPriceValue() {
            return this.priceValue;
        }
    }
}
