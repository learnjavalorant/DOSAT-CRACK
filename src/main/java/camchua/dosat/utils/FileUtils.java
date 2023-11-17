// Decompiled with: FernFlower
// Class Version: 16
package camchua.dosat.utils;

import java.io.InputStream;
import org.bukkit.plugin.Plugin;

public class FileUtils {
    public static byte[] readResource(Plugin plugin, String resourcePath) {
        try {
            InputStream is = plugin.getResource(resourcePath);

            byte[] var3;
            try {
                var3 = is.readAllBytes();
            } catch (Throwable var6) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            if (is != null) {
                is.close();
            }

            return var3;
        } catch (Exception var7) {
            var7.printStackTrace();
            return new byte[0];
        }
    }
}
