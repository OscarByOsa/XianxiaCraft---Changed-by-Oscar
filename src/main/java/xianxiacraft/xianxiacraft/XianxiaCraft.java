package xianxiacraft.xianxiacraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xianxiacraft.xianxiacraft.commands.PlayerCommands;
import xianxiacraft.xianxiacraft.customItems.ToolItems;
import xianxiacraft.xianxiacraft.handlers.*;
import xianxiacraft.xianxiacraft.QiManagers.PointManager;
import xianxiacraft.xianxiacraft.QiManagers.QiManager;
import xianxiacraft.xianxiacraft.QiManagers.ScoreboardManager1;
import xianxiacraft.xianxiacraft.QiManagers.ManualManager;
import xianxiacraft.xianxiacraft.commands.CultPassiveCommandExecutor;
import xianxiacraft.xianxiacraft.commands.OperatorCommands;
import xianxiacraft.xianxiacraft.handlers.Manuals.*;
import xianxiacraft.xianxiacraft.runnables.RunAura;
import xianxiacraft.xianxiacraft.util.ManualItems;
import xianxiacraft.xianxiacraft.compatibility.CoreDetector;
import xianxiacraft.xianxiacraft.compatibility.FoliaScheduler;
import xianxiacraft.xianxiacraft.compatibility.VersionManager;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;
import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManualQiRegen;
import static xianxiacraft.xianxiacraft.QiManagers.PointManager.getMaxQi;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.getQi;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.subtractQi;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.*;
import static xianxiacraft.xianxiacraft.commands.CultPassiveCommandExecutor.runAuraMap;
import static xianxiacraft.xianxiacraft.handlers.Manuals.FattyManual.fattyManualQiMove;
import static xianxiacraft.xianxiacraft.handlers.Manuals.FungalManual.fungalManualQiMove;
import static xianxiacraft.xianxiacraft.handlers.Manuals.SugarFiendManual.sugarFiendQiMove;
import static xianxiacraft.xianxiacraft.util.CountNearbyBlocks.checkIfBlockUnderPlayerSomewhere;
import static xianxiacraft.xianxiacraft.util.CountNearbyBlocks.countNearbyBlocks;
import static xianxiacraft.xianxiacraft.util.ParticleEffects.qiAuraParticleEffect;

public final class XianxiaCraft extends JavaPlugin {

    // Менеджеры данных
    PointManager pointManager;
    ManualManager manualManager;
    QiManager qiManager;
    private static BukkitAudiences adventure;
    private FoliaScheduler foliaScheduler;

    @Override
    public void onEnable() {
        try {
        // Сохраняем конфиг по умолчанию
        saveDefaultConfig();
        
        // Инициализация системы совместимости ПЕРВОЙ
        VersionManager.detectVersion();
        this.foliaScheduler = VersionManager.getScheduler(this);
        
        String coreInfo = CoreDetector.getCoreInfo();
        getLogger().info("╔══════════════════════════════════════╗");
        getLogger().info("║          XianxiaCraft v1.2.0         ║");
        getLogger().info("║    Поддержка ядер: Paper, Purpur,    ║");
        getLogger().info("║         Folia, Spigot 1.17-1.21      ║");
        getLogger().info("║                                      ║");
        getLogger().info("║  Обнаружено ядро: " + String.format("%-18s", coreInfo) + "║");
        getLogger().info("╚══════════════════════════════════════╝");

        // Инициализация менеджеров данных
        pointManager = new PointManager(this);
        pointManager.loadPointData();
        pointManager.loadDaoAttainmentData();

        manualManager = new ManualManager(this);
        manualManager.loadManualData();

        qiManager = new QiManager(this);
        qiManager.loadQiData();

        adventure = BukkitAudiences.create(this);

        // Регистрация обработчиков событий
        new ScoreboardManager1(this);
        new PointHandler(this);
        new HitEvents(this);
        new ItemDropEvents(this);
        new MoveEvents(this);
        new CustomItemEvents(this);
        new PlayerDeathHandler(this);

        // Регистрация команд
        CultPassiveCommandExecutor cultPassiveCommandExecutor = new CultPassiveCommandExecutor(this);
        Objects.requireNonNull(getCommand("qipunch")).setExecutor(cultPassiveCommandExecutor);
        Objects.requireNonNull(getCommand("qimine")).setExecutor(cultPassiveCommandExecutor);
        Objects.requireNonNull(getCommand("qimove")).setExecutor(cultPassiveCommandExecutor);
        Objects.requireNonNull(getCommand("detonate")).setExecutor(cultPassiveCommandExecutor);
        Objects.requireNonNull(getCommand("qiaura")).setExecutor(cultPassiveCommandExecutor);
        Objects.requireNonNull(getCommand("qifly")).setExecutor(cultPassiveCommandExecutor);

        PlayerCommands playerCommands = new PlayerCommands(this);
        Objects.requireNonNull(getCommand("cultutorial")).setExecutor(playerCommands);
        Objects.requireNonNull(getCommand("manualaccept")).setExecutor(playerCommands);
        Objects.requireNonNull(getCommand("dantianscoreboard")).setExecutor(playerCommands);

        OperatorCommands operatorCommands = new OperatorCommands();
        Objects.requireNonNull(getCommand("addstage")).setExecutor(operatorCommands);
        Objects.requireNonNull(getCommand("checkstats")).setExecutor(operatorCommands);
        Objects.requireNonNull(getCommand("obtain")).setExecutor(operatorCommands);

        // Инициализация предметов
        new ToolItems(this);
        ManualItems.init();
        ToolItems.init2();

        // Инициализация мануалов
        initializeManuals();

        // Запуск оптимизированных таймеров
        setupOptimizedSchedulers();

        getLogger().info("Плагин успешно запущен!");
        
    } catch (Exception e) {
        getLogger().severe("Критическая ошибка при запуске плагина: " + e.getMessage());
        e.printStackTrace();
        // Отключаем плагин при критической ошибке
        getServer().getPluginManager().disablePlugin(this);
    }
}

