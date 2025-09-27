package xianxiacraft.xianxiacraft.QiManagers;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.lang.Math;

import static xianxiacraft.xianxiacraft.QiManagers.ManualManager.getManual;

public class PointManager implements Listener {

    // Карта для хранения очков культивации игроков: UUID -> количество очков
    private static Map<UUID, Integer> pointMap = new HashMap<>();
    
    // Карта для хранения достижений Дао игроков: UUID -> набор названий мануалов
    public static Map<UUID, Set<String>> daoAttainmentMap = new HashMap<>();
    
    // Кэш для максимального ци
    private static final Map<UUID, Integer> maxQiCache = new HashMap<>();
    
    private final JavaPlugin plugin;

    public PointManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Расчет максимального запаса ци игрока (экспоненциальный рост: 10 × 2^стадия)
    public static int getMaxQi(Player player){
        UUID playerId = player.getUniqueId();
        if (maxQiCache.containsKey(playerId)) {
            return maxQiCache.get(playerId);
        }
        int maxQi = 10 * (1 << getStage(player)); // Быстрее чем Math.pow
        maxQiCache.put(playerId, maxQi);
        return maxQi;
    }

    // Метод получения стадии культиватора на основе очков (логарифмическая функция)
    public static int getStage(Player player){
        int points = pointMap.getOrDefault(player.getUniqueId(), 0);
        // Формула: log₂(points/20 + 1)
        // Прогрессия: 0, 20, 40, 80, 160 и т.д.
        return (int) (Math.log((points / 20.0) + 1) / Math.log(2));
    }

    // Получение количества очков культивации игрока
    public static int getPoints(Player player) {
        return pointMap.getOrDefault(player.getUniqueId(), 0);
    }

    // Метод очистки кеша
    public static void clearCache(UUID playerId) {
        maxQiCache.remove(playerId);
    }

    // Метод добавления очков с проверкой достижения Дао
    public static void addPoints(Player player, int points) {
        UUID id = player.getUniqueId();
        pointMap.put(id, getPoints(player) + points);
    
        // Проверка достижения 10+ стадии для получения достижения Дао
        if(getStage(player) >= 10){
            Set<String> t = daoAttainmentMap.getOrDefault(id, new HashSet<>());
            // Добавление мануала в достижения если его еще нет
            if(t.add(getManual(player))){
                player.sendMessage(ChatColor.DARK_PURPLE + "Вы достигли Познания Дао.");
                player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1.0F, 1.0F);
            }
            daoAttainmentMap.put(id, t);
        }
        
        // Очистка кэша после изменения очков
        clearCache(id);
    }
    
    // Метод добавления очков без проверки достижения Дао (для внутреннего использования)
    public static void addPointsWithoutDaoCheck(Player player, int points) {
        UUID id = player.getUniqueId();
        pointMap.put(id, getPoints(player) + points);
        clearCache(id);
    }

    // Установка точного количества очков игроку
    public static void setPoints(Player player, int points) {
        UUID id = player.getUniqueId();
        pointMap.put(id, points);
        clearCache(id);
    }

    // Расчет процента прогресса до следующей стадии
    public static double percentToNextStage(Player player) {
        int points = getPoints(player);
        int stage = getStage(player);

        // Формула: 100 × (текущие_очки - очки_начала_стадии) / (очки_следующей_стадии - очки_начала_стадии)
        double startOfStage = 20 * Math.pow(2, stage) - 20; // Очки на начало текущей стадии
        double startOfNextStage = 20 * Math.pow(2, stage + 1) - 20; // Очки на начало следующей стадии
        
        if (startOfNextStage <= startOfStage) {
            return 100.0; // Если следующая стадия недостижима
        }
        
        double percentage = 100 * (points - startOfStage) / (startOfNextStage - startOfStage);
        
        // Ограничение процента от 0 до 100
        percentage = Math.max(0, Math.min(100, percentage));
        
        // Округление до двух знаков после запятой
        return Math.round(percentage * 100.0) / 100.0;
    }

    // Сохранение данных очков в YAML файл
    public void savePointData() {
        File dataFolder = plugin.getDataFolder();
        File dataFile = new File(dataFolder, "point_data.yml");
        try {
            if (!dataFile.exists()) {
                dataFolder.mkdirs();
                dataFile.createNewFile();
            }

            FileWriter writer = new FileWriter(dataFile);
            Yaml yaml = new Yaml();
            yaml.dump(pointMap, writer); // Сериализация карты очков
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Загрузка данных очков из YAML файла
    @SuppressWarnings("unchecked")
    public void loadPointData() {
        File dataFile = new File(plugin.getDataFolder(), "point_data.yml");
        try {
            if (dataFile.exists()) {
                FileReader reader = new FileReader(dataFile);
                LoaderOptions loaderOptions = new LoaderOptions();
                loaderOptions.setTagInspector(tag -> true);
                Yaml yaml = new Yaml(loaderOptions);
                Object loadedData = yaml.load(reader);
                if (loadedData instanceof Map) {
                    pointMap = (Map<UUID, Integer>) loadedData;
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Сохранение данных достижений Дао в YAML файл
    public void saveDaoAttainmentData() {
        File dataFolder = plugin.getDataFolder();
        File dataFile = new File(dataFolder, "dao_attainment_data.yml");
        try {
            if (!dataFile.exists()) {
                dataFolder.mkdirs();
                dataFile.createNewFile();
            }

            FileWriter writer = new FileWriter(dataFile);
            Yaml yaml = new Yaml();
            yaml.dump(daoAttainmentMap, writer); // Сериализация карты достижений
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Загрузка данных достижений Дао из YAML файла
    @SuppressWarnings("unchecked")
    public void loadDaoAttainmentData() {
        File dataFile = new File(plugin.getDataFolder(), "dao_attainment_data.yml");
        try {
            if (dataFile.exists()) {
                FileReader reader = new FileReader(dataFile);
                LoaderOptions loaderOptions = new LoaderOptions();
                loaderOptions.setTagInspector(tag -> true);
                Yaml yaml = new Yaml(loaderOptions);
                Object loadedData = yaml.load(reader);
                if (loadedData instanceof Map) {
                    daoAttainmentMap = (Map<UUID, Set<String>>) loadedData;
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}