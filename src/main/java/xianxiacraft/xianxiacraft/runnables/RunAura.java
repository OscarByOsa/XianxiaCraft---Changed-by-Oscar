package xianxiacraft.xianxiacraft.runnables;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xianxiacraft.xianxiacraft.compatibility.CoreDetector;
import xianxiacraft.xianxiacraft.compatibility.FoliaScheduler;
import xianxiacraft.xianxiacraft.compatibility.VersionManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getStage;

public class RunAura {
    private final JavaPlugin plugin;
    private final Player player;
    private final FoliaScheduler scheduler;
    private int angle = 0;
    private BukkitRunnable currentTask;
    private final Map<Integer, FallingBlock[]> auraBlocks = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // Кэш предварительно рассчитанных точек
    private static final Map<String, List<Vector>> preCalculatedPointsCache = new ConcurrentHashMap<>();

    public RunAura(JavaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.scheduler = VersionManager.getScheduler(plugin);
    }

    public void start() {
        int playerStage = getStage(player);
        String playerManual = getManual(player);

        int numberOfRings = Math.min(playerStage / 2, 5); // Ограничить максимум 5 колец
        double radius = 2 + playerStage / 2.0;

        // Предварительный расчет точек один раз
        String cacheKey = playerStage + "_" + numberOfRings;
        List<Vector> points = preCalculatedPointsCache.computeIfAbsent(cacheKey, k -> 
            calculateSpherePoints(numberOfRings, radius)
        );

        Material[] blockTypes = getBlockTypesForManual(playerManual);

        // Автоматическое определение оптимального метода запуска
        if (CoreDetector.isFoliaSupported()) {
            startFoliaAura(points, blockTypes);
        } else {
            startStandardAura(points, blockTypes);
        }
        
        plugin.getLogger().info("Аура запущена для " + player.getName() + " на ядре: " + CoreDetector.detectServerCore().getName());
    }

    private List<Vector> calculateSpherePoints(int rings, double radius) {
        List<Vector> points = new ArrayList<>();
        int pointsPerRing = 12; // Уменьшить количество точек для оптимизации

        for (int ring = 0; ring < rings; ring++) {
            double ringRadius = radius * (ring + 1) / rings;
            double ringHeight = (ring % 2 == 0) ? ringRadius * 0.5 : -ringRadius * 0.5;

            for (int i = 0; i < pointsPerRing; i++) {
                double angle = 2 * Math.PI * i / pointsPerRing;
                double x = ringRadius * Math.cos(angle);
                double z = ringRadius * Math.sin(angle);
                points.add(new Vector(x, ringHeight, z));
            }
        }
        return points;
    }

    private Material[] getBlockTypesForManual(String manual) {
        Material primary, secondary;
        
        switch(manual) {
            case "Ice Manual": primary = Material.PACKED_ICE; secondary = Material.BLUE_ICE; break;
            case "Sugar Fiend": primary = Material.NETHERRACK; secondary = Material.NETHER_QUARTZ_ORE; break;
            case "Fatty Manual": primary = Material.DIRT; secondary = Material.COARSE_DIRT; break;
            case "Fungal Manual": primary = Material.MYCELIUM; secondary = Material.MYCELIUM; break;
            case "Ironskin Manual": primary = Material.RAW_IRON_BLOCK; secondary = Material.IRON_BLOCK; break;
            case "LightningManual": primary = Material.RAW_COPPER_BLOCK; secondary = Material.COPPER_BLOCK; break;
            case "Phoenix Manual": primary = Material.HONEYCOMB_BLOCK; secondary = Material.MAGMA_BLOCK; break;
            case "Poison Manual": primary = Material.WARPED_WART_BLOCK; secondary = Material.NETHER_WART_BLOCK; break;
            case "Space Manual": primary = Material.BLACK_CONCRETE; secondary = Material.END_STONE; break;
            case "Demonic Manual": primary = Material.NETHERRACK; secondary = Material.NETHERRACK; break;
            default: primary = Material.GLASS; secondary = Material.GLASS;
        }
        
        return new Material[]{primary, secondary};
    }

