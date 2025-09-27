package xianxiacraft.xianxiacraft.compatibility;

import org.bukkit.Bukkit;

public class VersionManager {
    private static VersionAdapter adapter;
    private static CoreDetector.ServerCore serverCore;
    
    public static void detectVersion() {
        // Сначала определяем ядро сервера
        serverCore = CoreDetector.detectServerCore();
        
        // Безопасное определение версии Minecraft
        String version = "unknown";
        try {
            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            String[] packageParts = packageName.split("\\.");
            
            if (packageParts.length >= 4) {
                version = packageParts[3];
            } else {
                // Альтернативный метод определения версии
                String bukkitVersion = Bukkit.getBukkitVersion();
                if (bukkitVersion.contains("1.21")) version = "v1_21_R1";
                else if (bukkitVersion.contains("1.20")) version = "v1_20_R3";
                else if (bukkitVersion.contains("1.19")) version = "v1_19_R3";
                else if (bukkitVersion.contains("1.18")) version = "v1_18_R2";
                else if (bukkitVersion.contains("1.17")) version = "v1_17_R1";
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[XianxiaCraft] Ошибка определения версии: " + e.getMessage());
            version = "v1_19_R3"; // Фолбэк
        }
        
        // Выбираем адаптер в зависимости от версии
        Bukkit.getLogger().info("[XianxiaCraft] Определена версия: " + version);
        
        switch (version) {
            case "v1_21_R1": 
                adapter = new Version_1_21_Adapter(); 
                break;
            case "v1_20_R3": 
                adapter = new Version_1_20_Adapter(); 
                break;
            case "v1_19_R3": 
                adapter = new Version_1_19_Adapter(); 
                break;
            case "v1_18_R2": 
                adapter = new Version_1_18_Adapter(); 
                break;
            case "v1_17_R1": 
                adapter = new Version_1_17_Adapter(); 
                break;
            default: 
                Bukkit.getLogger().warning("[XianxiaCraft] Неизвестная версия " + version + ", используется адаптер для 1.19");
                adapter = new Version_1_19_Adapter();
        }
        
        Bukkit.getLogger().info("[XianxiaCraft] Загружен адаптер для: " + adapter.getVersionName() + 
                               " на ядре: " + serverCore.getName());
    }
    
    public static VersionAdapter getAdapter() {
        return adapter;
    }
    
    public static CoreDetector.ServerCore getServerCore() {
        return serverCore;
    }
    
    public static FoliaScheduler getScheduler(org.bukkit.plugin.java.JavaPlugin plugin) {
        return new FoliaScheduler(plugin);
    }
}