package xianxiacraft.xianxiacraft.QiManagers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import xianxiacraft.xianxiacraft.XianxiaCraft;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static xianxiacraft.xianxiacraft.QiManagers.PointManager.*;
import static xianxiacraft.xianxiacraft.QiManagers.QiManager.getQi;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.*;
import static xianxiacraft.xianxiacraft.QiManagers.TechniqueManager.setAuraBool;

public class ScoreboardManager1 implements Listener {
    // Карты для хранения scoreboard и отдельных счетчиков для каждого игрока
    private static Map<UUID, Scoreboard> scoreboardMap = new HashMap<>(); // UUID -> Scoreboard
    private static Map<UUID, Score> scoreQiMap = new HashMap<>(); // UUID -> счетчик ци
    private static Map<UUID,Score> scoreStageMap = new HashMap<>(); // UUID -> счетчик стадии

    private final JavaPlugin plugin;

    public ScoreboardManager1(XianxiaCraft plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Создание scoreboard при входе игрока и отключение свечения
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        createScoreboard(player);
        player.setGlowing(false); // Гарантия что игрок не светится при входе
    }

    // Удаление scoreboard при выходе игрока
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeScoreboard(player);
    }

    // Создание кастомного scoreboard для игрока
    public void createScoreboard(Player player) {
        // Создание нового scoreboard
        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
        // Создание objective с названием "customObjective" и заголовком "Dantian"
        Objective objective = scoreboard.registerNewObjective("customObjective", "dummy", ChatColor.RED + "" + ChatColor.BOLD + "Dantian");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); // Размещение в боковой панели

        // Получение текущих данных игрока
        int qi = getQi(player);
        int maxQi = getMaxQi(player);
        int stage = getStage(player);
        double percent = percentToNextStage(player);

        // Создание строки для отображения ци
        Score scoreQi = objective.getScore(ChatColor.GOLD + "" + "Qi: " + qi + "/" + maxQi);
        scoreQi.setScore(0); // Позиция в scoreboard (0 - самая нижняя строка)

        // Создание строки для отображения стадии и прогресса
        Score scoreStage = objective.getScore(ChatColor.GOLD + "" + "Stage: " + stage + " [" + percent + "%]");
        scoreStage.setScore(1); // Позиция выше чем ци

        // Сохранение ссылок в картах для последующего обновления
        scoreboardMap.put(player.getUniqueId(), scoreboard);
        scoreQiMap.put(player.getUniqueId(), scoreQi);
        scoreStageMap.put(player.getUniqueId(), scoreStage);
        player.setScoreboard(scoreboard); // Применение scoreboard к игроку
    }

    // Обновление данных в scoreboard игрока
    public static void updateScoreboard(Player player) {
        Scoreboard scoreboard = scoreboardMap.get(player.getUniqueId());
        if (scoreboard != null) {
            Objective objective = scoreboard.getObjective("customObjective");
            if (objective != null) {

                Score scoreQi = scoreQiMap.get(player.getUniqueId());
                Score scoreStage = scoreStageMap.get(player.getUniqueId());

                // Получение актуальных данных
                int qi = getQi(player);
                int maxQi = getMaxQi(player);
                int stage = getStage(player);
                double percent = percentToNextStage(player);

                // Удаление старых записей
                scoreboard.resetScores(scoreQi.getEntry());
                scoreboard.resetScores((scoreStage.getEntry()));

                // Создание новых записей с обновленными данными
                scoreQi = objective.getScore(ChatColor.GOLD + "" + "Qi: " + qi + "/" + maxQi);
                scoreQi.setScore(0);

                scoreStage = objective.getScore(ChatColor.GOLD + "" + "Stage: " + stage + " [" + percent + "%]");
                scoreStage.setScore(1);

                // Обновление карт
                scoreboardMap.put(player.getUniqueId(), scoreboard);
                scoreQiMap.put(player.getUniqueId(), scoreQi);
                scoreStageMap.put(player.getUniqueId(), scoreStage);
            }
        }
    }

    // Удаление кастомного scoreboard и возврат к основному
    public void removeScoreboard(Player player) {
        Scoreboard scoreboard = scoreboardMap.remove(player.getUniqueId());
        if (scoreboard != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    // Скрытие scoreboard (установка пустого scoreboard)
    public static void hideScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            Scoreboard emptyScoreboard = manager.getNewScoreboard(); // Пустой scoreboard
            player.setScoreboard(emptyScoreboard); // Скрытие интерфейса
        }
    }

    // Показ scoreboard (восстановление кастомного или основного)
    public static void showScoreboard(Player player) {
        Scoreboard scoreboard = scoreboardMap.get(player.getUniqueId());
        if (scoreboard != null) {
            player.setScoreboard(scoreboard); // Восстановление кастомного
        } else {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard()); // Возврат к основному
        }
    }
}