    // Оптимизированный метод для Folia
    private void startFoliaAura(List<Vector> points, Material[] blockTypes) {
        scheduler.runTaskForEntity(player, () -> {
            if (!player.isOnline() || !player.isValid()) {
                stop();
                return;
            }

            Location center = player.getLocation();
            updateAuraBlocks(center, points, blockTypes);
            angle = (angle + 5) % 360;
        });

        // Перезапускаем задачу через оптимальный интервал
        long interval = getOptimalInterval();
        scheduler.runTaskLater(() -> startFoliaAura(points, blockTypes), interval);
    }

    // Стандартный метод для Spigot/Paper/Purpur
    private void startStandardAura(List<Vector> points, Material[] blockTypes) {
        currentTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !player.isValid()) {
                    stop();
                    return;
                }

                Location center = player.getLocation();
                updateAuraBlocks(center, points, blockTypes);
                angle = (angle + 5) % 360;
            }
        };
        
        long interval = getOptimalInterval();
        currentTask.runTaskTimer(plugin, 0L, interval);
    }

    private void updateAuraBlocks(Location center, List<Vector> points, Material[] blockTypes) {
        FallingBlock[] currentBlocks = auraBlocks.getOrDefault(player.getUniqueId().hashCode(), new FallingBlock[points.size()]);

        for (int i = 0; i < points.size(); i++) {
            Vector point = points.get(i);
            Location loc = center.clone().add(rotatePoint(point, angle));
            
            if (i < currentBlocks.length && currentBlocks[i] != null && !currentBlocks[i].isDead()) {
                // Плавное перемещение существующего блока
                currentBlocks[i].teleport(loc);
            } else {
                // Создание нового блока
                if (currentBlocks[i] != null && !currentBlocks[i].isDead()) {
                    currentBlocks[i].remove();
                }
                
                Material blockType = random.nextBoolean() ? blockTypes[0] : blockTypes[1];
                try {
                    FallingBlock newBlock = center.getWorld().spawnFallingBlock(loc, blockType.createBlockData());
                    newBlock.setDropItem(false);
                    newBlock.setGravity(false);
                    newBlock.setInvulnerable(true);
                    
                    if (i < currentBlocks.length) {
                        currentBlocks[i] = newBlock;
                    }
                } catch (Exception e) {
                    // Игнорировать ошибки создания блоков
                }
            }
        }
        
        auraBlocks.put(player.getUniqueId().hashCode(), currentBlocks);
    }

    private Vector rotatePoint(Vector point, double angleDegrees) {
        double angle = Math.toRadians(angleDegrees);
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        
        return new Vector(
            point.getX() * cos - point.getZ() * sin,
            point.getY(),
            point.getX() * sin + point.getZ() * cos
        );
    }

    // Оптимальные интервалы для разных ядер
    private long getOptimalInterval() {
        switch (CoreDetector.detectServerCore()) {
            case FOLIA: return 3L;    // Folia быстрее
            case PURPUR: return 4L;   // Purpur хорошо оптимизирован
            case PAPER: return 5L;    // Paper стандарт
            case SPIGOT: return 8L;   // Spigot медленнее
            default: return 5L;
        }
    }

    public void stop() {
        if (currentTask != null) {
            currentTask.cancel();
            currentTask = null;
        }

        FallingBlock[] blocks = auraBlocks.remove(player.getUniqueId().hashCode());
        if (blocks != null) {
            for (FallingBlock block : blocks) {
                if (block != null && !block.isDead()) {
                    try {
                        block.remove();
                    } catch (Exception e) {
                        // Игнорировать ошибки удаления
                    }
                }
            }
        }
        
        plugin.getLogger().info("Аура остановлена для " + player.getName());
    }
}