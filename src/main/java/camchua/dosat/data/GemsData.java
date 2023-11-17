// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.data;

import camchua.dosat.data.GemsData;
import camchua.dosat.manager.FileManager;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;

public class GemsData {
    private static HashMap<String, GemsData.GemsObject> gems = new HashMap();

    public static GemsData.GemsObject getData(String player) {
        if (!gems.containsKey(player)) {
            GemsData.GemsObject obj = new GemsData.GemsObject(player, new HashMap());
            gems.put(player, obj);
            return obj;
        } else {
            return (GemsData.GemsObject)gems.get(player);
        }
    }

    public static void saveToConfig() {
        FileConfiguration data = FileManager.getFileConfig(FileManager.Files.DATA);
        data.set("Gems", (Object)null);

        for(String player : gems.keySet()) {
            GemsData.GemsObject obj = (GemsData.GemsObject)gems.get(player);

            for(String gemsType : obj.getGemsList().keySet()) {
                int level = obj.getGemsList().get(gemsType);
                data.set("Gems." + player + ".GemsList." + gemsType, level);
            }
        }

        FileManager.saveFileConfig(data, FileManager.Files.DATA);
    }

    public static void loadFromConfig() {
        FileConfiguration data = FileManager.getFileConfig(FileManager.Files.DATA);
        if (data.contains("Gems")) {
            for(String player : data.getConfigurationSection("Gems").getKeys(false)) {
                HashMap<String, Integer> gemsList = new HashMap();
                if (data.contains("Gems." + player + ".GemsList")) {
                    for(String gemsType : data.getConfigurationSection("Gems." + player + ".GemsList").getKeys(false)) {
                        int level = data.getInt("Gems." + player + ".GemsList." + gemsType);
                        gemsList.put(gemsType, level);
                    }
                }

                gems.put(player, new GemsData.GemsObject(player, gemsList));
            }
        }

    }

    public static class GemsObject {
        private String player;
        private HashMap<String, Integer> gemsList;

        public GemsObject(String player, HashMap<String, Integer> gemsList) {
            this.player = player;
            this.gemsList = gemsList;
        }

        public String getPlayer() {
            return this.player;
        }

        public HashMap<String, Integer> getGemsList() {
            return this.gemsList;
        }
    }
}