    private void initializeManuals() {
        // Создание экземпляров всех мануалов
        List<Object> manualList = new ArrayList<>();
        manualList.add(new IronSkinManual());
        manualList.add(new FattyManual());
        manualList.add(new IceManual());
        manualList.add(new PhoenixManual());
        manualList.add(new SpaceManual());
        manualList.add(new SugarFiendManual());
        manualList.add(new VampireManual());
        manualList.add(new PoisonManual());
        manualList.add(new FungalManual());
        manualList.add(new LightningManual());
        
        getLogger().info("Загружено мануалов: " + manualList.size());
    }

    private void setupOptimizedSchedulers() {
        // Оптимальные интервалы из конфига
        long qiInterval = getConfig().getLong("settings.qi-update-interval", getOptimalQiInterval());
        long particleInterval = getConfig().getLong("settings.particle-interval", getOptimalParticleInterval());

        // Основное обновление ци
        if (CoreDetector.isFoliaSupported()) {
            foliaScheduler.runTaskTimer(this::updateQiLevels, 0L, qiInterval);
        } else {
            Bukkit.getScheduler().runTaskTimer(this, this::updateQiLevels, 0L, qiInterval);
        }
        
        // Частицы ауры
        if (CoreDetector.isFoliaSupported()) {
            foliaScheduler.runTaskTimer(this::auraParticles, 10L, particleInterval);
        } else {
            Bukkit.getScheduler().runTaskTimer(this, this::auraParticles, 10L, particleInterval);
        }

        getLogger().info("Таймеры настроены: Qi=" + qiInterval + "t, Particles=" + particleInterval + "t");
    }

    private long getOptimalQiInterval() {
        switch (CoreDetector.detectServerCore()) {
            case FOLIA: return 40L;
            case PURPUR: return 50L;
            case PAPER: return 60L;
            case SPIGOT: return 80L;
            default: return 60L;
        }
    }

    private long getOptimalParticleInterval() {
        switch (CoreDetector.detectServerCore()) {
            case FOLIA: return 5L;
            case PURPUR: return 8L;
            case PAPER: return 10L;
            case SPIGOT: return 15L;
            default: return 10L;
        }
    }

    @Override
    public void onDisable() {
        try {
        // Корректная отмена задач
        if (foliaScheduler != null) {
            foliaScheduler.cancelAllTasks();
        } else {
            Bukkit.getScheduler().cancelTasks(this);
        }

        // Сохранение данных (проверяем на null)
        if (pointManager != null) {
            pointManager.savePointData();
            pointManager.saveDaoAttainmentData();
        }
        if (manualManager != null) {
            manualManager.saveManualData();
        }
        if (qiManager != null) {
            qiManager.saveQiData();
        }

        // Очистка кэшей
        for (Player player : Bukkit.getOnlinePlayers()) {
            PointManager.clearCache(player.getUniqueId());
        }

        if (adventure != null) {
            adventure.close();
            adventure = null;
        }

        getLogger().info("Плагин корректно выключен. Данные сохранены.");
    } catch (Exception e) {
        getLogger().warning("Ошибка при выключении плагина: " + e.getMessage());
    }
}

