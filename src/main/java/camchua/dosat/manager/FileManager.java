// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.manager;

import camchua.dosat.manager.FileManager;
import camchua.dosat.utils.Utils;
import java.io.File;
import java.util.HashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class FileManager {
    private static HashMap<FileManager.Files, File> file = new HashMap();
    private static HashMap<FileManager.Files, FileConfiguration> configuration = new HashMap();

    public static void setup(Plugin plugin) {
        FileManager.Files[] var4;
        for(FileManager.Files f : var4 = FileManager.Files.values()) {
            String location = Utils.isLegacy() ? f.getLegacyLocation() : f.getLocation();
            File fl = new File(plugin.getDataFolder(), location);
            if (!fl.exists()) {
                fl.getParentFile().mkdirs();
                plugin.saveResource(location, false);
            }

            YamlConfiguration config = new YamlConfiguration();

            try {
                config.load(fl);
            } catch (Exception var9) {
            }

            file.put(f, fl);
            configuration.put(f, config);
        }

    }

    public static FileConfiguration getFileConfig(FileManager.Files f) {
        return (FileConfiguration)configuration.get(f);
    }

    public static void saveFileConfig(FileConfiguration data, FileManager.Files f) {
        try {
            data.save((File)file.get(f));
        } catch (Exception var3) {
        }

    }

    public static void loadFileConfig(FileConfiguration data, FileManager.Files f) {
        try {
            data.load((File)file.get(f));
            configuration.replace(f, data);
        } catch (Exception var3) {
        }

    }

    public static enum Files {
        CONFIG("config-1.13.yml", "config.yml"),
        ARENAS("arenas.yml", "arenas.yml"),
        DATA("data.yml", "data.yml"),
        GEMS("gems-1.13.yml", "gems.yml");

        private String location;
        private String legacyLocation;

        private Files(String l, String legacyLocation) {
            this.location = l;
            this.legacyLocation = legacyLocation;
        }

        public String getLocation() {
            return this.location;
        }

        public String getLegacyLocation() {
            return this.legacyLocation;
        }

        // $FF: synthetic method
        private static FileManager.Files[] $values() {
            return new FileManager.Files[]{CONFIG, ARENAS, DATA, GEMS};
        }
    }
}
