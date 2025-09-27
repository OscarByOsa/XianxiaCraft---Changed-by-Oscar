package xianxiacraft.xianxiacraft.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class FoliaScheduler {
    
    private final JavaPlugin plugin;
    
    public FoliaScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    // Простая обертка над стандартным шедулером
    public BukkitTask runTask(Runnable task) {
        return Bukkit.getScheduler().runTask(plugin, task);
    }
    
    public BukkitTask runTaskLater(Runnable task, long delayTicks) {
        return Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
    }
    
    public BukkitTask runTaskTimer(Runnable task, long delayTicks, long periodTicks) {
        return Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }
    
    // Для Folia-совместимости - используем стандартные методы
    public BukkitTask runTaskForEntity(Entity entity, Runnable task) {
        return runTask(task); // Фолбэк на стандартный метод
    }
    
    public BukkitTask runTaskAtLocation(Location location, Runnable task) {
        return runTask(task); // Фолбэк на стандартный метод
    }
    
    public BukkitTask runTaskAsync(Runnable task) {
        if (CoreDetector.isAsyncSupported()) {
            return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        } else {
            return runTask(task);
        }
    }
    
    public void cancelAllTasks() {
        Bukkit.getScheduler().cancelTasks(plugin);
    }
}