    private void updateQiLevels() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String playerManual = getManual(player);

            if (!playerManual.equals("none")) {
                int currentQi = getQi(player);
                int maxQi = getMaxQi(player);
                double qiRegenPercent = getManualQiRegen(playerManual);

                // Регенерация ци
                if (currentQi < maxQi) {
                    int regenAmount = (int) Math.ceil(maxQi * qiRegenPercent);
                    if (currentQi + regenAmount > maxQi) {
                        QiManager.setQi(player, maxQi);
                    } else {
                        QiManager.addQi(player, regenAmount);
                    }
                }

                // Пассивные затраты ци для техник
                processQiCosts(player, playerManual);

                // Обновление интерфейса
                ScoreboardManager1.updateScoreboard(player);
            }
        }
    }

    private void processQiCosts(Player player, String playerManual) {
        // QiMine - ускорение копания
        if (getMineBool(player) && getQi(player) >= 3) {
            QiManager.subtractQi(player, 3);
        } else if (getMineBool(player)) {
            setMineBool(player, false);
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            player.sendMessage(ChatColor.GOLD + "Недостаточно ци для копания. QiMine: Выкл");
        }

        // Картина Тайцзи - скрытие
        if (getHiddenByTaijiPaintingBool(player)) {
            int cost = (int) Math.ceil(getMaxQi(player) * (getManualQiRegen(playerManual) + 0.01)) + 1;
            if (getQi(player) >= cost) {
                QiManager.subtractQi(player, cost);
            } else {
                setHiddenByTaijiPaintingBool(player, false);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p != player) p.showPlayer(this, player);
                }
                player.sendMessage(ChatColor.GOLD + "Закончилась ци для картины. Скрытие снято.");
            }
        }

        // QiMove - техники движения
        if (getMoveBool(player)) {
            processMovementCosts(player, playerManual);
        }

        // QiAura - аура
        if (getAuraBool(player)) {
            if (getQi(player) >= 100) {
                subtractQi(player, 100);
            } else {
                setAuraBool(player, false);
                runAuraMap.getOrDefault(player, new RunAura(this, player)).stop();
                qiAuraGlow(player, false);
                player.sendMessage(ChatColor.GOLD + "Недостаточно ци для ауры.");
            }
        }

        // QiFly - полет
        if (getFlyBool(player) && player.isFlying() && getQi(player) >= 100) {
            subtractQi(player, 100);
        } else if (getFlyBool(player) && player.isFlying()) {
            setFlyBool(player, false);
            player.setAllowFlight(false);
            player.setFlySpeed(0.1f);
            player.sendMessage(ChatColor.GOLD + "Недостаточно ци для полета.");
        }
    }

private void processMovementCosts(Player player, String playerManual) {
    int cost;
    switch (playerManual) {
        case "Sugar Fiend":
        case "Fatty Manual":
            cost = 12;
            break;
        case "Fungal Manual":
            cost = 16;
            break;
        case "Ice Manual":
            cost = 20;
            break;
        default:
            cost = 10;
            break;
    }

    if (getQi(player) >= cost) {
        QiManager.subtractQi(player, cost);
        
        // Специальные эффекты для грибного мануала
        if (playerManual.equals("Fungal Manual")) {
            if (countNearbyBlocks(player, Material.MYCELIUM, 2) > 0 || 
                checkIfBlockUnderPlayerSomewhere(player, Material.MYCELIUM, 20)) {
                fungalManualQiMove(player, true);
            }
        }
    } else {
        setMoveBool(player, false);
        switch (playerManual) {
            case "Sugar Fiend":
                sugarFiendQiMove(player, false);
                break;
            case "Fatty Manual":
                fattyManualQiMove(player, false);
                break;
            case "Fungal Manual":
                fungalManualQiMove(player, false);
                break;
        }
        player.sendMessage(ChatColor.GOLD + "Недостаточно ци для движения. QiMove: Выкл");
    }
}

    private void auraParticles() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (getAuraBool(player)) {
                qiAuraParticleEffect(player);
            }
        }
    }

    public static BukkitAudiences getAdventure() {
        return adventure;
    }
}