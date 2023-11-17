// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.data;

import camchua.dosat.Main;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataStorage {
    public static boolean checkData(String name) {
        File f = new File(Main.getPlugin().getDataFolder(), "data//" + name + ".yml");
        return f.exists();
    }

    public static File getFile(String name) {
        File f = new File(Main.getPlugin().getDataFolder(), "data//" + name + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
                f.getParentFile().mkdirs();
                Main.getPlugin().saveResource("data//" + name + ".yml", false);
            } catch (Exception var3) {
            }
        }

        return f;
    }

    public static FileConfiguration getData(String name) {
        File f = getFile(name);
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(f);
        } catch (Exception var4) {
        }

        return config;
    }

    public static void saveData(FileConfiguration data, String name) {
        try {
            data.save(getFile(name));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }
}
