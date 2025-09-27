package xianxiacraft.xianxiacraft.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.logging.Logger;

public class CoreDetector {
    
    public enum ServerCore {
        SPIGOT("Spigot"),
        PAPER("Paper"),
        PURPUR("Purpur"),
        FOLIA("Folia"),
        UNKNOWN("Unknown");
        
        private final String name;
        
        ServerCore(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
    }
    
    private static ServerCore detectedCore = null;
    
    public static ServerCore detectServerCore() {
        if (detectedCore != null) {
            return detectedCore;
        }
        
        Server server = Bukkit.getServer();
        String serverVersion = server.getVersion().toLowerCase();
        Logger logger = Bukkit.getLogger();
        
        // Простая проверка по версии (без сложных зависимостей)
        if (serverVersion.contains("folia")) {
            detectedCore = ServerCore.FOLIA;
        } else if (serverVersion.contains("purpur")) {
            detectedCore = ServerCore.PURPUR;
        } else if (serverVersion.contains("paper")) {
            detectedCore = ServerCore.PAPER;
        } else {
            detectedCore = ServerCore.SPIGOT;
        }
        
        logger.info("[XianxiaCraft] Обнаружено ядро: " + detectedCore.getName());
        return detectedCore;
    }
    
    public static boolean isFoliaSupported() {
        return detectServerCore() == ServerCore.FOLIA;
    }
    
    public static boolean isAsyncSupported() {
        ServerCore core = detectServerCore();
        return core == ServerCore.PAPER || core == ServerCore.PURPUR || core == ServerCore.FOLIA;
    }
    
    public static String getCoreInfo() {
        ServerCore core = detectServerCore();
        return core.getName() + " (" + Bukkit.getServer().getVersion() + ")";
    }
}